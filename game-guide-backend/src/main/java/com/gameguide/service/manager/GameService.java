package com.gameguide.service.manager;

import com.gameguide.common.PageResult;
import com.gameguide.dto.GameDTO;
import com.gameguide.vo.GameVO;

import java.util.List;

public interface GameService {
    
    Long createGame(GameDTO gameDTO);
    
    void updateGame(Long id, GameDTO gameDTO);
    
    void deleteGame(Long id);
    
    GameVO getGameById(Long id);
    
    PageResult<GameVO> listGames(Integer pageNum, Integer pageSize);
    
    List<GameVO> listAllGames();
    
    PageResult<GameVO> searchGames(String keyword, Integer pageNum, Integer pageSize);
}

