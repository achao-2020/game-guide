package com.gameguide.mapper;

import com.gameguide.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    int insert(Category category);
    
    int update(Category category);
    
    int deleteById(@Param("id") Long id);
    
    Category selectById(@Param("id") Long id);
    
    List<Category> selectAll();
}

