package com.gameguide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Guide {
    private Long id;
    private Long gameId;
    private Long categoryId;
    private String title;
    private String content;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

