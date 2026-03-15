package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dao.GameDao;
import com.gameguide.dto.GameDTO;
import com.gameguide.entity.Game;
import com.gameguide.exception.BusinessException;
import com.gameguide.service.GameService;
import com.gameguide.vo.GameVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGame(GameDTO gameDTO) {
        Game game = new Game();
        BeanUtils.copyProperties(gameDTO, game);
        gameDao.insert(game);
        return game.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGame(Long id, GameDTO gameDTO) {
        Game existingGame = gameDao.selectById(id);
        if (existingGame == null) {
            throw new BusinessException("游戏不存在");
        }
        Game game = new Game();
        BeanUtils.copyProperties(gameDTO, game);
        game.setId(id);
        gameDao.update(game);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGame(Long id) {
        Game existingGame = gameDao.selectById(id);
        if (existingGame == null) {
            throw new BusinessException("游戏不存在");
        }
        gameDao.deleteById(id);
    }

    @Override
    public GameVO getGameById(Long id) {
        Game game = gameDao.selectById(id);
        if (game == null) {
            throw new BusinessException("游戏不存在");
        }
        return convertToVO(game);
    }

    @Override
    public PageResult<GameVO> listGames(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Game> games = gameDao.selectAll();
        PageInfo<Game> pageInfo = new PageInfo<>(games);
        List<GameVO> gameVOList = games.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, gameVOList);
    }

    @Override
    public List<GameVO> listAllGames() {
        return gameDao.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<GameVO> searchGames(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Game> games = gameDao.searchByKeyword(keyword);
        PageInfo<Game> pageInfo = new PageInfo<>(games);
        List<GameVO> gameVOList = games.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, gameVOList);
    }

    private GameVO convertToVO(Game game) {
        GameVO gameVO = new GameVO();
        BeanUtils.copyProperties(game, gameVO);
        return gameVO;
    }
}
