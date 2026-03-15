package com.gameguide.dao;

import com.gameguide.entity.Tag;

import java.util.List;

/**
 * 标签数据访问层接口
 */
public interface TagDao {

    int insert(Tag tag);

    int update(Tag tag);

    int deleteById(Long id);

    Tag selectById(Long id);

    List<Tag> selectAll();

    List<Tag> selectByGuideId(Long guideId);
}


