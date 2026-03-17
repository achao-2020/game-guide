package com.gameguide.dao;

import com.gameguide.entity.GuideSearch;
import com.gameguide.vo.GuideSearchVO;

import java.util.List;

public interface GuideSearchDao {

    void upsert(GuideSearch guideSearch);

    /** 更新 embedding 向量（单独更新，避免每次都重算） */
    void updateEmbedding(Long guideId, String embeddingStr);

    void deleteByGuideId(Long guideId);

    /** 全文搜索（tsvector + ILIKE 兜底） */
    List<GuideSearchVO> fullTextSearch(String keyword, Long gameId);

    /** 向量相似度检索 Top-K */
    List<GuideSearchVO> vectorSearch(String embeddingStr, Long gameId, int topK);

    /** 查询所有 embedding 为空的记录（用于批量回填） */
    List<GuideSearch> selectWithoutEmbedding();
}
