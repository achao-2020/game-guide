package com.gameguide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class GuideDTO {
    
    @NotNull(message = "游戏ID不能为空")
    private Long gameId;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotBlank(message = "攻略标题不能为空")
    @Size(max = 500, message = "攻略标题长度不能超过500个字符")
    private String title;
    
    @NotBlank(message = "攻略内容不能为空")
    private String content;
    
    private List<Long> tagIds;
}

