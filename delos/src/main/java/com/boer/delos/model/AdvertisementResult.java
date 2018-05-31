package com.boer.delos.model;

/**
 * Created by zhukang on 16/7/14.
 */
public class AdvertisementResult {

    private int ret;
    private AdvertisementData response;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public AdvertisementData getResponse() {
        return response;
    }

    public void setResponse(AdvertisementData response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}


