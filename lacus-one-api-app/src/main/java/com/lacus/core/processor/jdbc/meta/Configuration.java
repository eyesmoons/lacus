package com.lacus.core.processor.jdbc.meta;

import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;


@Slf4j
public class Configuration {

    private final ConcurrentHashMap<String, FutureTask<SQLTemplate>> templateCache;

    private transient boolean cacheTemplate;

    private Charset charset;

    public Configuration() {
        this(true, Charset.defaultCharset());
    }

    public Configuration(boolean cacheTemplate, Charset charset) {
        super();

        this.cacheTemplate = cacheTemplate;
        this.charset = charset;

        templateCache = new ConcurrentHashMap<>();
    }

    public SQLTemplate getTemplate(final String content) {
        if (cacheTemplate) {
            FutureTask<SQLTemplate> f = templateCache.get(content);
            if (f == null) {
                FutureTask<SQLTemplate> ft = new FutureTask<>(
                        () -> createTemplate(content));
                f = templateCache.putIfAbsent(content, ft);
                if (f == null) {
                    ft.run();
                    f = ft;
                }
            }
            try {
                return f.get();
            } catch (Exception e) {
                log.error("xml解析错误", e);
                templateCache.remove(content);
                throw new ApiException(ResultCode.API_PARAMETER_ERROR, "解析Sql错误,请检查配置Sql");
            }
        }

        return createTemplate(content);

    }

    private SQLTemplate createTemplate(String content) {
        SQLTemplate template = new SQLTemplate.SqlTemplateBuilder(this, content)
                .build();
        return template;
    }

    public SQLTemplate getTemplate(InputStream in) throws IOException {

        String content;
        try {
            content = readerContent(in);
        } catch (IOException e) {
            throw new IOException("Error reading template ", e);
        }

        return getTemplate(content);

    }

    public SQLTemplate getTemplate(File tplFile) throws FileNotFoundException,
            IOException {

        return this.getTemplate(new FileInputStream(tplFile));
    }

    private String readerContent(InputStream in) throws IOException {

        StringBuilder sb = new StringBuilder(in.available());

        InputStreamReader inputStreamReader = new InputStreamReader(
                new BufferedInputStream(in), charset);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();

        return sb.toString();
    }

    public boolean isCacheTemplate() {
        return cacheTemplate;
    }

    public void setCacheTemplate(boolean cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
