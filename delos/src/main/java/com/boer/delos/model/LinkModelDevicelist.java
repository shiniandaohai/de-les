package com.boer.delos.model;

public class LinkModelDevicelist implements java.io.Serializable {
    private static final long serialVersionUID = 7303742359423479452L;
    private String deviceAddr;
    private String roomname;
    private String areaname;
    private String devicename;
    private String areaid;
    private String roomId;
    private String devicetype;
    private LinkModelDevicelistParams params;
    private String deviceid;

    public String getDeviceAddr() {
        return this.deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public String getRoomname() {
        return this.roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getAreaname() {
        return this.areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getDevicename() {
        return this.devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getAreaid() {
        return this.areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDevicetype() {
        return this.devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public LinkModelDevicelistParams getParams() {
        return this.params;
    }

    public void setParams(LinkModelDevicelistParams params) {
        this.params = params;
    }

    public String getDeviceid() {
        return this.deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
