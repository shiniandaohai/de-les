package com.boer.delos.model;

import com.boer.delos.activity.camera.zxing.decoding.InactivityTimer;

import java.io.Serializable;
/**
 * @author XieQingTing
 * @Description: 设备状态
 * create at 2016/5/19 16:03
 *
 */
public class DeviceStatus implements Serializable{

    private String name;
    private DeviceStatusValue value;
    private String keyId;
    private String addr;
    private String type;
    private Integer offline;  //add sun   离线状态0:在线。1:离线

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceStatusValue getValue() {
        return value;
    }

    public void setValue(DeviceStatusValue value) {
        this.value = value;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

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
}
