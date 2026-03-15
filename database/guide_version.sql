-- =====================================================
-- 攻略版本与发布管理 v2（PostgreSQL）
-- 设计说明：
--   guide 表：每次编辑保存一份快照，去掉版本/发布字段
--   guide_version 表：版本管理主表，含发布状态
-- =====================================================

-- ========== 修改 guide 表：去掉版本/发布相关字段 ==========
ALTER TABLE guide DROP COLUMN IF EXISTS latest_version;
ALTER TABLE guide DROP COLUMN IF EXISTS published_version;
ALTER TABLE guide DROP COLUMN IF EXISTS published_at;

-- ========== 重建 guide_version 表 ==========
DROP TABLE IF EXISTS guide_version;
DROP SEQUENCE IF EXISTS guide_version_id_seq;

CREATE SEQUENCE IF NOT EXISTS guide_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- guide_version：版本管理主表
-- 每次发布前先保存 guide 快照，再更新 guide_version
CREATE TABLE IF NOT EXISTS guide_version (
    id               BIGINT       PRIMARY KEY DEFAULT nextval('guide_version_id_seq'),
    game_id          BIGINT       NOT NULL,
    version_no       INTEGER      NOT NULL,
    published_version BIGINT      NULL REFERENCES guide(id) ON DELETE SET NULL,
    published_at     TIMESTAMP    NULL,
    published_status SMALLINT     NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  guide_version                   IS '攻略版本管理表';
COMMENT ON COLUMN guide_version.id                IS '版本主键';
COMMENT ON COLUMN guide_version.game_id           IS '所属游戏ID';
COMMENT ON COLUMN guide_version.version_no        IS '版本号（自增有序）';
COMMENT ON COLUMN guide_version.published_version IS '已发布的 guide 快照 ID（外键）';
COMMENT ON COLUMN guide_version.published_at      IS '最近一次发布时间';
COMMENT ON COLUMN guide_version.published_status  IS '发布状态：0=未发布，1=已发布';
COMMENT ON COLUMN guide_version.created_at        IS '创建时间';
COMMENT ON COLUMN guide_version.updated_at        IS '最后更新时间';

-- guide 表增加 guide_version_id 外键，指向所属版本管理记录
ALTER TABLE guide ADD COLUMN IF NOT EXISTS guide_version_id BIGINT NULL
    REFERENCES guide_version(id) ON DELETE SET NULL;
COMMENT ON COLUMN guide.guide_version_id IS '所属版本管理记录ID';

-- ========== 索引 ==========
CREATE INDEX IF NOT EXISTS idx_gv_game_id           ON guide_version(game_id);
CREATE INDEX IF NOT EXISTS idx_gv_published_status  ON guide_version(published_status);
CREATE INDEX IF NOT EXISTS idx_gv_published_at      ON guide_version(published_at DESC);
CREATE INDEX IF NOT EXISTS idx_guide_guide_version  ON guide(guide_version_id);
