package com.boer.delos.model;

/**
 * Created by zhukang on 16/7/21.
 */
public class LinkPlanResult {

    private int ret;
    private LinkPlan response;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public LinkPlan getResponse() {
        return response;
    }

    public void setResponse(LinkPlan response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
