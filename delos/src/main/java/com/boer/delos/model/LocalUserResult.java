package com.boer.delos.model;

import java.io.Serializable;

/**
 * Created by zhukang on 16/7/22.
 */
public class LocalUserResult extends BaseResult implements Serializable {

    private static final long serialVersionUID = 6189646516517214847L;
    private String message;
    private String token;
    private User userInfo;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }
}
