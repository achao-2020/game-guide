package com.gameguide.spider;

public interface SpiderStrategy {

    /**
     * 是否支持该网站
     */
    boolean supports(String site);

    /**
     * 执行爬虫
     */
    void spider();
}