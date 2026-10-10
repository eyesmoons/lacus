package com.lacus.domain.lakeintelligence;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.lakeintelligence.entity.LakeTaskEntity;
import com.lacus.domain.lakeintelligence.dto.ClassifyResponse;
import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import com.lacus.service.lakeintelligence.ILakeTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像分类推理业务逻辑
 */
@Slf4j
@Service
public class ClassifyBusiness {

    @Autowired
    private MlServiceFeign mlServiceFeign;

    @Autowired
    private ILakeTaskService lakeTaskService;

    /**
     * 图像分类推理
     *
     * @param image  待分类图片
     * @param taskId 训练任务ID（选中某次训练产出的模型）
     * @return 分类结果
     */
    public ClassifyResponse classify(MultipartFile image, Long taskId) {
        if (taskId == null) {
            throw new CustomException("请选择模型");
        }
        LakeTaskEntity task = lakeTaskService.getById(taskId);
        if (task == null) {
            throw new CustomException("训练任务[" + taskId + "]不存在");
        }
        if (task.getModelPath() == null || task.getModelPath().isEmpty()) {
            throw new CustomException("该训练任务没有可用的模型文件");
        }
        Map<String, String> params = new HashMap<>();
        params.put("model_path", task.getModelPath());

        Map<String, Object> response;
        try {
            response = mlServiceFeign.classify(image, params);
        } catch (Exception e) {
            throw new CustomException("分类推理失败：" + e.getMessage());
        }

        if (response == null || (response.get("code") != null && Integer.valueOf(-1).equals(response.get("code")))) {
            throw new CustomException("分类推理失败：" + (response != null ? response.get("message") : "无响应"));
        }

        // 解析响应
        ClassifyResponse classifyResponse = new ClassifyResponse();
        classifyResponse.setClassName((String) response.get("class_name"));
        if (response.get("confidence") instanceof Number) {
            classifyResponse.setConfidence(((Number) response.get("confidence")).doubleValue());
        }
        if (response.get("class_id") instanceof Number) {
            classifyResponse.setClassId(((Number) response.get("class_id")).intValue());
        }
        return classifyResponse;
    }

    public List<ClassifyResponse> batchClassify(List<MultipartFile> images, Long taskId) {
        List<ClassifyResponse> results = new ArrayList<>();
        for (MultipartFile image : images) {
            results.add(classify(image, taskId));
        }
        return results;
    }

    public List<Map<String, Object>> listClasses(String modelId) {
        // 返回预定义的分类类别
        List<Map<String, Object>> classes = new ArrayList<>();
        String[][] classData = {{"0", "上衣"}, {"1", "鞋"}, {"2", "包"}, {"3", "下装"}, {"4", "手表"}};
        for (String[] c : classData) {
            classes.add(new HashMap<String, Object>() {{
                put("id", Integer.parseInt(c[0]));
                put("name", c[1]);
            }});
        }
        return classes;
    }
}
