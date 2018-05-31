package com.boer.delos.model;

/**
 * Created by Administrator on 2016/11/16 0016.
 * add by sunzhibin
 */

public class VertifyAdminResult {

    /**
     * hostId : 0012025AE47F
     * nickname : 管理员
     * password : 21232f297a57a5a743894a0e4a801fc3
     * status : 0
     * statusinfo :
     * token : f91476115d0e462c3d9a22f370ba5be5241139e3admin
     * updatetime : 2016-11-16 17:11:40
     * username : admin
     */

    private ResponseBean response;
    /**
     * response : {"hostId":"0012025AE47F","nickname":"管理员","password":"21232f297a57a5a743894a0e4a801fc3","status":0,"statusinfo":"","token":"f91476115d0e462c3d9a22f370ba5be5241139e3admin","updatetime":"2016-11-16 17:11:40","username":"admin"}
     * ret : 0
     */

    private int ret;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public static class ResponseBean {
        private String hostId;
        private String nickname;
        private String password;
        private int status;
        private String statusinfo;
        private String token;
        private String updatetime;
        private String username;

        public String getHostId() {
            return hostId;
        }

        public void setHostId(String hostId) {
            this.hostId = hostId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusinfo() {
            return statusinfo;
        }

        public void setStatusinfo(String statusinfo) {
            this.statusinfo = statusinfo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
