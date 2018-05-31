package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhukang on 16/7/29.
 */
public class RoomModeActionResult extends BaseResult implements Serializable {

    private static final long serialVersionUID = 8192556872484507918L;
    private List<ModeAct> response;

    public List<ModeAct> getResponse() {
        return response;
    }

    public void setResponse(List<ModeAct> response) {
        this.response = response;
    }
}
