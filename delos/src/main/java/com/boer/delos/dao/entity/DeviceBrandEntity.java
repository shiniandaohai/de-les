package com.boer.delos.dao.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 设备品牌 ： 格力，三星。。
 * Created by dell on 2016/7/14.
 */
@Table(name="brands")//可以不加，加上避免混淆错误，也可以在混淆时选择不混淆该类
public class DeviceBrandEntity implements BaseEntity {
    @Id//注释可以不加，加上只是增强可读性，避免混淆错误
    @NoAutoIncrement//若主键为int 或long则为自增长，若不需要添加此选项
    private int id;//必须存在id

    @Column(column = "device_id")
    private int device_id;

    @Column(column = "brandname")
    private String brandname;

    @Column(column = "ebrandname")
    private String ebrandname;

    @Column(column = "model_q")
    private int model_q;

    @Column(column = "model_list")
    private String model_list;

    @Column(column = "others")
    private String others;

    public DeviceBrandEntity() {
    }


    public DeviceBrandEntity(int id, int device_id, String brandname, String ebrandname, int model_q, String model_list, String others) {
        this.id = id;
        this.device_id = device_id;
        this.brandname = brandname;
        this.ebrandname = ebrandname;
        this.model_q = model_q;
        this.model_list = model_list;
        this.others = others;
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

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getEbrandname() {
        return ebrandname;
    }

    public void setEbrandname(String ebrandname) {
        this.ebrandname = ebrandname;
    }

    public int getModel_q() {
        return model_q;
    }

    public void setModel_q(int model_q) {
        this.model_q = model_q;
    }

    public String getModel_list() {
        return model_list;
    }

    public void setModel_list(String model_list) {
        this.model_list = model_list;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}