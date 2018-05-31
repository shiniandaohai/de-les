package com.boer.delos.model;

/**
 * Created by gaolong on 2017/11/14.
 */

public class ThirdUser {

    /**************************************eg.*******************************************************/
//    avatarUrl = "https://tvax2.sinaimg.cn/default/images/default_avatar_male_180.gif";
//    gender = "\U7537";
//    name = "\U7528\U62375536474773";
//    sessionKey = 8b4d9b663c59c78ed09228b1250038672c50b408e29e5a673044288113b38b67;
//    thirdPartyAccount = 5536474773;
//    thirdPartyType = weibo;


    String gender;
    String name;
    String thirdPartyType;
    String iconurl;
    String uid;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThirdPartyType() {
        return thirdPartyType;
    }

    public void setThirdPartyType(String thirdPartyType) {
        this.thirdPartyType = thirdPartyType;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
