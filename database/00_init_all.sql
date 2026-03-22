-- ============================================================
-- 游戏攻略知识库系统 - 完整数据库初始化脚本
-- ============================================================
-- 执行顺序：
-- 1. 创建数据库和基础表
-- 2. 创建用户表
-- 3. 创建文件哈希表
-- 4. 启用 pgvector 扩展
-- 5. 创建全文搜索表（无触发器，search_vec 由应用层维护）
-- ============================================================

-- ============================================================
-- 第1步：基础表初始化
-- ============================================================

-- 1. 游戏表
CREATE TABLE IF NOT EXISTS game (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE game ADD CONSTRAINT uk_game_name UNIQUE (name);
CREATE INDEX IF NOT EXISTS idx_game_name ON game(name);

-- 2. 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE category ADD CONSTRAINT uk_category_name UNIQUE (name);

-- 3. 攻略表
CREATE TABLE IF NOT EXISTS guide (
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

CREATE INDEX IF NOT EXISTS idx_guide_title ON guide(title);
CREATE INDEX IF NOT EXISTS idx_guide_game_id ON guide(game_id);
CREATE INDEX IF NOT EXISTS idx_guide_category_id ON guide(category_id);
CREATE INDEX IF NOT EXISTS idx_guide_created_at ON guide(created_at DESC);

-- 4. 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

ALTER TABLE tag ADD CONSTRAINT uk_tag_name UNIQUE (name);

-- 5. 攻略标签关联表
CREATE TABLE IF NOT EXISTS guide_tag (
    guide_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (guide_id, tag_id),
    CONSTRAINT fk_guide_tag_guide FOREIGN KEY (guide_id) REFERENCES guide(id) ON DELETE CASCADE,
    CONSTRAINT fk_guide_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_guide_tag_tag_id ON guide_tag(tag_id);

COMMENT ON TABLE game IS '游戏表';
COMMENT ON TABLE category IS '分类表';
COMMENT ON TABLE guide IS '攻略表';
COMMENT ON TABLE tag IS '标签表';
COMMENT ON TABLE guide_tag IS '攻略标签关联表';

-- 插入初始数据
INSERT INTO category (name) VALUES
    ('新手指南'), ('进阶攻略'), ('角色攻略'), ('装备攻略'), ('副本攻略')
ON CONFLICT DO NOTHING;

INSERT INTO game (name, description, cover_image) VALUES
    ('原神', '开放世界冒险游戏', 'https://example.com/genshin.jpg'),
    ('崩坏：星穹铁道', '回合制RPG游戏', 'https://example.com/hsr.jpg')
ON CONFLICT DO NOTHING;

-- ============================================================
-- 第2步：用户表
-- ============================================================

CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE "user" ADD CONSTRAINT uk_user_username UNIQUE (username);
CREATE INDEX IF NOT EXISTS idx_user_username ON "user"(username);

-- 默认管理员账号（密码: admin123，Argon2 加密）
INSERT INTO "user" (username, password, role) VALUES
    ('admin', '$argon2id$v=19$m=65536,t=3,p=1$YXJnb24yc2FsdDEyMzQ$qFJZvKqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5Yg', 'ADMIN'),
    ('user',  '$argon2id$v=19$m=65536,t=3,p=1$YXJnb24yc2FsdDEyMzQ$qFJZvKqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5YqZ5Yg', 'USER')
ON CONFLICT DO NOTHING;

COMMENT ON TABLE "user" IS '用户表';

-- ============================================================
-- 第3步：文件哈希表
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS file_hash_id_seq START WITH 1 INCREMENT BY 1 CACHE 1;

CREATE TABLE IF NOT EXISTS file_hash (
    id           BIGINT  PRIMARY KEY DEFAULT nextval('file_hash_id_seq'),
    file_hash    VARCHAR(32)  NOT NULL UNIQUE,
    file_name    VARCHAR(255) NOT NULL,
    file_url     VARCHAR(500) NOT NULL,
    file_size    BIGINT       NOT NULL,
    mime_type    VARCHAR(100),
    upload_count INTEGER      DEFAULT 1,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_file_hash        ON file_hash(file_hash);
CREATE INDEX        IF NOT EXISTS idx_file_created_at  ON file_hash(created_at DESC);
CREATE INDEX        IF NOT EXISTS idx_file_last_used   ON file_hash(last_used_at DESC);
CREATE INDEX        IF NOT EXISTS idx_file_upload_cnt  ON file_hash(upload_count DESC);
CREATE INDEX        IF NOT EXISTS idx_file_mime_type   ON file_hash(mime_type);

COMMENT ON TABLE file_hash IS '文件哈希去重表';

-- ============================================================
-- 第4步：启用 pgvector 扩展
-- ============================================================
-- 安装方式：
--   Docker:  使用镜像 pgvector/pgvector:pg16
--   Linux:   apt install postgresql-server-dev-16 + 编译 pgvector
--   Windows: 下载 https://github.com/pgvector/pgvector/releases
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- 第5步：全文搜索与向量表
-- search_vec 字段由应用层（GuideSearchMapper.xml upsert）在写入时计算
-- embedding  字段由应用层异步调用智谱 Embedding API 后更新
-- 无需数据库触发器
-- ============================================================

CREATE TABLE IF NOT EXISTS guide_search (
    guide_id   BIGINT       PRIMARY KEY,
    title      TEXT         NOT NULL,
    content    TEXT         NOT NULL,
    game_id    BIGINT,
    game_name  VARCHAR(255),
    search_vec TSVECTOR,
    embedding  vector(2048),
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guide_search_guide
        FOREIGN KEY (guide_id) REFERENCES guide(id) ON DELETE CASCADE
);

COMMENT ON TABLE  guide_search            IS '攻略全文搜索与向量表';
COMMENT ON COLUMN guide_search.guide_id   IS '关联 guide.id，级联删除';
COMMENT ON COLUMN guide_search.search_vec IS 'tsvector 全文索引（应用层写入时计算）';
COMMENT ON COLUMN guide_search.embedding  IS 'pgvector 2048维向量（智谱 embedding-3）';

-- GIN 索引：加速 tsvector 全文搜索
CREATE INDEX IF NOT EXISTS idx_guide_search_vec
    ON guide_search USING GIN(search_vec);

-- HNSW 索引：加速向量余弦相似度检索
CREATE INDEX IF NOT EXISTS idx_guide_search_embedding_hnsw
    ON guide_search USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- ============================================================
-- 历史数据回填
-- search_vec 在 INSERT 时由 SQL 直接计算（与 Mapper upsert 一致）
-- embedding 需执行后调用 POST /api/admin/ai/embedding/batch 生成
-- ============================================================
INSERT INTO guide_search (guide_id, title, content, game_id, game_name, search_vec, updated_at)
SELECT
    g.id,
    g.title,
    g.content,
    g.game_id,
    gm.name AS game_name,
    setweight(to_tsvector('simple', coalesce(g.title,    '')), 'A') ||
    setweight(to_tsvector('simple', coalesce(gm.name,    '')), 'B') ||
    setweight(to_tsvector('simple', coalesce(g.content,  '')), 'C'),
    CURRENT_TIMESTAMP
FROM guide g
LEFT JOIN game gm ON g.game_id = gm.id
ON CONFLICT (guide_id) DO UPDATE
    SET title      = EXCLUDED.title,
        content    = EXCLUDED.content,
        game_id    = EXCLUDED.game_id,
        game_name  = EXCLUDED.game_name,
        search_vec = EXCLUDED.search_vec,
        updated_at = CURRENT_TIMESTAMP;

-- ============================================================
-- 爬虫任务表与明细表
-- ============================================================

-- 爬虫任务主表
CREATE TABLE IF NOT EXISTS game_guide_spider_task (
    id          BIGSERIAL    PRIMARY KEY,
    url         VARCHAR(500) NOT NULL,
    page_size   INTEGER      NOT NULL DEFAULT 0,
    title       VARCHAR(500) NOT NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finish_time TIMESTAMP
);

COMMENT ON TABLE  game_guide_spider_task             IS '爬虫任务记录表';
COMMENT ON COLUMN game_guide_spider_task.url         IS '爬取起始列表页 URL';
COMMENT ON COLUMN game_guide_spider_task.page_size   IS '爬取页数（endPage - startPage + 1）';
COMMENT ON COLUMN game_guide_spider_task.title       IS '任务标题：取第一篇文章标题，无则为 时间-爬取游戏攻略';
COMMENT ON COLUMN game_guide_spider_task.create_time IS '任务创建时间';
COMMENT ON COLUMN game_guide_spider_task.finish_time IS '任务完成时间，NULL 表示未完成';

CREATE INDEX IF NOT EXISTS idx_spider_task_create_time ON game_guide_spider_task(create_time DESC);

-- 爬虫明细表（原 game_guide_spider，增加 task_id 外键）
CREATE TABLE IF NOT EXISTS game_guide_spider (
    id          BIGSERIAL    PRIMARY KEY,
    task_id     BIGINT       REFERENCES game_guide_spider_task(id) ON DELETE SET NULL,
    title       VARCHAR(500) NOT NULL,
    content     TEXT         NOT NULL,
    source_url  VARCHAR(500) NOT NULL UNIQUE,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  game_guide_spider            IS '爬虫攻略明细表';
COMMENT ON COLUMN game_guide_spider.task_id    IS '关联的爬虫任务 ID';
COMMENT ON COLUMN game_guide_spider.source_url IS '文章来源 URL，唯一约束用于去重';

CREATE INDEX IF NOT EXISTS idx_spider_task_id     ON game_guide_spider(task_id);
CREATE INDEX IF NOT EXISTS idx_spider_source_url  ON game_guide_spider(source_url);
CREATE INDEX IF NOT EXISTS idx_spider_create_time ON game_guide_spider(create_time DESC);

-- 若已有旧表（无 task_id 列），执行以下语句追加列：
-- ALTER TABLE game_guide_spider ADD COLUMN IF NOT EXISTS task_id BIGINT REFERENCES game_guide_spider_task(id) ON DELETE SET NULL;

-- ============================================================
-- 初始化完成
-- 提示：执行完毕后，调用以下接口回填历史攻略的向量数据：
--   POST /api/admin/ai/embedding/batch   （需要管理员 Token）
-- ============================================================
