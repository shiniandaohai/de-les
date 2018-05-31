package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 家庭管理
 * create at 2016/4/12 9:53
 *
 */
public class FamilyManange implements Serializable {
    private String groupTitle;
    private List<FamilyManageChild> childList;

    public FamilyManange(String groupTitle,List<FamilyManageChild> childList) {
        this.groupTitle=groupTitle;
        this.childList=childList;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public List<FamilyManageChild> getChildList() {
        return childList;
    }

    public void setChildList(List<FamilyManageChild> childList) {
        this.childList = childList;
    }
}
