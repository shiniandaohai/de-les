package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/14 0014 15:41
 * @Modify:
 * @ModifyDate:
 */


public class PressureResult extends BaseResult {
    /**
     * bpm : 74
     * detail :
     * measuretime : 1488879013
     * valueH : 127
     * valueL : 84
     */
    private List<PressureBean> data;

    public List<PressureBean> getData() {
        return data;
    }
    public void setData(List<PressureBean> data) {
        this.data = data;
    }

    public static class PressureBean {
        private int bpm;
        private String detail;
        private int measuretime;
        private int valueH;
        private int valueL;

        public int getBpm() {
            return bpm;
        }

        public void setBpm(int bpm) {
            this.bpm = bpm;
        }

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

        public int getValueH() {
            return valueH;
        }

        public void setValueH(int valueH) {
            this.valueH = valueH;
        }

        public int getValueL() {
            return valueL;
        }

        public void setValueL(int valueL) {
            this.valueL = valueL;
        }
    }
}
