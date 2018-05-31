package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by gaolong on 2017/4/19.
 */
public class AirCleanDetailChart implements Serializable {

    /**
     * time : 1491062399
     * addr : B530F50B004B12000000
     * payload : {"addr":"B530F50B004B12000000","timestamp":1251735774,"value":{"temp":"25","screen":0,"humidity":58,"rate":100,"mode":4,"delta":null,"AQI":0,"pm25":1},"time":1251735774,"type":"AirFilter","name":"空气净化器"}
     */

    private int time;
    private String addr;
    private PayloadBean payload;

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

    public PayloadBean getPayload() {
        return payload;
    }

    public void setPayload(PayloadBean payload) {
        this.payload = payload;
    }

    public static class PayloadBean implements Serializable {
        /**
         * addr : B530F50B004B12000000
         * timestamp : 1251735774
         * value : {"temp":"25","screen":0,"humidity":58,"rate":100,"mode":4,"delta":null,"AQI":0,"pm25":1}
         * time : 1251735774
         * type : AirFilter
         * name : 空气净化器
         */

        private String addr;
        private int timestamp;
        private ValueBean value;
        private int time;
        private String type;
        private String name;


        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
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

        public static class ValueBean {
            /**
             * temp : 25
             * screen : 0
             * humidity : 58
             * rate : 100
             * mode : 4
             * delta : null
             * AQI : 0
             * pm25 : 1
             */

            private String temp;
            private int screen;
            private int humidity;
            private int rate;
            private int mode;
            private Object delta;
            private int AQI;
            private int pm25;
            private int TVOC;

            public int getTVOC() {
                return TVOC;
            }

            public void setTVOC(int TVOC) {
                this.TVOC = TVOC;
            }

            public String getTemp() {
                return temp;
            }

            public void setTemp(String temp) {
                this.temp = temp;
            }

            public int getScreen() {
                return screen;
            }

            public void setScreen(int screen) {
                this.screen = screen;
            }

            public int getHumidity() {
                return humidity;
            }

            public void setHumidity(int humidity) {
                this.humidity = humidity;
            }

            public int getRate() {
                return rate;
            }

            public void setRate(int rate) {
                this.rate = rate;
            }

            public int getMode() {
                return mode;
            }

            public void setMode(int mode) {
                this.mode = mode;
            }

            public Object getDelta() {
                return delta;
            }

            public void setDelta(Object delta) {
                this.delta = delta;
            }

            public int getAQI() {
                return AQI;
            }

            public void setAQI(int AQI) {
                this.AQI = AQI;
            }

            public int getPm25() {
                return pm25;
            }

            public void setPm25(int pm25) {
                this.pm25 = pm25;
            }
        }
    }
}
