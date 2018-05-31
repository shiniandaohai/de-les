package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirClean implements Serializable {

    private int res;    //空气净化器颜色&&&&&&&&&&&&&&&&&&皮肤仪器为图片
    private int resSelector;
    private String name;
    private boolean isCheck;
    private transient int airRes;

    public int getAirRes() {
        return airRes;
    }

    public void setAirRes(int airRes) {
        this.airRes = airRes;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getResSelector() {
        return resSelector;
    }

    public void setResSelector(int resSelector) {
        this.resSelector = resSelector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
