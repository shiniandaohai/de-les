package com.boer.delos.activity.smartdoorbell.imageloader;

import android.os.Handler;

public class CmdEventBusEntity {
    public static final int CMD_TYPE_DOOR_BELL_LIGHT_SWITCH =0;
    public static final int CMD_TYPE_DOOR_BELL_PIR_SWITCH=1;
    public static final int CMD_TYPE_DOOR_BELL_SENSE_TIME =2;
    public static final int CMD_TYPE_DOOR_BELL_SENSE_SENSITIVITY=3;
    public static final int CMD_TYPE_DOOR_BELL_FORMAT =4;
    public static final int CMD_TYPE_DOOR_BELL_CAPTURE_NUM=5;
    public static final int CMD_TYPE_DOOR_BELL_RINGTONE=6;
    public static final int CMD_TYPE_DOOR_BELL_VOLUME=7;
    public static final int CMD_TYPE_DOOR_BELL_GET_PIR_INFO =8;
    public static final int CMD_TYPE_DOOR_BELL_SET_PIR_INFO =9;
    public static final int CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO =10;
    private Handler mHandler;

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    private int cmdType;

    public String getCmdStr() {
        return cmdStr;
    }

    public void setCmdStr(String cmdStr) {
        this.cmdStr = cmdStr;
    }

    private String cmdStr;
    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    private int cmd;
    public CmdEventBusEntity() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
