package com.boer.delos.model;

/**
 * Created by ACER~ on 2016/4/18.
 */
public class FamilyManageChild {
    private int resID;
    private String contacts;

    public FamilyManageChild() {
    }

    public FamilyManageChild(int resID, String contacts) {
        this.resID = resID;
        this.contacts = contacts;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
}
