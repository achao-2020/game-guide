package com.gameguide.mapper;

import com.gameguide.entity.GuideSearch;
import com.gameguide.vo.GuideSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideSearchMapper {

    /** 新增或更新全文搜索记录 */
    int upsert(GuideSearch guideSearch);

    /** 按 guide_id 删除 */
    int deleteByGuideId(@Param("guideId") Long guideId);

    /** 全文搜索，返回含高亮摘要的结果 */
    List<GuideSearchVO> fullTextSearch(@Param("keyword") String keyword,
                                       @Param("gameId")  Long gameId);
}

