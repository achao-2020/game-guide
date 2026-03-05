package com.gameguide.mapper;

import com.gameguide.entity.GuideTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideTagMapper {
    
    int insert(GuideTag guideTag);
    
    int deleteByGuideId(@Param("guideId") Long guideId);
    
    int deleteByTagId(@Param("tagId") Long tagId);
    
    List<GuideTag> selectByGuideId(@Param("guideId") Long guideId);
}

