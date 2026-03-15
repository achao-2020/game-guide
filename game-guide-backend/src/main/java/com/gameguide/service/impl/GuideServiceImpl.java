package com.gameguide.service.impl;

import com.gameguide.common.PageResult;
import com.gameguide.dao.CategoryDao;
import com.gameguide.dao.GameDao;
import com.gameguide.dao.GuideDao;
import com.gameguide.dao.GuideTagDao;
import com.gameguide.dao.TagDao;
import com.gameguide.dto.GuideDTO;
import com.gameguide.entity.Guide;
import com.gameguide.entity.GuideTag;
import com.gameguide.exception.BusinessException;
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

    private final GuideDao guideDao;
    private final GameDao gameDao;
    private final CategoryDao categoryDao;
    private final TagDao tagDao;
    private final GuideTagDao guideTagDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGuide(GuideDTO guideDTO) {
        if (gameDao.selectById(guideDTO.getGameId()) == null) {
            throw new BusinessException("游戏不存在");
        }
        if (categoryDao.selectById(guideDTO.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 创建 guide 记录
        Guide guide = new Guide();
        guide.setGameId(guideDTO.getGameId());
        guide.setCategoryId(guideDTO.getCategoryId());
        guide.setTitle(guideDTO.getTitle());
        guide.setContent(guideDTO.getContent());
        guideDao.insert(guide);
        
        // 关联标签
        saveTagRelations(guide.getId(), guideDTO.getTagIds());
        
        return guide.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGuide(Long guideId, GuideDTO guideDTO) {
        Guide existingGuide = guideDao.selectById(guideId);
        if (existingGuide == null) {
            throw new BusinessException("攻略不存在");
        }
        if (gameDao.selectById(guideDTO.getGameId()) == null) {
            throw new BusinessException("游戏不存在");
        }
        if (categoryDao.selectById(guideDTO.getCategoryId()) == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 更新 guide 记录
        Guide guide = new Guide();
        guide.setId(guideId);
        guide.setGameId(guideDTO.getGameId());
        guide.setCategoryId(guideDTO.getCategoryId());
        guide.setTitle(guideDTO.getTitle());
        guide.setContent(guideDTO.getContent());
        guideDao.update(guide);

        // 更新标签关联
        guideTagDao.deleteByGuideId(guideId);
        saveTagRelations(guideId, guideDTO.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuide(Long guideId) {
        Guide existingGuide = guideDao.selectById(guideId);
        if (existingGuide == null) {
            throw new BusinessException("攻略不存在");
        }
        guideTagDao.deleteByGuideId(guideId);
        guideDao.deleteById(guideId);
    }

    @Override
    public GuideVO getGuideById(Long guideId) {
        GuideVO guideVO = guideDao.selectVOById(guideId);
        if (guideVO == null) {
            throw new BusinessException("攻略不存在");
        }
        guideDao.incrementViewCount(guideId);
        guideVO.setTags(loadTags(guideId));
        return guideVO;
    }

    @Override
    public PageResult<GuideVO> listGuides(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideDao.selectAll();
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        loadTagsForList(guides);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    @Override
    public PageResult<GuideVO> searchGuides(String keyword, Long gameId, Long categoryId,
                                            Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideDao.searchByConditions(keyword, gameId, categoryId);
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        loadTagsForList(guides);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    @Override
    public List<GuideVO> listAllGuides() {
        List<GuideVO> guides = guideDao.selectAll();
        loadTagsForList(guides);
        return guides;
    }

    @Override
    public PageResult<GuideVO> listPublishedGuides(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideDao.selectAll();
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        loadTagsForList(guides);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    @Override
    public PageResult<GuideVO> searchPublishedGuides(String keyword, Long gameId, Long categoryId,
                                                     Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GuideVO> guides = guideDao.searchByConditions(keyword, gameId, categoryId);
        PageInfo<GuideVO> pageInfo = new PageInfo<>(guides);
        loadTagsForList(guides);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, guides);
    }

    // ==================== private helpers ====================

    private void saveTagRelations(Long guideId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) return;
        for (Long tagId : tagIds) {
            if (tagDao.selectById(tagId) == null) {
                throw new BusinessException("标签ID " + tagId + " 不存在");
            }
            GuideTag guideTag = new GuideTag();
            guideTag.setGuideId(guideId);
            guideTag.setTagId(tagId);
            guideTagDao.insert(guideTag);
        }
    }

    private List<TagVO> loadTags(Long guideId) {
        return tagDao.selectByGuideId(guideId).stream()
                    .map(tag -> {
                        TagVO tagVO = new TagVO();
                        BeanUtils.copyProperties(tag, tagVO);
                        return tagVO;
                    })
                    .collect(Collectors.toList());
        }
        
    private void loadTagsForList(List<GuideVO> guides) {
        for (GuideVO guideVO : guides) {
            guideVO.setTags(loadTags(guideVO.getId()));
        }
    }
}
