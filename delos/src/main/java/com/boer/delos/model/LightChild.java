package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by ACER~ on 2016/6/5.
 */
public class LightChild implements Serializable {



    private String state;
    private String state2;
    private String state3;
    private String state4;
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getState() {
        return state;
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

    public String getState4() {
        return state4;
    }

    public void setState4(String state4) {
        this.state4 = state4;
    }
}
