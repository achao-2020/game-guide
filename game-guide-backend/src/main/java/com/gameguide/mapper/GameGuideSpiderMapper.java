package com.gameguide.mapper;

import com.gameguide.entity.GameGuideSpider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameGuideSpiderMapper {

    int insert(GameGuideSpider gameGuideSpider);

    int deleteById(@Param("id") Long id);

    GameGuideSpider selectById(@Param("id") Long id);

    List<GameGuideSpider> selectAll();

    List<GameGuideSpider> selectByKeyword(@Param("keyword") String keyword);

    GameGuideSpider selectBySourceUrl(@Param("sourceUrl") String sourceUrl);

    List<GameGuideSpider> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    long countAll();
}

