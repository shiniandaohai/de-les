package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhukang on 16/7/25.
 */
public class ApplyInfosResult extends BaseResult implements Serializable{

    /**
     * applyStatus : 2
     * applyUser : {"birthday":"2016年11月10日","constellation":"天蝎座","email":"11111@163.com","height":0,"hostId":["49862ec8a4ba08bb304f8bff","323f8f317fe172547668a38b"],"id":"581b0311e4b0fb68433764db","mobile":"18300602756","name":"测得天啊啦啦","remark":"天空战记JJ6组HK咯天津蓟","sex":"0","signature":"天空战记JJ6组HK咯天津蓟","weight":0}
     * hostId : 000007D15A3E
     * hostName : 5A3E
     * permission : [""]
     * remark :
     * status : 0
     * user : {"avatarUrl":"/image/5860cd80e4b0281fb377f9e5","birthday":"1989年06月01日","constellation":"双子座","email":"","height":0,"hostId":["49862ec8a4ba08bb304f8bff","778c7cbcd4d854afbc8f29e0","4494572a99c5a2d703463ed9","c853983db4e8aa5ea86e388f"],"id":"578ca8c0e4b0e9a2fcbf448a","mobile":"18262253113","name":"ChenJC","remark":"ChenJC","sex":"0","signature":"ChenJC","weight":0}
     */

    private List<AppliesBean> applies;

    public List<AppliesBean> getApplies() {
        return applies;
    }

    public void setApplies(List<AppliesBean> applies) {
        this.applies = applies;
    }

    public static class AppliesBean {
        private int applyStatus;
        /**
         * birthday : 2016年11月10日
         * constellation : 天蝎座
         * email : 11111@163.com
         * height : 0
         * hostId : ["49862ec8a4ba08bb304f8bff","323f8f317fe172547668a38b"]
         * id : 581b0311e4b0fb68433764db
         * mobile : 18300602756
         * name : 测得天啊啦啦
         * remark : 天空战记JJ6组HK咯天津蓟
         * sex : 0
         * signature : 天空战记JJ6组HK咯天津蓟
         * weight : 0
         */

        private User applyUser;
        private String hostId;
        private String hostName;
        private String remark;
        private int status;
        /**
         * avatarUrl : /image/5860cd80e4b0281fb377f9e5
         * birthday : 1989年06月01日
         * constellation : 双子座
         * email :
         * height : 0
         * hostId : ["49862ec8a4ba08bb304f8bff","778c7cbcd4d854afbc8f29e0","4494572a99c5a2d703463ed9","c853983db4e8aa5ea86e388f"]
         * id : 578ca8c0e4b0e9a2fcbf448a
         * mobile : 18262253113
         * name : ChenJC
         * remark : ChenJC
         * sex : 0
         * signature : ChenJC
         * weight : 0
         */

        private List<String> permission;


        public User getApplyUser() {
            return applyUser;
        }

        public void setApplyUser(User applyUser) {
            this.applyUser = applyUser;
        }

        public int getApplyStatus() {
            return applyStatus;
        }

        public void setApplyStatus(int applyStatus) {
            this.applyStatus = applyStatus;
        }

        public String getHostId() {
            return hostId;
        }

        public void setHostId(String hostId) {
            this.hostId = hostId;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
        public List<String> getPermission() {
            return permission;
        }

        public void setPermission(List<String> permission) {
            this.permission = permission;
        }

    }
}
