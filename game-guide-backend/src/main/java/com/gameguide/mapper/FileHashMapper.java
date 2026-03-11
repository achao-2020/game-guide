package com.gameguide.mapper;

import com.gameguide.entity.FileHash;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

/**
 * 文件哈希表 Mapper
 */
@Mapper
public interface FileHashMapper {
    
    /**
     * 根据文件哈希值查询文件信息
     */
    @Select("SELECT * FROM file_hash WHERE file_hash = #{fileHash}")
    FileHash selectByFileHash(@Param("fileHash") String fileHash);
    
    /**
     * 插入文件哈希记录
     */
    @Insert("INSERT INTO file_hash (file_hash, file_name, file_url, file_size, mime_type, upload_count, created_at, last_used_at) " +
            "VALUES (#{fileHash}, #{fileName}, #{fileUrl}, #{fileSize}, #{mimeType}, 1, NOW(), NOW())")
    int insert(FileHash fileHash);
    
    /**
     * 更新文件使用信息（增加上传次数和更新最后使用时间）
     */
    @Update("UPDATE file_hash SET upload_count = upload_count + 1, last_used_at = NOW() WHERE file_hash = #{fileHash}")
    int updateUsageInfo(@Param("fileHash") String fileHash);
}

