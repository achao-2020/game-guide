package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dto.TagDTO;
import com.gameguide.entity.Tag;
import com.gameguide.exception.BusinessException;
import com.gameguide.mapper.TagMapper;
import com.gameguide.service.TagService;
import com.gameguide.vo.TagVO;
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
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        tagMapper.insert(tag);
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(Long id, TagDTO tagDTO) {
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null) {
            throw new BusinessException("标签不存在");
        }
        
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        tag.setId(id);
        tagMapper.update(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        Tag existingTag = tagMapper.selectById(id);
        if (existingTag == null) {
            throw new BusinessException("标签不存在");
        }
        tagMapper.deleteById(id);
    }

    @Override
    public TagVO getTagById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return convertToVO(tag);
    }

    @Override
    public PageResult<TagVO> listTags(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Tag> tags = tagMapper.selectAll();
        PageInfo<Tag> pageInfo = new PageInfo<>(tags);
        
        List<TagVO> tagVOList = tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, tagVOList);
    }

    @Override
    public List<TagVO> listAllTags() {
        List<Tag> tags = tagMapper.selectAll();
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private TagVO convertToVO(Tag tag) {
        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }
}

