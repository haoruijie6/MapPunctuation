package com.ruoyi.map.domain.po;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 线路图信息对象 t_image_puancuation_information
 *
 * @author haoruijei
 * @date 2022-07-09
 */
public class TImagePuancuationInformationPo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 线路图id
     */
    private String id;

    /**
     * 线路图名称
     */
    private String imageName;

    /**
     * 线路图路径
     */
    private String imageUrl;

    /**
     * 线路图宽度
     */
    private String imageWidth;

    /**
     * 线路图高度
     */
    private String imageHeigth;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageHeigth(String imageHeigth) {
        this.imageHeigth = imageHeigth;
    }

    public String getImageHeigth() {
        return imageHeigth;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("imageName", getImageName())
                .append("imageUrl", getImageUrl())
                .append("imageWidth", getImageWidth())
                .append("imageHeigth", getImageHeigth())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
