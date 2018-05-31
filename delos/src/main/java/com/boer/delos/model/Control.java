package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description:
 * create at 2016/4/27 16:46
 *
 */
public class Control implements Serializable{

    private String addr;// 设备的Mac地址
    private String type;// 设备类型
    private int channel;// 设备通道

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
