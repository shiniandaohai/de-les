package com.boer.delos.activity.healthylife.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/4 0004.
 * 保存尿检仪数据
 */
public class SaveUrineDataPreferences {
    private String urineLeu;
    private String urineNit;
    private String urineUbg;
    private String urinePro;
    private String urinePh;
    private String urineBld;
    private String urineSg;
    private String urineKet;
    private String urineBil;
    private String urineGlu;
    private String urineVC;
    private String date = "";

    /*尿检的分*/
    private String urineScore;

    private static SaveUrineDataPreferences instance;

    private SaveUrineDataPreferences() {

    }

    public static SaveUrineDataPreferences getInstance() {
        if (instance == null) {
            instance = new SaveUrineDataPreferences();
        }
        return instance;
    }

    public void readUrineDataXml(Context context) {
        SharedPreferences spGet = context.getSharedPreferences("UrineData", Activity.MODE_PRIVATE);
        urineLeu = spGet.getString("urineLeu", "0");
        urineNit = spGet.getString("urineNit", "0");
        urineUbg = spGet.getString("urineUbg", "0");
        urinePro = spGet.getString("urinePro", "0");
        urinePh = spGet.getString("urinePh", "0");
        urineBld = spGet.getString("urineBld", "0");
        urineSg = spGet.getString("urineSg", "0");
        urineKet = spGet.getString("urineKet", "0");
        urineBil = spGet.getString("urineBil", "0");
        urineGlu = spGet.getString("urineGlu", "0");
        urineVC = spGet.getString("urineVC", "0");
        urineScore = spGet.getString("urineScore", "0");
        date = spGet.getString("date", "0");
    }

    public void writeUrineDataXml(Context context, String urineLeu,
                                  String urineNit,
                                  String urineUbg,
                                  String urinePro,
                                  String urinePh,
                                  String urineBld,
                                  String urineSg,
                                  String urineKet,
                                  String urineBil,
                                  String urineGlu,
                                  String urineVC,
                                  String urineScore,
                                  String date) {
        SharedPreferences spSet = context.getSharedPreferences("UrineData", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spSet.edit();
        editor.putString("urineLeu", urineLeu);
        editor.putString("urineNit", urineNit);
        editor.putString("urineUbg", urineUbg);
        editor.putString("urinePro", urinePro);
        editor.putString("urinePh", urinePh);
        editor.putString("urineBld", urineBld);
        editor.putString("urineSg", urineSg);
        editor.putString("urineKet", urineKet);
        editor.putString("urineBil", urineBil);
        editor.putString("urineGlu", urineGlu);
        editor.putString("urineVC", urineVC);
        editor.putString("urineScore", urineScore);//urineScore = spGet.readString("urineScore", "0");
        editor.putString("date", date);
        editor.commit();
    }

    public String getUrineScore() {
        return urineScore;
    }

    public void setUrineScore(String urineScore) {
        this.urineScore = urineScore;
    }

    public String getUrineLeu() {
        return urineLeu;
    }

    public void setUrineLeu(String urineLeu) {
        this.urineLeu = urineLeu;
    }

    public String getUrineNit() {
        return urineNit;
    }

    public void setUrineNit(String urineNit) {
        this.urineNit = urineNit;
    }

    public String getUrineUbg() {
        return urineUbg;
    }

    public void setUrineUbg(String urineUbg) {
        this.urineUbg = urineUbg;
    }

    public String getUrinePro() {
        return urinePro;
    }

    public void setUrinePro(String urinePro) {
        this.urinePro = urinePro;
    }

    public String getUrinePh() {
        return urinePh;
    }

    public void setUrinePh(String urinePh) {
        this.urinePh = urinePh;
    }

    public String getUrineBld() {
        return urineBld;
    }

    public void setUrineBld(String urineBld) {
        this.urineBld = urineBld;
    }

    public String getUrineSg() {
        return urineSg;
    }

    public void setUrineSg(String urineSg) {
        this.urineSg = urineSg;
    }

    public String getUrineKet() {
        return urineKet;
    }

    public void setUrineKet(String urineKet) {
        this.urineKet = urineKet;
    }

    public String getUrineBil() {
        return urineBil;
    }

    public void setUrineBil(String urineBil) {
        this.urineBil = urineBil;
    }

    public String getUrineGlu() {
        return urineGlu;
    }

    public void setUrineGlu(String urineGlu) {
        this.urineGlu = urineGlu;
    }

    public String getUrineVC() {
        return urineVC;
    }

    public void setUrineVC(String urineVC) {
        this.urineVC = urineVC;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
