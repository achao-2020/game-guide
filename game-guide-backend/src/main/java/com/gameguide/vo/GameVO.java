package com.gameguide.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GameVO {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

