package com.spritelab.netconnect.model;

public class InformationModel {
    private String textInfo;
    private Integer imgInfo;

    public InformationModel(String textInfo, Integer imgInfo) {
        this.textInfo = textInfo;
        this.imgInfo = imgInfo;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public Integer getImgInfo() {
        return imgInfo;
    }

    public void setTextInfo(String textInfo) {
        this.textInfo = textInfo;
    }

    public void setImgInfo(Integer imgInfo) {
        this.imgInfo = imgInfo;
    }
}
