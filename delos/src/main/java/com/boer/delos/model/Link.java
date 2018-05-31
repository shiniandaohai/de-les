package com.boer.delos.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Link implements Serializable {
    private Boolean isCurrent; //当前模式
    private int modeId;
    private String name;
    private String tag;
    private Boolean hasActiveTask; //定时任务
    // 不用序列化
    private transient String flag; // null:  全局模式   room :房间模式

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Boolean getHasActiveTask() {
        return hasActiveTask;
    }

    public void setHasActiveTask(Boolean hasActiveTask) {
        this.hasActiveTask = hasActiveTask;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
