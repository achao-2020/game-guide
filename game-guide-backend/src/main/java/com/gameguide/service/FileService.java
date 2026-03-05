package com.gameguide.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 * 委托给FileStorageStrategy进行实际的文件上传操作
 */
@Slf4j
@Service
public class FileService {

    private final FileStorageStrategy fileStorageStrategy;

    @Autowired
    public FileService(FileStorageStrategy fileStorageStrategy) {
        this.fileStorageStrategy = fileStorageStrategy;
    }

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        return fileStorageStrategy.uploadFile(file);
    }
}

