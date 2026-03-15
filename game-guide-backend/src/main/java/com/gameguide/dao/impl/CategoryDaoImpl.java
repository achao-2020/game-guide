package com.gameguide.dao.impl;

import com.gameguide.dao.CategoryDao;
import com.gameguide.entity.Category;
import com.gameguide.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryDaoImpl implements CategoryDao {

    private final CategoryMapper categoryMapper;

    @Override
    public int insert(Category category) {
        return categoryMapper.insert(category);
    }

    @Override
    public int update(Category category) {
        return categoryMapper.update(category);
    }

    @Override
    public int deleteById(Long id) {
        return categoryMapper.deleteById(id);
    }

    @Override
    public Category selectById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectAll();
    }
}


