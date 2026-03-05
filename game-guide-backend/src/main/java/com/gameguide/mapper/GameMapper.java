package com.gameguide.mapper;

import com.gameguide.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper {
    
    int insert(Game game);
    
    int update(Game game);
    
    int deleteById(@Param("id") Long id);
    
    Game selectById(@Param("id") Long id);
    
    List<Game> selectAll();
    
    List<Game> searchByKeyword(@Param("keyword") String keyword);
}

