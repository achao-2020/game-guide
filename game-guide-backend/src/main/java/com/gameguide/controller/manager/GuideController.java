package com.gameguide.controller.manager;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dto.GuideDTO;
import com.gameguide.service.manager.GuideService;
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

    /**
     * 新建攻略
     */
    @PostMapping
    public Result<Long> createGuide(@Valid @RequestBody GuideDTO guideDTO) {
        Long guideId = guideService.createGuide(guideDTO);
        return Result.success(guideId);
    }

    /**
     * 后台：分页查询所有攻略
     */
    @GetMapping
    public Result<PageResult<GuideVO>> listGuides(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.listGuides(pageNum, pageSize));
    }

    /**
     * 后台：查询所有攻略（不分页）
     */
    @GetMapping("/all")
    public Result<List<GuideVO>> listAllGuides() {
        return Result.success(guideService.listAllGuides());
    }

    /**
     * 后台：获取攻略详情
     */
    @GetMapping("/{guideId}")
    public Result<GuideVO> getGuideById(@PathVariable Long guideId) {
        return Result.success(guideService.getGuideById(guideId));
    }

    /**
     * 后台：搜索攻略
     */
    @GetMapping("/search")
    public Result<PageResult<GuideVO>> searchGuides(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.searchGuides(keyword, gameId, categoryId, pageNum, pageSize));
    }

    /**
     * 编辑攻略
     */
    @PutMapping("/{guideId}")
    public Result<Void> updateGuide(@PathVariable Long guideId,
                                    @Valid @RequestBody GuideDTO guideDTO) {
        guideService.updateGuide(guideId, guideDTO);
        return Result.success();
    }

    /**
     * 删除攻略
     */
    @DeleteMapping("/{guideId}")
    public Result<Void> deleteGuide(@PathVariable Long guideId) {
        guideService.deleteGuide(guideId);
        return Result.success();
    }

    /**
     * 前台：分页查询所有攻略
     */
    @GetMapping("/public/list")
    public Result<PageResult<GuideVO>> listPublishedGuides(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.listPublishedGuides(pageNum, pageSize));
    }

    /**
     * 前台：获取攻略详情
     */
    @GetMapping("/public/{guideId}")
    public Result<GuideVO> getPublishedGuideById(@PathVariable Long guideId) {
        return Result.success(guideService.getGuideById(guideId));
    }

    /**
     * 前台：搜索攻略
     */
    @GetMapping("/public/search")
    public Result<PageResult<GuideVO>> searchPublishedGuides(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.searchPublishedGuides(keyword, gameId, categoryId, pageNum, pageSize));
    }
}
