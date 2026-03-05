package com.gameguide.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameguide.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        
        log.error("========== 认证失败，触发 JwtAuthenticationEntryPoint ==========");
        log.error("请求路径: {} {}", request.getMethod(), request.getRequestURI());
        log.error("异常信息: {}", authException.getMessage());
        log.error("异常类型: {}", authException.getClass().getName());
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Result<Void> result = Result.error(401, "未授权，请先登录");
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}


