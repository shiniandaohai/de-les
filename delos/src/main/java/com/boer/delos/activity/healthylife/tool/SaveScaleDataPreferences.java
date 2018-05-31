package com.boer.delos.activity.healthylife.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 保存体脂称数据
 */
public class SaveScaleDataPreferences {
   private String Kg;
   private String BMI;
   private String BodyFatRatio;
   private String MuscleRate;
   private String WaterContent;
   private String Bone;
   private String BasalMetabolism;
   private String time;

    private static SaveScaleDataPreferences instance;
    private SaveScaleDataPreferences() {
    }
    public static SaveScaleDataPreferences getInstance() {
        if (instance == null) {
            instance = new SaveScaleDataPreferences();
        }
        return instance;
    }
    public void readScaleDataXml(Context context) {
        SharedPreferences spGet = context.getSharedPreferences("ScaleData", Activity.MODE_PRIVATE);
        Kg=spGet.getString("Kg", "0");
        BMI=spGet.getString("BMI", "0");
        BodyFatRatio= spGet.getString("BodyFatRatio", "0");
        MuscleRate=spGet.getString("MuscleRate", "0");
        WaterContent=spGet.getString("WaterContent", "0");
        Bone= spGet.getString("Bone", "0");
        BasalMetabolism=spGet.getString("BasalMetabolism", "0");
        time=spGet.getString("time", "0");
    }

    public void writeScaleDataXml(Context context, String Kg, String BMI, String BodyFatRatio
            , String MuscleRate, String WaterContent, String Bone, String BasalMetabolism, String time) {
        SharedPreferences spSet = context.getSharedPreferences("ScaleData", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSet.edit();
        editor.putString("Kg", Kg);
        editor.putString("BMI", BMI);
        editor.putString("BodyFatRatio", BodyFatRatio);
        editor.putString("muscle", MuscleRate);
        editor.putString("water", WaterContent);
        editor.putString("bone", Bone);
        editor.putString("Kcal", BasalMetabolism);
        editor.putString("time", time);
        editor.commit();
    }

    public String getKg() {
        return Kg;
    }

    public void setKg(String kg) {
        Kg = kg;
    }

    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    public String getBodyFatRatio() {
        return BodyFatRatio;
    }

    public void setBodyFatRatio(String bodyFatRatio) {
        BodyFatRatio = bodyFatRatio;
    }

    public String getMuscleRate() {
        return MuscleRate;
    }

    public void setMuscleRate(String muscleRate) {
        MuscleRate = muscleRate;
    }

    public String getWaterContent() {
        return WaterContent;
    }

    public void setWaterContent(String waterContent) {
        WaterContent = waterContent;
    }

    public String getBone() {
        return Bone;
    }

    public void setBone(String bone) {
        Bone = bone;
    }

    public String getBasalMetabolism() {
        return BasalMetabolism;
    }

    public void setBasalMetabolism(String basalMetabolism) {
        BasalMetabolism = basalMetabolism;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
