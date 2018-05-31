package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by gaolong on 2017/4/19.
 */
public class SkinDetailChart  implements Serializable{


    private String measuretime;

    public String getMeasuretime() {
        return measuretime;
    }

    public void setMeasuretime(String measuretime) {
        this.measuretime = measuretime;
    }

    /**
     * skin_left : {"water":"59.4%","grease":"25.6%","elastic":"25.4"}
     * skin_nose : {"water":"51.2%","grease":"20.5%","elastic":"25.4"}
     * skin_brow : {"water":"59.8%","grease":"23.5%","elastic":"25.4"}
     * skin_eye : {"water":"21.7%","grease":"21.8%","elastic":"25.4"}
     * skin_hand : {"water":"46.7%","grease":"19.4%","elastic":"25.4"}
     * skin_right : {"water":"20.2%","grease":"26.9%","elastic":"25.4"}
     */

    private SkinArea.DetailBean.SkinLeftBean skin_left;
    private SkinArea.DetailBean.SkinNoseBean skin_nose;
    private SkinArea.DetailBean.SkinBrowBean skin_brow;
    private SkinArea.DetailBean.SkinEyeBean skin_eye;
    private SkinArea.DetailBean.SkinHandBean skin_hand;
    private SkinArea.DetailBean.SkinRightBean skin_right;

    public SkinArea.DetailBean.SkinLeftBean getSkin_left() {
        return skin_left;
    }

    public void setSkin_left(SkinArea.DetailBean.SkinLeftBean skin_left) {
        this.skin_left = skin_left;
    }

    public SkinArea.DetailBean.SkinNoseBean getSkin_nose() {
        return skin_nose;
    }

    public void setSkin_nose(SkinArea.DetailBean.SkinNoseBean skin_nose) {
        this.skin_nose = skin_nose;
    }

    public SkinArea.DetailBean.SkinBrowBean getSkin_brow() {
        return skin_brow;
    }

    public void setSkin_brow(SkinArea.DetailBean.SkinBrowBean skin_brow) {
        this.skin_brow = skin_brow;
    }

    public SkinArea.DetailBean.SkinEyeBean getSkin_eye() {
        return skin_eye;
    }

    public void setSkin_eye(SkinArea.DetailBean.SkinEyeBean skin_eye) {
        this.skin_eye = skin_eye;
    }

    public SkinArea.DetailBean.SkinHandBean getSkin_hand() {
        return skin_hand;
    }

    public void setSkin_hand(SkinArea.DetailBean.SkinHandBean skin_hand) {
        this.skin_hand = skin_hand;
    }

    public SkinArea.DetailBean.SkinRightBean getSkin_right() {
        return skin_right;
    }

    public void setSkin_right(SkinArea.DetailBean.SkinRightBean skin_right) {
        this.skin_right = skin_right;
    }

    public static class SkinLeftBean {
        /**
         * water : 59.4%
         * grease : 25.6%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;
        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }

    public static class SkinNoseBean {
        /**
         * water : 51.2%
         * grease : 20.5%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;
        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }

    public static class SkinBrowBean {
        /**
         * water : 59.8%
         * grease : 23.5%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;
        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }

    public static class SkinEyeBean {
        /**
         * water : 21.7%
         * grease : 21.8%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;
        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }

    public static class SkinHandBean {
        /**
         * water : 46.7%
         * grease : 19.4%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;

        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }

    public static class SkinRightBean {
        /**
         * water : 20.2%
         * grease : 26.9%
         * elastic : 25.4
         */

        private String water;
        private String grease;
        private String elastic;

        private transient boolean completed;

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getWater() {
            return water;
        }

        public void setWater(String water) {
            this.water = water;
        }

        public String getGrease() {
            return grease;
        }

        public void setGrease(String grease) {
            this.grease = grease;
        }

        public String getElastic() {
            return elastic;
        }

        public void setElastic(String elastic) {
            this.elastic = elastic;
        }
    }


}
