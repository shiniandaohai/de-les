package com.boer.delos.model;

public class DeviceCopy implements java.io.Serializable {
    private static final long serialVersionUID = -6155124745185982446L;
    private String X;
    private String Y;
    private String addr;
    private String areaId;
    private String areaname;
    private String dismiss;
    private String keyId;
    private String name;
    private String note;
    private String roomId;
    private String roomname;
    private String timestamp;
    private String type;

    public DeviceCopy(String roomname) {
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

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
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
        return this.keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDismiss() {
        return this.dismiss;
    }

    public void setDismiss(String dismiss) {
        this.dismiss = dismiss;
    }
}
