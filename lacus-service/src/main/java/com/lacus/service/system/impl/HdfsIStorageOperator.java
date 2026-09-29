package com.lacus.service.system.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lacus.common.config.HdfsStorageProperties;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.system.entity.StorageEntity;
import com.lacus.enums.ResourceType;
import com.lacus.service.system.IStorageOperate;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.CommonUtils;
import com.lacus.utils.HttpUtils;
import com.lacus.utils.JSONUtils;
import com.lacus.utils.KerberosHttpClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lacus.common.constant.Constants.COLON;
import static com.lacus.common.constant.Constants.COMMA;
import static com.lacus.common.constant.Constants.DEFAULT_HDFS_CONFIG;
import static com.lacus.common.constant.Constants.DOUBLE_SLASH;
import static com.lacus.common.constant.Constants.FOLDER_SEPARATOR;
import static com.lacus.common.constant.Constants.HADOOP_RM_STATE_ACTIVE;
import static com.lacus.common.constant.Constants.HDFS_DEFAULT_FS;

@Slf4j
@Service("storageOperator")
public class HdfsIStorageOperator implements Closeable, IStorageOperate {

    protected static HdfsStorageProperties hdfsProperties = new HdfsStorageProperties();

    @Getter
    private volatile boolean yarnEnabled = false;

    @Getter
    private Configuration configuration;
    private FileSystem fs;

    public HdfsIStorageOperator() {
        this(new HdfsStorageProperties());
    }

    public HdfsIStorageOperator(HdfsStorageProperties hdfsStorageProperties) {
        hdfsProperties = hdfsStorageProperties;
        init();
    }

