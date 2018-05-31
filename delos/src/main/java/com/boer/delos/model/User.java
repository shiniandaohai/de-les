package com.boer.delos.model;

import java.util.List;

public class User implements java.io.Serializable {
    private static final long serialVersionUID = -7020619477594468968L;
    private String id;
    private String name;
    private String mobile;
    private String email;
    //        private List<Host> hostId;
    private List<Object> hostId; //Object 代表：Host/String
    private String uuid;
    private String avatarUrl;
    private String signature;
    private String remark;
    private String birthday;
    private String sex;// 性别：0:男，1:女
    private String constellation;//星座
    private float weight;
    private float height;
    private String inviteCode;

    public List<Object> getHostId() {
        return hostId;
    }

    public void setHostId(List<Object> hostId) {
        this.hostId = hostId;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        if (sex == null) {
            sex = "0";
        }
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getConstellation() {
        if (constellation == null) {
            constellation = "";
        }
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getRemark() {
        if (remark == null) {
            remark = "";
        }
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

//    public List<Host> getHostId() {
//        return this.hostId;
//    }
//
//    public void setHostId(List<Host> hostId) {
//        this.hostId = hostId;
//    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getEmail() {
        if (this.email == null) {
            this.email = "";
        }
        return this.email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        if (avatarUrl == null) {
            avatarUrl = "";
        }
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getInviteCode() {
        return this.inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMobile() {
        if (mobile == null) {
            mobile = "";
        }
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
