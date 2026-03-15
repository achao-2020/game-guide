-- =====================================================
-- 移除攻略版本管理功能（PostgreSQL）
-- 设计说明：
--   删除 guide_version 表
--   从 guide 表中删除 guide_version_id 字段
--   简化攻略管理，只在 guide 表中存储
-- =====================================================

-- ========== 删除 guide 表的外键约束 ==========
ALTER TABLE guide DROP COLUMN IF EXISTS guide_version_id;

-- ========== 删除 guide_version 表 ==========
DROP TABLE IF EXISTS guide_version;
DROP SEQUENCE IF EXISTS guide_version_id_seq;

-- ========== 删除相关索引 ==========
DROP INDEX IF EXISTS idx_gv_game_id;
DROP INDEX IF EXISTS idx_gv_published_status;
DROP INDEX IF EXISTS idx_gv_published_at;
DROP INDEX IF EXISTS idx_guide_guide_version;

