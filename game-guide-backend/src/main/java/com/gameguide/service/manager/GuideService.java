package com.gameguide.service.manager;

import com.gameguide.common.PageResult;
import com.gameguide.dto.GuideDTO;
import com.gameguide.vo.GuideSearchVO;
import com.gameguide.vo.GuideVO;

import java.util.List;

public interface GuideService {
    
    /**
     * 新建攻略
     */
    Long createGuide(GuideDTO guideDTO);
    
    /**
     * 编辑攻略
     */
    void updateGuide(Long guideId, GuideDTO guideDTO);
    
    /**
     * 删除攻略
     */
    void deleteGuide(Long guideId);
    
    /**
     * 获取攻略详情
     */
    GuideVO getGuideById(Long guideId);
    
    /**
     * 后台：分页查询所有攻略
     */
    PageResult<GuideVO> listGuides(Integer pageNum, Integer pageSize);
    
    /**
     * 后台：按条件搜索攻略
     */
    PageResult<GuideVO> searchGuides(String keyword, Long gameId, Long categoryId,
                                     Integer pageNum, Integer pageSize);
    
    /**
     * 后台：查询所有攻略（不分页，用于下拉选择等）
     */
    List<GuideVO> listAllGuides();

    /**
     * 前台：分页查询所有攻略
     */
    PageResult<GuideVO> listPublishedGuides(Integer pageNum, Integer pageSize);

    /**
     * 前台：按条件搜索攻略
     */
    PageResult<GuideVO> searchPublishedGuides(String keyword, Long gameId, Long categoryId,
                                              Integer pageNum, Integer pageSize);

    /**
     * 前台：全文搜索攻略（基于 PostgreSQL tsvector）
     */
    PageResult<GuideSearchVO> fullTextSearchGuides(String keyword, Long gameId,
                                              Integer pageNum, Integer pageSize);
}
