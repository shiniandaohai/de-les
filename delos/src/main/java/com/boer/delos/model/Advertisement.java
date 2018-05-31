package com.boer.delos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangkai on 16/5/30.
 */
public class Advertisement {
    private long timestamp;
    private String title;
    private String detail;
    @SerializedName("iphone-pic")
    private String iphone_pic;
    @SerializedName("ipad-pic")
    private String ipad_pic;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIphone_pic() {
        return iphone_pic;
    }

    public void setIphone_pic(String iphone_pic) {
        this.iphone_pic = iphone_pic;
    }

    public String getIpad_pic() {
        return ipad_pic;
    }

    public void setIpad_pic(String ipad_pic) {
        this.ipad_pic = ipad_pic;
    }
}
