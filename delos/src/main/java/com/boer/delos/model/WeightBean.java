package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author: sunzhibin
 * @e-mail:
 * @Description:
 * @Date 2016/11/18 0018 ${time}
 */


public class WeightBean implements Serializable {
    private int measuretime;
    private float weight;
    private float fatrate;
    private String detail;

    public static class WeightDetailBean {
        /**
         * muscle : 45.4
         * Kcal : 183
         * BMI : 4.1
         * water : 70.0
         * bone : 4.1
         */
        private String muscle;
        private String Kcal;
        private String BMI;
        private String water;
        private String bone;

        public String getMuscle() {
            return muscle;
        }

        public void setMuscle(String muscle) {
            this.muscle = muscle;
        }

        public String getKcal() {
            return Kcal;
        }

        public void setKcal(String kcal) {
            Kcal = kcal;
        }

        public String getBMI() {
            return BMI;
        }

        public void setBMI(String BMI) {
            this.BMI = BMI;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getBone() {
            return bone;
        }

        public void setBone(String bone) {
            this.bone = bone;
        }
    }


    public int getMeasuretime() {
        return measuretime;
    }

    public void setMeasuretime(int measuretime) {
        this.measuretime = measuretime;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getFatrate() {
        return fatrate;
    }

    public void setFatrate(float fatrate) {
        this.fatrate = fatrate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}

