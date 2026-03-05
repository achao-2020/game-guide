package com.gameguide.service;

import com.gameguide.common.PageResult;
import com.gameguide.dto.CategoryDTO;
import com.gameguide.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    
    Long createCategory(CategoryDTO categoryDTO);
    
    void updateCategory(Long id, CategoryDTO categoryDTO);
    
    void deleteCategory(Long id);
    
    CategoryVO getCategoryById(Long id);
    
    PageResult<CategoryVO> listCategories(Integer pageNum, Integer pageSize);
    
    List<CategoryVO> listAllCategories();
}

