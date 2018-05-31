package com.boer.delos.dao.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 设备型号
 * Created by dell on 2016/7/14.
 */
@Table(name="model")//可以不加，加上避免混淆错误，也可以在混淆时选择不混淆该类
public class DeviceModelEntity implements BaseEntity {
    @Id//注释可以不加，加上只是增强可读性，避免混淆错误
    @NoAutoIncrement//若主键为int 或long则为自增长，若不需要添加此选项
    private int id;//必须存在id

    @Column(column = "device_id")
    private int device_id;

    @Column(column = "m_code")
    private String m_code;

    @Column(column = "m_label")
    private String m_label;

    @Column(column = "m_search_string")
    private String m_search_string;

    @Column(column = "m_format_id")
    private String m_format_id;

    @Column(column = "m_keyfile")
    private String m_keyfile;

    @Column(column = "m_key_squency")
    private int m_key_squency;



    //for show
    private String mBrandanModel;

    //for remotecontroller
    private String mBrandName;

    public DeviceModelEntity() { }

    public DeviceModelEntity(int id, int device_id, String m_code, String m_label, String m_search_string, String m_format_id, String m_keyfile, int m_key_squency, String mBrandanModel, String mBrandName) {
        this.id = id;
        this.device_id = device_id;
        this.m_code = m_code;
        this.m_label = m_label;
        this.m_search_string = m_search_string;
        this.m_format_id = m_format_id;
        this.m_keyfile = m_keyfile;
        this.m_key_squency = m_key_squency;
        this.mBrandanModel = mBrandanModel;
        this.mBrandName = mBrandName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getM_code() {
        return m_code;
    }

    public void setM_code(String m_code) {
        this.m_code = m_code;
    }

    public String getM_label() {
        return m_label;
    }

    public void setM_label(String m_label) {
        this.m_label = m_label;
    }

    public String getM_search_string() {
        return m_search_string;
    }

    public void setM_search_string(String m_search_string) {
        this.m_search_string = m_search_string;
    }

    public String getM_format_id() {
        return m_format_id;
    }

    public void setM_format_id(String m_format_id) {
        this.m_format_id = m_format_id;
    }

    public String getM_keyfile() {
        return m_keyfile;
    }

    public void setM_keyfile(String m_keyfile) {
        this.m_keyfile = m_keyfile;
    }

    public int getM_key_squency() {
        return m_key_squency;
    }

    public void setM_key_squency(int m_key_squency) {
        this.m_key_squency = m_key_squency;
    }

    public String getmBrandanModel() {
        return mBrandanModel;
    }

    public void setmBrandanModel(String mBrandanModel) {
        this.mBrandanModel = mBrandanModel;
    }

    public String getmBrandName() {
        return mBrandName;
    }

    public void setmBrandName(String mBrandName) {
        this.mBrandName = mBrandName;
    }

}