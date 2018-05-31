package com.boer.delos.model;

import java.io.Serializable;

/**
 * 遥控器 - 品牌型号
 * Created by dell on 2016/7/14.
 */
public class RemoteDeviceMode implements Serializable{
    private String mId; //数据库itemID
    private String mName;
    private String mModeID;  //类型ID， 用于查询对应的遥控器的。

    public RemoteDeviceMode() {

    }

    public RemoteDeviceMode(String mId, String mName, String mType, String mModeID) {
        this.mId = mId;
        this.mName = mName;
        this.mModeID = mModeID;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmModeID() {
        return mModeID;
    }

    public void setmModeID(String mModeID) {
        this.mModeID = mModeID;
    }
}
