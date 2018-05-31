package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by gaolong on 2017/4/16.
 */
public class AirCleanData implements Serializable{


    /**
     * name : 空气净化器
     * value : {"temp":"25.2","weekAccPur":6378,"screen":1,"humidity":"58.0","HEPA":4320,"totalAccPur":6378,"rate":64,"TVOC":46,"mode":4,"qualityLevel":0,"AQI":0,"yearAccPur":0,"pm25":1,"monthAccPur":0}
     * time : 1492324860
     * offline : 0
     * type : AirFilter
     * addr : 9446F50B004B12000000
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

    public static class ValueBean implements Serializable{
        /**
         * temp : 25.2
         * weekAccPur : 6378
         * screen : 1
         * humidity : 58.0
         * HEPA : 4320
         * totalAccPur : 6378
         * rate : 64
         * TVOC : 46
         * mode : 4
         * qualityLevel : 0
         * AQI : 0
         * yearAccPur : 0
         * pm25 : 1
         * monthAccPur : 0
         */

        private String temp;
        private int weekAccPur;
        private int screen;
        private String humidity;
        private int HEPA;
        private int totalAccPur;
        private int rate;
        private int TVOC;
        private int mode;
        private int qualityLevel;
        private int AQI;
        private int yearAccPur;
        private int pm25;
        private int monthAccPur;

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public int getWeekAccPur() {
            return weekAccPur;
        }

        public void setWeekAccPur(int weekAccPur) {
            this.weekAccPur = weekAccPur;
        }

        public int getScreen() {
            return screen;
        }

        public void setScreen(int screen) {
            this.screen = screen;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public int getHEPA() {
            return HEPA;
        }

        public void setHEPA(int HEPA) {
            this.HEPA = HEPA;
        }

        public int getTotalAccPur() {
            return totalAccPur;
        }

        public void setTotalAccPur(int totalAccPur) {
            this.totalAccPur = totalAccPur;
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public int getTVOC() {
            return TVOC;
        }

        public void setTVOC(int TVOC) {
            this.TVOC = TVOC;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getQualityLevel() {
            return qualityLevel;
        }

        public void setQualityLevel(int qualityLevel) {
            this.qualityLevel = qualityLevel;
        }

        public int getAQI() {
            return AQI;
        }

        public void setAQI(int AQI) {
            this.AQI = AQI;
        }

        public int getYearAccPur() {
            return yearAccPur;
        }

        public void setYearAccPur(int yearAccPur) {
            this.yearAccPur = yearAccPur;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }

        public int getMonthAccPur() {
            return monthAccPur;
        }

        public void setMonthAccPur(int monthAccPur) {
            this.monthAccPur = monthAccPur;
        }
    }
}
