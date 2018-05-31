package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家具列表一级列表的实体类
 * create at 2016/4/15 14:15
 */
public class Furniture implements Serializable {

    private String groupTitle;
    private String areaId;
    private List<FurnitureChild> childList;

    public Furniture() {
    }

    public Furniture(String groupTitle, List<FurnitureChild> childList) {
        this.groupTitle = groupTitle;
        this.childList = childList;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public List<FurnitureChild> getChildList() {
        return childList;
    }

    public void setChildList(List<FurnitureChild> childList) {
        this.childList = childList;
    }
}
