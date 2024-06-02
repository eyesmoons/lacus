package com.lacus.domain.system.resources;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.io.Files;
import com.lacus.common.constant.Constants;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.system.entity.StorageEntity;
import com.lacus.dao.system.entity.SysEnvEntity;
import com.lacus.dao.system.entity.SysResourcesEntity;
import com.lacus.domain.system.env.dto.EnvDTO;
import com.lacus.domain.system.resources.command.ResourceAddCommand;
import com.lacus.domain.system.resources.dto.ResourceComponent;
import com.lacus.domain.system.resources.dto.visitor.ResourceTreeVisitor;
import com.lacus.domain.system.resources.dto.visitor.Visitor;
import com.lacus.domain.system.resources.model.ResourceModel;
import com.lacus.domain.system.resources.model.ResourceModelFactory;
import com.lacus.domain.system.resources.query.ResourceQuery;
import com.lacus.enums.ResUploadType;
import com.lacus.enums.ResourceType;
import com.lacus.enums.Status;
import com.lacus.service.system.IStorageOperate;
import com.lacus.service.system.ISysResourcesService;
import com.lacus.utils.PropertyUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lacus.common.constant.Constants.*;

/**
 * @author shengyu
 * @date 2024/5/8 20:40
 */
@Slf4j
@Service
public class ResourceService {

    @Autowired
    private IStorageOperate storageOperate;

    @Autowired
    private ISysResourcesService sysResourcesService;

