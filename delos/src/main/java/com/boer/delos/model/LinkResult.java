package com.boer.delos.model;

import java.util.List;

/**
 * Created by zhukang on 16/7/16.
 */
public class LinkResult {
    private int ret;
    private List<Link> response;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<Link> getResponse() {
        return response;
    }

    public void setResponse(List<Link> response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
