package com.boer.delos.model;

import java.util.List;

/**
 * Created by zhukang on 16/7/13.
 */
public class HostResult extends BaseResult {

    private List<Host> hosts;
    private String currentHostId;

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public String getCurrentHostId() {
        return currentHostId;
    }

    public void setCurrentHostId(String currentHostId) {
        this.currentHostId = currentHostId;
    }

}
