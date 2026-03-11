-- =====================================================
-- 文件哈希表（PostgreSQL 版本）
-- 用于存储上传文件的哈希值和文件信息，实现文件去重
-- =====================================================

-- 创建序列（用于自增ID）
CREATE SEQUENCE IF NOT EXISTS file_hash_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 创建文件哈希表
CREATE TABLE IF NOT EXISTS file_hash (
    id BIGINT PRIMARY KEY DEFAULT nextval('file_hash_id_seq'),
    file_hash VARCHAR(32) NOT NULL UNIQUE,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    upload_count INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加表注释
COMMENT ON TABLE file_hash IS '文件哈希表';
COMMENT ON COLUMN file_hash.id IS '主键ID';
COMMENT ON COLUMN file_hash.file_hash IS '文件哈希值（MD5）';
COMMENT ON COLUMN file_hash.file_name IS '文件名';
COMMENT ON COLUMN file_hash.file_url IS '文件访问URL';
COMMENT ON COLUMN file_hash.file_size IS '文件大小（字节）';
COMMENT ON COLUMN file_hash.mime_type IS '文件MIME类型';
COMMENT ON COLUMN file_hash.upload_count IS '上传次数（用于统计）';
COMMENT ON COLUMN file_hash.created_at IS '创建时间';
COMMENT ON COLUMN file_hash.last_used_at IS '最后使用时间';

-- =====================================================
-- 创建索引
-- =====================================================

-- 唯一索引：用于快速查询文件是否存在
CREATE UNIQUE INDEX IF NOT EXISTS uk_file_hash ON file_hash(file_hash);

-- 创建时间索引：用于按创建时间排序和查询
CREATE INDEX IF NOT EXISTS idx_created_at ON file_hash(created_at DESC);

-- 最后使用时间索引：用于查询最近使用的文件，便于清理过期文件
CREATE INDEX IF NOT EXISTS idx_last_used_at ON file_hash(last_used_at DESC);

-- 上传次数索引：用于统计热门文件
CREATE INDEX IF NOT EXISTS idx_upload_count ON file_hash(upload_count DESC);

-- MIME类型索引：用于按文件类型查询
CREATE INDEX IF NOT EXISTS idx_mime_type ON file_hash(mime_type);

-- =====================================================
-- 索引说明
-- =====================================================
-- uk_file_hash: 唯一索引，用于快速查询文件是否存在
-- idx_created_at: 用于按创建时间排序和查询
-- idx_last_used_at: 用于查询最近使用的文件，便于清理过期文件
-- idx_upload_count: 用于统计热门文件
-- idx_mime_type: 用于按文件类型查询

-- =====================================================
-- 示例查询语句
-- =====================================================

-- 查询文件是否存在
-- SELECT * FROM file_hash WHERE file_hash = 'xxx';

-- 查询最近上传的文件
-- SELECT * FROM file_hash ORDER BY created_at DESC LIMIT 10;

-- 查询最常上传的文件
-- SELECT * FROM file_hash ORDER BY upload_count DESC LIMIT 10;

-- 查询指定时间范围内的文件
-- SELECT * FROM file_hash WHERE created_at BETWEEN '2024-01-01' AND '2024-12-31';

-- 查询特定MIME类型的文件
-- SELECT * FROM file_hash WHERE mime_type LIKE 'image/%';

-- 统计文件总数和总大小
-- SELECT COUNT(*) as total_files, SUM(file_size) as total_size FROM file_hash;

-- 查询重复上传最多的文件
-- SELECT * FROM file_hash WHERE upload_count > 1 ORDER BY upload_count DESC;

-- 删除7天前未使用的文件记录（用于清理）
-- DELETE FROM file_hash WHERE last_used_at < CURRENT_TIMESTAMP - INTERVAL '7 days';
