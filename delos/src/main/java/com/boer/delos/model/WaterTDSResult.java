package com.boer.delos.model;

import java.util.List;

/**
 * Created by apple on 17/5/8.
 */

public class WaterTDSResult extends BaseResult {

    private List<WaterBean> response;

    public List<WaterBean> getResponse() {
        return response;
    }

    public void setResponse(List<WaterBean> response) {
        this.response = response;
    }

    public static class WaterBean {
        /**
         * time : 1494259199
         * addr : F8668A0A004B12000000
         * payload : {"addr":"F8668A0A004B12000000","timestamp":1492827735,"value":{"purifying":0,"filterLevel4":93,"pureTDS":29,"filterLevel1":94,"filterLevel2":94,"filterLevel3":94,"state":25,"rawTDS":61,"rawCisternLevel":0,"diagnosis":0,"delta":0,"dewatering":0,"rawCisternPos":0,"waterLevel":2,"machineState":0,"delta":0},"time":1492827735,"type":"TableWaterFilter","name":"水质净化器"}
         */

        private int time;
        private String addr;
        private String payload;

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }


    public static class PayloadBean {
        private String addr;
        private long timestamp;
        private long time;
        private String type;
        private String name;
        private DeviceStatusValue value;

        public String getAddr() {
            return addr;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
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

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

//    public static class ValueBean {
//        /**
//         * value : {"purifying":0,"filterLevel4":93,"pureTDS":29,"filterLevel1":94,"filterLevel2":94,"filterLevel3":94,"state":25,"rawTDS":61,"rawCisternLevel":0,"diagnosis":0,"dewatering":0,"rawCisternPos":0,"waterLevel":2,"machineState":0,"delta":0}
//         */
//        /**
//         * purifying : 0
//         * filterLevel4 : 93
//         * pureTDS : 29
//         * filterLevel1 : 94
//         * filterLevel2 : 94
//         * filterLevel3 : 94
//         * state : 25
//         * rawTDS : 61
//         * rawCisternLevel : 0
//         * diagnosis : 0
//         * dewatering : 0
//         * rawCisternPos : 0
//         * waterLevel : 2
//         * machineState : 0
//         * delta : 0
//         */
//        private int purifying;
//        private int filterLevel4;
//        private int pureTDS;
//        private int filterLevel1;
//        private int filterLevel2;
//        private int filterLevel3;
//        private int state;
//        private int rawTDS;
//        private int rawCisternLevel;
//        private int diagnosis;
//        private int dewatering;
//        private int rawCisternPos;
//        private int waterLevel;
//        private int machineState;
//        private int delta;
//
//        public int getPurifying() {
//            return purifying;
//        }
//
//        public void setPurifying(int purifying) {
//            this.purifying = purifying;
//        }
//
//        public int getFilterLevel4() {
//            return filterLevel4;
//        }
//
//        public void setFilterLevel4(int filterLevel4) {
//            this.filterLevel4 = filterLevel4;
//        }
//
//        public int getPureTDS() {
//            return pureTDS;
//        }
//
//        public void setPureTDS(int pureTDS) {
//            this.pureTDS = pureTDS;
//        }
//
//        public int getFilterLevel1() {
//            return filterLevel1;
//        }
//
//        public void setFilterLevel1(int filterLevel1) {
//            this.filterLevel1 = filterLevel1;
//        }
//
//        public int getFilterLevel2() {
//            return filterLevel2;
//        }
//
//        public void setFilterLevel2(int filterLevel2) {
//            this.filterLevel2 = filterLevel2;
//        }
//
//        public int getFilterLevel3() {
//            return filterLevel3;
//        }
//
//        public void setFilterLevel3(int filterLevel3) {
//            this.filterLevel3 = filterLevel3;
//        }
//
//        public int getState() {
//            return state;
//        }
//
//        public void setState(int state) {
//            this.state = state;
//        }
//
//        public int getRawTDS() {
//            return rawTDS;
//        }
//
//        public void setRawTDS(int rawTDS) {
//            this.rawTDS = rawTDS;
//        }
//
//        public int getRawCisternLevel() {
//            return rawCisternLevel;
//        }
//
//        public void setRawCisternLevel(int rawCisternLevel) {
//            this.rawCisternLevel = rawCisternLevel;
//        }
//
//        public int getDiagnosis() {
//            return diagnosis;
//        }
//
//        public void setDiagnosis(int diagnosis) {
//            this.diagnosis = diagnosis;
//        }
//
//        public int getDewatering() {
//            return dewatering;
//        }
//
//        public void setDewatering(int dewatering) {
//            this.dewatering = dewatering;
//        }
//
//        public int getRawCisternPos() {
//            return rawCisternPos;
//        }
//
//        public void setRawCisternPos(int rawCisternPos) {
//            this.rawCisternPos = rawCisternPos;
//        }
//
//        public int getWaterLevel() {
//            return waterLevel;
//        }
//
//        public void setWaterLevel(int waterLevel) {
//            this.waterLevel = waterLevel;
//        }
//
//        public int getMachineState() {
//            return machineState;
//        }
//
//        public void setMachineState(int machineState) {
//            this.machineState = machineState;
//        }
//
//        public int getDelta() {
//            return delta;
//        }
//
//        public void setDelta(int delta) {
//            this.delta = delta;
//        }
//    }


}
