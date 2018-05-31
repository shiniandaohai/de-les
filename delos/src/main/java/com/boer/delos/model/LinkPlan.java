package com.boer.delos.model;

import java.util.List;

public class LinkPlan {
    private long timestamp;
    private LinkPlanAlarmAct alarmAct;
    private LinkPlan alarmRecover;
    private String addr;
    private int modeId;
    private List<ModeDevice> devicelist;
    private List<ModeDevice> deviceList;

    public List<ModeDevice> getDevicelist() {
        return devicelist;
    }

    public void setDevicelist(List<ModeDevice> devicelist) {
        this.devicelist = devicelist;
    }

    public List<ModeDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<ModeDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public LinkPlan getAlarmRecover() {
        return alarmRecover;
    }

    public void setAlarmRecover(LinkPlan alarmRecover) {
        this.alarmRecover = alarmRecover;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public LinkPlanAlarmAct getAlarmAct() {
        return this.alarmAct;
    }

    public void setAlarmAct(LinkPlanAlarmAct alarmAct) {
        this.alarmAct = alarmAct;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getModeId() {
        return this.modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }
}
