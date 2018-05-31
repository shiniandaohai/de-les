package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 控制设备
 * create at 2016/5/25 13:43
 */
public class ControlDevice implements Serializable {
    private String areaName;
    private String deviceName;
    private String roomName;
    private ControlDeviceValue value;
    private String addr;
    private String type;
    private String brand; //背景音乐

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public ControlDeviceValue getValue() {
        return this.value;
    }

    public void setValue(ControlDeviceValue value) {
        this.value = value;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
