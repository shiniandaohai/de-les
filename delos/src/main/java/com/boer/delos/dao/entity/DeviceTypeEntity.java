package com.boer.delos.dao.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 设备类型 空调 电视
 * Created by dell on 2016/7/14.
 */
@Table(name="device")//可以不加，加上避免混淆错误，也可以在混淆时选择不混淆该类
public class DeviceTypeEntity implements BaseEntity{
    @Id//注释可以不加，加上只是增强可读性，避免混淆错误
    @NoAutoIncrement//若主键为int 或long则为自增长，若不需要添加此选项
    private int id ;//必须存在id

    @Column(column="device_name")//可以不写，则创建的表中列名就是address
    private String device_name;

    public DeviceTypeEntity() {}
    public DeviceTypeEntity(int id, String device_name) {
        super();
        this.id = id;
        this.device_name = device_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}