package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dto.GuideDTO;
import com.gameguide.entity.Guide;
import com.gameguide.entity.GuideTag;
import com.gameguide.exception.BusinessException;
import com.gameguide.mapper.CategoryMapper;
import com.gameguide.mapper.GameMapper;
import com.gameguide.mapper.GuideMapper;
import com.gameguide.mapper.GuideTagMapper;
import com.gameguide.mapper.TagMapper;
import com.gameguide.service.GuideService;
import com.gameguide.vo.GuideVO;
import com.gameguide.vo.TagVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideMapper guideMapper;
    private final GameMapper gameMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final GuideTagMapper guideTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGuide(GuideDTO guideDTO) {
        // 验证游戏是否存在
        if (gameMapper.selectById(guideDTO.getGameId()) == null) {
            throw new BusinessException("游戏不存在");
        }
        
        // 验证分类是否存在
        if (categoryMapper.selectById(guideDTO.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 创建攻略
        Guide guide = new Guide();
        BeanUtils.copyProperties(guideDTO, guide);
        guideMapper.insert(guide);
        
        // 关联标签
        if (!CollectionUtils.isEmpty(guideDTO.getTagIds())) {
            for (Long tagId : guideDTO.getTagIds()) {
                if (tagMapper.selectById(tagId) == null) {
                    throw new BusinessException("标签ID " + tagId + " 不存在");
                }
                GuideTag guideTag = new GuideTag();
                guideTag.setGuideId(guide.getId());
                guideTag.setTagId(tagId);
                guideTagMapper.insert(guideTag);
            }
        }
        
        return guide.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGuide(Long id, GuideDTO guideDTO) {
        Guide existingGuide = guideMapper.selectById(id);
        if (existingGuide == null) {
            throw new BusinessException("攻略不存在");
        }
        
        // 验证游戏是否存在
        if (gameMapper.selectById(guideDTO.getGameId()) == null) {
            throw new BusinessException("游戏不存在");
        }
        
        // 验证分类是否存在
        if (categoryMapper.selectById(guideDTO.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 更新攻略
        Guide guide = new Guide();
        BeanUtils.copyProperties(guideDTO, guide);
        guide.setId(id);
        guideMapper.update(guide);
        
        // 删除旧的标签关联
        guideTagMapper.deleteByGuideId(id);
        
        // 添加新的标签关联
        if (!CollectionUtils.isEmpty(guideDTO.getTagIds())) {
            for (Long tagId : guideDTO.getTagIds()) {
                if (tagMapper.selectById(tagId) == null) {
                    throw new BusinessException("标签ID " + tagId + " 不存在");
                }
                GuideTag guideTag = new GuideTag();
                guideTag.setGuideId(id);
                guideTag.setTagId(tagId);
                guideTagMapper.insert(guideTag);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuide(Long id) {
        Guide existingGuide = guideMapper.selectById(id);
        if (existingGuide == null) {
            throw new BusinessException("攻略不存在");
        }
        
        // 删除标签关联
        guideTagMapper.deleteByGuideId(id);
        
        // 删除攻略
        guideMapper.deleteById(id);
    }

    @Override
    public GuideVO getGuideById(Long id) {
        GuideVO guideVO = guideMapper.selectVOById(id);
        if (guideVO == null) {
            throw new BusinessException("攻略不存在");
        }
        
        // 增加浏览量
        guideMapper.incrementViewCount(id);
        
        // 查询标签
        List<TagVO> tags = tagMapper.selectByGuideId(id).stream()
                .map(tag -> {
                    TagVO tagVO = new TagVO();
                    BeanUtils.copyProperties(tag, tagVO);
                    return tagVO;
                })
                .collect(Collectors.toList());
        guideVO.setTags(tags);
        
        return guideVO;
    }

    @Override
    public PageResult<GuideVO> listGuides(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideMapper.selectAll();
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        
        // 为每个攻略加载标签
        for (GuideVO guideVO : guides) {
            List<TagVO> tags = tagMapper.selectByGuideId(guideVO.getId()).stream()
                    .map(tag -> {
                        TagVO tagVO = new TagVO();
                        BeanUtils.copyProperties(tag, tagVO);
                        return tagVO;
                    })
                    .collect(Collectors.toList());
            guideVO.setTags(tags);
        }
        
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    @Override
    public PageResult<GuideVO> searchGuides(String keyword, Long gameId, Long categoryId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideMapper.searchByConditions(keyword, gameId, categoryId);
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        
        // 为每个攻略加载标签
        for (GuideVO guideVO : guides) {
            List<TagVO> tags = tagMapper.selectByGuideId(guideVO.getId()).stream()
                    .map(tag -> {
                        TagVO tagVO = new TagVO();
                        BeanUtils.copyProperties(tag, tagVO);
                        return tagVO;
                    })
                    .collect(Collectors.toList());
            guideVO.setTags(tags);
        }
        
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    @Override
    public List<GuideVO> listAllGuides() {
        List<GuideVO> guides = guideMapper.selectAll();
        
        // 为每个攻略加载标签
        for (GuideVO guideVO : guides) {
            List<TagVO> tags = tagMapper.selectByGuideId(guideVO.getId()).stream()
                    .map(tag -> {
                        TagVO tagVO = new TagVO();
                        BeanUtils.copyProperties(tag, tagVO);
                        return tagVO;
                    })
                    .collect(Collectors.toList());
            guideVO.setTags(tags);
        }
        
        return guides;
    }
}

