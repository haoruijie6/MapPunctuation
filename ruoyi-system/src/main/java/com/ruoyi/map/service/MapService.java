package com.ruoyi.map.service;

import com.ruoyi.map.domain.dto.ImagePuancuationInformationDto;
import com.ruoyi.map.domain.po.TImagePuancuationInformationPo;

public interface MapService {

    /**
     * 添加线路图
     * @param image 图片和标点对象
     * @return 结果
     */
    int addImageAndSgin(ImagePuancuationInformationDto image);
}
