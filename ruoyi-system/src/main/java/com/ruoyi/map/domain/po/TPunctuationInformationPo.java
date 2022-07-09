package com.ruoyi.map.domain.po;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;


/**
 * 标点基本信息对象 t_punctuation_information
 *
 * @author haoruijie
 * @date 2022-07-09
 */
public class TPunctuationInformationPo {
    private static final long serialVersionUID = 1L;

    /**
     * 标点id
     */
    private String id;

    /**
     * 图片id
     */
    private String imageId;

    /**
     * 标点相对于图片的x轴距离
     */
    private String relativeToPictureX;

    /**
     * 标点相对于图片的y轴距离
     */
    private String relativeToPictureY;

    /**
     * 标点宽度
     */
    private String signWidth;

    /**
     * 标点高度
     */
    private String signHeight;

    /**
     * 标点元素
     */
    private String punctuationElement;

    /**
     * 标点内容
     */
    private String signContent;

    /**
     * 标点形状
     */
    private String signShape;

    /**
     * 标点颜色
     */
    private String signColor;

    /**
     * 标点状态0正常 1 删除
     */
    private Long state;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setRelativeToPictureX(String relativeToPictureX) {
        this.relativeToPictureX = relativeToPictureX;
    }

    public String getRelativeToPictureX() {
        return relativeToPictureX;
    }

    public void setRelativeToPictureY(String relativeToPictureY) {
        this.relativeToPictureY = relativeToPictureY;
    }

    public String getRelativeToPictureY() {
        return relativeToPictureY;
    }

    public void setSignWidth(String signWidth) {
        this.signWidth = signWidth;
    }

    public String getSignWidth() {
        return signWidth;
    }

    public void setSignHeight(String signHeight) {
        this.signHeight = signHeight;
    }

    public String getSignHeight() {
        return signHeight;
    }

    public void setSignContent(String signContent) {
        this.signContent = signContent;
    }

    public String getSignContent() {
        return signContent;
    }

    public void setSignShape(String signShape) {
        this.signShape = signShape;
    }

    public String getSignShape() {
        return signShape;
    }

    public void setSignColor(String signColor) {
        this.signColor = signColor;
    }

    public String getSignColor() {
        return signColor;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public Long getState() {
        return state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("imageId", getImageId())
                .append("relativeToPictureX", getRelativeToPictureX())
                .append("relativeToPictureY", getRelativeToPictureY())
                .append("signWidth", getSignWidth())
                .append("signHeight", getSignHeight())
                .append("signContent", getSignContent())
                .append("signShape", getSignShape())
                .append("signColor", getSignColor())
                .append("state", getState())
                .toString();
    }

    public String getPunctuationElement() {
        return punctuationElement;
    }

    public void setPunctuationElement(String punctuationElement) {
        this.punctuationElement = punctuationElement;
    }
}
