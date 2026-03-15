package com.gameguide.dao;

import com.gameguide.entity.Game;

import java.util.List;

/**
 * 游戏数据访问层接口
 */
public interface GameDao {

    int insert(Game game);

    int update(Game game);

    int deleteById(Long id);

    Game selectById(Long id);

    List<Game> selectAll();

    List<Game> searchByKeyword(String keyword);
}


