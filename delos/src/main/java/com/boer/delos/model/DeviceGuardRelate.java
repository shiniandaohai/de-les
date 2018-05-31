package com.boer.delos.model;

public class DeviceGuardRelate {
    private DeviceStatus deviceStatus;
    private DeviceGuardInfo deviceProp;

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public DeviceGuardInfo getDeviceProp() {
        return this.deviceProp;
    }

    public void setDeviceProp(DeviceGuardInfo deviceProp) {
        this.deviceProp = deviceProp;
    }

    @Override
    public String toString() {
        return "mac=" + deviceProp.getAddr();
    }
}
