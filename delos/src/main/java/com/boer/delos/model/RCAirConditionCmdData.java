package com.boer.delos.model;

import java.io.Serializable;

/**
 * 空调指令数据格式
 * Created by dell on 2016/7/15.
 */
public class RCAirConditionCmdData implements Serializable{
    private int cKey;
    private int cMode; //运转模式
    private int cOnoff; //
    private int cTemp; //温度
    private int cWind; //风速
    private int cWinddir; //风向

    private int isOn; //开关
    private int mode; //这个是什么 mode？
    private int temp; //温度
    private int windDir; //风向
    private int windStr; //风力

    public RCAirConditionCmdData() {}

    public RCAirConditionCmdData(int cKey, int cMode, int cOnoff, int cTemp, int cWind, int cWinddir, int isOn, int mode, int temp, int windDir, int windStr) {
        this.cKey = cKey;
        this.cMode = cMode;
        this.cOnoff = cOnoff;
        this.cTemp = cTemp;
        this.cWind = cWind;
        this.cWinddir = cWinddir;
        this.isOn = isOn;
        this.mode = mode;
        this.temp = temp;
        this.windDir = windDir;
        this.windStr = windStr;
    }

    public int getcKey() {
        return cKey;
    }

    public void setcKey(int cKey) {
        this.cKey = cKey;
    }

    public int getcMode() {
        return cMode;
    }

    public void setcMode(int cMode) {
        this.cMode = cMode;
    }

    public int getcOnoff() {
        return cOnoff;
    }

    public void setcOnoff(int cOnoff) {
        this.cOnoff = cOnoff;
    }

    public int getcTemp() {
        return cTemp;
    }

    public void setcTemp(int cTemp) {
        this.cTemp = cTemp;
    }

    public int getcWind() {
        return cWind;
    }

    public void setcWind(int cWind) {
        this.cWind = cWind;
    }

    public int getcWinddir() {
        return cWinddir;
    }

    public void setcWinddir(int cWinddir) {
        this.cWinddir = cWinddir;
    }

    public int getIsOn() {
        return isOn;
    }

    public void setIsOn(int isOn) {
        this.isOn = isOn;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getWindDir() {
        return windDir;
    }

    public void setWindDir(int windDir) {
        this.windDir = windDir;
    }

    public int getWindStr() {
        return windStr;
    }

    public void setWindStr(int windStr) {
        this.windStr = windStr;
    }
}
