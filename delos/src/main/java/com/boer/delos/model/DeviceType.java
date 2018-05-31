package com.boer.delos.model;

/**
 * 设备类型，名字， 数据库 ID， for remotecontroller.
 * Created by dell on 2016/7/15.
 */
public class DeviceType {
    public static final String AIRCONTIDION = "AirCondition";
    public static final String TV = "TV";

    public static final int idAirCondition = 1;
    public static final int idTV = 2;

    public static int getDeviceIdByType(String type) {
        if(type.equals(AIRCONTIDION)) {
            return idAirCondition;
        } else if(type.equals(TV)){
            return idTV;
        }
        return -1; //throw exception
    }
}
