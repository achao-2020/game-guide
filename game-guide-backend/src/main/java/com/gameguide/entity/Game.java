package com.gameguide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Game {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

