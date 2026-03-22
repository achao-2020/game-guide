package com.gameguide.mapper;

import com.gameguide.entity.GameGuideSpiderTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameGuideSpiderTaskMapper {

    int insert(GameGuideSpiderTask task);

    int updateFinishTime(@Param("id") Long id);

    int updateTitle(@Param("id") Long id, @Param("title") String title);

    GameGuideSpiderTask selectById(@Param("id") Long id);

    List<GameGuideSpiderTask> selectAll();
}

