# 文件存储服务架构说明

## 概述
本项目实现了基于策略模式的文件存储服务，支持本地存储和MinIO对象存储两种方式。

## 架构设计

### 1. 接口层
- `IFileStorageService`: 文件存储服务接口，定义了统一的文件上传方法

### 2. 实现层
- `LocalFileStorageService`: 本地文件存储实现
- `MinioFileStorageService`: MinIO对象存储实现（使用Amazon S3兼容API）

### 3. 策略层
- `FileStorageStrategy`: 策略选择器，根据配置动态选择存储服务

### 4. 服务层
- `FileService`: 文件服务门面，委托给策略选择器处理

## 配置说明

在 `application.yml` 中配置存储类型：

```yaml
file:
  storage:
    type: local  # 可选值: local 或 minio
```

### 本地存储配置
```yaml
file:
  upload:
    path: D:/project/game-guide/uploads
    url-prefix: /api/files/
```

### MinIO存储配置
```yaml
minio:
  endpoint: http://192.168.2.108:9001
  access-key: minio_5nAdys
  secret-key: minio_jM3WYB
  bucket-name: game-guide
```

## 使用方式

### 切换存储方式
只需修改配置文件中的 `file.storage.type` 值：
- `local`: 使用本地文件存储
- `minio`: 使用MinIO对象存储

### 代码调用
```java
@Autowired
private FileService fileService;

public String upload(MultipartFile file) {
    return fileService.uploadFile(file);
}
```

## MinIO特性
- 自动创建bucket（如果不存在）
- 自动设置公开访问策略
- 使用Amazon S3兼容API
- 支持图片文件上传
- 返回完整的访问URL

## 依赖
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

