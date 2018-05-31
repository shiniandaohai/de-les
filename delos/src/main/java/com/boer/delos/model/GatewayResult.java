package com.boer.delos.model;

/**
 * Created by zhukang on 16/7/14.
 */
public class GatewayResult {

    private int ret;
    private Gateway response;
    private String msg;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public Gateway getResponse() {
        return response;
    }

    public void setResponse(Gateway response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
