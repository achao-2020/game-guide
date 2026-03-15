package com.gameguide.dao;

import com.gameguide.entity.GuideTag;

import java.util.List;

/**
 * 攻略标签关联数据访问层接口
 */
public interface GuideTagDao {

    int insert(GuideTag guideTag);

    int deleteByGuideId(Long guideId);

    int deleteByTagId(Long tagId);

    List<GuideTag> selectByGuideId(Long guideId);
}


