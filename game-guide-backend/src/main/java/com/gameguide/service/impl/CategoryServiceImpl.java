package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dto.CategoryDTO;
import com.gameguide.entity.Category;
import com.gameguide.exception.BusinessException;
import com.gameguide.mapper.CategoryMapper;
import com.gameguide.service.CategoryService;
import com.gameguide.vo.CategoryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setId(id);
        categoryMapper.update(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return convertToVO(category);
    }

    @Override
    public PageResult<CategoryVO> listCategories(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category> categories = categoryMapper.selectAll();
        PageInfo<Category> pageInfo = new PageInfo<>(categories);
        
        List<CategoryVO> categoryVOList = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, categoryVOList);
    }

    @Override
    public List<CategoryVO> listAllCategories() {
        List<Category> categories = categoryMapper.selectAll();
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }
}

