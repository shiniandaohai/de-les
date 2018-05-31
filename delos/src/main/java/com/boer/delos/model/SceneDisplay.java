package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 场景显示
 * create at 2016/4/12 9:54
 *
 */
public class SceneDisplay implements Serializable{

    private String groupTitle;
    private List<SceneDisplayChild> childList;
    private int count;
    private boolean isFinish;

    public void setImageView(boolean isFinish) {
        this.isFinish=isFinish;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public SceneDisplay() {}

    public SceneDisplay(String groupTitle, List<SceneDisplayChild> childList) {
        this.groupTitle = groupTitle;
        this.childList = childList;
    }

    public SceneDisplay(String groupTitle, List<SceneDisplayChild> childList, int count) {
        this.groupTitle = groupTitle;
        this.childList = childList;
        this.count = count;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public List<SceneDisplayChild> getChildList() {
        return childList;
    }

    public void setChildList(List<SceneDisplayChild> childList) {
        this.childList = childList;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    /*private int resId;
    private String itemName;
    private String count;
    public boolean isFinish;

    public void setImageView(boolean isFinish) {
        this.isFinish=isFinish;
    }

    public SceneDisplay(int resId, String itemName, String count) {
        this.resId = resId;
        this.itemName = itemName;
        this.count = count;
    }



    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }*/
}
