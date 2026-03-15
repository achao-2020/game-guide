package com.gameguide.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 全文搜索结果 VO，含高亮摘要
 */
@Data
public class GuideSearchVO {
    private Long guideId;
    private String title;
    /** ts_headline 生成的高亮摘要 */
    private String headline;
    private Long gameId;
    private String gameName;
    private LocalDateTime updatedAt;
}

