package com.gameguide.service.spider;

import com.gameguide.entity.GameGuideSpider;

import java.util.List;

public interface SpiderService {
    void crawl(String site);

    List<GameGuideSpider> preview(String site, String titleSelector, String contentSelector);
}