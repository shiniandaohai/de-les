package com.boer.delos.model;

import java.io.Serializable;

public class AlarmDetail implements Serializable{
    private int timestamp;
    private String message;
    private String alarming;
    private String confirmed;
    private String roomname;
    private String name;
    private String hostId;
    private String addr;
    private String delay;
    private String type;
    private String producetime;

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlarming() {
        return this.alarming;
    }

    public void setAlarming(String alarming) {
        this.alarming = alarming;
    }

    public String getConfirmed() {
        return this.confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
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

    public String getHostId() {
        return this.hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDelay() {
        return this.delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProducetime() {
        return this.producetime;
    }

    public void setProducetime(String producetime) {
        this.producetime = producetime;
    }
}
