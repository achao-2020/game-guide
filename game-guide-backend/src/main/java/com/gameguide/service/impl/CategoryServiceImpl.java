package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dao.CategoryDao;
import com.gameguide.dto.CategoryDTO;
import com.gameguide.entity.Category;
import com.gameguide.exception.BusinessException;
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

    private final CategoryDao categoryDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryDao.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryDao.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setId(id);
        categoryDao.update(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Category existingCategory = categoryDao.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }
        categoryDao.deleteById(id);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryDao.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return convertToVO(category);
    }

    @Override
    public PageResult<CategoryVO> listCategories(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category> categories = categoryDao.selectAll();
        PageInfo<Category> pageInfo = new PageInfo<>(categories);
        List<CategoryVO> categoryVOList = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, categoryVOList);
    }

    @Override
    public List<CategoryVO> listAllCategories() {
        return categoryDao.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private CategoryVO convertToVO(Category category) {
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }
}
