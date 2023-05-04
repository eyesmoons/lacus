package com.lacus.core.security.handle;

import cn.hutool.json.JSONUtil;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.error.ErrorCode.Client;
import com.lacus.common.utils.ServletHolderUtil;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 认证失败处理类 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        ResponseDTO<Object> responseDTO = ResponseDTO.fail(Client.COMMON_NO_AUTHORIZATION, request.getRequestURI());
        ServletHolderUtil.renderString(response, JSONUtil.toJsonStr(responseDTO));
    }
}
