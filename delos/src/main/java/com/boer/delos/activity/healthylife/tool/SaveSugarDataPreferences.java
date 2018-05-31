package com.boer.delos.activity.healthylife.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 保存血糖仪数据
 */
public class SaveSugarDataPreferences {
    private String value = "0";
    private String date = "";
    private static SaveSugarDataPreferences instance;

    private SaveSugarDataPreferences() {

    }

    public static SaveSugarDataPreferences getInstance() {
        if (instance == null) {
            instance = new SaveSugarDataPreferences();
        }
        return instance;
    }

    public void readSugarDataXml(Context context) {
        SharedPreferences spGet = context.getSharedPreferences("SugarData", Activity.MODE_PRIVATE);
        value = spGet.getString("value", "0");
        date = spGet.getString("date", "");
    }

    public void writeSugarDataXml(Context context, String value, String date) {
        SharedPreferences spSet = context.getSharedPreferences("SugarData", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSet.edit();
        editor.putString("value", value);
        editor.putString("date", date);
        editor.commit();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
