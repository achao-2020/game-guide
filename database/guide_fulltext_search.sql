-- 游戏攻略全文搜索表及触发器
-- 依赖: guide 表已存在（见 init.sql）
-- 使用 PostgreSQL 内置 tsvector 实现中文友好的全文搜索
-- 注意: 中文推荐安装 zhparser 扩展，未安装时退化为 simple 分词

-- 1. 全文搜索向量表
CREATE TABLE IF NOT EXISTS guide_search (
    guide_id   BIGINT PRIMARY KEY,
    title      TEXT NOT NULL,
    content    TEXT NOT NULL,
    game_id    BIGINT,
    game_name  VARCHAR(255),
    search_vec TSVECTOR,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guide_search_guide
        FOREIGN KEY (guide_id) REFERENCES guide(id) ON DELETE CASCADE
);

COMMENT ON TABLE guide_search IS '攻略全文搜索向量表';
COMMENT ON COLUMN guide_search.guide_id   IS '关联 guide.id';
COMMENT ON COLUMN guide_search.search_vec IS 'tsvector 全文索引向量';

-- 2. GIN 索引，加速全文搜索
CREATE INDEX IF NOT EXISTS idx_guide_search_vec ON guide_search USING GIN(search_vec);

-- 3. 触发器函数：自动维护 search_vec
--    优先使用 zhparser（中文），降级到 simple
CREATE OR REPLACE FUNCTION guide_search_update_vec()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vec :=
        setweight(to_tsvector('simple', coalesce(NEW.title,   '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(NEW.game_name,'')),'B') ||
        setweight(to_tsvector('simple', coalesce(NEW.content,  '')), 'C');
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4. 绑定触发器
DROP TRIGGER IF EXISTS trg_guide_search_vec ON guide_search;
CREATE TRIGGER trg_guide_search_vec
    BEFORE INSERT OR UPDATE ON guide_search
    FOR EACH ROW EXECUTE FUNCTION guide_search_update_vec();

-- 5. 历史数据回填（从已有 guide + game 关联数据填入）
INSERT INTO guide_search (guide_id, title, content, game_id, game_name)
SELECT
    g.id,
    g.title,
    g.content,
    g.game_id,
    gm.name AS game_name
FROM guide g
LEFT JOIN game gm ON g.game_id = gm.id
ON CONFLICT (guide_id) DO UPDATE
    SET title     = EXCLUDED.title,
        content   = EXCLUDED.content,
        game_id   = EXCLUDED.game_id,
        game_name = EXCLUDED.game_name;

