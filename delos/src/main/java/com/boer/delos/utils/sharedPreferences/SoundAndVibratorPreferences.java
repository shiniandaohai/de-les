package com.boer.delos.utils.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 保存体脂称数据
 */
public class SoundAndVibratorPreferences {
    public String readSoundAndVibratorXml(Context context){
        SharedPreferences spGet=context.getSharedPreferences("SoundAndVibrator", Activity.MODE_PRIVATE);
        return   spGet.getString("tone", "0")+","+  spGet.getString("vibration", "0");
    }
    /**
     * @param context
     * @param tone 铃声 0~5
     * @param vibration 震动 0：无震动，1：震动
     */
    public void writeSoundAndVibratorDataXml(Context context,String tone,String vibration){
        SharedPreferences spSet=context.getSharedPreferences("SoundAndVibrator",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=spSet.edit();
        editor.putString("tone", tone);
        editor.putString("vibration", vibration);
        editor.commit();
    }

}
