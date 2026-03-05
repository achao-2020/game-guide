package com.gameguide.enums;

/**
 * 文件存储类型枚举
 */
public enum FileStorageType {
    
    /**
     * 本地文件存储
     */
    LOCAL("local", "本地存储"),
    
    /**
     * MinIO对象存储
     */
    MINIO("minio", "MinIO对象存储");
    
    private final String code;
    private final String description;
    
    FileStorageType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     * @param code 存储类型代码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static FileStorageType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (FileStorageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * 判断code是否有效
     * @param code 存储类型代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}

