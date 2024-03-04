package com.lacus.function;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.qtone.bigdata.etl.DynamicEtlFunction;
import com.lacus.common.enums.ErrorMsgEnum;
import com.lacus.common.exception.CustomException;
import com.lacus.handler.FailExecutionHandler;
import com.lacus.script.FsClassLoader;
import com.lacus.script.JavaSourceFromString;
import com.lacus.script.ScriptFileManager;
import com.lacus.sink.DorisStreamLoad;
import com.lacus.model.DynamicETL;
import com.lacus.model.ErrorMsgModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Preconditions;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ETLFunction extends RichMapFunction<Map<String, String>, Map<String, String>> {

    private static final long serialVersionUID = -4657635960865734809L;
    public final static String DEFAULT_LANGUAGE = "java";
    private final DynamicETL dynamicETL;
    private DynamicEtlFunction dynamicEtlFunction;
    private final FailExecutionHandler failureHandler;
    private final Long jobId;
    private final Long instanceId;
    private final String topic;
    private final Map<String, DorisStreamLoad> dorisMap;
    private final String dataSourceName;

    public ETLFunction(DynamicETL dynamicETL, FailExecutionHandler failureHandler,
                       Long jobId, Long instanceId, String topic,
                       String dataSourceName, Map<String, DorisStreamLoad> dorisMap) {
        Preconditions.checkArgument(DEFAULT_LANGUAGE.equalsIgnoreCase(dynamicETL.getEtlLanguage()), "动态ETL目前只支持JAVA语言");
        this.dynamicETL = dynamicETL;
        this.failureHandler = failureHandler;
        this.jobId = jobId;
        this.instanceId = instanceId;
        this.topic = topic;
        this.dataSourceName = dataSourceName;
        this.dorisMap = dorisMap;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // 在每个节点编译
        this.dynamicEtlFunction = compileScript();
        this.failureHandler.failExecutionInit();
    }

    private DynamicEtlFunction compileScript() {
        // 接口的类加载器
        ClassLoader etlClassLoader = DynamicEtlFunction.class.getClassLoader();
        // 设置当前的线程类加载器
        Thread.currentThread().setContextClassLoader(etlClassLoader);

        String className = parseClassName(dynamicETL.getEtlScript());

        String projectPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        log.info("project path：{}", projectPath);
        String classPath = getJarFiles(String.format("%s/libs/ext", projectPath));
        log.info("class path：{}", classPath);
        // 编译的选项，对应于命令行参数
        List<String> options = new ArrayList<>();
        options.add("-classpath");
        options.add(classPath);

        // 需要进行编译的代码
        Iterable<? extends JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>() {
            private static final long serialVersionUID = 1962005602348433877L;
            {
                add(new JavaSourceFromString(className, dynamicETL.getEtlScript()));
            }
        };

        // 使用系统编译器
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        ScriptFileManager scriptFileManager = new ScriptFileManager(standardJavaFileManager);

        // 使用stringWriter来收集错误。
        StringWriter errorStringWriter = new StringWriter();

        // 开始进行编译
        boolean ok = javaCompiler.getTask(errorStringWriter, scriptFileManager, diagnostic -> {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {

                errorStringWriter.append(diagnostic.toString());
            }
        }, options, null, compilationUnits).call();

        if (!ok) {
            String errorMessage = errorStringWriter.toString();
            // 编译出错，直接抛错。
            log.error("Java代码编译错误:{}", errorMessage);
            throw new CustomException("动态脚本编译错误:" + errorMessage);
        }

        // 获取到编译后的二进制数据。
        final Map<String, byte[]> allBuffers = scriptFileManager.getAllBuffers();
        final byte[] catBytes = allBuffers.get(className);

        // 使用自定义的ClassLoader
        FsClassLoader fsClassLoader = new FsClassLoader(etlClassLoader, className, catBytes);
        Class<?> scriptClass = fsClassLoader.findClass(className);
        Object scriptObj = null;
        try {
            scriptObj = scriptClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("获取反射类失败:{}", className, e);
        }
        Preconditions.checkArgument(scriptObj != null, "找不到类:" + className);
        Preconditions.checkArgument(scriptObj instanceof DynamicEtlFunction, className + "类未实现 DynamicEtlFunction 接口");
        return (DynamicEtlFunction) scriptObj;
    }

    @Override
    public Map<String, String> map(Map<String, String> value) {
        if (dynamicETL == null) {
            return value;
        }
        Map<String, String> resultMap = new HashMap<>();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            String key = entry.getKey();
            DorisStreamLoad dorisStreamLoad = dorisMap.get(key);
            List<?> cleanResult = null;
            try {
                Map<String, Object> contextMap = new HashMap<>();
                contextMap.put(key, entry.getValue());
                cleanResult = dynamicEtlFunction.process(entry.getValue(), contextMap);
            } catch (Exception ex) {
                log.error("数据清洗失败:", ex);
                // send error data to kafka
                ErrorMsgModel errorMsg = new ErrorMsgModel();
                errorMsg.setJobId(jobId);
                errorMsg.setInstanceId(instanceId);
                errorMsg.setErrorData(entry.getValue());
                errorMsg.setSourceKafkaTopic(topic);
                errorMsg.setSinkDatasourceName(dataSourceName);
                errorMsg.setSinkDbName(dorisStreamLoad.getDb());
                errorMsg.setSinkTableName(dorisStreamLoad.getTbl());
                errorMsg.setErrorMsgType(ErrorMsgEnum.CLEAN_ERROR.name());
                errorMsg.setErrorType(ErrorMsgEnum.CLEAN_ERROR.getStatus());
                errorMsg.setErrorDorisMsg(ex.getMessage());
                errorMsg.setErrorMsg(ExceptionUtils.getStackTrace(ex));
                failureHandler.failExecution(errorMsg, " 数据清洗失败: ");
            }
            if (ObjectUtils.isNotEmpty(cleanResult)) {
                resultMap.put(key, JSON.toJSONString(cleanResult, JSONWriter.Feature.LargeObject));
            }
        }
        return resultMap;
    }

    @Override
    public void close() throws Exception {
        super.close();
        failureHandler.failExecutionClose();
    }

    /**
     * 解析类名
     */
    private static String parseClassName(String script) {
        final String REGEX = "public\\s+class\\s+(\\w+)\\s+implements\\s+DynamicEtlFunction";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(script);
        String className = null;
        if (matcher.find()) {
            className = matcher.group(1);
        }
        if (StringUtils.isBlank(className)) {
            log.error("找不到类名:{}", script);
            throw new CustomException("找不到类名");
        }
        log.info("解析到类名:{}", className);
        return className;
    }

    /**
     * 查找该目录下的所有的jar文件
     */
    private static String getJarFiles(String jarPath) {
        File sourceFile = new File(jarPath);
        StringJoiner sj = new StringJoiner(File.pathSeparator);
        if (sourceFile.exists() && sourceFile.isDirectory()) {
            for (File file : Objects.requireNonNull(sourceFile.listFiles())) {
                if (!file.isDirectory() && file.getName().endsWith(".jar")) {
                    sj.add(file.getPath());
                }
            }
        }
        return sj.toString();
    }
}
