package com.boer.delos.model;

import java.util.List;

public class LinkPlanAlarmAct {
    private List<ModeDevice> actList;

    public List<ModeDevice> getDevicelist() {
        return actList;
    }

    public void setDevicelist(List<ModeDevice> actList) {
        this.actList = actList;
    }
}
