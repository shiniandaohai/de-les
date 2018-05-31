package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by pengjiyang on 2016/3/31.
 */
public class CameraList implements Serializable {

    private int resId;
    private String centerText;
    private String rightText;

    public CameraList(){}

    public CameraList(int resId, String centerText, String rightText) {
        this.resId = resId;
        this.centerText = centerText;
        this.rightText = rightText;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }
}
