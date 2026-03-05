package com.gameguide.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordUtil {
    public static void main(String[] args) {
        // 使用 Argon2 加密算法
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
                16,      // saltLength: 16 字节
                32,      // hashLength: 32 字节
                1,       // parallelism: 1 个线程
                65536,   // memory: 64 MB
                3        // iterations: 3 次迭代
        );
        
        String password = "admin123";
        String encoded = encoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("密码加密工具 - Argon2 算法");
        System.out.println("========================================");
        System.out.println("原始密码: " + password);
        System.out.println("\nArgon2 加密后的密码:");
        System.out.println(encoded);
        System.out.println("\n密码长度: " + encoded.length());
        
        // 验证密码是否正确
        boolean matches = encoder.matches(password, encoded);
        System.out.println("验证结果: " + (matches ? "✓ 成功" : "✗ 失败"));
        
        System.out.println("\n========================================");
        System.out.println("请将上面的加密密码复制到 user.sql 文件中");
        System.out.println("========================================");
    }
}

