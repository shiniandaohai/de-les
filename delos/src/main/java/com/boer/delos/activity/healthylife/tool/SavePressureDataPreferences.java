package com.boer.delos.activity.healthylife.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 保存血压仪数据
 */
public class SavePressureDataPreferences {
    private String familyMemberId;
    private String measuretime;
    private String valueH;
    private String valueL;
    private String bpm;
    private String detail;
    private String date;

    private static SavePressureDataPreferences instance;

    private SavePressureDataPreferences() {

    }

    public static SavePressureDataPreferences getInstance() {
        if (instance == null) {
            instance = new SavePressureDataPreferences();
        }
        return instance;
    }


    public void readPressureDataXml(Context context) {
        SharedPreferences spGet = context.getSharedPreferences("PressureData", Activity.MODE_PRIVATE);
        familyMemberId = spGet.getString("familyMemberId", "");
        measuretime = spGet.getString("measuretime", "");
        valueH = spGet.getString("valueH", "0");
        valueL = spGet.getString("valueL", "0");
        bpm = spGet.getString("bpm", "");
        detail = spGet.getString("detail", "");
        date = spGet.getString("date", "");
    }

    public void writePressureDataXml(Context context, String measuretime, String valueH
            , String valueL, String bpm, String date) {
        SharedPreferences spSet = context.getSharedPreferences("PressureData", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSet.edit();
        editor.putString("familyMemberId", "0");
        editor.putString("measuretime", measuretime);
        editor.putString("valueH", valueH);
        editor.putString("valueL", valueL);
        editor.putString("bpm", bpm);
        editor.putString("detail", "");
        editor.putString("date", date);
        editor.commit();
    }

    public String getFamilyMemberId() {
        return familyMemberId;
    }

    public void setFamilyMemberId(String familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public String getMeasuretime() {
        return measuretime;
    }

    public void setMeasuretime(String measuretime) {
        this.measuretime = measuretime;
    }

    public String getValueH() {
        return valueH;
    }

    public void setValueH(String valueH) {
        this.valueH = valueH;
    }

    public String getValueL() {
        return valueL;
    }

    public void setValueL(String valueL) {
        this.valueL = valueL;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
