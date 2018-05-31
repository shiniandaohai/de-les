package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description: 绿色生活接口参数中的时间model
 * create at 2016/5/18 15:58
 *
 */
public class Time implements Serializable {

    private String time;

    public Time() {}

    public Time(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
