package com.gameguide.service;

import com.gameguide.common.PageResult;
import com.gameguide.dto.GuideDTO;
import com.gameguide.vo.GuideVO;

import java.util.List;

public interface GuideService {
    
    Long createGuide(GuideDTO guideDTO);
    
    void updateGuide(Long id, GuideDTO guideDTO);
    
    void deleteGuide(Long id);
    
    GuideVO getGuideById(Long id);
    
    PageResult<GuideVO> listGuides(Integer pageNum, Integer pageSize);
    
    PageResult<GuideVO> searchGuides(String keyword, Long gameId, Long categoryId, Integer pageNum, Integer pageSize);
    
    List<GuideVO> listAllGuides();
}

