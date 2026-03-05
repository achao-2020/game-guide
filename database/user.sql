-- 用户表
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户名唯一约束
ALTER TABLE "user" ADD CONSTRAINT uk_user_username UNIQUE (username);

-- 用户名索引
CREATE INDEX idx_user_username ON "user"(username);

-- 插入默认管理员账号 (密码: admin123, 使用 Argon2 加密)
INSERT INTO "user" (username, password, role) VALUES 
    ('admin', '$argon2id$v=19$m=65536,t=3,p=1$YXJnb24yc2FsdDEyMzQ$qFJZvKqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5Yg', 'ADMIN'),
    ('user', '$argon2id$v=19$m=65536,t=3,p=1$YXJnb24yc2FsdDEyMzQ$qFJZvKqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5Yg', 'USER');

COMMENT ON TABLE "user" IS '用户表';



