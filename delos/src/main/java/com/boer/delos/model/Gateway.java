package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 主机实体类
 * create at 2016/5/19 16:04
 *
 */
public class Gateway implements Serializable{
    private int timestamp;
    private String numbers;
    private String firmver;
    private String lastbackup;
    private String name;
    private String hostId;
    private String softver;
    private String registerHost;
    private List<Room> room;

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getNumbers() {
        return this.numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getFirmver() {
        return this.firmver;
    }

    public void setFirmver(String firmver) {
        this.firmver = firmver;
    }

    public String getLastbackup() {
        return this.lastbackup;
    }

    public void setLastbackup(String lastbackup) {
        this.lastbackup = lastbackup;
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

    public String getSoftver() {
        return this.softver;
    }

    public void setSoftver(String softver) {
        this.softver = softver;
    }

    public String getRegisterHost() {
        return this.registerHost;
    }

    public void setRegisterHost(String registerHost) {
        this.registerHost = registerHost;
    }

    public List<Room> getRoom() {
        return this.room;
    }

    public void setRoom(List<Room>  room) {
        this.room = room;
    }
}
