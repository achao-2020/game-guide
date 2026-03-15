package com.gameguide.config;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * HttpClient 配置类
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfig {

    private final HttpClientProperties httpClientProperties;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(httpClientProperties.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpClientProperties.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(httpClientProperties.getWriteTimeout(), TimeUnit.SECONDS)
                .build();
    }
}

