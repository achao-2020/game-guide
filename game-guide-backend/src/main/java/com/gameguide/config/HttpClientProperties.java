package com.gameguide.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HttpClient 配置属性
 */
@Data
@ConfigurationProperties(prefix = "http.client")
public class HttpClientProperties {

    /** 连接超时时间（秒） */
    private long connectTimeout = 10;

    /** 读取超时时间（秒） */
    private long readTimeout = 15;

    /** 写入超时时间（秒） */
    private long writeTimeout = 10;
}

