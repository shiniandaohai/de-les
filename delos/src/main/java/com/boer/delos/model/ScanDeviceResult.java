package com.boer.delos.model;

import java.util.List;

/**
 * Created by zhukang on 16/7/11.
 */
public class ScanDeviceResult {

    private int ret;
    private String msg;
    private List<ScanDevice> response;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ScanDevice> getResponse() {
        return response;
    }

    public void setResponse(List<ScanDevice> response) {
        this.response = response;
    }
}
