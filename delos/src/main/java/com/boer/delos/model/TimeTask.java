package com.boer.delos.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/7/20.
 */
public class TimeTask implements Serializable {

    private static final long serialVersionUID = 6181293951133967394L;
    private String id;
    private int modeId;
    @SerializedName("switch")
    private String on;
    private String type;
    private String triggerTime;
    private List<Map<String, String>> repeat;

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public List<Map<String, String>> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<Map<String, String>> repeat) {
        this.repeat = repeat;
    }
}
