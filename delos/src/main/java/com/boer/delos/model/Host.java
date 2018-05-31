package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 主机model
 * create at 2016/5/11 16:02
 *
 */
public class Host implements Serializable{

    private String hostId; // 主机Id
    private String id; // id
    private String name; //	主机名
    private List<Family> families; // 主机数组
    private boolean isExpand;// 判断二级列表是否已展开

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Family> getFamilies() {
        return families;
    }

    public void setFamilies(List<Family> families) {
        this.families = families;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }
}
