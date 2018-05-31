package com.boer.delos.model;

/**
 * Created by zhukang on 16/7/13.
 */
public class EncryResult {

    private String sresult;

    public String getSresult() {
        if(sresult == null){
            sresult = "";
        }
        return sresult;
    }

    public void setSresult(String sresult) {
        this.sresult = sresult;
    }
}
