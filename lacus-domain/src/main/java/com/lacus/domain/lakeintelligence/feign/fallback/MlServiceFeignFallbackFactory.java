package com.lacus.domain.lakeintelligence.feign.fallback;

import com.lacus.domain.lakeintelligence.feign.MlServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * ML 服务 Feign 降级工厂
 *
 * <p>当 ML 服务不可用时返回友好错误信息，避免上游调用方直接抛出异常。</p>
 */
@Component
@Slf4j
public class MlServiceFeignFallbackFactory implements FallbackFactory<MlServiceFeign> {

    private static final int FALLBACK_CODE = -1;

    @Override
    public MlServiceFeign create(Throwable cause) {
        log.error("调用 ML 服务失败，原因：[{}]，触发降级！", cause.getMessage());

        return new MlServiceFeign() {

            private Map<String, Object> fail(String operation) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", FALLBACK_CODE);
                result.put("status", "error");
                result.put("message", "ML 服务暂不可用[" + operation + "]：" + cause.getMessage());
                return result;
            }

            @Override
            public Map<String, Object> startTrain(Map<String, Object> request) {
                return fail("启动训练");
            }

            @Override
            public Map<String, Object> getTrainProgress(String taskId) {
                return fail("查询训练进度");
            }

            @Override
            public Map<String, Object> cancelTrain(String taskId) {
                return fail("取消训练");
            }

            @Override
            public Map<String, Object> buildVectors(Map<String, Object> request) {
                return fail("构建向量库");
            }

            @Override
            public Map<String, Object> getBuildProgress(String taskId) {
                return fail("查询构建进度");
            }

            @Override
            public Map<String, Object> search(MultipartFile image, Map<String, String> params) {
                return fail("相似检索");
            }

            @Override
            public Map<String, Object> probeSource(Map<String, Object> request) {
                return fail("数据源探测");
            }

            @Override
            public Map<String, Object> classify(MultipartFile image, Map<String, String> params) {
                return fail("图像分类");
            }
        };
    }
}
