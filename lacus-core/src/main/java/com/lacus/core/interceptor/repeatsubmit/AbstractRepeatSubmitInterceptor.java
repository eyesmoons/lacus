package com.lacus.core.interceptor.repeatsubmit;

import cn.hutool.json.JSONUtil;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.error.ErrorCode.Client;
import com.lacus.common.utils.ServletHolderUtil;
import com.lacus.core.annotations.RepeatSubmit;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 防止重复提交拦截器
 */
@Component
public abstract class AbstractRepeatSubmitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (this.isRepeatSubmit(request, annotation)) {
                    ResponseDTO<Object> responseDTO = ResponseDTO.fail(Client.COMMON_REQUEST_TO_OFTEN);
                    ServletHolderUtil.renderString(response, JSONUtil.toJsonStr(responseDTO));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
