package com.gameguide.service.manager.impl;

import com.gameguide.entity.FileHash;
import com.gameguide.enums.FileStorageType;
import com.gameguide.service.FileHashService;
import com.gameguide.service.manager.IFileStorageService;
import com.gameguide.util.FileUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

/**
 * MinIO对象存储服务实现
 * 使用Amazon S3兼容的API
 * 支持文件哈希去重
 */
@Slf4j
@Service("minioFileStorageService")
@RequiredArgsConstructor
public class MinioFileStorageService implements IFileStorageService {

    @Value("${minio.endpoint:http://192.168.2.108:9001}")
    private String endpoint;

    @Value("${minio.access-key:minio_5nAdys}")
    private String accessKey;

    @Value("${minio.secret-key:minio_jM3WYB}")
    private String secretKey;

    @Value("${minio.bucket-name:game-guide}")
    private String bucketName;

    private MinioClient minioClient;
    
    private final FileHashService fileHashService;

    @PostConstruct
    public void init() {
        try {
            // 初始化MinIO客户端
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 检查bucket是否存在，不存在则创建
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("创建MinIO bucket: {}", bucketName);
                
                // 设置bucket为公开访问（可选）
                String policy = """
                        {
                            "Version": "2012-10-17",
                            "Statement": [
                                {
                                    "Effect": "Allow",
                                    "Principal": {"AWS": ["*"]},
                                    "Action": ["s3:GetObject"],
                                    "Resource": ["arn:aws:s3:::%s/*"]
                                }
                            ]
                        }
                        """.formatted(bucketName);
                
                minioClient.setBucketPolicy(
                        io.minio.SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
                log.info("设置MinIO bucket公开访问策略");
            }

            log.info("MinIO客户端初始化成功: {}", endpoint);
        } catch (Exception e) {
            log.error("MinIO客户端初始化失败", e);
            throw new RuntimeException("MinIO客户端初始化失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // 验证文件
        String contentType = FileUtils.validateFile(file);

        try {
            // 计算文件哈希值
            String fileHash = fileHashService.calculateFileHash(file);
            
            // 检查文件是否已存在于数据库
            FileHash existingFile = fileHashService.getFileByHash(fileHash);
            if (existingFile != null) {
                // 文件已存在，更新使用信息
                fileHashService.updateFileUsage(fileHash);
                log.info("文件已存在于MinIO，返回已有文件: {}", existingFile.getFileName());
                return existingFile.getFileUrl();
            }

            // 生成唯一文件名
            String fileName = FileUtils.generateUniqueFileName(file);

            try (InputStream inputStream = file.getInputStream()) {
                // 上传文件到MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(contentType)
                                .build()
                );

                log.info("文件上传到MinIO成功: {}", fileName);

                // 构建文件访问URL
                String fileUrl = String.format("%s/%s/%s", endpoint, bucketName, fileName);

                // 保存文件哈希记录到数据库
                fileHashService.saveFileHash(fileHash, fileName, fileUrl, file, contentType);

                // 返回文件访问URL
                return fileUrl;
            }
        } catch (Exception e) {
            log.error("文件上传到MinIO失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.MINIO;
    }
}

