package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description: 家具列表二级列表实体类
 * create at 2016/4/15 14:12
 *
 */
public class FurnitureChild implements Serializable {

    // TODO 此处图片应从服务器获取,故resId的类型最终应为String
    private int resId;
    private String childTitle;//子条目标题
    private boolean isOnline;// 是否在线
    private String comment;// 备注
    private boolean isData = true;// 是否是数据(如果是数据，则隐藏当前条目的按钮，否则隐藏数据布局并显示按钮)
    private String areaId;
    private String areaName;
    private Device device;

    public FurnitureChild() {}

    public FurnitureChild(int resId, String childTitle, String comment, boolean isOnline) {
        this.resId = resId;
        this.childTitle = childTitle;
        this.isOnline = isOnline;
        this.comment = comment;
    }

    public FurnitureChild(int resId, String childTitle, String areaId, String areaName, String comment, boolean isOnline, Device device) {
        this.resId = resId;
        this.childTitle = childTitle;
        this.isOnline = isOnline;
        this.comment = comment;
        this.areaId = areaId;
        this.areaName = areaName;
        this.device = device;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getChildTitle() {
        return childTitle;
    }

    public void setChildTitle(String childTitle) {
        this.childTitle = childTitle;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isData() {
        return isData;
    }

    public void setIsData(boolean isData) {
        this.isData = isData;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
