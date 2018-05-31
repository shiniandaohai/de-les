package com.boer.delos.model;

public class RelateDeviceControls implements java.io.Serializable {
    private static final long serialVersionUID = -8892832697478923139L;
    private String addr;
    private String type;
    private String channel;

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
