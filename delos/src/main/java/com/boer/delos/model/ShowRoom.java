package com.boer.delos.model;

/**
 * Created by gaolong on 2017/4/5.
 */
public class ShowRoom {


    /**
     * type : 主卧
     * roomId : 1
     * name : 卧室
     * timestamp : 1491360291
     */

    private String type;
    private String roomId;
    private String name;
    private int timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
