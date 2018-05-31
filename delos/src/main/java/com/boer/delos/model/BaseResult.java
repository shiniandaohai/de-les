package com.boer.delos.model;

/**
 * Created by zhukang on 16/7/13.
 */
public class BaseResult {

    private int ret;
    private String msg;
    private String md5; //不一定全都有

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
