package com.gameguide.service;

import com.gameguide.common.PageResult;
import com.gameguide.dto.TagDTO;
import com.gameguide.vo.TagVO;

import java.util.List;

public interface TagService {
    
    Long createTag(TagDTO tagDTO);
    
    void updateTag(Long id, TagDTO tagDTO);
    
    void deleteTag(Long id);
    
    TagVO getTagById(Long id);
    
    PageResult<TagVO> listTags(Integer pageNum, Integer pageSize);
    
    List<TagVO> listAllTags();
}

