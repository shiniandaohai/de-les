package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/8/11.
 */
public class DeviceResult extends BaseResult implements Serializable {
    private static final long serialVersionUID = -577728610308001365L;

    private Device response;

    public Device getResponse() {
        return response;
    }

    public void setResponse(Device response) {
        this.response = response;
    }
}
