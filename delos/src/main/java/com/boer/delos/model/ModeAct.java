package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhukang on 16/7/20.
 */
public class ModeAct implements Serializable {


    private static final long serialVersionUID = -8584965584894777888L;

    private List<ModeDevice> deviceList;  // 历史原因 上报去掉
    private List<ModeDevice> devicelist;  // change by sunzhibin 上报主机处理小写，  查询可能会有大写，

    private String modeId;
    private String name;
    private String roomId;
    private int timestamp;
    private String tag;
    private String serialNo;
    private TimeTask timeTask;

    // add by sunzhibin
    private String updateTask; //1-任务有变化，0-任务无变化

    public String getUpdateTask() {
        return updateTask;
    }

    public void setUpdateTask(String updateTask) {
        this.updateTask = updateTask;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public TimeTask getTimeTask() {
        return timeTask;
    }

    public void setTimeTask(TimeTask timeTask) {
        this.timeTask = timeTask;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<ModeDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<ModeDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public List<ModeDevice> getDevicelist() {
        return devicelist;
    }

    public void setDevicelist(List<ModeDevice> devicelist) {
        this.devicelist = devicelist;
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
