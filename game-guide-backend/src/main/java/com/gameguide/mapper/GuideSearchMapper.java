package com.gameguide.mapper;

import com.gameguide.entity.GuideSearch;
import com.gameguide.vo.GuideSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideSearchMapper {

    /** 新增或更新全文搜索记录（触发器自动维护 search_vec） */
    int upsert(GuideSearch guideSearch);

    /** 单独更新 embedding 列 */
    int updateEmbedding(@Param("guideId") Long guideId,
                        @Param("embeddingStr") String embeddingStr);

    /** 按 guide_id 删除 */
    int deleteByGuideId(@Param("guideId") Long guideId);

    /** 全文搜索 */
    List<GuideSearchVO> fullTextSearch(@Param("keyword") String keyword,
                                       @Param("gameId")  Long gameId);

    /** 向量相似度检索 */
    List<GuideSearchVO> vectorSearch(@Param("embeddingStr") String embeddingStr,
                                     @Param("gameId")       Long gameId,
                                     @Param("topK")         int  topK);

    /** 查询所有 embedding 为空的记录 */
    List<GuideSearch> selectWithoutEmbedding();
}
