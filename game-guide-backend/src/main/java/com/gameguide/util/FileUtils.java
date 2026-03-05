package com.gameguide.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 文件工具类
 * 提供文件验证、文件名处理等通用方法
 */
public class FileUtils {

    /**
     * 验证文件是否为空
     * @param file 文件
     * @throws RuntimeException 如果文件为空
     */
    public static void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
    }

    /**
     * 验证文件名
     * @param filename 文件名
     * @throws RuntimeException 如果文件名为空
     */
    public static void validateFileName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
    }

    /**
     * 验证文件类型是否为图片
     * @param contentType 文件MIME类型
     * @throws RuntimeException 如果不是图片类型
     */
    public static void validateImageType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持上传图片文件");
        }
    }

    /**
     * 验证文件（综合验证：非空、文件名、图片类型）
     * @param file 文件
     * @return 文件的ContentType
     * @throws RuntimeException 如果验证失败
     */
    public static String validateFile(MultipartFile file) {
        validateFileNotEmpty(file);
        
        String originalFilename = file.getOriginalFilename();
        validateFileName(originalFilename);
        
        String contentType = file.getContentType();
        validateImageType(contentType);
        
        return contentType;
    }

    /**
     * 获取文件扩展名（包含点号）
     * @param filename 文件名
     * @return 扩展名，如 ".jpg"，如果没有扩展名则返回空字符串
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        
        return "";
    }

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return UUID + 原始文件扩展名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 生成唯一文件名（从MultipartFile）
     * @param file 文件
     * @return UUID + 原始文件扩展名
     */
    public static String generateUniqueFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return UUID.randomUUID().toString();
        }
        return generateUniqueFileName(originalFilename);
    }
}