    /**
     * init hadoop configuration
     */
    public void init() throws NullPointerException {
        try {
            configuration = new HdfsConfiguration();
            String hdfsUser = hdfsProperties.getUser();
            if (CommonUtils.loadKerberosConf(configuration)) {
                hdfsUser = "";
            }

            String defaultFS = getDefaultFS();
            if (StringUtils.isNotBlank(defaultFS)) {
                Map<String, String> fsRelatedProps = CommonPropertyUtils.getByPrefix("fs.");
                configuration.set(HDFS_DEFAULT_FS, defaultFS);
                fsRelatedProps.forEach((key, value) -> configuration.set(key, value));
            } else {
                log.error("请设置:{}", DEFAULT_HDFS_CONFIG);
                throw new NullPointerException(String.format("请设置: %s", DEFAULT_HDFS_CONFIG));
            }
            if (!defaultFS.startsWith("file")) {
                log.info("get property:{} -> {}, from core-site.xml hdfs-site.xml ", DEFAULT_HDFS_CONFIG, defaultFS);
            }

            if (StringUtils.isNotEmpty(hdfsUser)) {
                UserGroupInformation ugi = UserGroupInformation.createRemoteUser(hdfsUser);
                ugi.doAs((PrivilegedExceptionAction<Boolean>) () -> {
                    fs = FileSystem.get(configuration);
                    return true;
                });
            } else {
                fs = FileSystem.get(configuration);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @return DefaultFS
     */
    public String getDefaultFS() {
        String defaultFS = hdfsProperties.getDefaultFS();
        if (StringUtils.isBlank(defaultFS)) {
            defaultFS = getConfiguration().get(HDFS_DEFAULT_FS);
        }
        return defaultFS;
    }

    public String getApplicationUrl(String applicationId) throws CustomException {
        yarnEnabled = true;
        String appUrl = StringUtils.isEmpty(hdfsProperties.getYarnResourceRmIds())
                ? hdfsProperties.getYarnAppStatusAddress()
                : getAppAddress(hdfsProperties.getYarnAppStatusAddress(), hdfsProperties.getYarnResourceRmIds());
        if (StringUtils.isBlank(appUrl)) {
            throw new CustomException("yarn application url generation failed");
        }
        log.debug("yarn application url:{}, applicationId:{}", appUrl, applicationId);
        return String.format(appUrl, hdfsProperties.getHadoopResourceManagerHttpAddressPort(), applicationId);
    }

    public String getJobHistoryUrl(String applicationId) {
        String jobId = applicationId.replace("application", "job");
        return String.format(hdfsProperties.getYarnJobHistoryStatusAddress(), jobId);
    }

    /**
     * 查看hdfs文件内容
     */
    public byte[] catFile(String hdfsFilePath) throws IOException {
        if (StringUtils.isBlank(hdfsFilePath)) {
            log.error("hdfs 路径为空:{}", hdfsFilePath);
            return new byte[0];
        }
        try (FSDataInputStream fsDataInputStream = fs.open(new Path(hdfsFilePath))) {
            return IOUtils.toByteArray(fsDataInputStream);
        }
    }

    /**
     * 查看hdfs人间内容
     *
     * @param hdfsFilePath hdfs文件路径
     * @param skipLineNums 跳过的行数
     * @param limit        读取的行数
     */
    public List<String> catFile(String hdfsFilePath, int skipLineNums, int limit) throws IOException {

        if (StringUtils.isBlank(hdfsFilePath)) {
            log.error("hdfs file path:{} is blank", hdfsFilePath);
            return Collections.emptyList();
        }

        try (FSDataInputStream in = fs.open(new Path(hdfsFilePath))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Stream<String> stream = br.lines().skip(skipLineNums).limit(limit);
            return stream.collect(Collectors.toList());
        }
    }

    @Override
    public List<String> vimFile(String hdfsFilePath, int skipLineNums, int limit) throws IOException {
        return catFile(hdfsFilePath, skipLineNums, limit);
    }

    /**
     * 创建hdfs目录
     */
    @Override
    public boolean mkdir(String hdfsPath) throws IOException {
        return fs.mkdirs(new Path(addFolderSeparatorIfNotExisted(hdfsPath)));
    }

    @Override
    public void download(String srcHdfsFilePath, String dstFile, boolean overwrite) throws IOException {
        copyHdfsToLocal(srcHdfsFilePath, dstFile, false, overwrite);
    }

    /**
     * 复制hdfs文件
     *
     * @param srcPath      source hdfs path
     * @param dstPath      destination hdfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     */
    @Override
    public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        return FileUtil.copy(fs, new Path(srcPath), fs, new Path(dstPath), deleteSource, overwrite, fs.getConf());
    }

    /**
     * 复制本地文件到hdfs
     *
     * @param srcFile      local file
     * @param dstHdfsPath  destination hdfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     */
    public void copyLocalToHdfs(String srcFile, String dstHdfsPath, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcFile);
        Path dstPath = new Path(dstHdfsPath);
        fs.copyFromLocalFile(deleteSource, overwrite, srcPath, dstPath);
    }

    /**
     * 上传文件到hdfs
     *
     * @param srcFile      local file
     * @param dstPath      destination hdfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     */
    @Override
    public void upload(String srcFile, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        copyLocalToHdfs(srcFile, dstPath, deleteSource, overwrite);
    }

    /**
     * 下载hdfs文件到本地
     *
     * @param srcHdfsFilePath source hdfs file path
     * @param dstFile         destination file
     * @param deleteSource    delete source
     * @param overwrite       overwrite
     */
    public void copyHdfsToLocal(String srcHdfsFilePath, String dstFile, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcHdfsFilePath);
        File dstPath = new File(dstFile);
        if (dstPath.exists()) {
            if (dstPath.isFile()) {
                if (overwrite) {
                    Files.delete(dstPath.toPath());
                }
            } else {
                log.error("目标文件必须是文件");
            }
        }
        if (!dstPath.getParentFile().exists() && !dstPath.getParentFile().mkdirs()) {
            return;
        }
        FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
    }

