package com.gameguide.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * game_guide_spider_task 表：爬虫任务记录
 */
@Data
public class GameGuideSpiderTask {
    private Long id;
    /** 爬取起始 URL（列表页基础地址） */
    private String url;
    /** 分页大小（endPage - startPage + 1） */
    private Integer pageSize;
    /** 任务标题：取第一篇文章标题，若无则为 {时间}-{爬取游戏攻略} */
    private String title;
    /** 任务创建时间 */
    private LocalDateTime createTime;
    /** 任务完成时间 */
    private LocalDateTime finishTime;
}

