package com.boer.delos.activity.smartdoorbell.imageloader;

import android.os.Handler;

public class CommonEventBusEntity {
    private int eventType;
    private Object eventData;
    private Handler mHandler;

    public CommonEventBusEntity() {
    }

    public CommonEventBusEntity(int eventType, Object eventData, Handler mHandler) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.mHandler = mHandler;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Object getEventData() {
        return eventData;
    }

    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
