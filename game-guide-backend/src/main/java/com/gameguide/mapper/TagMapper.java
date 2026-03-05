package com.gameguide.mapper;

import com.gameguide.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    
    int insert(Tag tag);
    
    int update(Tag tag);
    
    int deleteById(@Param("id") Long id);
    
    Tag selectById(@Param("id") Long id);
    
    List<Tag> selectAll();
    
    List<Tag> selectByGuideId(@Param("guideId") Long guideId);
}

