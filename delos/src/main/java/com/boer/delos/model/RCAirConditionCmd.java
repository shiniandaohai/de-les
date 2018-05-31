package com.boer.delos.model;

import java.io.Serializable;

/**
 * 空调遥控器指令
 * Created by dell on 2016/7/15.
 */
public class RCAirConditionCmd implements Serializable{
    private RCAirConditionCmdData AcData;
    private String addr;
    private String areaName;
    private String deviceName;
    private String extraCmd = "newInfrared";
    private String roomName;
    private String type = "AirCondition";

    private RemoteCMatchData value;

    public RCAirConditionCmd(){
        super();
    }

    public RCAirConditionCmd(RCAirConditionCmdData acData, String addr, String areaname, String deviceName, String extraCmd, String roomname, String type, RemoteCMatchData value) {
        super();
        AcData = acData;
        this.addr = addr;
        this.areaName = areaname;
        this.deviceName = deviceName;
        this.extraCmd = extraCmd;
        this.roomName = roomname;
        this.type = type;
        this.value = value;
    }

    public RCAirConditionCmdData getAcData() {
        return AcData;
    }

    public void setAcData(RCAirConditionCmdData acData) {
        AcData = acData;
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

    public void setAreaName(String areaname) {
        this.areaName = areaname;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public void setRoomName(String roomname) {
        this.roomName = roomname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RemoteCMatchData getValue() {
        return value;
    }

    public void setValue(RemoteCMatchData value) {
        this.value = value;
    }
}
