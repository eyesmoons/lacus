package com.lacus.domain.lakeintelligence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeDatasetEntity;
import com.lacus.domain.lakeintelligence.command.CreateDatasetRequest;
import com.lacus.domain.lakeintelligence.dto.DatasetDTO;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.domain.lakeintelligence.query.DatasetPageQuery;
import com.lacus.enums.DatasetStatus;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 数据集业务逻辑
 */
@Slf4j
@Service
public class DatasetBusiness {

    @Autowired
    private ILakeDatasetService lakeDatasetService;

    @Autowired
    private MlServiceFeign mlServiceFeign;

    @Value("${storage.root:/data/lake-intelligence}")
    private String storageRoot;

    /**
     * 分页查询数据集列表
     */
    public PageDTO pageList(DatasetPageQuery query) {
        return new PageDTO(lakeDatasetService.page(query.toPage(), query.toQueryWrapper()));
    }

    /**
     * 创建数据集
     *
     * <p>支持通过 task_type 参数路由到不同的数据集校验逻辑：
     * SIMILARITY 仅需图片目录；CLASSIFICATION 需要标签信息（CSV 或子目录结构）。</p>
     */
    public DatasetDTO createDataset(CreateDatasetRequest request) {
        if (lakeDatasetService.isDatasetNameDuplicated(null, request.getDatasetName())) {
            throw new CustomException("数据集名称[" + request.getDatasetName() + "]已存在");
        }
        // 根据 task_type 路由校验逻辑
        validateDatasetByTaskType(request.getTaskType(), request.getSourceConfig());

        LakeDatasetEntity entity = new LakeDatasetEntity();
        entity.setDatasetName(request.getDatasetName());
        entity.setDescription(request.getDescription());
        entity.setStorageSource(request.getStorageSource());
        entity.setSourceConfig(request.getSourceConfig());
        entity.setLocalPath(extractLocalPath(request.getSourceConfig()));
        entity.setTaskType(request.getTaskType());
        entity.setStatus(request.getStatus() != null && !request.getStatus().isEmpty()
                ? request.getStatus()
                : DatasetStatus.PROCESSING.getCode());
        entity.setImageCount(request.getImageCount());
        entity.setCreatorId(request.getCreatorId() != null ? request.getCreatorId() : String.valueOf(com.lacus.core.security.AuthenticationUtils.getUserId()));
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setDeleted(0);
        lakeDatasetService.save(entity);
        return toDTO(entity);
    }

    /**
     * 从数据源配置 JSON 中解析本地路径（仅 LOCAL 来源会带 localPath）
     */
    private String extractLocalPath(String sourceConfig) {
        if (sourceConfig == null || sourceConfig.isEmpty()) {
            return null;
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<?, ?> config = mapper.readValue(sourceConfig, Map.class);
            Object localPath = config.get("localPath");
            return localPath != null ? localPath.toString() : null;
        } catch (Exception e) {
            log.warn("解析数据源配置中的 localPath 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据任务类型校验数据集是否满足要求
     *
     * @param taskType     任务类型
     * @param sourceConfig 数据源配置 JSON
     */
    private void validateDatasetByTaskType(String taskType, String sourceConfig) {
        if (taskType == null || taskType.isEmpty()) {
            return;
        }
        if ("CLASSIFICATION".equalsIgnoreCase(taskType)) {
            validateClassificationDataset(sourceConfig);
        }
        // SIMILARITY 暂无特殊校验，预留扩展点
    }

    /**
     * 校验分类数据集是否包含标签信息
     *
     * <p>要求 source_config 中提供 labels_file（CSV 标签文件）或 class_dirs（按类别分组的子目录）。</p>
     */
    private void validateClassificationDataset(String sourceConfig) {
        if (sourceConfig == null || sourceConfig.isEmpty()) {
            throw new CustomException("分类数据集需要提供数据源配置（含标签信息）");
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<?, ?> config = mapper.readValue(sourceConfig, Map.class);
            boolean hasLabels = config.containsKey("labels_file") && config.get("labels_file") != null
                    && !config.get("labels_file").toString().isEmpty();
            boolean hasClassDirs = config.containsKey("class_dirs") && config.get("class_dirs") != null;
            if (!hasLabels && !hasClassDirs) {
                throw new CustomException("分类数据集需要提供 labels_file（标签CSV）或 class_dirs（按类别子目录）");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("解析数据源配置失败：" + e.getMessage());
        }
    }

    /**
     * 上传数据集文件（zip 格式），解压并统计图片数量
     */
    public Map<String, Object> uploadDatasetFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".zip")) {
            throw new CustomException("只支持 .zip 格式文件");
        }

        try {
            // 创建存储根目录
            java.nio.file.Path rootPath = java.nio.file.Paths.get(storageRoot);
            if (!java.nio.file.Files.exists(rootPath)) {
                java.nio.file.Files.createDirectories(rootPath);
            }

            // 创建 datasets 目录
            java.nio.file.Path datasetsPath = rootPath.resolve("datasets");
            if (!java.nio.file.Files.exists(datasetsPath)) {
                java.nio.file.Files.createDirectories(datasetsPath);
            }

            // 生成唯一目录名
            String timestamp = String.valueOf(System.currentTimeMillis());
            java.nio.file.Path datasetDir = datasetsPath.resolve(timestamp);
            java.nio.file.Files.createDirectories(datasetDir);

            // 保存 zip 文件
            java.nio.file.Path zipPath = datasetDir.resolve(originalFilename);
            file.transferTo(zipPath.toFile());

            // 解压 zip 文件
            unzipFile(zipPath.toString(), datasetDir.toString());

            // 统计图片数量
            int imageCount = countImageFiles(datasetDir.toFile());

            Map<String, Object> result = new HashMap<>();
            result.put("localPath", datasetDir.toString());
            result.put("imageCount", imageCount);
            result.put("fileName", originalFilename);

            return result;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("文件上传失败：" + e.getMessage(), e);
        }
    }

    private void unzipFile(String zipPath, String destDir) throws Exception {
        File zipFile = new File(zipPath);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(destDir, entry.getName());
                // 防止路径穿越攻击
                if (!entryFile.getCanonicalPath().startsWith(new File(destDir).getCanonicalPath())) {
                    throw new CustomException("zip 文件包含非法路径");
                }
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    entryFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
        // 解压完成后删除 zip 文件
        zipFile.delete();
    }

    /**
     * 数据源探测
     */
    public Map<String, Object> probeSource(String uri) {
        Map<String, Object> request = new HashMap<>();
        request.put("uri", uri);
        try {
            Map<String, Object> response = mlServiceFeign.probeSource(request);
            if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
                throw new CustomException("数据源探测失败：" + (response != null ? response.get("message") : "无响应"));
            }
            return response;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("数据源探测异常：" + e.getMessage());
        }
    }

