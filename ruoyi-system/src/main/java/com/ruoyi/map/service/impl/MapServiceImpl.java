package com.ruoyi.map.service.impl;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.map.domain.dto.ImagePuancuationInformationDto;
import com.ruoyi.map.domain.po.TImagePuancuationInformationPo;
import com.ruoyi.map.mapper.ImagePuancuationInformationMapper;
import com.ruoyi.map.mapper.PunctuationInformationMapper;
import com.ruoyi.map.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 线路图
 */
@Service
public class MapServiceImpl implements MapService {

    @Autowired
    private PunctuationInformationMapper punctuationInformationMapper;

    @Autowired
    private ImagePuancuationInformationMapper imagePuancuationInformationMapper;
    /**
     * 添加线路图
     * @param image 图片和标点对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addImageAndSgin(ImagePuancuationInformationDto image) {
        image.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        image.setCreateTime(DateUtils.getNowDate());
        int i = punctuationInformationMapper.insertTPunctuationInformation(image.gettPunctuationInformationPoList());
        System.err.println("添加标点数量"+i);
        //将线图片存入数据库
        return imagePuancuationInformationMapper.insertTImagePuancuationInformation(image);
    }

    /**
     * 查询图片
     * @return 结果
     */
    @Override
    public List<ImagePuancuationInformationDto> queryImageAndSgin(ImagePuancuationInformationDto image) {
        return imagePuancuationInformationMapper.queryImageAndSgin(String.valueOf(SecurityUtils.getUserId()),image);
    }

    @Override
    public int deleteImageAndSgin(String id) {
        punctuationInformationMapper.deleteSgin(id);
        return imagePuancuationInformationMapper.deleteImage(id);
    }
}
