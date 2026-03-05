package com.gameguide.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义密码编码器 - 使用 Argon2 算法
 * Argon2 是目前最安全的密码哈希算法，赢得了 2015 年密码哈希竞赛
 * 
 * 安全特性：
 * - 抗 GPU 和 ASIC 攻击
 * - 可配置内存使用量、迭代次数和并行度
 * - 比 BCrypt 和 PBKDF2 更安全
 */
@Slf4j
public class CustomPasswordEncoder implements PasswordEncoder {

    private final Argon2PasswordEncoder argon2PasswordEncoder;

    public CustomPasswordEncoder() {
        log.info("初始化 CustomPasswordEncoder - 使用 Argon2 算法");
        // 配置 Argon2 参数
        // saltLength: 盐值长度（字节）
        // hashLength: 哈希长度（字节）
        // parallelism: 并行度
        // memory: 内存使用量（KB）
        // iterations: 迭代次数
        this.argon2PasswordEncoder = new Argon2PasswordEncoder(
                16,      // saltLength: 16 字节
                32,      // hashLength: 32 字节
                1,       // parallelism: 1 个线程
                65536,   // memory: 64 MB
                3        // iterations: 3 次迭代
        );
    }

    @Override
    public String encode(CharSequence rawPassword) {
        log.debug("正在使用 Argon2 加密密码...");
        String encodedPassword = argon2PasswordEncoder.encode(rawPassword);
        log.debug("密码加密完成，长度: {}", encodedPassword.length());
        return encodedPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.info("========== 进入 CustomPasswordEncoder.matches 方法 ==========");
        log.info("原始密码长度: {}", rawPassword.length());
        log.info("加密密码前缀: {}...", encodedPassword.substring(0, Math.min(20, encodedPassword.length())));
        
        boolean result = argon2PasswordEncoder.matches(rawPassword, encodedPassword);
        
        if (result) {
            log.info("✓ 密码验证成功");
        } else {
            log.warn("✗ 密码验证失败");
        }
        
        log.info("========== 退出 CustomPasswordEncoder.matches 方法 ==========");
        return result;
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return argon2PasswordEncoder.upgradeEncoding(encodedPassword);
    }
}

