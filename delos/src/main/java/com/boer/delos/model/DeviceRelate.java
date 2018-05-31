package com.boer.delos.model;

import java.io.Serializable;

public class DeviceRelate implements Serializable {
    private static final long serialVersionUID = -8220080029436861942L;
    private DeviceStatus deviceStatus;
    private Device deviceProp;

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Device getDeviceProp() {
        return this.deviceProp;
    }

    public void setDeviceProp(Device deviceProp) {
        this.deviceProp = deviceProp;
    }

    @Override
    public String toString() {
        return String.format("type:%s,mac:%s", deviceProp.getType(), deviceProp.getAddr());
    }
}