package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 报警电话
 * create at 2016/5/17 10:59
 *
 */
public class AlarmPhone implements Serializable{
    private String phone;
    public AlarmPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
