package com.gameguide.service.spider.impl;

import com.gameguide.entity.GameGuideSpider;
import com.gameguide.service.spider.SpiderService;
import com.gameguide.spider.SpiderContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpiderServiceImpl implements SpiderService {

    @Autowired
    private SpiderContext spiderContext;

    @Override
    public void crawl(String site) {
        spiderContext.execute(site);
    }

    @Override
    public List<GameGuideSpider> preview(String site, String titleSelector, String contentSelector) {
        return spiderContext.preview(site, titleSelector, contentSelector);
    }
}
