package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 区域管理
 * create at 2016/4/12 9:54
 *
 */
public class SceneManage implements Serializable {
    private int resId;
    private boolean isCheck;
    private int type;


    public SceneManage() {
    }

    public SceneManage(int resId, int itemName,int type) {
        this.resId = resId;
        this.itemName = itemName;
        this.type=type;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getItemName() {
        return itemName;
    }

    public void setItemName(int itemName) {
        this.itemName = itemName;
    }

    private int itemName;



    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }



    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
