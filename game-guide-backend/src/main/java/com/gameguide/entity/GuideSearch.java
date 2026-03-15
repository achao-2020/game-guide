package com.gameguide.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * guide_search 表：攻略全文搜索向量
 */
@Data
public class GuideSearch {
    private Long guideId;
    private String title;
    private String content;
    private Long gameId;
    private String gameName;
    private LocalDateTime updatedAt;
}

