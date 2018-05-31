package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description: 新增设备实体
 * create at 2016/4/18 9:57
 *
 */
public class AddDevice implements Serializable{

    private static final long serialVersionUID = -2233830049017173928L;
    private int resId;// 设备图片Id
    private int itemText;// 设备名称
    private String type;// 设备类型
    private boolean isFirst = false;

    public AddDevice() {}

    public AddDevice(int resId, int itemText) {
        this.resId = resId;
        this.itemText = itemText;
    }

    public AddDevice(int resId, int itemText, String type) {
        this.resId = resId;
        this.itemText = itemText;
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getItemText() {
        return itemText;
    }

    public void setItemText(int itemText) {
        this.itemText = itemText;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
