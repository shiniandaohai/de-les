package com.boer.delos.model;

import java.util.List;

/**
 * Created by zhukang on 16/7/8.
 */
public class DeviceRelateResult {
    private int ret;
    private String msg;
    private List<DeviceRelate> response;
    private List<String> newAlarmList;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<DeviceRelate> getResponse() {
        return response;
    }

    public void setResponse(List<DeviceRelate> response) {
        this.response = response;
    }

    public List<String> getNewAlarmList() {
        return newAlarmList;
    }

    public void setNewAlarmList(List<String> newAlarmList) {
        this.newAlarmList = newAlarmList;
    }
}
