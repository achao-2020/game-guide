package com.gameguide.dao;

import com.gameguide.entity.Category;

import java.util.List;

/**
 * 分类数据访问层接口
 */
public interface CategoryDao {

    int insert(Category category);

    int update(Category category);

    int deleteById(Long id);

    Category selectById(Long id);

    List<Category> selectAll();
}


