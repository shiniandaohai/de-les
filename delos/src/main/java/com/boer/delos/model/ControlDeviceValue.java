package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 控制设备的value
 * create at 2016/5/25 13:43
 */
public class ControlDeviceValue implements Serializable {
    private static final long serialVersionUID = 5408862238103340002L;
    private String state;
    private String state2;
    private String state3;
    private String state4;
    private String set;
    private String coeff;
    private String lightingTime;//0//3//5

//    // add by sunzhibin
//    private String position;  //TODO 门窗磁


    //节律灯
    private String n4SerialNo;
    private String buttonName;
    private String keypadName;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;

    //背景音乐
    /**
     * brand = Wise485;
     * cmd = 1;
     * data = 0;
     * dataLen = 1;
     */
    private String brand;
    private String cmd;
    private String data;
    private String dataLen;
    private String volume;
    private String modeId;

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(String modeId) {
        this.modeId = modeId;
    }

    public String getN4SerialNo() {
        return n4SerialNo;
    }

    public void setN4SerialNo(String n4SerialNo) {
        this.n4SerialNo = n4SerialNo;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getKeypadName() {
        return keypadName;
    }

    public void setKeypadName(String keypadName) {
        this.keypadName = keypadName;
    }

    public String getLightingTime() {
        return lightingTime;
    }

    public void setLightingTime(String lightingTime) {
        this.lightingTime = lightingTime;
    }

    private RemoteCMatchData value;

    public RemoteCMatchData getValue() {
        return value;
    }

    public void setValue(RemoteCMatchData value) {
        this.value = value;
    }

    public String getState4() {
        return state4;
    }

    public void setState4(String state4) {
        this.state4 = state4;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public String getState3() {
        return state3;
    }

    public void setState3(String state3) {
        this.state3 = state3;
    }

    public String getCoeff() {
        return coeff;
    }

    public void setCoeff(String coeff) {
        this.coeff = coeff;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

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

    public String getDataLen() {
        return dataLen;
    }

    public void setDataLen(String dataLen) {
        this.dataLen = dataLen;
    }
}
