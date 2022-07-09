package com.ruoyi.map.mapper;

import com.ruoyi.map.domain.po.TPunctuationInformationPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PunctuationInformationMapper {
    /**
     * 查询标点基本信息
     *
     * @param id 标点基本信息主键
     * @return 标点基本信息
     */
    public TPunctuationInformationPo selectTPunctuationInformationById(String id);

    /**
     * 查询标点基本信息列表
     *
     * @param tPunctuationInformation 标点基本信息
     * @return 标点基本信息集合
     */
    public List<TPunctuationInformationPo> selectTPunctuationInformationList(TPunctuationInformationPo tPunctuationInformation);

    /**
     * 新增标点基本信息
     *
     * @param tPunctuationInformation 标点基本信息
     * @return 结果
     */
    public int insertTPunctuationInformation(@Param("tPunctuationInformation") List<TPunctuationInformationPo> tPunctuationInformation);

    /**
     * 修改标点基本信息
     *
     * @param tPunctuationInformation 标点基本信息
     * @return 结果
     */
    public int updateTPunctuationInformation(TPunctuationInformationPo tPunctuationInformation);

    /**
     * 删除标点基本信息
     *
     * @param id 标点基本信息主键
     * @return 结果
     */
    public int deleteTPunctuationInformationById(String id);

    /**
     * 批量删除标点基本信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTPunctuationInformationByIds(String[] ids);

}
