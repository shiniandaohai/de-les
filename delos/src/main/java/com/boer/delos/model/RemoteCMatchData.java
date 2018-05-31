package com.boer.delos.model;

import java.io.Serializable;

/**
 * 红外遥控， 匹配格式数据
 * Created by dell on 2016/7/15.
 */
public class RemoteCMatchData implements Serializable {
    String brandname;
    String c3rv;
    String device_type;
    String fid;
    String format_string;
    String m_code;
    String m_key_squency;
    String m_label;
    String index;

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getIndex() {

        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public RemoteCMatchData() {}

    public RemoteCMatchData(String brandname, String c3rv, String device_type,
                            String fid, String format_string, String m_code,
                            String m_key_squency, String m_label) {
        this.brandname = brandname;
        this.c3rv = c3rv;
        this.device_type = device_type;
        this.fid = fid;
        this.format_string = format_string;
        this.m_code = m_code;
        this.m_key_squency = m_key_squency;
        this.m_label = m_label;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getC3rv() {
        return c3rv;
    }

    public void setC3rv(String c3rv) {
        this.c3rv = c3rv;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        switch (device_type) {
            case 1:
                this.device_type = "AC";
                break;
            case 2:
                this.device_type = "TV";
                break;
            default:
                break;
        }
//        this.device_type = device_type;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFormat_string() {
        return format_string;
    }

    public void setFormat_string(String format_string) {
        this.format_string = format_string;
    }

    public String getM_code() {
        return m_code;
    }

    public void setM_code(String m_code) {
        this.m_code = m_code;
    }

    public String getM_key_squency() {
        return m_key_squency;
    }

    public void setM_key_squency(String m_key_squency) {
        this.m_key_squency = m_key_squency;
    }

    public String getM_label() {
        return m_label;
    }

    public void setM_label(String m_label) {
        this.m_label = m_label;
    }
}
