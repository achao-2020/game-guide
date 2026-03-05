package com.gameguide.service.impl;

import com.gameguide.enums.FileStorageType;
import com.gameguide.service.IFileStorageService;
import com.gameguide.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service("localFileStorageService")
public class LocalFileStorageService implements IFileStorageService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/api/files/}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file) {
        // 验证文件
        FileUtils.validateFile(file);

        // 生成唯一文件名
        String fileName = FileUtils.generateUniqueFileName(file);

        try {
            // 创建上传目录（使用绝对路径）
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            log.info("文件上传成功: {} -> {}", fileName, filePath);

            // 返回访问URL
            return urlPrefix + fileName;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.LOCAL;
    }
}

