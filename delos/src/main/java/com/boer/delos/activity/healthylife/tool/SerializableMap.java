package com.boer.delos.activity.healthylife.tool;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 * 用来在bundle中传递map
 */
public class SerializableMap implements Serializable {
    private Map<String,String> stringMap;
    private Map<String,int[]> intsMap;

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public Map<String, int[]> getIntsMap() {
        return intsMap;
    }

    public void setIntsMap(Map<String, int[]> intsMap) {
        this.intsMap = intsMap;
    }
}
