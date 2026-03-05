package com.gameguide.controller;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dto.GuideDTO;
import com.gameguide.service.GuideService;
import com.gameguide.vo.GuideVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping
    public Result<Long> createGuide(@Valid @RequestBody GuideDTO guideDTO) {
        Long id = guideService.createGuide(guideDTO);
        return Result.success(id);
    }

    @GetMapping
    public Result<PageResult<GuideVO>> listGuides(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<GuideVO> result = guideService.listGuides(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<GuideVO>> listAllGuides() {
        List<GuideVO> result = guideService.listAllGuides();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<GuideVO> getGuideById(@PathVariable Long id) {
        GuideVO guideVO = guideService.getGuideById(id);
        return Result.success(guideVO);
    }

    @GetMapping("/search")
    public Result<PageResult<GuideVO>> searchGuides(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<GuideVO> result = guideService.searchGuides(keyword, gameId, categoryId, pageNum, pageSize);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> updateGuide(@PathVariable Long id, @Valid @RequestBody GuideDTO guideDTO) {
        guideService.updateGuide(id, guideDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
        return Result.success();
    }
}

