package com.gameguide.security;

import com.gameguide.config.AppAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AppAuthProperties appAuthProperties;
    private final Environment environment;

    /** 判断当前是否处于 dev 环境且开启了忽略认证 */
    private boolean isIgnoreAuthEnabled() {
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        return isDevProfile && appAuthProperties.isIgnoreAuth();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        if (isIgnoreAuthEnabled()) {
            // dev 环境 + ignore-auth=true：放行所有请求，认证由 Filter 中注入的虚拟用户承载
            log.warn("[DEV] ignore-auth=true，已关闭接口认证限制，请勿在生产环境使用！");
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(auth -> auth
                            // 认证接口，无需登录
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/login").permitAll()
                            // 文件访问，无需登录
                            .requestMatchers("/api/files/**").permitAll()
                            // GET 请求，无需登录
                            .requestMatchers(HttpMethod.GET, "/api/games/**", "/api/guides/**", "/api/categories/**", "/api/tags/**").permitAll()
                            // 其他 POST/PUT/DELETE 请求需要 ADMIN 角色
                            .requestMatchers(HttpMethod.POST, "/api/games/**", "/api/guides/**", "/api/categories/**", "/api/tags/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/games/**", "/api/guides/**", "/api/categories/**", "/api/tags/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/games/**", "/api/guides/**", "/api/categories/**", "/api/tags/**").hasRole("ADMIN")
                            // 其他所有请求需要认证
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    );
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（生产环境建议指定具体域名）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}



