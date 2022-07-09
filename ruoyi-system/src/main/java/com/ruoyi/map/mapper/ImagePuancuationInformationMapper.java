package com.ruoyi.map.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.ruoyi.map.domain.po.TImagePuancuationInformationPo;
@Mapper
public interface ImagePuancuationInformationMapper {
    /**
     * 查询线路图信息
     *
     * @param id 线路图信息主键
     * @return 线路图信息
     */
    public TImagePuancuationInformationPo selectTImagePuancuationInformationById(String id);

    /**
     * 查询线路图信息列表
     *
     * @param tImagePuancuationInformation 线路图信息
     * @return 线路图信息集合
     */
    public List<TImagePuancuationInformationPo> selectTImagePuancuationInformationList(TImagePuancuationInformationPo tImagePuancuationInformation);

    /**
     * 新增线路图信息
     *
     * @param tImagePuancuationInformation 线路图信息
     * @return 结果
     */
    public int insertTImagePuancuationInformation(TImagePuancuationInformationPo tImagePuancuationInformation);

    /**
     * 修改线路图信息
     *
     * @param tImagePuancuationInformation 线路图信息
     * @return 结果
     */
    public int updateTImagePuancuationInformation(TImagePuancuationInformationPo tImagePuancuationInformation);

    /**
     * 删除线路图信息
     *
     * @param id 线路图信息主键
     * @return 结果
     */
    public int deleteTImagePuancuationInformationById(String id);

    /**
     * 批量删除线路图信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTImagePuancuationInformationByIds(String[] ids);
}
