package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: create at 2016/5/11 15:51
 */
public class Family implements Serializable {

    private static final long serialVersionUID = 2455460253844806809L;
    private String userId;    // 用户Id
    private String userAlias; // 用户别名
    private String hostId;    // 主机Id
    private String hostAlias; // 主机别名
    private int admin;     // 0:普通用户，1：管理员
    private String permission; // 权限数组（以“,”分隔）
    private int share;     // 0:未分享，1：已分享
    private int applyStatus;  // 申请状态
    private User user;        // 用户对象→注册→user数据
    private List<String> shareIds; //分享用户Ids

    private String limitTime;
    private int limitStatus;

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    public int getLimitStatus() {
        return limitStatus;
    }

    public void setLimitStatus(int limitStatus) {
        this.limitStatus = limitStatus;
    }


    public List<String> getShareIds() {
        return shareIds;
    }

    public void setShareIds(List<String> shareIds) {
        this.shareIds = shareIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostAlias() {
        return hostAlias;
    }

    public void setHostAlias(String hostAlias) {
        this.hostAlias = hostAlias;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass() == o.getClass()) {
            Family family = (Family) o;
            if (this.getUserId().equals(family.getUserId())) {

                return true;
            }
        }
        return false;
    }
}
