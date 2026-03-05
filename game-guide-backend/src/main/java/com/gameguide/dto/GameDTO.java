package com.gameguide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GameDTO {
    
    @NotBlank(message = "游戏名称不能为空")
    @Size(max = 255, message = "游戏名称长度不能超过255个字符")
    private String name;
    
    private String description;
    
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImage;
}

