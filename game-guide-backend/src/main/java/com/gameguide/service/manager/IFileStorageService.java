package com.gameguide.service.manager;

import com.gameguide.enums.FileStorageType;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 * 定义文件上传的通用方法
 */
public interface IFileStorageService {
    
    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 获取存储类型
     * @return 存储类型枚举
     */
    FileStorageType getStorageType();
}

