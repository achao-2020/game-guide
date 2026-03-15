package com.gameguide.controller.manager;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.service.manager.GuideService;
import com.gameguide.vo.GuideSearchVO;
import com.gameguide.vo.GuideVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台公开攻略接口
 * 仅返回已发布版本的攻略数据
 */
@RestController
@RequestMapping("/api/public/guides")
@RequiredArgsConstructor
public class PublicGuideController {

    private final GuideService guideService;

    /**
     * 前台：分页查询已发布攻略
     */
    @GetMapping
    public Result<PageResult<GuideVO>> listPublishedGuides(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.listPublishedGuides(pageNum, pageSize));
    }

    /**
     * 前台：按条件搜索已发布攻略（ILIKE 模糊搜索）
     */
    @GetMapping("/search")
    public Result<PageResult<GuideVO>> searchPublishedGuides(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long gameId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.searchPublishedGuides(
                keyword, gameId, categoryId, pageNum, pageSize));
    }

    /**
     * 前台：全文搜索攻略（PostgreSQL tsvector，含高亮摘要）
     */
    @GetMapping("/fulltext")
    public Result<PageResult<GuideSearchVO>> fullTextSearch(
            @RequestParam String keyword,
            @RequestParam(required = false) Long gameId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guideService.fullTextSearchGuides(keyword, gameId, pageNum, pageSize));
    }
}
