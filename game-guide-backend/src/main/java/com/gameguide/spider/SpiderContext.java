package com.gameguide.spider;

import com.gameguide.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpiderContext {

    private final List<SpiderStrategy> strategies;

    public SpiderContext(List<SpiderStrategy> strategies) {
        this.strategies = strategies;
    }

    public void execute(String site) {

        for (SpiderStrategy strategy : strategies) {

            if (strategy.supports(site)) {
                strategy.spider();
                return;
            }
        }

        throw new BusinessException("未找到对应爬虫策略:" + site);
    }
}