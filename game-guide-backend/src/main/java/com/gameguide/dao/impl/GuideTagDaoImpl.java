package com.gameguide.dao.impl;

import com.gameguide.dao.GuideTagDao;
import com.gameguide.entity.GuideTag;
import com.gameguide.mapper.GuideTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuideTagDaoImpl implements GuideTagDao {

    private final GuideTagMapper guideTagMapper;

    @Override
    public int insert(GuideTag guideTag) {
        return guideTagMapper.insert(guideTag);
    }

    @Override
    public int deleteByGuideId(Long guideId) {
        return guideTagMapper.deleteByGuideId(guideId);
    }

    @Override
    public int deleteByTagId(Long tagId) {
        return guideTagMapper.deleteByTagId(tagId);
    }

    @Override
    public List<GuideTag> selectByGuideId(Long guideId) {
        return guideTagMapper.selectByGuideId(guideId);
    }
}


