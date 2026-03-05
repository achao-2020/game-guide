package com.gameguide.controller;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dto.CategoryDTO;
import com.gameguide.service.CategoryService;
import com.gameguide.vo.CategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Long id = categoryService.createCategory(categoryDTO);
        return Result.success(id);
    }

    @GetMapping
    public Result<PageResult<CategoryVO>> listCategories(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<CategoryVO> result = categoryService.listCategories(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<CategoryVO>> listAllCategories() {
        List<CategoryVO> result = categoryService.listAllCategories();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryVO categoryVO = categoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}

