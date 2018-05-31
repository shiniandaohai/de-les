package com.boer.delos.model;

/**
 * 设备信息 with remoe controller info
 * Created by dell on 2016/7/15.
 */
public class DeviceWithRemoteCtlInfo extends Device {
     //设备遥控器信息.
    private RemoteCMatchData remoteInfo;

    public DeviceWithRemoteCtlInfo(String roomname) {
        super(roomname);
    }


    public RemoteCMatchData getRemoteInfo() {
        return remoteInfo;
    }

    public void setRemoteInfo(RemoteCMatchData remoteInfo) {
        this.remoteInfo = remoteInfo;
    }

    public void setmDevice(Device device) {
        this.setKeyId(device.getKeyId());
        this.setAddr(device.getAddr());
        this.setAreaId(device.getAreaId());
        this.setAreaname(device.getAreaname());
        this.setDismiss(device.getDismiss());
        this.setName(device.getName());
        this.setNote(device.getNote());
        this.setRoomId(device.getRoomId());
        this.setGuardInfo(device.getGuardInfo());
        this.setRoomname(device.getRoomname());
        this.setTimestamp(device.getTimestamp());
        this.setType(device.getType());
        this.setX(device.getX());
        this.setY(device.getY());

    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("----------remoteinfo-------------");
        builder.append("\n");
        builder.append("brandname : " + remoteInfo.getBrandname());
        builder.append("\n");
        builder.append("c3rv : " + remoteInfo.getC3rv());
        builder.append("\n");
        builder.append("device_type : " + remoteInfo.getDevice_type());
        builder.append("\n");
        builder.append("fid : " + remoteInfo.getFid());
        builder.append("\n");
        builder.append("formatString : " + remoteInfo.getFormat_string());
        builder.append("\n");
        builder.append("m_code: " + remoteInfo.getM_code());
        builder.append("\n");
        builder.append("m_key_squency : " + remoteInfo.getM_key_squency());
        builder.append("\n");
        builder.append("m_label: " + remoteInfo.getM_label());
        builder.append("\n");

        return builder.toString();
    }
}
