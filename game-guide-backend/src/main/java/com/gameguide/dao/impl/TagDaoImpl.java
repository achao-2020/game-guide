package com.gameguide.dao.impl;

import com.gameguide.dao.TagDao;
import com.gameguide.entity.Tag;
import com.gameguide.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {

    private final TagMapper tagMapper;

    @Override
    public int insert(Tag tag) {
        return tagMapper.insert(tag);
    }

    @Override
    public int update(Tag tag) {
        return tagMapper.update(tag);
    }

    @Override
    public int deleteById(Long id) {
        return tagMapper.deleteById(id);
    }

    @Override
    public Tag selectById(Long id) {
        return tagMapper.selectById(id);
    }

    @Override
    public List<Tag> selectAll() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Tag> selectByGuideId(Long guideId) {
        return tagMapper.selectByGuideId(guideId);
    }

    @Override
    public Tag selectByName(String name) {
        return tagMapper.selectByName(name);
    }
}


