package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/6 0006.
 * 门禁数据
 */
public class GuardInfo implements Serializable {


    private static final long serialVersionUID = 1915828658821344107L;
    private String account;
    private String SIP;
    private String pwd;
    private String hostID;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSIP() {
        return SIP;
    }

    public void setSIP(String SIP) {
        this.SIP = SIP;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }
}
