package com.gameguide.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件哈希表
 * 用于存储上传文件的哈希值和文件信息，实现文件去重
 */
@Data
public class FileHash {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 文件哈希值（MD5）
     */
    private String fileHash;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件MIME类型
     */
    private String mimeType;
    
    /**
     * 上传次数（用于统计）
     */
    private Integer uploadCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;
}

