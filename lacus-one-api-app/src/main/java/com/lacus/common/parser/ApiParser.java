package com.lacus.common.parser;

import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.enums.HttpRequestEnum;
import com.lacus.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ApiParser {

    public static void parse(HttpServletRequest request) {
        Map<String, Object> result = null;
        String requestURI = request.getRequestURI();
        String method = request.getMethod().toLowerCase(Locale.ROOT);
        HttpRequestEnum enums = HttpRequestEnum.getMethodEnum(method);
        if (enums == null) {
            throw new ApiException(ResultCode.METHOD_NOT_FOUND, "目前只支持GET或POST请求");
        }
        switch (enums) {
            case GET:
                GetParseParam getInstance = GetParseParam.getInstance();
                result = getInstance.parse(request);
                break;
            case POST:
                PostParseParam postInstance = PostParseParam.getInstance();
                result = postInstance.parse(request);
                break;
        }
        ApiThreadLocal apiThreadLocal = new ApiThreadLocal(requestURI, result, request.getRemoteAddr(), method);
        apiThreadLocal.set();
    }

    abstract static class ParseParam {
        public abstract <T> Map<String, T> parse(HttpServletRequest request);
    }

    static class GetParseParam extends ParseParam {
        private GetParseParam() {
        }

        private static GetParseParam getClass = null;

        public static GetParseParam getInstance() {
            if (getClass == null) {
                getClass = new GetParseParam();
            }
            return getClass;
        }

        @Override
        public <T> Map<String, T> parse(HttpServletRequest request) {
            Map<String, T> paramMap = new HashMap<>();
            if (request == null) {
                return paramMap;
            }
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String key = paramNames.nextElement();
                String value = request.getParameter(key);
                paramMap.put(key, (T) value);
            }
            return paramMap;
        }
    }

    static class PostParseParam extends ParseParam {
        private PostParseParam() {
        }

        private static PostParseParam postClass = null;

        public static PostParseParam getInstance() {
            if (postClass == null) {
                postClass = new PostParseParam();
            }
            return postClass;
        }

        @Override
        public <T> Map<String, T> parse(HttpServletRequest request) {
            Map<String, T> paramMap = new HashMap<>();
            if (request == null) {
                return paramMap;
            }
            try {
                String paramsStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                JSONObject paramObj = JSONObject.parseObject(paramsStr);
                Set<String> keySet = paramObj.keySet();
                for (String key : keySet) {
                    paramMap.put(key, (T) paramObj.get(key));
                }
            } catch (IOException e) {
                log.error("params parser error.");
            }
            return paramMap;
        }
    }
}
