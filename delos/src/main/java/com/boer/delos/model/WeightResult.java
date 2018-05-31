package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @e-mail:
 * @Description: "新增设备"适配器
 * @Date 2016/11/18 0018 ${time}
 */


public class WeightResult extends BaseResult{


    /**
     * ret : 0
     * data : [{"measuretime":1479461283,"weight":12,"fatrate":5,"detail":"{\n  \"muscle\" : \"45.4\",\n  \"Kcal\" : \"183\",\n  \"BMI\" : \"4.1\",\n  \"water\" : \"70.0\",\n  \"bone\" : \"4.1\"\n}"},{"measuretime":1479452348,"weight":14,"fatrate":5,"detail":"{\n  \"muscle\" : \"45.7\",\n  \"Kcal\" : \"216\",\n  \"BMI\" : \"4.8\",\n  \"water\" : \"70.0\",\n  \"bone\" : \"3.5\"\n}"}]
     */

    /**
     * measuretime : 1479461283
     * weight : 12.0
     * fatrate : 5.0
     * detail : {
     "muscle" : "45.4",
     "Kcal" : "183",
     "BMI" : "4.1",
     "water" : "70.0",
     "bone" : "4.1"
     }
     */

    private List<WeightBean> data;

    public List<WeightBean> getData() {
        return data;
    }

    public void setData(List<WeightBean> data) {
        this.data = data;
    }


}
