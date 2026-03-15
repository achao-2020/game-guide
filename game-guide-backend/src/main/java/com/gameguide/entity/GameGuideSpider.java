package com.gameguide.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * game_guide_spider 表：爬取的网络攻略
 */
@Data
public class GameGuideSpider {
    private Long id;
    private String title;
    private String content;
    private String sourceUrl;
    private LocalDateTime createTime;
}

