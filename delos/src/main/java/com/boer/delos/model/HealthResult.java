package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/19 0019 21:54
 * @Modify:
 * @ModifyDate:
 */


public class HealthResult extends BaseResult {

    /**
     * blood_pressure : {"data":[{"userId":"58eaf55fe4b0ffc75d7842fc","familyMemberId":0,"measuretime":1492429716,"valueH":106,"valueL":70,"bpm":87,"detail":"nothing"}]}
     * blood_glucose : {"data":[{"userId":"58eaf55fe4b0ffc75d7842fc","familyMemberId":0,"mesuredate":1492403400,"measuretime":3,"value":3,"detail":""}]}
     * body_weight : {"data":[{"userId":"58eaf55fe4b0ffc75d7842fc","familyMemberId":0,"measuretime":1492473917,"weight":49.1,"fatrate":29.1,"detail":"{\n  \"muscle\" : \"34.8\",\n  \"Kcal\" : \"1019\",\n  \"BMI\" : \"19.1\",\n  \"water\" : \"54.2\",\n  \"bone\" : \"1.2\"\n}"}]}
     * urine : {"data":[{"userId":"58eaf55fe4b0ffc75d7842fc","familyMemberId":0,"measuretime":1492485774,"detail":"{\n  \"urineUbg\" : \"0.000\",\n  \"urineSg\" : \"6.000\",\n  \"urineTureTime\" : \"1492485774\",\n  \"urineGlu\" : \"0.000\",\n  \"urineLeu\" : \"0.000\",\n  \"urinePh\" : \"0.0\",\n  \"urineVC\" : \"3\",\n  \"urinePro\" : \"0.000\",\n  \"urineNit\" : \"0.000\",\n  \"urineKet\" : \"0.000\",\n  \"urineBld\" : \"0.000\",\n  \"urineBil\" : \"0.000\"\n}"}]}
     * skin : {"data":[]}
     */

    private ResponseBean response;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        private PressureResult.PressureBean blood_pressure;
        private SugarResult.SugarBean blood_glucose;
        private WeightBean body_weight;
        private UrineResult.UrineBean urine;
//        private Skin skin;


        public PressureResult.PressureBean getBlood_pressure() {
            return blood_pressure;
        }

        public void setBlood_pressure(PressureResult.PressureBean blood_pressure) {
            this.blood_pressure = blood_pressure;
        }

        public SugarResult.SugarBean getBlood_glucose() {
            return blood_glucose;
        }

        public void setBlood_glucose(SugarResult.SugarBean blood_glucose) {
            this.blood_glucose = blood_glucose;
        }

        public WeightBean getBody_weight() {
            return body_weight;
        }

        public void setBody_weight(WeightBean body_weight) {
            this.body_weight = body_weight;
        }

        public UrineResult.UrineBean getUrine() {
            return urine;
        }

        public void setUrine(UrineResult.UrineBean urine) {
            this.urine = urine;
        }
    }
}
