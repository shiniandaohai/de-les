package com.boer.delos.model;

import java.io.Serializable;

/**
 * @author XieQingTing
 * @Description: 场景显示
 * create at 2016/5/19 16:06
 *
 */
public class SceneDisplayChild implements Serializable {
    // TODO 此处图片应从服务器获取,故resId的类型最终应为String
    private int resId;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    private Device device;

    public SceneDisplayChild(int resId,Device device) {

        this.resId = resId;
        this.device=device;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }



}
