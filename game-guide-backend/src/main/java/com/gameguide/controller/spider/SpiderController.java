package com.gameguide.controller.spider;

import com.gameguide.service.spider.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spider")
public class SpiderController {

    @Autowired
    private SpiderService spiderService;

    @GetMapping("/start")
    public String start(@RequestParam String site) {
        spiderService.crawl(site);

        return "spider started";
    }
}