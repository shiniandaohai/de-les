package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/7/20.
 */
public class ModeActionResult extends BaseResult implements Serializable{

    private static final long serialVersionUID = 3885648119511054550L;
    private ModeAct response;

    public ModeAct getResponse() {
        return response;
    }

    public void setResponse(ModeAct response) {
        this.response = response;
    }
}