    /**
     * 删除数据集
     */
    public void deleteDataset(Long datasetId) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        lakeDatasetService.removeById(datasetId);
        log.info("数据集[{}]已删除", datasetId);
    }

    /**
     * 获取数据集详情
     */
    public DatasetDTO detail(Long datasetId) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        return toDTO(entity);
    }

    private DatasetDTO toDTO(LakeDatasetEntity entity) {
        DatasetDTO dto = new DatasetDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public Map<String, Object> getClassStats(Long datasetId) {
        // 返回模拟的类别分布统计
        Map<String, Object> stats = new HashMap<>();
        stats.put("datasetId", datasetId);
        stats.put("totalSamples", 100);
        List<Map<String, Object>> distribution = new ArrayList<>();
        distribution.add(new HashMap<String, Object>() {{
            put("classId", 0);
            put("className", "上衣");
            put("count", 25);
        }});
        distribution.add(new HashMap<String, Object>() {{
            put("classId", 1);
            put("className", "鞋");
            put("count", 20);
        }});
        distribution.add(new HashMap<String, Object>() {{
            put("classId", 2);
            put("className", "包");
            put("count", 15);
        }});
        distribution.add(new HashMap<String, Object>() {{
            put("classId", 3);
            put("className", "下装");
            put("count", 22);
        }});
        distribution.add(new HashMap<String, Object>() {{
            put("classId", 4);
            put("className", "手表");
            put("count", 18);
        }});
        stats.put("classDistribution", distribution);
        return stats;
    }

    /**
     * 解析数据集：扫描本地目录中的图片文件，统计数量，更新状态为 READY
     */
    @Transactional
    public DatasetDTO parseDataset(Long datasetId) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (entity == null) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }

        String localPath = entity.getLocalPath();
        if (localPath == null || localPath.isEmpty()) {
            // 如果没有本地路径，尝试从 source_config 构建
            String sourceConfig = entity.getSourceConfig();
            if (sourceConfig != null && !sourceConfig.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<?, ?> config = mapper.readValue(sourceConfig, Map.class);
                    localPath = (String) config.get("localPath");
                } catch (Exception e) {
                    throw new CustomException("解析本地路径失败：" + e.getMessage());
                }
            }
        }

        if (localPath == null || localPath.isEmpty()) {
            throw new CustomException("数据集本地路径不存在，无法解析");
        }

        // 扫描图片文件
        File dir = new File(localPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new CustomException("数据集目录不存在：" + localPath);
        }

        int imageCount = countImageFiles(dir);

        // 更新数据集
        entity.setImageCount(imageCount);
        entity.setStatus(DatasetStatus.READY.getCode());
        entity.setUpdateTime(new Date());
        lakeDatasetService.updateById(entity);

        log.info("数据集[{}]解析完成，图片数量：{}", datasetId, imageCount);
        return toDTO(entity);
    }

    /**
     * 解析数据集内的图片文件（相对路径），含路径穿越防护
     */
    public File resolveImage(Long datasetId, String relativePath) {
        LakeDatasetEntity entity = lakeDatasetService.getById(datasetId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new CustomException("数据集[" + datasetId + "]不存在");
        }
        String localPath = entity.getLocalPath();
        if (localPath == null || localPath.isEmpty()) {
            throw new CustomException("数据集[" + datasetId + "]本地路径不存在");
        }
        File root = new File(localPath);
        File file = new File(root, relativePath);
        try {
            String rootCanonical = root.getCanonicalPath();
            String fileCanonical = file.getCanonicalPath();
            if (!fileCanonical.startsWith(rootCanonical + File.separator)) {
                throw new CustomException("图片路径非法");
            }
        } catch (java.io.IOException e) {
            throw new CustomException("图片路径校验失败：" + e.getMessage());
        }
        if (!file.isFile()) {
            throw new CustomException("图片不存在：" + relativePath);
        }
        return file;
    }

    private int countImageFiles(File dir) {
        int count = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") ||
                    name.endsWith(".bmp") || name.endsWith(".gif") || name.endsWith(".webp")) {
                    count++;
                    // 限制最大统计数量，避免大目录耗时过长
                    if (count >= 50000) break;
                }
            } else if (file.isDirectory()) {
                count += countImageFiles(file);
                if (count >= 50000) break;
            }
        }
        return count;
    }
}
