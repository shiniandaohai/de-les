package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author PengJiYang
 * @Description: 绿色生活接口参数中的地址model
 * create at 2016/5/18 15:56
 *
 */
public class Addr implements Serializable{

    private static final long serialVersionUID = -5237311344237642093L;
    private String addr;
    private String name;
    public Addr() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Addr(String addr) {
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
