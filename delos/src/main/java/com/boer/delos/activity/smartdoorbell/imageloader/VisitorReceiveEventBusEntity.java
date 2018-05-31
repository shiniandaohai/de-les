package com.boer.delos.activity.smartdoorbell.imageloader;

public class VisitorReceiveEventBusEntity {
    public static final int EVENT_TYPE_GET_VISITOR_LIST=0;
    private int eventType;
    private Object eventData;

    public VisitorReceiveEventBusEntity() {
    }

    public VisitorReceiveEventBusEntity(int eventType, Object eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public static int getEventTypeGetVisitorList() {
        return EVENT_TYPE_GET_VISITOR_LIST;
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
