package com.gameguide.controller.manager;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dto.GameDTO;
import com.gameguide.service.manager.GameService;
import com.gameguide.vo.GameVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public Result<Long> createGame(@Valid @RequestBody GameDTO gameDTO) {
        Long id = gameService.createGame(gameDTO);
        return Result.success(id);
    }

    @GetMapping
    public Result<PageResult<GameVO>> listGames(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<GameVO> result = gameService.listGames(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<GameVO>> listAllGames() {
        List<GameVO> result = gameService.listAllGames();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<GameVO> getGameById(@PathVariable Long id) {
        GameVO gameVO = gameService.getGameById(id);
        return Result.success(gameVO);
    }

    @GetMapping("/search")
    public Result<PageResult<GameVO>> searchGames(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<GameVO> result = gameService.searchGames(keyword, pageNum, pageSize);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> updateGame(@PathVariable Long id, @Valid @RequestBody GameDTO gameDTO) {
        gameService.updateGame(id, gameDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return Result.success();
    }
}

