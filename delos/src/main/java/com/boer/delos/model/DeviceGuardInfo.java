package com.boer.delos.model;


public class DeviceGuardInfo implements java.io.Serializable {
    private static final long serialVersionUID = -6155124745185982446L;
    private String X;
    private String Y;
    private String addr;
    private String areaId;
    private String areaname;
    private Boolean dismiss;// 是否解除绑定，false:未解除；true:解除；默认为false

    //    public String getDismiss1() {
//        return dismiss ? "1" : "0";
//    }
//
//    private String dismiss1;//是否解除绑定，0:未解除；1:解除；默认为0
    private String keyId;
    private String name;
    private String note;
    private String roomId;
    private String roomname;
    private Long timestamp;
    private String type;
    private GuardInfo guardInfo;


//    private boolean isChecked;//选择模式界面的device是否被选中


    public DeviceGuardInfo(String roomname) {
        this.roomname = roomname;
    }

    public String getAreaname() {
        return this.areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getAreaId() {
        return this.areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoomname() {
        return this.roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getY() {
        return this.Y;
    }

    public void setY(String Y) {
        this.Y = Y;
    }

    public String getX() {
        return this.X;
    }

    public void setX(String X) {
        this.X = X;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
//    public boolean isChecked() {
//        return isChecked;
//    }
//
//    public void setIsChecked(boolean isChecked) {
//        this.isChecked = isChecked;
//    }
//    public String getDismiss() {
//        return dismiss;
//    }
//
//    public void setDismiss(String dismiss) {
//        this.dismiss = dismiss;
//    }


    public boolean getDismiss() {
        return this.dismiss;
    }

    public void setDismiss(boolean dismiss) {
        this.dismiss = dismiss;
    }

    public GuardInfo getGuardInfo() {
        return guardInfo;
    }

    public void setGuardInfo(GuardInfo guardInfo) {
        this.guardInfo = guardInfo;
    }
}
