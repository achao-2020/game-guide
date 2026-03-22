package com.gameguide.dao.impl;

import com.gameguide.dao.GameGuideSpiderTaskDao;
import com.gameguide.entity.GameGuideSpiderTask;
import com.gameguide.mapper.GameGuideSpiderTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameGuideSpiderTaskDaoImpl implements GameGuideSpiderTaskDao {

    private final GameGuideSpiderTaskMapper gameGuideSpiderTaskMapper;

    @Override
    public int insert(GameGuideSpiderTask task) {
        return gameGuideSpiderTaskMapper.insert(task);
    }

    @Override
    public int updateFinishTime(Long id) {
        return gameGuideSpiderTaskMapper.updateFinishTime(id);
    }

    @Override
    public int updateTitle(Long id, String title) {
        return gameGuideSpiderTaskMapper.updateTitle(id, title);
    }

    @Override
    public GameGuideSpiderTask selectById(Long id) {
        return gameGuideSpiderTaskMapper.selectById(id);
    }

    @Override
    public List<GameGuideSpiderTask> selectAll() {
        return gameGuideSpiderTaskMapper.selectAll();
    }
}

