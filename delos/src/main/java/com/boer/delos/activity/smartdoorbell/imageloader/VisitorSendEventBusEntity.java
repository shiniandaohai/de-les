package com.boer.delos.activity.smartdoorbell.imageloader;

import android.os.Handler;

public class VisitorSendEventBusEntity {
    public static final int EVENT_TYPE_GET_VISITOR_LIST=0;
    public static final int EVENT_TYPE_DEL_ONE_VISITOR=1;
    public static final int EVENT_TYPE_DEL_ALL_VISITOR=2;
    private int eventType;
    private Object eventData;

    public VisitorSendEventBusEntity() {
    }

    public VisitorSendEventBusEntity(int eventType, Handler mHandler, Object eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
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
}
