package com.gameguide.service;

import com.gameguide.enums.FileStorageType;
import com.gameguide.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件存储策略选择器
 * 根据配置选择使用本地存储或MinIO存储
 */
@Slf4j
@Service
public class FileStorageStrategy {

    @Value("${file.storage.type:local}")
    private String storageTypeConfig;

    private final List<IFileStorageService> storageServices;

    @Autowired
    public FileStorageStrategy(List<IFileStorageService> storageServices) {
        this.storageServices = storageServices;
    }

    /**
     * 获取当前配置的存储服务
     */
    public IFileStorageService getStorageService() {
        // 将配置转换为枚举
        FileStorageType storageType = FileStorageType.fromCode(storageTypeConfig);
        
        if (storageType == null) {
            log.warn("无效的存储类型配置: {}，使用默认本地存储", storageTypeConfig);
            storageType = FileStorageType.LOCAL;
        }
        
        IFileStorageService service = null;
        
        // 根据枚举类型选择服务
        for (IFileStorageService storageService : storageServices) {
            if (storageService.getStorageType().equals(storageType)) {
                service = storageService;
            }
        }

        if (service == null) {
            throw new BusinessException("未找到匹配的存储服务");
        }

        log.debug("使用文件存储服务: {}", service.getStorageType().getDescription());
        return service;
    }

    /**
     * 上传文件（使用当前配置的存储策略）
     */
    public String uploadFile(MultipartFile file) {
        return getStorageService().uploadFile(file);
    }
}

