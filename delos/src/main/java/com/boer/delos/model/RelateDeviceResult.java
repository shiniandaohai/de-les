package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhukang on 16/8/8.
 */
public class RelateDeviceResult extends BaseResult implements Serializable{

    private static final long serialVersionUID = -3171444006594084746L;

    private List<RelateDevice> response;

    public List<RelateDevice> getResponse() {
        return response;
    }

    public void setResponse(List<RelateDevice> response) {
        this.response = response;
    }
}
