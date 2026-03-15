package com.gameguide.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 爬虫配置属性
 */
@Data
@ConfigurationProperties(prefix = "spider")
public class SpiderProperties {

    /**
     * Vgover 爬虫配置
     */
    @Data
    public static class VgoverConfig {
        /** 起始页码 */
        private int startPage = 1;

        /** 结束页码 */
        private int endPage = 10;

        /** 每篇文章间隔延迟（毫秒） */
        private long delayMillis = 1000;

        /** 爬取的 topic ID */
        private String topicId = "1612";
    }

    private VgoverConfig vgover = new VgoverConfig();
}