    /**
     * 删除hdfs文件
     *
     * @param hdfsFilePath hdfs文件路径，可以是文件或目录
     * @param recursive    是否递归删除
     */
    @Override
    public boolean delete(String hdfsFilePath, boolean recursive) throws IOException {
        return fs.delete(new Path(hdfsFilePath), recursive);
    }

    /**
     * 批量删除文件
     *
     * @param filePath  文件目录
     * @param recursive 是否递归删除
     */

    @Override
    public boolean delete(String filePath, List<String> childrenPathArray, boolean recursive) throws IOException {
        if (filePath.endsWith("/")) {
            return fs.delete(new Path(filePath), true);
        }
        return fs.delete(new Path(filePath), recursive);
    }

    /**
     * 检查hdfs文件是否存在
     */
    @Override
    public boolean exists(String hdfsFilePath) throws IOException {
        return fs.exists(new Path(hdfsFilePath));
    }

    /**
     * 列出hdfs目录下所有文件
     */
    @Override
    public List<StorageEntity> listFilesStatus(String path, ResourceType type) throws IOException {
        List<StorageEntity> storageEntityList = new ArrayList<>();
        try {
            Path filePath = new Path(path);
            if (!fs.exists(filePath)) {
                return storageEntityList;
            }
            FileStatus[] fileStatuses = fs.listStatus(filePath);
            for (FileStatus fileStatus : fileStatuses) {
                if (fileStatus.isDirectory()) {
                    String fullName = fileStatus.getPath().toString();
                    fullName = addFolderSeparatorIfNotExisted(fullName);

                    String suffix = StringUtils.difference(path, fullName);
                    String fileName = fullName;

                    StorageEntity entity = new StorageEntity();
                    entity.setAlias(suffix);
                    entity.setFileName(fileName);
                    entity.setFullName(fullName);
                    entity.setDirectory(true);
                    entity.setType(type);
                    entity.setSize(fileStatus.getLen());
                    entity.setCreateTime(new Date(fileStatus.getModificationTime()));
                    entity.setUpdateTime(new Date(fileStatus.getModificationTime()));
                    entity.setPfullName(path);

                    storageEntityList.add(entity);
                } else {
                    String fullName = fileStatus.getPath().toString();
                    String[] aliasArr = fullName.split("/");
                    String alias = aliasArr[aliasArr.length - 1];

                    StorageEntity entity = new StorageEntity();
                    entity.setAlias(alias);
                    entity.setFileName(fullName);
                    entity.setFullName(fullName);
                    entity.setDirectory(false);
                    entity.setType(type);
                    entity.setSize(fileStatus.getLen());
                    entity.setCreateTime(new Date(fileStatus.getModificationTime()));
                    entity.setUpdateTime(new Date(fileStatus.getModificationTime()));
                    entity.setPfullName(path);
                    storageEntityList.add(entity);
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("hdfs文件路径不存在");
        } catch (IOException e) {
            throw new IOException("列出文件异常", e);
        }

        return storageEntityList;
    }

    @Override
    public StorageEntity getFileStatus(String path, ResourceType type) throws IOException {
        try {
            FileStatus fileStatus = fs.getFileStatus(new Path(path));
            String alias;
            String fileName = "";
            String fullName = fileStatus.getPath().toString();
            if (fileStatus.isDirectory()) {
                fullName = addFolderSeparatorIfNotExisted(fullName);
                alias = findDirAlias(fullName);
            } else {
                String[] aliasArr = fileStatus.getPath().toString().split("/");
                alias = aliasArr[aliasArr.length - 1];
                fileName = fileStatus.getPath().toString();
            }

            StorageEntity entity = new StorageEntity();
            entity.setAlias(alias);
            entity.setFileName(fileName);
            entity.setFullName(fullName);
            entity.setDirectory(fileStatus.isDirectory());
            entity.setType(type);
            entity.setSize(fileStatus.getLen());
            entity.setCreateTime(new Date(fileStatus.getModificationTime()));
            entity.setUpdateTime(new Date(fileStatus.getModificationTime()));
            entity.setPfullName(path);
            return entity;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("hdfs文件路径不存在");
        } catch (IOException e) {
            throw new IOException("列出文件异常", e);
        }
    }


    /**
     * 重命名hdfs文件
     */
    public boolean rename(String src, String dst) throws IOException {
        return fs.rename(new Path(src), new Path(dst));
    }

    public String getHdfsPath() {
        return hdfsProperties.getDefaultFS();
    }

    public static String getAppAddress(String appAddress, String rmHa) {
        String[] split1 = appAddress.split(DOUBLE_SLASH);
        if (split1.length != 2) {
            return null;
        }
        String start = split1[0] + DOUBLE_SLASH;
        String[] split2 = split1[1].split(COLON);

        if (split2.length != 2) {
            return null;
        }
        String end = COLON + split2[1];
        String activeRM = YarnHAAdminUtils.getActiveRMName(start, rmHa);
        if (StringUtils.isEmpty(activeRM)) {
            return null;
        }
        return start + activeRM + end;
    }

    @Override
    public void close() throws IOException {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    private static final class YarnHAAdminUtils {

        /**
         * 获取活跃的rm节点
         */
        public static String getActiveRMName(String protocol, String rmIds) {
            String[] rmIdArr = rmIds.split(COMMA);
            String yarnUrl = protocol + "%s:" + hdfsProperties.getHadoopResourceManagerHttpAddressPort() + "/ws/v1/cluster/info";
            try {
                for (String rmId : rmIdArr) {
                    String state = getRMState(String.format(yarnUrl, rmId));
                    if (HADOOP_RM_STATE_ACTIVE.equals(state)) {
                        return rmId;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return null;
        }

        /**
         * 获取rm节点状态
         */
        public static String getRMState(String url) {

            String retStr = Boolean.TRUE
                    .equals(hdfsProperties.isHadoopSecurityAuthStartupState())
                    ? KerberosHttpClient.get(url)
                    : HttpUtils.get(url);

            if (StringUtils.isEmpty(retStr)) {
                return null;
            }
            ObjectNode jsonObject = JSONUtils.parseObject(retStr);
            if (!jsonObject.has("clusterInfo")) {
                return null;
            }
            return jsonObject.get("clusterInfo").path("haState").asText();
        }
    }

    @Override
    public List<StorageEntity> listFilesStatusRecursively(String path, ResourceType type) {
        List<StorageEntity> storageEntityList = new ArrayList<>();
        LinkedList<StorageEntity> foldersToFetch = new LinkedList<>();
        do {
            String pathToExplore;
            if (foldersToFetch.isEmpty()) {
                pathToExplore = path;
            } else {
                pathToExplore = foldersToFetch.pop().getFullName();
            }
            try {
                List<StorageEntity> tempList = listFilesStatus(pathToExplore, type);
                for (StorageEntity temp : tempList) {
                    if (temp.isDirectory()) {
                        foldersToFetch.add(temp);
                    }
                }
                storageEntityList.addAll(tempList);
            } catch (IOException e) {
                log.error("Resource path: {}", pathToExplore, e);
                return storageEntityList;
            }

        } while (!foldersToFetch.isEmpty());
        return storageEntityList;
    }

    private String findDirAlias(String myStr) {
        if (!myStr.endsWith("/")) {
            return myStr;
        }
        int lastIndex = myStr.lastIndexOf("/");
        String subbedString = myStr.substring(0, lastIndex);
        int secondLastIndex = subbedString.lastIndexOf("/");
        return myStr.substring(secondLastIndex + 1, lastIndex + 1);
    }

    private String addFolderSeparatorIfNotExisted(String fullName) {
        return fullName.endsWith(FOLDER_SEPARATOR) ? fullName : fullName + FOLDER_SEPARATOR;
    }
}
