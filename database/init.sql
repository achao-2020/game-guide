-- 游戏攻略知识库系统 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE game_guide;

-- 连接到数据库
\c game_guide;

-- 1. 游戏表
CREATE TABLE game (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 游戏名称唯一约束
ALTER TABLE game ADD CONSTRAINT uk_game_name UNIQUE (name);

-- 游戏名称索引
CREATE INDEX idx_game_name ON game(name);

-- 2. 分类表
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 分类名称唯一约束
ALTER TABLE category ADD CONSTRAINT uk_category_name UNIQUE (name);

-- 3. 攻略表
CREATE TABLE guide (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guide_game FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
    CONSTRAINT fk_guide_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT
);

-- 攻略标题索引（用于搜索）
CREATE INDEX idx_guide_title ON guide(title);

-- 攻略游戏ID索引
CREATE INDEX idx_guide_game_id ON guide(game_id);

-- 攻略分类ID索引
CREATE INDEX idx_guide_category_id ON guide(category_id);

-- 攻略创建时间索引
CREATE INDEX idx_guide_created_at ON guide(created_at DESC);

-- 4. 标签表
CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 标签名称唯一约束
ALTER TABLE tag ADD CONSTRAINT uk_tag_name UNIQUE (name);

-- 5. 攻略标签关联表
CREATE TABLE guide_tag (
    guide_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (guide_id, tag_id),
    CONSTRAINT fk_guide_tag_guide FOREIGN KEY (guide_id) REFERENCES guide(id) ON DELETE CASCADE,
    CONSTRAINT fk_guide_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- 标签ID索引（用于查询某个标签下的所有攻略）
CREATE INDEX idx_guide_tag_tag_id ON guide_tag(tag_id);

-- 插入初始数据
INSERT INTO category (name) VALUES 
    ('新手指南'),
    ('进阶攻略'),
    ('角色攻略'),
    ('装备攻略'),
    ('副本攻略');

INSERT INTO game (name, description, cover_image) VALUES 
    ('原神', '开放世界冒险游戏', 'https://example.com/genshin.jpg'),
    ('崩坏：星穹铁道', '回合制RPG游戏', 'https://example.com/hsr.jpg');

COMMENT ON TABLE game IS '游戏表';
COMMENT ON TABLE category IS '分类表';
COMMENT ON TABLE guide IS '攻略表';
COMMENT ON TABLE tag IS '标签表';
COMMENT ON TABLE guide_tag IS '攻略标签关联表';

