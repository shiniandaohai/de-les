package com.boer.delos.model;

import java.io.Serializable;

/**
 * 电视遥控器指令
 * Created by dell on 2016/7/15.
 */
public class RCTVCmd implements Serializable{
    private String addr;
    private String areaName;
    private String extraCmd = "newInfrared";
    private String roomName;
    private String type = "TV";
    private String deviceName;  //协议返回值 和  发送的字段 不同。
    private RemoteCMatchData value;

    public RCTVCmd() {}

    public RCTVCmd(String addr, String areaName, String extraCmd, String roomName, String type, String deviceName, RemoteCMatchData value) {
        this.addr = addr;
        this.areaName = areaName;
        this.extraCmd = extraCmd;
        this.roomName = roomName;
        this.type = type;
        this.deviceName = deviceName;
        this.value = value;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getExtraCmd() {
        return extraCmd;
    }

    public void setExtraCmd(String extraCmd) {
        this.extraCmd = extraCmd;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public RemoteCMatchData getValue() {
        return value;
    }

    public void setValue(RemoteCMatchData value) {
        this.value = value;
    }
}
