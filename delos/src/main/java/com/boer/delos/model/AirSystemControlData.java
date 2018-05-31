package com.boer.delos.model;

/**
 * Created by gaolong on 2018/1/26.
 */

public class AirSystemControlData {


    /**
     * addr : 3Dxxxxxxxxxxxxxxxx00
     * areaName : xxx
     * deviceName : 地暖
     * roomName : xxxx
     * type : AirSystem
     * value : {"cmd":"1","data":"1"}
     */

    private String addr;
    private String areaName;
    private String deviceName;
    private String roomName;
    private String type;
    private AirSystemControlData.ValueBean value;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AirSystemControlData.ValueBean getValue() {
        return value;
    }

    public void setValue(AirSystemControlData.ValueBean value) {
        this.value = value;
    }

    public static class ValueBean {
        /**
         * cmd : 1
         * data : 1
         */

        private String cmd;
        private String data;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
