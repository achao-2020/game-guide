package com.gameguide.dao;

import com.gameguide.entity.GameGuideSpiderTask;

import java.util.List;

/**
 * 爬虫任务数据访问层接口
 */
public interface GameGuideSpiderTaskDao {

    int insert(GameGuideSpiderTask task);

    int updateFinishTime(Long id);

    int updateTitle(Long id, String title);

    GameGuideSpiderTask selectById(Long id);

    List<GameGuideSpiderTask> selectAll();
}

