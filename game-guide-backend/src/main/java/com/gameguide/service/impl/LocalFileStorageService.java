package com.gameguide.service.impl;

import com.gameguide.entity.FileHash;
import com.gameguide.enums.FileStorageType;
import com.gameguide.service.FileHashService;
import com.gameguide.service.IFileStorageService;
import com.gameguide.util.FileUtils;
import lombok.RequiredArgsConstructor;
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
 * 使用数据库存储文件哈希值，实现文件去重
 */
@Slf4j
@Service("localFileStorageService")
@RequiredArgsConstructor
public class LocalFileStorageService implements IFileStorageService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/api/files/}")
    private String urlPrefix;

    private final FileHashService fileHashService;

    @Override
    public String uploadFile(MultipartFile file) {
        // 验证文件
        FileUtils.validateFile(file);

        try {
            // 计算文件哈希值
            String fileHash = fileHashService.calculateFileHash(file);
            
            // 检查文件是否已存在于数据库
            FileHash existingFile = fileHashService.getFileByHash(fileHash);
            if (existingFile != null) {
                // 文件已存在，更新使用信息
                fileHashService.updateFileUsage(fileHash);
                log.info("文件已存在，返回已有文件: {}", existingFile.getFileName());
                return existingFile.getFileUrl();
            }

            // 生成唯一文件名
            String fileName = FileUtils.generateUniqueFileName(file);

            // 创建上传目录（使用绝对路径）
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 构建文件访问URL
            String fileUrl = urlPrefix + fileName;

            // 保存文件哈希记录到数据库
            fileHashService.saveFileHash(fileHash, fileName, fileUrl, file, file.getContentType());

            log.info("文件上传成功: {} -> {}", fileName, filePath);

            // 返回访问URL
            return fileUrl;
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

