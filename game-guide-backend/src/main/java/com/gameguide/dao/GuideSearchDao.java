package com.gameguide.dao;

import com.gameguide.entity.GuideSearch;
import com.gameguide.vo.GuideSearchVO;

import java.util.List;

public interface GuideSearchDao {

    void upsert(GuideSearch guideSearch);

    void deleteByGuideId(Long guideId);

    List<GuideSearchVO> fullTextSearch(String keyword, Long gameId);
}

