package com.boer.delos.model;

/**
 * Created by gaolong on 2018/1/26.
 */

public class AirSystemData {


    /**
     * name : 真新风系统
     * value : {"co2":1000,"temperature":"24","voc":"0.00","runState":0,"humidity":"26.5","setSpeed":0,"state":0,"mode":0,"dust":8,"dedusting":0,"fanRelay":0}
     * time : 1516934814
     * offline : 0
     * type : AirSystem
     * addr : D48BF60B004B12000000
     */

    private String name;
    private ValueBean value;
    private int time;
    private int offline;
    private String type;
    private String addr;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueBean getValue() {
        return value;
    }

    public void setValue(ValueBean value) {
        this.value = value;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public static class ValueBean {
        /**
         * co2 : 1000
         * temperature : 24
         * voc : 0.00
         * runState : 0
         * humidity : 26.5
         * setSpeed : 0
         * state : 0
         * mode : 0
         * dust : 8
         * dedusting : 0
         * fanRelay : 0
         */

        private int co2;
        private String temperature;
        private String voc;
        private int runState;
        private String humidity;
        private int setSpeed;
        private int state;
        private int mode;
        private int dust;
        private int dedusting;
        private int fanRelay;

        public int getCo2() {
            return co2;
        }

        public void setCo2(int co2) {
            this.co2 = co2;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getVoc() {
            return voc;
        }

        public void setVoc(String voc) {
            this.voc = voc;
        }

        public int getRunState() {
            return runState;
        }

        public void setRunState(int runState) {
            this.runState = runState;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public int getSetSpeed() {
            return setSpeed;
        }

        public void setSetSpeed(int setSpeed) {
            this.setSpeed = setSpeed;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getDust() {
            return dust;
        }

        public void setDust(int dust) {
            this.dust = dust;
        }

        public int getDedusting() {
            return dedusting;
        }

        public void setDedusting(int dedusting) {
            this.dedusting = dedusting;
        }

        public int getFanRelay() {
            return fanRelay;
        }

        public void setFanRelay(int fanRelay) {
            this.fanRelay = fanRelay;
        }
    }
}
