package com.boer.delos.model;

import java.util.List;

/**
 * Created by apple on 17/5/2.
 */

public class ShareUser extends BaseResult {


    private List<UserBean> user;

    public List<UserBean> getUser() {
        return user;
    }

    public void setUser(List<UserBean> user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * userId : 58eaf55fe4b0ffc75d7842fc
         * userName : 王王
         * avatarUrl : /image/58f57e1fe4b018fca6ba3c7f
         * mobile : 15152208332
         */

        private String userId;
        private String userName;
        private String avatarUrl;
        private String mobile;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserBean userBean = (UserBean) o;

            return userId.equals(userBean.userId);

        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
