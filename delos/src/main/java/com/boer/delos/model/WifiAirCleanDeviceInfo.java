package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/28.
 */

public class WifiAirCleanDeviceInfo implements Serializable {
    private String OfflineState;
    private String deviceName;
    private String nickName;
    private String sn;
    private String airgear;
    private String clock;
    private String state;
    private String airPower;
    private String activetime;

    public String getOfflineState() {
        return OfflineState;
    }

    public void setOfflineState(String offlineState) {
        OfflineState = offlineState;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAirgear() {
        return airgear;
    }

    public void setAirgear(String airgear) {
        this.airgear = airgear;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAirPower() {
        return airPower;
    }

    public void setAirPower(String airPower) {
        this.airPower = airPower;
    }

    public String getActivetime() {
        return activetime;
    }

    public void setActivetime(String activetime) {
        this.activetime = activetime;
    }
}
