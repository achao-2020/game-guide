package com.gameguide.dao;

import com.gameguide.entity.FileHash;

/**
 * 文件哈希数据访问层接口
 */
public interface FileHashDao {

    FileHash selectByFileHash(String fileHash);

    int insert(FileHash fileHash);

    int updateUsageInfo(String fileHash);
}


