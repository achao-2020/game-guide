package com.gameguide.controller.manager;

import com.gameguide.common.Result;
import com.gameguide.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final OkHttpClient okHttpClient;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(file);
        return Result.success(fileUrl);
    }

    /**
     * 图片转链：下载第三方 HTTP 图片并上传到本地存储，返回本地 URL
     */
    @PostMapping("/rehost")
    public Result<String> rehostImage(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("url");
        if (imageUrl == null || imageUrl.isBlank()) {
            return Result.error("图片 URL 不能为空");
        }
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return Result.error("仅支持 HTTP/HTTPS 图片链接");
        }
        try {
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful() || responseBody == null) {
                    return Result.error("下载图片失败: HTTP " + response.code());
                }
                byte[] bytes = responseBody.bytes();
                String contentType = responseBody.contentType() != null
                        ? responseBody.contentType().toString() : "image/jpeg";
                // 从 URL 提取文件名
                String path = new URL(imageUrl).getPath();
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                if (fileName.isBlank() || !fileName.contains(".")) {
                    fileName = "image.jpg";
                }
                MultipartFile multipartFile = new ByteArrayMultipartFile(fileName, contentType, bytes);
                String localUrl = fileService.uploadFile(multipartFile);
                return Result.success(localUrl);
            }
        } catch (Exception e) {
            log.error("图片转链失败: {}, 原因: {}", imageUrl, e.getMessage());
            return Result.error("图片转链失败: " + e.getMessage());
        }
    }

    /**
     * 轻量 MultipartFile 实现，基于字节数组，无需 spring-test 依赖
     */
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final String fileName;
        private final String contentType;
        private final byte[] content;

        ByteArrayMultipartFile(String fileName, String contentType, byte[] content) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.content = content;
        }

        @Override public String getName() { return fileName; }
        @Override public String getOriginalFilename() { return fileName; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content == null || content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }
}
