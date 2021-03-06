package com.ruoyi.map.mapper;

import com.ruoyi.map.domain.dto.ImagePuancuationInformationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.ruoyi.map.domain.po.TImagePuancuationInformationPo;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ImagePuancuationInformationMapper {

    /**
     * 新增线路图信息
     *
     * @param tImagePuancuationInformation 线路图信息
     * @return 结果
     */
    public int insertTImagePuancuationInformation(TImagePuancuationInformationPo tImagePuancuationInformation);


    /**
     * 查询线路图信息
     * @param userId userId
     * @return 结果
     */
    List<ImagePuancuationInformationDto> queryImageAndSgin(@Param("userId") String userId,
                                                           @Param("image") ImagePuancuationInformationDto image);

    /**
     * 删除图片
     * @param id 图片id
     * @return 结果
     */
    int deleteImage(String id);
}
