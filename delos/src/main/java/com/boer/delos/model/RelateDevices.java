package com.boer.delos.model;

import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 关联设备
 * create at 2016/7/14 15:26
 */
public class RelateDevices {
    private String groupTitle;
    private int resId;
    private Device device;
    private List<Map<String, Object>> childList;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public List<Map<String, Object>> getChildList() {
        return childList;
    }

    public void setChildList(List<Map<String, Object>> childList) {
        this.childList = childList;
    }
}
