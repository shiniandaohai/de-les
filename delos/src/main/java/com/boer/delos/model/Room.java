package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;
/**
 * @author XieQingTing
 * @Description: 房间实体类
 * create at 2016/5/19 16:06
 *
 */
public class Room implements Serializable{

    private String roomId;
    private String name;
    private List<Area> areas;
    private String type;
    private boolean isEdit;

    private boolean lightShow; //房间灯是否亮

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;

        Room room = (Room) o;

        return roomId != null ? roomId.equals(room.roomId) : room.roomId == null;

    }

    public boolean isLightShow() {
        return lightShow;
    }

    public void setLightShow(boolean lightShow) {
        this.lightShow = lightShow;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public Room(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Area> getAreas() {
        return this.areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
