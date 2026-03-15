package com.gameguide.mapper;

import com.gameguide.entity.Guide;
import com.gameguide.vo.GuideVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideMapper {
    
    int insert(Guide guide);
    
    int update(Guide guide);
    
    int deleteById(@Param("id") Long id);
    
    Guide selectById(@Param("id") Long id);
    
    GuideVO selectVOById(@Param("id") Long id);
    
    /** 查询所有攻略 */
    List<GuideVO> selectAll();
    
    List<GuideVO> searchByConditions(@Param("keyword") String keyword, 
                                      @Param("gameId") Long gameId, 
                                      @Param("categoryId") Long categoryId);
    
    int incrementViewCount(@Param("id") Long id);
}
