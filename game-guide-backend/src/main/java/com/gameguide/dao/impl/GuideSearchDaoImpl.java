package com.gameguide.dao.impl;

import com.gameguide.dao.GuideSearchDao;
import com.gameguide.entity.GuideSearch;
import com.gameguide.mapper.GuideSearchMapper;
import com.gameguide.vo.GuideSearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuideSearchDaoImpl implements GuideSearchDao {

    private final GuideSearchMapper guideSearchMapper;

    @Override
    public void upsert(GuideSearch guideSearch) {
        guideSearchMapper.upsert(guideSearch);
    }

    @Override
    public void deleteByGuideId(Long guideId) {
        guideSearchMapper.deleteByGuideId(guideId);
    }

    @Override
    public List<GuideSearchVO> fullTextSearch(String keyword, Long gameId) {
        return guideSearchMapper.fullTextSearch(keyword, gameId);
    }
}

