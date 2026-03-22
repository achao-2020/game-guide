package com.gameguide.controller.spider;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dao.GameGuideSpiderDao;
import com.gameguide.dao.GameGuideSpiderTaskDao;
import com.gameguide.entity.GameGuideSpider;
import com.gameguide.entity.GameGuideSpiderTask;
import com.gameguide.service.spider.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/spider")
public class SpiderController {

    @Autowired
    private SpiderService spiderService;

    @Autowired
    private GameGuideSpiderDao gameGuideSpiderDao;

    @Autowired
    private GameGuideSpiderTaskDao gameGuideSpiderTaskDao;

    /**
     * 原有爬取并保存接口（兼容旧逻辑）
     */
    @GetMapping("/start")
    public String start(@RequestParam String site) {
        spiderService.crawl(site);
        return "spider started";
    }

    /**
     * 预览爬取：根据自定义 selector 爬取内容，不保存，返回结果列表
     */
    @GetMapping("/preview")
    public Result<List<GameGuideSpider>> preview(
            @RequestParam String site,
            @RequestParam String titleSelector,
            @RequestParam String contentSelector) {
        List<GameGuideSpider> results = spiderService.preview(site, titleSelector, contentSelector);
        return Result.success(results);
    }

    /**
     * 批量保存爬取结果
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody List<GameGuideSpider> items) {
        if (items == null || items.isEmpty()) {
            return Result.error("保存内容不能为空");
        }
        // 创建任务记录
        GameGuideSpiderTask task = new GameGuideSpiderTask();
        task.setUrl(items.get(0).getSourceUrl());
        task.setPageSize(items.size());
        task.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "-爬取游戏攻略");
        gameGuideSpiderTaskDao.insert(task);
        Long taskId = task.getId();

        boolean titleUpdated = false;
        for (GameGuideSpider item : items) {
            // 去重
            if (gameGuideSpiderDao.selectBySourceUrl(item.getSourceUrl()) != null) {
                continue;
            }
            item.setTaskId(taskId);
            item.setCreateTime(LocalDateTime.now());
            gameGuideSpiderDao.insert(item);
            if (!titleUpdated && item.getTitle() != null) {
                gameGuideSpiderTaskDao.updateTitle(taskId, item.getTitle());
                titleUpdated = true;
            }
        }
        gameGuideSpiderTaskDao.updateFinishTime(taskId);
        return Result.success();
    }

    /**
     * 分页查询已爬取攻略列表
     */
    @GetMapping("/list")
    public Result<PageResult<GameGuideSpider>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        long total = gameGuideSpiderDao.countAll();
        int offset = (pageNum - 1) * pageSize;
        List<GameGuideSpider> list = gameGuideSpiderDao.selectPage(offset, pageSize);
        return Result.success(new PageResult<>(total, pageNum, pageSize, list));
    }

    /**
     * 删除已爬取攻略
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        gameGuideSpiderDao.deleteById(id);
        return Result.success();
    }

    /**
     * 查询单条已爬取攻略详情
     */
    @GetMapping("/{id}")
    public Result<GameGuideSpider> getById(@PathVariable Long id) {
        return Result.success(gameGuideSpiderDao.selectById(id));
    }
}
