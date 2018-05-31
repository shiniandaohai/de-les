package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/7/20.
 */
public class LightName implements Serializable {

    private static final long serialVersionUID = 8438286574950983360L;
    private String name1;
    private String name2;
    private String name3;
    private String name4;

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }
}
