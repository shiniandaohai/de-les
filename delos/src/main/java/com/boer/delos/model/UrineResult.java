package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 尿检 查询结果
 * @CreateDate: 2017/3/9 0009 13:42
 * @Modify:
 * @ModifyDate:
 */


public class UrineResult extends BaseResult {
    private List<UrineBean> data;

    public static class UrineBean implements Serializable{
        private long measuretime;
        private String detail;

        private String score; //计算 得分

        public long getMeasuretime() {
            return measuretime;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public void setMeasuretime(long measuretime) {
            this.measuretime = measuretime;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    public static class UrineData {
        private String urineBil;
        private String urineBld;
        private String urineGlu;
        private String urineKet;
        private String urineLeu;
        private String urineNit;
        private String urinePh;
        private String urinePro;
        private String urineSg;
        private String urineUbg;
        private String urineVC;
        private String urineTureTime;


        public String getUrineTureTime() {
            return urineTureTime;
        }

        public void setUrineTureTime(String urineTureTime) {
            this.urineTureTime = urineTureTime;
        }

        public String getUrineVC() {
            return urineVC;
        }

        public void setUrineVC(String urineVC) {
            this.urineVC = urineVC;
        }

        public String getUrineBil() {
            return urineBil;
        }

        public void setUrineBil(String urineBil) {
            this.urineBil = urineBil;
        }

        public String getUrineBld() {
            return urineBld;
        }

        public void setUrineBld(String urineBld) {
            this.urineBld = urineBld;
        }

        public String getUrineGlu() {
            return urineGlu;
        }

        public void setUrineGlu(String urineGlu) {
            this.urineGlu = urineGlu;
        }

        public String getUrineKet() {
            return urineKet;
        }

        public void setUrineKet(String urineKet) {
            this.urineKet = urineKet;
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

        public String getUrinePh() {
            return urinePh;
        }

        public void setUrinePh(String urinePh) {
            this.urinePh = urinePh;
        }

        public String getUrinePro() {
            return urinePro;
        }

        public void setUrinePro(String urinePro) {
            this.urinePro = urinePro;
        }

        public String getUrineSg() {
            return urineSg;
        }

        public void setUrineSg(String urineSg) {
            this.urineSg = urineSg;
        }

        public String getUrineUbg() {
            return urineUbg;
        }

        public void setUrineUbg(String urineUbg) {
            this.urineUbg = urineUbg;
        }
    }

    public List<UrineBean> getData() {
        return data;
    }

    public void setData(List<UrineBean> data) {
        this.data = data;
    }


}
