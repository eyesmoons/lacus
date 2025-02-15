package com.lacus.domain.system.resources;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.io.Files;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.system.entity.StorageEntity;
import com.lacus.dao.system.entity.SysResourcesEntity;
import com.lacus.domain.system.resources.command.ResourceAddCommand;
import com.lacus.domain.system.resources.model.ResourceModel;
import com.lacus.domain.system.resources.model.ResourceModelFactory;
import com.lacus.domain.system.resources.query.ResourceQuery;
import com.lacus.enums.ResourceType;
import com.lacus.enums.Status;
import com.lacus.service.system.IStorageOperate;
import com.lacus.service.system.ISysResourcesService;
import com.lacus.utils.RegexUtils;
import com.lacus.utils.file.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lacus.common.constant.Constants.MAX_FILE_SIZE;
import static com.lacus.common.constant.Constants.RESOURCE_FULL_NAME_MAX_LENGTH;

/**
 * @author shengyu
 * @date 2024/5/8 20:40
 */
@Slf4j
@Service
public class ResourceBusiness {

    @Autowired
    private IStorageOperate storageOperate;

    @Autowired
    private ISysResourcesService sysResourcesService;

    public void createDirectory(Long pid, String name, ResourceType type, String remark) {
        if (FileUtil.directoryTraversal(name)) {
            log.warn("文件夹名称不合法:{}.", RegexUtils.escapeNRT(name));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        SysResourcesEntity pResource = sysResourcesService.getById(pid);
        String fullName;
        if (ObjectUtils.isEmpty(pResource)) {
            fullName = "/" + name + "/";
        } else {
            fullName = pResource.getFilePath() + File.separator + name + "/";
        }

        try {
            if (checkResourceExists(fullName)) {
                log.error("文件夹已经存在: {}", fullName);
                throw new CustomException(Status.RESOURCE_EXIST.getMsg(), Status.RESOURCE_EXIST.getCode());
            }
        } catch (Exception e) {
            log.warn("文件夹已经存在: {}.", fullName, e);
            throw new CustomException("文件夹已经存在");
        }
        createDirectory(fullName, type);
        addResource(pid, name, fullName, remark, 1);
    }

    public void uploadResource(Long pid, String aliaName, String remark, ResourceType type, MultipartFile file) {
        verifyFile(pid, aliaName, type, file);

        SysResourcesEntity pResource = sysResourcesService.getById(pid);
        String currDirNFileName = pResource.getFilePath() + File.separator + aliaName;
        try {
            if (checkResourceExists(currDirNFileName)) {
                log.error("文件已经存在，不能重复上传: {}", RegexUtils.escapeNRT(aliaName));
                throw new CustomException(Status.RESOURCE_EXIST.getMsg(), Status.RESOURCE_EXIST.getCode());
            }
        } catch (Exception e) {
            throw new CustomException("文件已经存在");
        }

        if (currDirNFileName.length() > RESOURCE_FULL_NAME_MAX_LENGTH) {
            log.error(
                    "Resource file's name is longer than max full name length, fullName:{}, " + "fullNameSize:{}, maxFullNameSize:{}",
                    RegexUtils.escapeNRT(aliaName), currDirNFileName.length(), RESOURCE_FULL_NAME_MAX_LENGTH);
            throw new CustomException(Status.RESOURCE_FULL_NAME_TOO_LONG_ERROR.getMsg(), Status.RESOURCE_FULL_NAME_TOO_LONG_ERROR.getCode());
        }

        if (!upload(currDirNFileName, file, type)) {
            log.error("文件上传失败: {}", RegexUtils.escapeNRT(file.getOriginalFilename()));
            throw new CustomException(String.format("文件上传失败: %s", file.getOriginalFilename()));
        }
        addResource(pid, aliaName, currDirNFileName, remark, 0);
    }

    public List<SysResourcesEntity> queryResourceDirectoryList(ResourceType type) {
        return sysResourcesService.listDirectory(type);
    }

    public List<SysResourcesEntity> queryResourceList(ResourceType type, Long pid, String fileName, Integer isDirectory) {
        return sysResourcesService.listResource(type, pid, fileName, isDirectory);
    }

    public PageDTO queryResourceListPaging(ResourceQuery query) {
        Page<SysResourcesEntity> page = sysResourcesService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public List<SysResourcesEntity> queryAllResources(Long pid) {
        List<SysResourcesEntity> allResources = new ArrayList<>();
        findAllResourcesRecursively(pid, allResources);
        return allResources;
    }

    public PageDTO queryAllResourcesPaging(ResourceQuery query) {
        Page<SysResourcesEntity> page = sysResourcesService.page(query.toPage(), query.toQueryWrapper());
        // 批量赋值pFilePath属性
        for (SysResourcesEntity file : page.getRecords()) {
            SysResourcesEntity pResource = sysResourcesService.getById(file.getPid());
            if (ObjectUtils.isNotEmpty(pResource)) {
                file.setPFilePath(pResource.getFilePath());
            }
        }
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    private void findAllResourcesRecursively(Long pid, List<SysResourcesEntity> allResources) {
        List<SysResourcesEntity> resources = sysResourcesService.listResource(ResourceType.FILE, pid, null, null);
        for (SysResourcesEntity resource : resources) {
            if (resource.getIsDirectory() == 1) {
                findAllResourcesRecursively(resource.getId(), allResources);
            } else if (resource.getIsDirectory() == 0) {
                SysResourcesEntity pResource = sysResourcesService.getById(resource.getPid());
                if (ObjectUtils.isNotEmpty(pResource)) {
                    resource.setPFilePath(pResource.getFilePath());
                }
                allResources.add(resource);
            }
        }
    }

    public void deleteResource(long id) throws IOException {
        StorageEntity resource;
        SysResourcesEntity byId = sysResourcesService.getById(id);
        try {
            if (ObjectUtils.isEmpty(byId)) {
                throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
            }
            resource = storageOperate.getFileStatus(byId.getFilePath(), null);
        } catch (Exception e) {
            log.error("{}, id: {}", e.getMessage(), id, e);
            throw new CustomException(String.format(e.getMessage() + "id: %s", id));
        }

        if (resource == null) {
            log.error("文件不存在，id：{}", id);
            throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
        }
        List<String> allChildren = storageOperate.listFilesStatusRecursively(byId.getFilePath(), resource.getType()).stream().map(StorageEntity::getFullName).collect(Collectors.toList());
        storageOperate.delete(byId.getFilePath(), allChildren, true);
        sysResourcesService.removeById(id);
    }

    public Resource downloadResource(long id) {
        SysResourcesEntity byId = sysResourcesService.getById(id);
        if (ObjectUtils.isEmpty(byId)) {
            throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
        }

        String filePath = byId.getFilePath();
        if (filePath.endsWith("/")) {
            log.error("文件夹不支持下载: {}", filePath);
            throw new CustomException("文件夹不支持下载");
        }

        String[] aliasArr = filePath.split("/");
        String alias = aliasArr[aliasArr.length - 1];
        String localFileName = FileUtil.getDownloadFilename(alias);
        log.info("Resource path is {}, download local filename is {}", alias, localFileName);

        try {
            storageOperate.download(filePath, localFileName, true);
            return FileUtil.file2Resource(localFileName);
        } catch (IOException e) {
            log.error("Download resource error, the path is {}, and local filename is {}, the error message is {}",
                    filePath, localFileName, e.getMessage());
            throw new CustomException("Download the resource file failed ,it may be related to your storage");
        }
    }

    private static void addResource(Long pid, String aliasName, String fullName, String remark, Integer isDirectory) {
        String fileName = new File(fullName).getName();
        ResourceAddCommand command = new ResourceAddCommand(pid, 0, aliasName, fileName, fullName, isDirectory, remark);
        ResourceModel model = ResourceModelFactory.loadFromAddCommand(command, new ResourceModel());
        model.insert();
    }

    private boolean checkResourceExists(String fullName) {
        boolean existResource = false;
        try {
            existResource = storageOperate.exists(fullName);
        } catch (IOException e) {
            log.error("error occurred when checking resource: {}", fullName, e);
        }
        return Boolean.TRUE.equals(existResource);
    }

    private void createDirectory(String fullName, ResourceType type) {
        try {
            if (!storageOperate.mkdir(fullName)) {
                throw new CustomException(String.format("Create resource directory: %s failed.", fullName));
            }
        } catch (Exception e) {
            throw new CustomException(String.format("create resource directory: %s failed.", fullName));
        }
    }

    private void verifyFile(Long pid, String name, ResourceType type, MultipartFile file) {
        if (pid == 0) {
            throw new CustomException("根目录不支持上传文件，请选择其他目录！");
        }
        if (FileUtil.directoryTraversal(name)) {
            log.warn("文件别名验证失败:{}.", RegexUtils.escapeNRT(name));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        if (file != null && FileUtil.directoryTraversal(Objects.requireNonNull(file.getOriginalFilename()))) {
            log.warn("文件原始名称验证失败:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        if (file != null) {
            if (file.isEmpty()) {
                log.warn("文件为空:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
                throw new CustomException(Status.RESOURCE_FILE_IS_EMPTY.getMsg(), Status.RESOURCE_FILE_IS_EMPTY.getCode());
            }

            String fileSuffix = Files.getFileExtension(file.getOriginalFilename());
            String nameSuffix = Files.getFileExtension(name);

            if (!fileSuffix.equalsIgnoreCase(nameSuffix)) {
                log.warn("重命名和文件后缀必须和原始文件后缀保持一致:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
                throw new CustomException(Status.RESOURCE_SUFFIX_FORBID_CHANGE.getMsg(), Status.RESOURCE_SUFFIX_FORBID_CHANGE.getCode());
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("文件大小超过最大限制:{}, fileSize:{}, maxFileSize:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()), file.getSize(), MAX_FILE_SIZE);
                throw new CustomException(Status.RESOURCE_SIZE_EXCEED_LIMIT.getMsg(), Status.RESOURCE_SIZE_EXCEED_LIMIT.getCode());
            }
        }
    }

    private boolean upload(String fullName, MultipartFile file, ResourceType type) {
        String fileSuffix = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String nameSuffix = Files.getFileExtension(fullName);

        if (!fileSuffix.equalsIgnoreCase(nameSuffix)) {
            return false;
        }
        String localFilename = FileUtil.getUploadFilename(UUID.randomUUID().toString());

        try {
            FileUtil.copyInputStreamToFile(file, localFilename);
            storageOperate.upload(localFilename, fullName, true, true);
        } catch (Exception e) {
            FileUtil.deleteFile(localFilename);
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public String readResource(Long id) {
        SysResourcesEntity resource = sysResourcesService.getById(id);
        if (resource == null) {
            throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg(), Status.RESOURCE_NOT_EXIST.getCode());
        }
        String fullName = storageOperate.getHdfsPath() + resource.getFilePath();
        String nameSuffix = Files.getFileExtension(fullName);
        String resourceViewSuffixes = FileUtil.getResourceViewSuffixes();
        if (StringUtils.isNotEmpty(resourceViewSuffixes)) {
            List<String> strList = Arrays.asList(resourceViewSuffixes.split(","));
            if (!strList.contains(nameSuffix)) {
                throw new CustomException("[" + fullName + "]不支持的文件后缀：" + nameSuffix);
            }
        }
        List<String> content;
        try {
            if (storageOperate.exists(fullName)) {
                content = storageOperate.vimFile(fullName, 0, 500000);
            } else {
                log.error("文件不存在: {}", fullName);
                return null;
            }
        } catch (Exception e) {
            throw new CustomException("文件读取失败: " + fullName);
        }
        return String.join("\n", content);
    }

    public void syncResources() {
        // 获取HDFS中的所有资源
        List<StorageEntity> hdfsResourceList = storageOperate.listFilesStatusRecursively(storageOperate.getHdfsPath() + "/", null);
        // 获取数据库中的所有资源
        List<SysResourcesEntity> dbResources = sysResourcesService.listAllResources();
        List<String> hdfsResources = hdfsResourceList.stream()
                .map(entity -> entity.getFullName().replaceFirst("^hdfs://[^/]+", "")) // 去掉HDFS前缀
                .collect(Collectors.toList());

        // 获取数据库中最大的id
        Long maxId = dbResources.stream().mapToLong(SysResourcesEntity::getId).max().orElse(0L);
        Long currentId = maxId + 1;

        // 创建路径到id的映射
        Map<String, Long> pathToIdMap = dbResources.stream()
                .collect(Collectors.toMap(SysResourcesEntity::getFilePath, SysResourcesEntity::getId));

        // 将HDFS中存在但数据库中不存在的资源添加到数据库
        for (StorageEntity hdfsEntity : hdfsResourceList) {
            String hdfsResource = hdfsEntity.getFullName().replaceFirst("^hdfs://[^/]+", "");
            boolean existsInDb = dbResources.stream().anyMatch(dbResource -> dbResource.getFilePath().equals(hdfsResource));
            if (!existsInDb) {
                log.info("同步HDFS资源到数据库: {}", hdfsResource);
                // 获取父资源的pid
                final String pPath = hdfsEntity.getPfullName();
                Long pid = null;
                if (pPath != null) {
                    String parentPath = pPath.replaceFirst("^hdfs://[^/]+", "");
                    if ("/".equals(parentPath)) {
                        pid = 0L; // 根目录
                    } else {
                        pid = pathToIdMap.get(parentPath);
                    }
                }
                // 为当前资源生成id
                pathToIdMap.put(hdfsResource, currentId);
                // 添加到数据库的逻辑
                addResource(pid, new File(hdfsResource).getName(), hdfsResource, null, hdfsEntity.isDirectory() ? 1 : 0);
                currentId++;
            }
        }

        // 将数据库中存在但HDFS中不存在的资源从数据库中删除
        for (SysResourcesEntity dbResource : dbResources) {
            boolean existsInHdfs = hdfsResources.contains(dbResource.getFilePath());
            if (!existsInHdfs) {
                log.info("从数据库中删除不存在于HDFS的资源: {}", dbResource.getFilePath());
                sysResourcesService.removeById(dbResource.getId());
            }
        }
    }
}
