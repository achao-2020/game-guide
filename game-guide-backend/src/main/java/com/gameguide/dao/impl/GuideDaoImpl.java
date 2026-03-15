package com.gameguide.dao.impl;

import com.gameguide.dao.GuideDao;
import com.gameguide.entity.Guide;
import com.gameguide.mapper.GuideMapper;
import com.gameguide.vo.GuideVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GuideDaoImpl implements GuideDao {

    private final GuideMapper guideMapper;

    @Override
    public int insert(Guide guide) {
        return guideMapper.insert(guide);
    }

    @Override
    public int update(Guide guide) {
        return guideMapper.update(guide);
    }

    @Override
    public int deleteById(Long id) {
        return guideMapper.deleteById(id);
    }


    @Override
    public Guide selectById(Long id) {
        return guideMapper.selectById(id);
    }

    @Override
    public GuideVO selectVOById(Long id) {
        return guideMapper.selectVOById(id);
    }

    @Override
    public List<GuideVO> selectAll() {
        return guideMapper.selectAll();
    }


    @Override
    public List<GuideVO> searchByConditions(String keyword, Long gameId, Long categoryId) {
        return guideMapper.searchByConditions(keyword, gameId, categoryId);
    }

    @Override
    public int incrementViewCount(Long id) {
        return guideMapper.incrementViewCount(id);
    }
}


