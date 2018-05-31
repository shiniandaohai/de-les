package com.boer.delos.model;

/**
 * Created by gaolong on 2017/4/22.
 */
public class SmartMirror {

    /**
     * id : 44-A8-22-EB-C4-A7
     * clientId : 120c83f76014388a79a
     * model : smartMirror
     * specification : 185-V-2-8
     * remark : 厨房
     * timestamp : 1492851865893
     */

    private String id;
    private String clientId;
    private String model;
    private String specification;
    private String remark;
    private long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
