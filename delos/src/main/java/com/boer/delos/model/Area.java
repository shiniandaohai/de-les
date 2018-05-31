package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 区域实体类
 * create at 2016/5/19 16:01
 *
 */
public class Area implements Serializable{
    private String name;
    private String areaId;
    private String roomId;
    private String pic;


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaId() {
        return this.areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
