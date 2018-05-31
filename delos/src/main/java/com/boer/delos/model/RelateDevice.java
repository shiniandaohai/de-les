package com.boer.delos.model;

import java.util.List;

public class RelateDevice implements java.io.Serializable {
    private static final long serialVersionUID = -4317383973067795441L;
    private int timestamp;
    private int dbId;
    private List<RelateDeviceControls> controls;

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<RelateDeviceControls> getControls() {
        return this.controls;
    }

    public void setControls(List<RelateDeviceControls> controls) {
        this.controls = controls;
    }

    public int getDbId() {
        return this.dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
