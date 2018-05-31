package com.boer.delos.model;

import java.io.Serializable;

/**
 * 监控快照实体
 * Created by pengjiyang on 2016/3/31.
 */
public class MonitorPicture implements Serializable {

    private int imageName;
    private boolean isDelete;

    private boolean isSelect;

    public MonitorPicture() {}

    public MonitorPicture(int imageName, boolean isDelete, boolean isSelect) {
        this.imageName = imageName;
        this.isDelete = isDelete;
        this.isSelect = isSelect;
    }

    public int getImageName() {
        return imageName;
    }

    public void setImageName(int imageName) {
        this.imageName = imageName;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
