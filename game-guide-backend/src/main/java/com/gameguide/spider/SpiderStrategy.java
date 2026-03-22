package com.gameguide.spider;

import com.gameguide.entity.GameGuideSpider;

import java.util.List;

public interface SpiderStrategy {

    /**
     * 是否支持该网站
     */
    boolean supports(String site);

    /**
     * 执行爬虫（使用默认 selector）
     */
    void spider(String site);

    /**
     * 执行爬虫，使用自定义 selector，爬取后不保存，返回结果列表
     */
    List<GameGuideSpider> spider(String site, String titleSelector, String contentSelector);
}