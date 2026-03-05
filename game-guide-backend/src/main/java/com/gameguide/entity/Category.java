package com.gameguide.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}

