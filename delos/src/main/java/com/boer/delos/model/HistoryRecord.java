package com.boer.delos.model;

import java.io.Serializable;

/**
 * 录像记录实体类
 * Created by pengjiyang on 2016/3/31.
 */
public class HistoryRecord implements Serializable {

    private int imageName;// 图片在服务器上的路径
    private String top;// 每个条目内容的最上方的文本
    private String center;// 每个条目内容的中间的文本
    private String bottom;// 每个条目内容的最下方的文本
    private boolean isDelete;// 是否删除

    public HistoryRecord(){}

    public HistoryRecord(int imageName, String top, String center, String bottom, boolean isDelete) {
        this.imageName = imageName;
        this.top = top;
        this.center = center;
        this.bottom = bottom;
        this.isDelete = isDelete;
    }

    public int getImageName() {
        return imageName;
    }

    public void setImageName(int imageName) {
        this.imageName = imageName;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
