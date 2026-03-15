package com.gameguide.dao.impl;

import com.gameguide.dao.FileHashDao;
import com.gameguide.entity.FileHash;
import com.gameguide.mapper.FileHashMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileHashDaoImpl implements FileHashDao {

    private final FileHashMapper fileHashMapper;

    @Override
    public FileHash selectByFileHash(String fileHash) {
        return fileHashMapper.selectByFileHash(fileHash);
    }

    @Override
    public int insert(FileHash fileHash) {
        return fileHashMapper.insert(fileHash);
    }

    @Override
    public int updateUsageInfo(String fileHash) {
        return fileHashMapper.updateUsageInfo(fileHash);
    }
}


