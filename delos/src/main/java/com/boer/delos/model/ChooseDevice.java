package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 选择设备
 * create at 2016/5/19 16:02
 *
 */
public class ChooseDevice implements Serializable{
    private String roomName;
    private String areaType;
    private String deviceName;
    private boolean isChecked;
    public ChooseDevice(String roomName, String areaType, String deviceName, boolean isChecked) {
        this.roomName = roomName;
        this.areaType = areaType;
        this.deviceName = deviceName;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }



    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
