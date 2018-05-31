package com.boer.delos.model;

public class LinkModelDevicelistParams implements java.io.Serializable {
    private static final long serialVersionUID = -3090710614093030444L;

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

    private String state;
    private String state2;
    private String state3;
    private String state4;

    public String getState2() {
        return this.state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
