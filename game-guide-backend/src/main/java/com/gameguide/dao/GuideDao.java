package com.gameguide.dao;

import com.gameguide.entity.Guide;
import com.gameguide.vo.GuideVO;

import java.util.List;

/**
 * 攻略数据访问层接口
 */
public interface GuideDao {

    int insert(Guide guide);

    int update(Guide guide);

    int deleteById(Long id);

    Guide selectById(Long id);

    GuideVO selectVOById(Long id);

    /** 查询所有攻略 */
    List<GuideVO> selectAll();

    List<GuideVO> searchByConditions(String keyword, Long gameId, Long categoryId);

    int incrementViewCount(Long id);
}
