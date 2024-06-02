package com.lacus.core.aspectj;

import cn.hutool.extra.servlet.ServletUtil;
import com.lacus.core.enums.LimitType;
import com.lacus.core.security.AuthenticationUtils;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.utils.ServletHolderUtil;
import com.lacus.core.annotations.RateLimiter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * 限流处理
 */
@Aspect
@Component
@Slf4j
@ConditionalOnExpression("'${lacus.embedded.redis}' != 'true'")
public class RateLimiterAspect {

    private RedisTemplate<Object, Object> redisTemplate;

    private RedisScript<Long> limitScript;

    @Autowired
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setLimitScript(RedisScript<Long> limitScript) {
        this.limitScript = limitScript;
    }

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        String key = rateLimiter.key();
        int time = rateLimiter.time();
        int count = rateLimiter.count();

        String combineKey = getCombineKey(rateLimiter, point);
        List<Object> keys = Collections.singletonList(combineKey);
        try {
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (number == null || number.intValue() > count) {
                throw new ApiException(ErrorCode.Client.COMMON_REQUEST_TO_OFTEN);
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), key);
        } catch (Exception e) {
            throw new RuntimeException("服务器限流异常，请稍候再试");
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        StringBuilder stringBuilder = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            LoginUser loginUser = AuthenticationUtils.getLoginUser();

            if (loginUser != null) {
                stringBuilder.append(loginUser.getLoginInfo().getIpAddress()).append("-");
            } else {
                stringBuilder.append(ServletUtil.getClientIP(ServletHolderUtil.getRequest())).append("-");
            }
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuilder.append(targetClass.getName()).append("-").append(method.getName());
        return stringBuilder.toString();
    }
}
