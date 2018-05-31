package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 设备管理一级列表的实体类
 * create at 2016/4/15 14:16
 *
 */
public class DeviceManage implements Serializable {

    private String groupTitle;
    private List<DeviceManageChild> childList;

    public DeviceManage() {}

    public DeviceManage(String groupTitle, List<DeviceManageChild> childList) {
        this.groupTitle = groupTitle;
        this.childList = childList;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public List<DeviceManageChild> getChildList() {
        return childList;
    }

    public void setChildList(List<DeviceManageChild> childList) {
        this.childList = childList;
    }
}
