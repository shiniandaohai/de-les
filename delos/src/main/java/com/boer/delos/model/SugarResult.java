package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/17 0017 09:33
 * @Modify:
 * @ModifyDate:
 */


public class SugarResult extends BaseResult {
    /**
     * detail :
     * measuretime : 3
     * mesuredate : 1457497800
     * value : 3
     */
    private List<SugarBean> data;

    public List<SugarBean> getData() {
        return data;
    }

    public void setData(List<SugarBean> data) {
        this.data = data;
    }

    public static class SugarBean {
        private String detail;
        private int measuretime;
        private int mesuredate;
        private float value;

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public int getMeasuretime() {
            return measuretime;
        }

        public void setMeasuretime(int measuretime) {
            this.measuretime = measuretime;
        }

        public int getMesuredate() {
            return mesuredate;
        }

        public void setMesuredate(int mesuredate) {
            this.mesuredate = mesuredate;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }
}
