package com.gameguide.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuideVO {
    private Long id;
    private Long gameId;
    private String gameName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Long viewCount;
    private List<TagVO> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

