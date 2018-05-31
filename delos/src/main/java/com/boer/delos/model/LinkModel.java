package com.boer.delos.model;

import java.util.List;

/**
 * @author XieQingTing
 * @Description: 联动模式
 * create at 2016/6/7 16:05
 *
 */
public class LinkModel implements java.io.Serializable {

    private static final long serialVersionUID = -536194742137082293L;

    private Long timestamp;
    private String tag;
    private String name;
    private String roomId;
    private String serialNo;

    private String modeId;

    private List<LinkModelDevicelist> devicelist;

    public List<LinkModelDevicelist> getDevicelist() {
        return devicelist;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    public void setDevicelist(List<LinkModelDevicelist> devicelist) {
        this.devicelist = devicelist;
    }

//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }
    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

}
