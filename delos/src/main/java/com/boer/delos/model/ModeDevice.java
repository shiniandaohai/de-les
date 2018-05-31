package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/7/20.
 */
public class ModeDevice implements Serializable {

    private static final long serialVersionUID = 2121647563812596268L;

    private String devicename;
    private String areaid;
    private String areaname;
    private String deviceAddr;
    private String roomId;
    private String roomname;
    private String devicetype;
    private String deviceid;
    private String brand;

    private ControlDeviceValue params;
    private LightName lightName;

    private transient boolean isHave; //是否在模式下

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModeDevice)) return false;

        ModeDevice that = (ModeDevice) o;

        if (!deviceAddr.equals(that.deviceAddr)) return false;
        return devicetype.equals(that.devicetype);

    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean have) {
        isHave = have;
    }

    public LightName getLightName() {
        return lightName;
    }

    public void setLightName(LightName lightName) {
        this.lightName = lightName;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public ControlDeviceValue getParams() {
        return params;
    }

    public void setParams(ControlDeviceValue params) {
        this.params = params;
    }
}
