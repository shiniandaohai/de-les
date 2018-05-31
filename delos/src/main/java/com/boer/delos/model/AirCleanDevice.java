package com.boer.delos.model;

/**
 * Created by gaolong on 2017/4/16.
 */
public class AirCleanDevice {


    /**
     * addr : 8675xxxxxxxxxx000000
     * type : AirFilter
     * deviceName : 空气净化器
     * roomname :
     * areaname :
     * value : {"cmd":"1","dataLen":"2","data":"4","speed":"20"}
     */

    private String addr;
    private String type;
    private String deviceName;
    private String roomname;
    private String areaname;
    private ValueBean value;

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public ValueBean getValue() {
        return value;
    }

    public void setValue(ValueBean value) {
        this.value = value;
    }

    public static class ValueBean {
        /**
         * cmd : 1
         * dataLen : 2
         * data : 4
         * speed : 20
         */

        private String cmd;
        private String dataLen;
        private String data;
        private String speed;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getDataLen() {
            return dataLen;
        }

        public void setDataLen(String dataLen) {
            this.dataLen = dataLen;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }
    }
}
