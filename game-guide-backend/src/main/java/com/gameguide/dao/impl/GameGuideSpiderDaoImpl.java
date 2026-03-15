package com.gameguide.dao.impl;

import com.gameguide.dao.GameGuideSpiderDao;
import com.gameguide.entity.GameGuideSpider;
import com.gameguide.mapper.GameGuideSpiderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameGuideSpiderDaoImpl implements GameGuideSpiderDao {

    private final GameGuideSpiderMapper gameGuideSpiderMapper;

    @Override
    public int insert(GameGuideSpider gameGuideSpider) {
        return gameGuideSpiderMapper.insert(gameGuideSpider);
    }

    @Override
    public int deleteById(Long id) {
        return gameGuideSpiderMapper.deleteById(id);
    }

    @Override
    public GameGuideSpider selectById(Long id) {
        return gameGuideSpiderMapper.selectById(id);
    }

    @Override
    public List<GameGuideSpider> selectAll() {
        return gameGuideSpiderMapper.selectAll();
    }

    @Override
    public List<GameGuideSpider> selectByKeyword(String keyword) {
        return gameGuideSpiderMapper.selectByKeyword(keyword);
    }

    @Override
    public GameGuideSpider selectBySourceUrl(String sourceUrl) {
        return gameGuideSpiderMapper.selectBySourceUrl(sourceUrl);
    }
}

