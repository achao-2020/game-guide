package com.gameguide.controller;

import com.gameguide.common.PageResult;
import com.gameguide.common.Result;
import com.gameguide.dto.TagDTO;
import com.gameguide.service.TagService;
import com.gameguide.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public Result<Long> createTag(@Valid @RequestBody TagDTO tagDTO) {
        Long id = tagService.createTag(tagDTO);
        return Result.success(id);
    }

    @GetMapping
    public Result<PageResult<TagVO>> listTags(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<TagVO> result = tagService.listTags(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<TagVO>> listAllTags() {
        List<TagVO> result = tagService.listAllTags();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<TagVO> getTagById(@PathVariable Long id) {
        TagVO tagVO = tagService.getTagById(id);
        return Result.success(tagVO);
    }

    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Long id, @Valid @RequestBody TagDTO tagDTO) {
        tagService.updateTag(id, tagDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}

