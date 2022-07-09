package com.ruoyi.map.domain.dto;

import com.ruoyi.map.domain.po.TImagePuancuationInformationPo;
import com.ruoyi.map.domain.po.TPunctuationInformationPo;

import java.util.List;

/**
 * @author haoruijie 添加线路图参数对象
 */
public class ImagePuancuationInformationDto extends TImagePuancuationInformationPo {

    /**
     * 图片中的标点对象
     */
    private List<TPunctuationInformationPo>  tPunctuationInformationPoList;

    public List<TPunctuationInformationPo> gettPunctuationInformationPoList() {
        return tPunctuationInformationPoList;
    }

    public void settPunctuationInformationPoList(List<TPunctuationInformationPo> tPunctuationInformationPoList) {
        this.tPunctuationInformationPoList = tPunctuationInformationPoList;
    }
}
