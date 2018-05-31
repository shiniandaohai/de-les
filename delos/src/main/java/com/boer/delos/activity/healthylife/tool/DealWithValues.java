package com.boer.delos.activity.healthylife.tool;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 处理获取的数据
 */
public class DealWithValues {
    private static DealWithValues mDealWithValues;

    private DealWithValues() {
    }

    public static synchronized DealWithValues getInstance() {
        if (mDealWithValues == null) {
            mDealWithValues = new DealWithValues();
        }
        return mDealWithValues;
    }

    //处理血糖数据
    public ArrayList<Map> dealWithSugar() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap2(R.color.red, "偏高", ">7.8mmol/L"));
        mapsArrayList.add(customMap2(R.color.green, "正常", "4.0 - 7.8mmol/L"));
        mapsArrayList.add(customMap2(R.color.yellow, "偏低", "<4.0mmol/L"));

        return mapsArrayList;
    }

    //血压
    public ArrayList<Map> dealWithPressure() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap(R.color.red, "单纯收缩期高血压", "高压>140或低压<90"));
        mapsArrayList.add(customMap(Color.argb(220, 44, 26, 1), "3级高血压", "高压>180或低压<110"));
        mapsArrayList.add(customMap(Color.argb(226, 111, 80, 1), "2级高血压", "高压160-179或低压100-109"));
        mapsArrayList.add(customMap(Color.argb(228, 138, 104, 1), "1级高血压", "高压140-159或低压90-99"));
        mapsArrayList.add(customMap(Color.argb(229, 139, 116, 1), "正常高值", "高压120-139或低压80-89"));

        mapsArrayList.add(customMap(R.color.green, "正常血压", "高压90-120或低压60-80"));

        mapsArrayList.add(customMap(R.color.yellow, "低血压", "高压<90或低压<60"));

        return mapsArrayList;
    }

    //血压
    public ArrayList<Map> dealWithPressureChart2() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap(getColor(R.color.red_bp_1), "单纯收缩期高血压", "高压>140或低压<90"));
        mapsArrayList.add(customMap(getColor(R.color.red_bp_2), "3级高血压", "高压>180或低压<110"));
        mapsArrayList.add(customMap(getColor(R.color.red_bp_3), "2级高血压", "高压160-179或低压100-109"));
        mapsArrayList.add(customMap(getColor(R.color.red_bp_4), "1级高血压", "高压140-159或低压90-99"));
        mapsArrayList.add(customMap(getColor(R.color.green_bp_1), "正常高值", "高压120-139或低压80-89"));

        mapsArrayList.add(customMap(getColor(R.color.green_bp_2), "正常血压", "高压90-120或低压60-80"));

        mapsArrayList.add(customMap(getColor(R.color.yellow_bp), "低血压", "高压<90或低压<60"));

        return mapsArrayList;
    }

    //心率
    public ArrayList<Map> dealWithHeartRate() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap2(getColor(R.color.green_bp), "心动过速", ">100"));
        mapsArrayList.add(customMap2(R.color.green, "正常", "60-100"));
        mapsArrayList.add(customMap2(R.color.yellow, "心动过缓", "<60"));
        return mapsArrayList;
    }

    //心率
    public ArrayList<Map> dealWithHeartRate2() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap2(getColor(R.color.red_bp_1), "心动过速", ">100"));
        mapsArrayList.add(customMap2(getColor(R.color.green_bp_1), "正常", "60-100"));
        mapsArrayList.add(customMap2(getColor(R.color.yellow_bp), "心动过缓", "<60"));
        return mapsArrayList;
    }


    public ArrayList<Map> dealWithWeight() {
        ArrayList<Map> mapsArrayList = new ArrayList<Map>();
        mapsArrayList.add(customMap2(R.color.red, "超级肥胖", "≥40kg/m²"));
        mapsArrayList.add(customMap2(Color.argb(220, 44, 26, 1), "严重肥胖", "30 - 39.9kg/m²"));
        mapsArrayList.add(customMap2(Color.argb(226, 111, 80, 1), "肥胖", "28 - 29.9kg/m²"));

        mapsArrayList.add(customMap2(Color.argb(228, 138, 104, 1), "超重", "24 - 27.9kg/m²"));
        mapsArrayList.add(customMap2(R.color.green, "正常", "18.5 - 23.9kg/m²"));
        mapsArrayList.add(customMap2(R.color.yellow, "消瘦", "<18.5kg/m²"));

        return mapsArrayList;
    }

    private int getColor(int resId) {
        Context mContext = BaseApplication.getAppContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getColor(resId);
        } else {
            return mContext.getResources().getColor(resId);
        }
    }

    /**
     * 根据高低压判断档次 --> dealWithPressure()
     *
     * @param highValue
     * @param lowValue
     * @return
     */
    public static int judgeBloodPressureState(int highValue, int lowValue) {
        int i;
        if (highValue > 140 && lowValue < 90) {
            i = 0;
        } else if (highValue >= 180 || lowValue > 110) {
            i = 1;
        } else if ((highValue >= 160 && highValue < 180) || (lowValue >= 100 && lowValue < 110)) {
            i = 2;
        } else if ((highValue >= 140 && highValue < 160) || (lowValue >= 90 && lowValue < 100)) {
            i = 3;
        } else if ((highValue >= 120 && highValue < 140) || (lowValue >= 80 && lowValue < 90)) {
            i = 4;
        } else if ((highValue >= 90 && highValue < 120) || (lowValue >= 60 && lowValue < 80)) {
            i = 5;
        } else {
            i = 6;
        }
        return i;
    }

    /**
     * 体重 标准 --> dealWithWeight()
     *
     * @param BMINum
     * @return
     */
    public static int judgeWeightState(float BMINum) {
        int i;
        if (BMINum >= 40) {
            i = 0;
        } else if (BMINum >= 30 && BMINum < 40) {
            i = 1;
        } else if (BMINum >= 28 && BMINum < 30) {
            i = 2;
        } else if (BMINum >= 24 && BMINum < 28) {
            i = 3;
        } else if (BMINum >= 18.5 && BMINum < 24) {
            i = 4;
        } else {
            i = 5;
        }
        return i;
    }

    public static int judgeHeartRateState(int heartRateNum) {
        int i = 1;
        if (heartRateNum > 100) {
            i = 0;
        } else if (heartRateNum < 60) {
            i = 2;
        } else {
            i = 1;
        }
        return i;
    }

    /**
     * 血糖档次 --> dealWithSugar()
     *
     * @param value
     * @return
     */
    public static int judgeBloodSugarState(float value) {
        int i;
        if (value > 7.8) {
            i = 0;
        } else if (value < 4.0) {
            i = 2;
        } else {
            i = 1;
        }

        if (value == 7.8 || value == 4.0) {
            i = 1;
        }
        return i;
    }


    //自定义map
    private Map customMap(int color, String title, String text) {
        Map map = new HashMap();
        map.put("color", color);
        map.put("title", title);
        map.put("text", text);
        return map;
    }

    //自定义map
    private Map customMap2(int color, String title, String value) {
        Map map = new HashMap();
        map.put("color", color);
        map.put("title", title);
        map.put("value", value);
        return map;
    }

}
