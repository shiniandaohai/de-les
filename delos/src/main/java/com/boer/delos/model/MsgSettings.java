package com.boer.delos.model;

/**
 * Created by gaolong on 2017/5/2.
 */
public class MsgSettings {


    /**
     * unDisturb : xxxxxxxxx
     * unDisturbStartTime : xxxxxxxxx
     * unDisturbEndTime : xxxxxxxxx
     * receiveSystemMessage : xxxxxxxxx
     * receiveAlarmMessage : xxxxxxxxx
     */

    private String unDisturb;
    private String unDisturbStartTime;
    private String unDisturbEndTime;
    private String receiveSystemMessage;
    private String receiveAlarmMessage;

    public String getUnDisturb() {
        return unDisturb;
    }

    public void setUnDisturb(String unDisturb) {
        this.unDisturb = unDisturb;
    }

    public String getUnDisturbStartTime() {
        return unDisturbStartTime;
    }

    public void setUnDisturbStartTime(String unDisturbStartTime) {
        this.unDisturbStartTime = unDisturbStartTime;
    }

    public String getUnDisturbEndTime() {
        return unDisturbEndTime;
    }

    public void setUnDisturbEndTime(String unDisturbEndTime) {
        this.unDisturbEndTime = unDisturbEndTime;
    }

    public String getReceiveSystemMessage() {
        return receiveSystemMessage;
    }

    public void setReceiveSystemMessage(String receiveSystemMessage) {
        this.receiveSystemMessage = receiveSystemMessage;
    }

    public String getReceiveAlarmMessage() {
        return receiveAlarmMessage;
    }

    public void setReceiveAlarmMessage(String receiveAlarmMessage) {
        this.receiveAlarmMessage = receiveAlarmMessage;
    }
}
