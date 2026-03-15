package com.gameguide.dao.impl;

import com.gameguide.dao.GameDao;
import com.gameguide.entity.Game;
import com.gameguide.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameDaoImpl implements GameDao {

    private final GameMapper gameMapper;

    @Override
    public int insert(Game game) {
        return gameMapper.insert(game);
    }

    @Override
    public int update(Game game) {
        return gameMapper.update(game);
    }

    @Override
    public int deleteById(Long id) {
        return gameMapper.deleteById(id);
    }

    @Override
    public Game selectById(Long id) {
        return gameMapper.selectById(id);
    }

    @Override
    public List<Game> selectAll() {
        return gameMapper.selectAll();
    }

    @Override
    public List<Game> searchByKeyword(String keyword) {
        return gameMapper.searchByKeyword(keyword);
    }
}


