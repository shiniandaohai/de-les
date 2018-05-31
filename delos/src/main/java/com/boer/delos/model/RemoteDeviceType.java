package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by dell on 2016/7/14.
 */
public class RemoteDeviceType implements Serializable{
    private String mId; //数据库itemID
    private String mName;
    private String mType;
    private String mTypeID;  //类型ID， 用于查询对应的遥控器的。

    public RemoteDeviceType() {

    }

    public RemoteDeviceType(String mId, String mName, String mType, String mTypeID) {
        this.mId = mId;
        this.mName = mName;
        this.mType = mType;
        this.mTypeID = mTypeID;
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

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmTypeID() {
        return mTypeID;
    }

    public void setmTypeID(String mTypeID) {
        this.mTypeID = mTypeID;
    }
}
