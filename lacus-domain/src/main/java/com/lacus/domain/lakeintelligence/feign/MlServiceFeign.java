package com.lacus.domain.lakeintelligence.feign;

import com.lacus.domain.lakeintelligence.feign.conf.MlServiceFeignConfiguration;
import com.lacus.domain.lakeintelligence.feign.fallback.MlServiceFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Python ML 服务 Feign 客户端
 *
 * <p>调用 ml_service 提供的训练、向量构建、相似检索等接口。</p>
 */
@FeignClient(name = "mlServiceFeignClient",
        url = "${ml.service.url}",
        contextId = "mlServiceFeignClient",
        fallbackFactory = MlServiceFeignFallbackFactory.class,
        configuration = MlServiceFeignConfiguration.class)
public interface MlServiceFeign {

    /**
     * 启动训练任务
     */
    @PostMapping("/api/train")
    Map<String, Object> startTrain(@RequestBody Map<String, Object> request);

    /**
     * 查询训练进度
     */
    @GetMapping("/api/train/{task_id}")
    Map<String, Object> getTrainProgress(@PathVariable("task_id") String taskId);

    /**
     * 取消训练任务
     */
    @PostMapping("/api/train/{task_id}/cancel")
    Map<String, Object> cancelTrain(@PathVariable("task_id") String taskId);

    /**
     * 构建向量库
     */
    @PostMapping("/api/vectors/build")
    Map<String, Object> buildVectors(@RequestBody Map<String, Object> request);

    /**
     * 查询向量构建进度
     */
    @GetMapping("/api/vectors/build/{task_id}")
    Map<String, Object> getBuildProgress(@PathVariable("task_id") String taskId);

    /**
     * 相似检索（图片上传）
     */
    @PostMapping(value = "/api/search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> search(@RequestPart(value = "image", required = false) MultipartFile image, @RequestParam Map<String, String> params);

    /**
     * 数据源探测
     */
    @PostMapping("/api/dataset/probe-source")
    Map<String, Object> probeSource(@RequestBody Map<String, Object> request);

    /**
     * 图像分类推理（图片上传）
     */
    @PostMapping(value = "/api/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> classify(@RequestPart(value = "image", required = false) MultipartFile image, @RequestParam Map<String, String> params);
}
