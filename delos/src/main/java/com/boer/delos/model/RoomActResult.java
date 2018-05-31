package com.boer.delos.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 17/5/4.
 */

public class RoomActResult {


    /**
     * ret : 0
     * response : [{"timeTask":{"switch":"off","repeat":[],"type":"delay"},"name":"晨起_239","devicelist":[],"serialNo":"0","modeId":185,"deviceList":[],"tag":"晨起","timestamp":1493796526,"roomId":"239"}]
     */

    private String ret;
    private List<ModeAct> response;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public List<ModeAct> getResponse() {
        return response;
    }

    public void setResponse(List<ModeAct> response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * timeTask : {"switch":"off","repeat":[],"type":"delay"}
         * name : 晨起_239
         * devicelist : []
         * serialNo : 0
         * modeId : 185
         * deviceList : []
         * tag : 晨起
         * timestamp : 1493796526
         * roomId : 239
         */
        private TimeTaskBean timeTask;
        private String name;
        private String serialNo;
        private int modeId;
        private String tag;
        private int timestamp;
        private String roomId;
        private List<?> devicelist;
        private List<?> deviceList;

        public TimeTaskBean getTimeTask() {
            return timeTask;
        }

        public void setTimeTask(TimeTaskBean timeTask) {
            this.timeTask = timeTask;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public int getModeId() {
            return modeId;
        }

        public void setModeId(int modeId) {
            this.modeId = modeId;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public List<?> getDevicelist() {
            return devicelist;
        }

        public void setDevicelist(List<?> devicelist) {
            this.devicelist = devicelist;
        }

        public List<?> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<?> deviceList) {
            this.deviceList = deviceList;
        }

        public static class TimeTaskBean {
            /**
             * switch : off
             * repeat : []
             * type : delay
             */

            @SerializedName("switch")
            private String switchX;
            private String type;
            private List<?> repeat;

            public String getSwitchX() {
                return switchX;
            }

            public void setSwitchX(String switchX) {
                this.switchX = switchX;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<?> getRepeat() {
                return repeat;
            }

            public void setRepeat(List<?> repeat) {
                this.repeat = repeat;
            }
        }
    }
}
