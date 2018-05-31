package com.boer.delos.dao.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 设备型号
 * Created by dell on 2016/7/14.
 */
@Table(name="formats")//可以不加，加上避免混淆错误，也可以在混淆时选择不混淆该类
public class DeviceFormatsEntity implements BaseEntity {
    @Id//注释可以不加，加上只是增强可读性，避免混淆错误
    @NoAutoIncrement//若主键为int 或long则为自增长，若不需要添加此选项
    private int id;//必须存在id

    @Column(column = "fid")
    private int fid;

    @Column(column = "device_id")
    private int device_id;

    @Column(column = "format_name")
    private String format_name;

    @Column(column = "format_string")
    private String format_string;

    @Column(column = "c3rv")
    private String c3rv;

    @Column(column = "matchs")
    private String matchs;

    public DeviceFormatsEntity() {}

    public DeviceFormatsEntity(int id, int fid, int device_id, String format_name, String format_string, String c3rv, String matchs) {
        this.id = id;
        this.fid = fid;
        this.device_id = device_id;
        this.format_name = format_name;
        this.format_string = format_string;
        this.c3rv = c3rv;
        this.matchs = matchs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public int getFid() {
        return fid;
    }*/
    public String getFid() {
        return String.valueOf(fid);
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getFormat_name() {
        return format_name;
    }

    public void setFormat_name(String format_name) {
        this.format_name = format_name;
    }

    public String getFormat_string() {
        return format_string;
    }

    public void setFormat_string(String format_string) {
        this.format_string = format_string;
    }

    public String getC3rv() {
        return c3rv;
    }

    public void setC3rv(String c3rv) {
        this.c3rv = c3rv;
    }

    public String getMatchs() {
        return matchs;
    }

    public void setMatchs(String matchs) {
        this.matchs = matchs;
    }
}