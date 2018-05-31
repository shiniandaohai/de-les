package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description: 设备管理二级列表实体类
 * create at 2016/4/7 14:12
 *
 */
public class DeviceManageChild implements Serializable {

    // TODO 此处图片应从服务器获取,故resId的类型最终应为String
    private int resId;
    private String childTitle;//子条目标题
    private String roomName;// 房间名称
    private String comment;// 备注
    private Device device;

    public DeviceManageChild() {}

    public DeviceManageChild(int resId, Device device) {
        this.resId = resId;
        this.device = device;
    }

    public DeviceManageChild(int resId, String childTitle, String roomName, String comment) {
        this.resId = resId;
        this.childTitle = childTitle;
        this.roomName = roomName;
        this.comment = comment;
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
