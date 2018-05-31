package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 联动模式
 * create at 2016/4/12 9:53
 *
 */
public class Model implements Serializable{
    private String device;
    private String roomName;
    private boolean isCheck;

    public Model(String device, String roomName, boolean isCheck) {
        this.device = device;
        this.roomName = roomName;
        this.isCheck = isCheck;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
