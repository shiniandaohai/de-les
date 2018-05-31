package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/8/11.
 */
public class RCAirConditionCmdDataResult extends BaseResult implements Serializable{
    private static final long serialVersionUID = 4489121701272429472L;

    private RCAirConditionCmdData response;

    public RCAirConditionCmdData getResponse() {
        return response;
    }

    public void setResponse(RCAirConditionCmdData response) {
        this.response = response;
    }
}
