package com.boer.delos.utils;

import android.content.SharedPreferences;

import com.boer.delos.commen.BaseApplication;


/**
 * @author wangkai
 * @Description: 存取Json缓存
 * create at 2015/11/3 16:20
 */
public class SharedUtils {
    private SharedPreferences sharedPreferences;
    private static SharedUtils instance = null;

    public static SharedUtils getInstance() {
        if (instance == null) {
            synchronized (SharedUtils.class) {
                if (instance == null) {
                    instance = new SharedUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 通过TAG取缓存对象
     *
     * @param TAG
     * @return
     */
    public String getTagSp(String TAG) {
        sharedPreferences = BaseApplication.getSharedPreferences();
        try {
            String s = sharedPreferences.getString(TAG, null);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 缓存
     *
     * @param TAG
     */
    public void saveJsonByTag(String TAG, String s) {
        sharedPreferences = BaseApplication.getSharedPreferences();
        sharedPreferences.edit().putString(TAG, s).commit();
    }

    /**
     * 缓存整型数据
     * @param TAG
     * @param positon
     */
    public void saveIntDataByTag(String TAG,int positon){
        sharedPreferences = BaseApplication.getSharedPreferences();
        sharedPreferences.edit().putInt(TAG, positon).commit();
    }

    public int  getIntDataByTag(String TAG){
        sharedPreferences = BaseApplication.getSharedPreferences();
        try {
            int p = sharedPreferences.getInt(TAG,0);
            return p;
        } catch (Exception e) {
            return -1;
        }
    }

}