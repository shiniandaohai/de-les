package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by ACER~ on 2016/6/4.
 */
public class GatewayInfo implements Serializable {
    private String hostId;
    private String hostName;
    private String ip;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
