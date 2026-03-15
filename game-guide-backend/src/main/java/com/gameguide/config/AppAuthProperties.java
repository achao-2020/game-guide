package com.gameguide.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 认证相关配置属性
 * ignore-auth 仅在 dev profile 下配置为 true 才生效
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {

    /**
     * 是否忽略用户认证，默认 false。
     * 只有激活 dev profile 且显式配置为 true 时才会跳过认证。
     */
    private boolean ignoreAuth = false;
}

