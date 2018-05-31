package com.boer.delos.model;

public class ScanDevice implements java.io.Serializable {
    private static final long serialVersionUID = 8446638930661292586L;
    private String id;
    private String name;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
