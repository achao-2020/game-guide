package com.gameguide.dao;

import com.gameguide.entity.GameGuideSpider;

import java.util.List;

/**
 * 爬虫攻略数据访问层接口
 */
public interface GameGuideSpiderDao {

    int insert(GameGuideSpider gameGuideSpider);

    int deleteById(Long id);

    GameGuideSpider selectById(Long id);

    List<GameGuideSpider> selectAll();

    List<GameGuideSpider> selectByKeyword(String keyword);

    GameGuideSpider selectBySourceUrl(String sourceUrl);
}

