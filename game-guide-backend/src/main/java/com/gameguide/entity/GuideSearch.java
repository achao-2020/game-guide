package com.gameguide.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * guide_search 表：攻略全文搜索 + 向量索引
 */
@Data
public class GuideSearch {
    private Long guideId;
    private String title;
    private String content;
    private Long gameId;
    private String gameName;
    /** pgvector 向量，存储时用 float[] */
    private float[] embedding;
    private LocalDateTime updatedAt;
}