    public void createDirectory(String aliaName, ResourceType type, String currentDir) {
        if (FileUtil.directoryTraversal(aliaName)) {
            log.warn("Parameter name is invalid, name:{}.", RegexUtils.escapeNRT(aliaName));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        String userResRootPath = ResourceType.UDF.equals(type) ? storageOperate.getUdfDir() : storageOperate.getResDir();
        String fullName = !currentDir.contains(userResRootPath) ? userResRootPath + aliaName : currentDir + aliaName;

        try {
            if (checkResourceExists(fullName)) {
                log.error("resource directory {} exists, can't create again", fullName);
                throw new CustomException(Status.RESOURCE_EXIST.getMsg(), Status.RESOURCE_EXIST.getCode());
            }
        } catch (Exception e) {
            log.warn("Resource exists, can't create again, fullName:{}.", fullName, e);
            throw new CustomException("resource already exists, can't recreate");
        }

        // create directory in hdfs
        createDirectory(fullName, type);
        // save to db
        addResource(0L, aliaName, fullName, 1);
    }

    public void uploadResource(Long pid, String aliaName, ResourceType type, MultipartFile file, String currentDir) {
        verifyFile(aliaName, type, file);

        // check resource name exists
        String userResRootPath = ResourceType.UDF.equals(type) ? storageOperate.getUdfDir() : storageOperate.getResDir();
        String currDirNFileName = !currentDir.contains(userResRootPath) ? userResRootPath + aliaName : currentDir + aliaName;

        try {
            if (checkResourceExists(currDirNFileName)) {
                log.error("resource {} has exist, can't recreate", RegexUtils.escapeNRT(aliaName));
                throw new CustomException(Status.RESOURCE_EXIST.getMsg(), Status.RESOURCE_EXIST.getCode());
            }
        } catch (Exception e) {
            throw new CustomException("resource already exists, can't recreate");
        }

        if (currDirNFileName.length() > RESOURCE_FULL_NAME_MAX_LENGTH) {
            log.error(
                    "Resource file's name is longer than max full name length, fullName:{}, " + "fullNameSize:{}, maxFullNameSize:{}",
                    RegexUtils.escapeNRT(aliaName), currDirNFileName.length(), RESOURCE_FULL_NAME_MAX_LENGTH);
            throw new CustomException(Status.RESOURCE_FULL_NAME_TOO_LONG_ERROR.getMsg(), Status.RESOURCE_FULL_NAME_TOO_LONG_ERROR.getCode());
        }

        // fail upload
        if (!upload(currDirNFileName, file, type)) {
            log.error("upload resource: {} file: {} failed.", RegexUtils.escapeNRT(aliaName), RegexUtils.escapeNRT(file.getOriginalFilename()));
            throw new CustomException(String.format("upload resource: %s file: %s failed.", aliaName, file.getOriginalFilename()));
        }

        // save to db
        addResource(pid, aliaName, currDirNFileName, 1);
    }

    public List<SysResourcesEntity> queryResourceDirectoryList(ResourceType type) {
        return sysResourcesService.listDirectory(type);
    }

    public List<SysResourcesEntity> queryResourceList(ResourceType type, Long pid, String fileName) {
        return sysResourcesService.listResource(type, pid, fileName);
    }

    public List<ResourceComponent> queryResourceListBak(Boolean isDirectory, ResourceType type, String fullName) {
        String defaultPath;
        List<StorageEntity> resourcesList;

        if (StringUtils.isBlank(fullName)) {
            defaultPath = storageOperate.getResDir();
            if (type.equals(ResourceType.UDF)) {
                defaultPath = storageOperate.getUdfDir();
            }
            resourcesList = storageOperate.listFilesStatusRecursively(defaultPath, defaultPath, type);
        } else {
            defaultPath = storageOperate.getResDir();
            if (type.equals(ResourceType.UDF)) {
                defaultPath = storageOperate.getUdfDir();
            }
            resourcesList = storageOperate.listFilesStatusRecursively(fullName, defaultPath, type);
        }
        Visitor resourceTreeVisitor = new ResourceTreeVisitor(resourcesList);
        return resourceTreeVisitor.visit(defaultPath).getChildren();
    }

    public PageDTO queryResourceListPaging(ResourceQuery query) {
        Page<SysEnvEntity> page = sysResourcesService.page(query.toPage(), query.toQueryWrapper());
        List<EnvDTO> records = page.getRecords().stream().map(EnvDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public void deleteResource(long id) throws IOException {
        String defaultPath = storageOperate.getResDir();
        StorageEntity resource;
        SysResourcesEntity byId = sysResourcesService.getById(id);
        try {
            if (ObjectUtils.isEmpty(byId)) {
                throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
            }
            resource = storageOperate.getFileStatus(byId.getFilePath(), defaultPath, null);
        } catch (Exception e) {
            log.error("{}, Resource id: {}", e.getMessage(), id, e);
            throw new CustomException(String.format(e.getMessage() + " resource id: %s", id));
        }

        if (resource == null) {
            log.error("Resource does not exist, resource id：{}", id);
            throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
        }
        List<String> allChildren = storageOperate.listFilesStatusRecursively(byId.getFilePath(), defaultPath, resource.getType()).stream().map(StorageEntity::getFullName).collect(Collectors.toList());
        // if resource type is UDF,need check whether it is bound by UDF function
        // TODO
        // delete file on hdfs
        storageOperate.delete(byId.getFilePath(), allChildren, true);

        // delete from db
        sysResourcesService.removeById(id);
    }

    public Resource downloadResource(long id) {
        SysResourcesEntity byId = sysResourcesService.getById(id);
        if (ObjectUtils.isEmpty(byId)) {
            throw new CustomException(Status.RESOURCE_NOT_EXIST.getMsg());
        }

        String filePath = byId.getFilePath();
        if (filePath.endsWith("/")) {
            log.error("resource {} is directory，can't download", filePath);
            throw new CustomException("can't download directory");
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

    private static void addResource(Long pid, String aliaName, String fullName, Integer isDirectory) {
        String fileName = new File(fullName).getName();
        ResourceAddCommand command = new ResourceAddCommand(pid, 0, aliaName, fileName, fullName, isDirectory);
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
        String resourceRootPath = storageOperate.getDir(type);
        try {
            if (!storageOperate.exists(resourceRootPath)) {
                storageOperate.createTenantDirIfNotExists();
            }

            if (!storageOperate.mkdir(fullName)) {
                throw new CustomException(String.format("Create resource directory: %s failed.", fullName));
            }
        } catch (Exception e) {
            throw new CustomException(String.format("create resource directory: %s failed.", fullName));
        }
    }

    private void verifyFile(String name, ResourceType type, MultipartFile file) {
        if (FileUtil.directoryTraversal(name)) {
            log.warn("Parameter file alias name verify failed, fileAliasName:{}.", RegexUtils.escapeNRT(name));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        if (file != null && FileUtil.directoryTraversal(Objects.requireNonNull(file.getOriginalFilename()))) {
            log.warn("File original name verify failed, fileOriginalName:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
            throw new CustomException(Status.VERIFY_PARAMETER_NAME_FAILED.getMsg(), Status.VERIFY_PARAMETER_NAME_FAILED.getCode());
        }

        if (file != null) {
            // file is empty
            if (file.isEmpty()) {
                log.warn("Parameter file is empty, fileOriginalName:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
                throw new CustomException(Status.RESOURCE_FILE_IS_EMPTY.getMsg(), Status.RESOURCE_FILE_IS_EMPTY.getCode());
            }

            // file suffix
            String fileSuffix = Files.getFileExtension(file.getOriginalFilename());
            String nameSuffix = Files.getFileExtension(name);

            // determine file suffix
            if (!fileSuffix.equalsIgnoreCase(nameSuffix)) {
                // rename file suffix and original suffix must be consistent
                log.warn("Rename file suffix and original suffix must be consistent, fileOriginalName:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()));
                throw new CustomException(Status.RESOURCE_SUFFIX_FORBID_CHANGE.getMsg(), Status.RESOURCE_SUFFIX_FORBID_CHANGE.getCode());
            }

            // If resource type is UDF, only jar packages are allowed to be uploaded, and the suffix must be .jar
            if (UDF.equals(type.name()) && !JAR.equalsIgnoreCase(fileSuffix)) {
                log.warn(Status.UDF_RESOURCE_SUFFIX_NOT_JAR.getMsg());
                throw new CustomException(Status.UDF_RESOURCE_SUFFIX_NOT_JAR.getMsg(), Status.UDF_RESOURCE_SUFFIX_NOT_JAR.getCode());
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("Resource file size is larger than max file size, fileOriginalName:{}, fileSize:{}, maxFileSize:{}.", RegexUtils.escapeNRT(file.getOriginalFilename()), file.getSize(), MAX_FILE_SIZE);
                throw new CustomException(Status.RESOURCE_SIZE_EXCEED_LIMIT.getMsg(), Status.RESOURCE_SIZE_EXCEED_LIMIT.getCode());
            }
        }
    }

    private boolean upload(String fullName, MultipartFile file, ResourceType type) {
        // save to local
        String fileSuffix = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String nameSuffix = Files.getFileExtension(fullName);

        // determine file suffix
        if (!fileSuffix.equalsIgnoreCase(nameSuffix)) {
            return false;
        }
        // random file name
        String localFilename = FileUtil.getUploadFilename(UUID.randomUUID().toString());

        // save file to hdfs, and delete original file
        String resourcePath = storageOperate.getDir(type);
        try {
            // if tenant dir not exists
            if (!storageOperate.exists(resourcePath)) {
                storageOperate.createTenantDirIfNotExists();
            }
            FileUtil.copyInputStreamToFile(file, localFilename);
            storageOperate.upload(localFilename, fullName, true, true);
        } catch (Exception e) {
            FileUtil.deleteFile(localFilename);
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private List<StorageEntity> queryStorageEntityList(String fullName, ResourceType type, boolean recursive) {
        List<StorageEntity> resourcesList;
        String resourceStorageType = PropertyUtils.getString(Constants.RESOURCE_STORAGE_TYPE, ResUploadType.LOCAL.name());
        String defaultPath = storageOperate.getResDir();
        if (type.equals(ResourceType.UDF)) {
            defaultPath = storageOperate.getUdfDir();
        }

        try {
            if (StringUtils.isBlank(fullName)) {
                fullName = defaultPath;
            }
            resourcesList = recursive ? storageOperate.listFilesStatusRecursively(fullName, defaultPath, type) : storageOperate.listFilesStatus(fullName, defaultPath, type);
        } catch (Exception e) {
            log.error("{} Resource path: {}", e.getMessage(), fullName, e);
            throw new CustomException(String.format(e.getMessage() + " make sure resource path: %s exists in %s", defaultPath, resourceStorageType));
        }
        return resourcesList;
    }

}