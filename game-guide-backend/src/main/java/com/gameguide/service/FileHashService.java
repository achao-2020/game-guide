package com.gameguide.service;

import com.gameguide.dao.FileHashDao;
import com.gameguide.entity.FileHash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;

/**
 * 文件哈希服务
 * 用于管理文件哈希值和去重
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileHashService {

    private final FileHashDao fileHashDao;

    /**
     * 计算文件的 MD5 哈希值
     */
    public String calculateFileHash(MultipartFile file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] fileBytes = file.getBytes();
            byte[] hashBytes = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("计算文件哈希值失败", e);
            throw new IOException("计算文件哈希值失败", e);
        }
    }

    /**
     * 根据哈希值查询文件是否存在
     */
    public FileHash getFileByHash(String fileHash) {
        return fileHashDao.selectByFileHash(fileHash);
    }

    /**
     * 保存文件哈希记录
     */
    public void saveFileHash(String fileHash, String fileName, String fileUrl,
                             MultipartFile file, String mimeType) {
        FileHash fileHashRecord = new FileHash();
        fileHashRecord.setFileHash(fileHash);
        fileHashRecord.setFileName(fileName);
        fileHashRecord.setFileUrl(fileUrl);
        fileHashRecord.setFileSize(file.getSize());
        fileHashRecord.setMimeType(mimeType);
        fileHashRecord.setUploadCount(1);
        fileHashRecord.setCreatedAt(LocalDateTime.now());
        fileHashRecord.setLastUsedAt(LocalDateTime.now());
        fileHashDao.insert(fileHashRecord);
        log.info("保存文件哈希记录: {} -> {}", fileHash, fileName);
    }

    /**
     * 更新文件使用信息
     */
    public void updateFileUsage(String fileHash) {
        fileHashDao.updateUsageInfo(fileHash);
        log.info("更新文件使用信息: {}", fileHash);
    }
}
