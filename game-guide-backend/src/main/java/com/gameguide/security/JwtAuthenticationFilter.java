package com.gameguide.security;

import com.gameguide.config.AppAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AppAuthProperties appAuthProperties;
    private final Environment environment;

    /** 判断当前是否处于 dev 环境且开启了忽略认证 */
    private boolean isIgnoreAuthEnabled() {
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        return isDevProfile && appAuthProperties.isIgnoreAuth();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.info("========== JwtAuthenticationFilter ==========");
        log.info("请求路径: {} {}", method, requestURI);

        // dev 环境且 ignore-auth=true：注入虚拟 ADMIN 用户，跳过 JWT 校验
        if (isIgnoreAuthEnabled()) {
            log.warn("[DEV] ignore-auth=true，跳过认证，自动注入 ADMIN 用户");
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "dev-user",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            log.info("发现 Token: {}...", token.substring(0, Math.min(20, token.length())));
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                log.info("Token 验证成功，用户: {}, 角色: {}", username, role);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Token 验证失败");
            }
        } else {
            log.info("未发现 Token，继续处理...");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


