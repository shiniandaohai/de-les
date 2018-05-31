package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by gaolong on 2017/4/14.
 */
public class SkinArea implements Serializable {


    /**
     * familyMemberId : 0
     * measuretime : 1492517809
     * detail : {"skin_left":{"water":"59.4%","grease":"25.6%","elastic":"25.4"},"skin_nose":{"water":"51.2%","grease":"20.5%","elastic":"25.4"},"skin_brow":{"water":"59.8%","grease":"23.5%","elastic":"25.4"},"skin_eye":{"water":"21.7%","grease":"21.8%","elastic":"25.4"},"skin_hand":{"water":"46.7%","grease":"19.4%","elastic":"25.4"},"skin_right":{"water":"20.2%","grease":"26.9%","elastic":"25.4"}}
     */

    private String familyMemberId;
    private String measuretime;
    private DetailBean detail;


    public String getFamilyMemberId() {
        return familyMemberId;
    }

    public void setFamilyMemberId(String familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public String getMeasuretime() {
        return measuretime;
    }

    public void setMeasuretime(String measuretime) {
        this.measuretime = measuretime;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }


    public static class DetailBean  implements Serializable{




        /**
         * skin_left : {"water":"59.4%","grease":"25.6%","elastic":"25.4"}
         * skin_nose : {"water":"51.2%","grease":"20.5%","elastic":"25.4"}
         * skin_brow : {"water":"59.8%","grease":"23.5%","elastic":"25.4"}
         * skin_eye : {"water":"21.7%","grease":"21.8%","elastic":"25.4"}
         * skin_hand : {"water":"46.7%","grease":"19.4%","elastic":"25.4"}
         * skin_right : {"water":"20.2%","grease":"26.9%","elastic":"25.4"}
         */

        private SkinLeftBean skin_left;
        private SkinNoseBean skin_nose;
        private SkinBrowBean skin_brow;
        private SkinEyeBean skin_eye;
        private SkinHandBean skin_hand;
        private SkinRightBean skin_right;

        public SkinLeftBean getSkin_left() {
            return skin_left;
        }

        public void setSkin_left(SkinLeftBean skin_left) {
            this.skin_left = skin_left;
        }

        public SkinNoseBean getSkin_nose() {
            return skin_nose;
        }

        public void setSkin_nose(SkinNoseBean skin_nose) {
            this.skin_nose = skin_nose;
        }

        public SkinBrowBean getSkin_brow() {
            return skin_brow;
        }

        public void setSkin_brow(SkinBrowBean skin_brow) {
            this.skin_brow = skin_brow;
        }

        public SkinEyeBean getSkin_eye() {
            return skin_eye;
        }

        public void setSkin_eye(SkinEyeBean skin_eye) {
            this.skin_eye = skin_eye;
        }

        public SkinHandBean getSkin_hand() {
            return skin_hand;
        }

        public void setSkin_hand(SkinHandBean skin_hand) {
            this.skin_hand = skin_hand;
        }

        public SkinRightBean getSkin_right() {
            return skin_right;
        }

        public void setSkin_right(SkinRightBean skin_right) {
            this.skin_right = skin_right;
        }

        public static class SkinLeftBean  implements Serializable{
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

        public static class SkinNoseBean  implements Serializable{
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

        public static class SkinBrowBean  implements Serializable{
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

        public static class SkinEyeBean  implements Serializable{
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

        public static class SkinHandBean  implements Serializable{
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

        public static class SkinRightBean  implements Serializable{
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
}
