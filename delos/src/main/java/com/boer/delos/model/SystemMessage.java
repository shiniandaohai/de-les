package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class SystemMessage {


    /**
     * ret : 0
     * msgList : [{"msgId":"579876fde4b0a9b1faa8e7df","msgTime":"2016-07-27","fromUser":{"id":"57690ad3e4b09162f72344e1","name":"default","mobile":"18761539299","email":"","hostId":["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"default","birthday":"1965年10月12日","constellation":null,"height":0,"weight":0,"sex":"0","inviteCode":"ejx9wl"},"toUser":{"id":"565e55a1e4b0e3e95871252d","name":"123456","mobile":"13395140022","email":"","hostId":[],"uuid":null,"avatarUrl":null,"signature":"default","remark":"123456","birthday":"1965年10月12日","constellation":"天秤座","height":0,"weight":0,"sex":"0","inviteCode":null},"detail":"","msgType":1001,"isRead":false,"hostRealId":"0012025AE47F","hostName":"Lihu-offi","timestamp":1469609725,"extra":{"applyStatus":1,"status":5},"toUserExtra":{"permissions":null,"isInBlackList":false}},{"msgId":"57985961e4b025b6bafeda8d","msgTime":"2016-07-27","fromUser":{"id":"57690ad3e4b09162f72344e1","name":"default","mobile":"18761539299","email":"","hostId":["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"default","birthday":"1965年10月12日","constellation":null,"height":0,"weight":0,"sex":"0","inviteCode":"ejx9wl"},"toUser":{"id":"565bda3ae4b0f07034cc8c07","name":"123456","mobile":"13395140020","email":"123@126.com","hostId":["49862ec8a4ba08bb304f8bff"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"123456","birthday":"1965年10月12日","constellation":"天秤座","height":0,"weight":0,"sex":"0","inviteCode":null},"detail":"解绑主机","msgType":20005,"isRead":false,"hostRealId":"0012025AE47F","hostName":"Lihu-offi","timestamp":1469602145,"extra":"","toUserExtra":""},{"msgId":"5798595ae4b025b6bafeda86","msgTime":"2016-07-27","fromUser":{"id":"57690ad3e4b09162f72344e1","name":"default","mobile":"18761539299","email":"","hostId":["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"default","birthday":"1965年10月12日","constellation":null,"height":0,"weight":0,"sex":"0","inviteCode":"ejx9wl"},"toUser":{"id":"565e55a1e4b0e3e95871252d","name":"123456","mobile":"13395140022","email":"","hostId":[],"uuid":null,"avatarUrl":null,"signature":"default","remark":"123456","birthday":"1965年10月12日","constellation":"天秤座","height":0,"weight":0,"sex":"0","inviteCode":null},"detail":"解绑主机","msgType":20005,"isRead":false,"hostRealId":"0012025AE47F","hostName":"Lihu-offi","timestamp":1469602138,"extra":"","toUserExtra":""},{"msgId":"57985956e4b025b6bafeda81","msgTime":"2016-07-27","fromUser":{"id":"57690ad3e4b09162f72344e1","name":"default","mobile":"18761539299","email":"","hostId":["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"default","birthday":"1965年10月12日","constellation":null,"height":0,"weight":0,"sex":"0","inviteCode":"ejx9wl"},"toUser":{"id":"566942d3e4b02f5643f3ffb3","name":"","mobile":"13395140005","email":"","hostId":["224ecd31fb541a8aa9f97c48","49862ec8a4ba08bb304f8bff"],"uuid":null,"avatarUrl":null,"signature":null,"remark":null,"birthday":null,"constellation":null,"height":null,"weight":null,"sex":null,"inviteCode":null},"detail":"解绑主机","msgType":20005,"isRead":false,"hostRealId":"0012025AE47F","hostName":"Lihu-offi","timestamp":1469602134,"extra":"","toUserExtra":""}]
     */

    private int ret;
    /**
     * msgId : 579876fde4b0a9b1faa8e7df
     * msgTime : 2016-07-27
     * fromUser : {"id":"57690ad3e4b09162f72344e1","name":"default","mobile":"18761539299","email":"","hostId":["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"],"uuid":null,"avatarUrl":null,"signature":"default","remark":"default","birthday":"1965年10月12日","constellation":null,"height":0,"weight":0,"sex":"0","inviteCode":"ejx9wl"}
     * toUser : {"id":"565e55a1e4b0e3e95871252d","name":"123456","mobile":"13395140022","email":"","hostId":[],"uuid":null,"avatarUrl":null,"signature":"default","remark":"123456","birthday":"1965年10月12日","constellation":"天秤座","height":0,"weight":0,"sex":"0","inviteCode":null}
     * detail :
     * msgType : 1001
     * isRead : false
     * hostRealId : 0012025AE47F
     * hostName : Lihu-offi
     * timestamp : 1469609725
     * extra : {"applyStatus":1,"status":5}
     * toUserExtra : {"permissions":null,"isInBlackList":false}
     */

    private List<MsgListBean> msgList;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<MsgListBean> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MsgListBean> msgList) {
        this.msgList = msgList;
    }

    public static class MsgListBean implements Serializable {
        private static final long serialVersionUID = -5473683681801695568L;
        private String msgId;
        private String msgTime;
        /**
         * id : 57690ad3e4b09162f72344e1
         * name : default
         * mobile : 18761539299
         * email :
         * hostId : ["778c7cbcd4d854afbc8f29e0","323f8f317fe172547668a38b","c853983db4e8aa5ea86e388f"]
         * uuid : null
         * avatarUrl : null
         * signature : default
         * remark : default
         * birthday : 1965年10月12日
         * constellation : null
         * height : 0
         * weight : 0
         * sex : 0
         * inviteCode : ejx9wl
         */

//        "limitTime": "1493465391726",
//        "limitStatus": 1,


        private String limitTime;
        private String limitStatus;

        public String getLimitTime() {
            return limitTime;
        }

        public void setLimitTime(String limitTime) {
            this.limitTime = limitTime;
        }

        public String getLimitStatus() {
            return limitStatus;
        }

        public void setLimitStatus(String limitStatus) {
            this.limitStatus = limitStatus;
        }

        private FromUserBean fromUser;
        /**
         * id : 565e55a1e4b0e3e95871252d
         * name : 123456
         * mobile : 13395140022
         * email :
         * hostId : []
         * uuid : null
         * avatarUrl : null
         * signature : default
         * remark : 123456
         * birthday : 1965年10月12日
         * constellation : 天秤座
         * height : 0
         * weight : 0
         * sex : 0
         * inviteCode : null
         */

        private ToUserBean toUser;
        private String detail;
        private int msgType;
        private boolean isRead;
        private String hostRealId;
        private String hostName;
        private int timestamp;
        /**
         * applyStatus : 1
         * status : 5
         */
        private ExtraBean extra;
        /**
         * permissions : null
         * isInBlackList : false
         */

        private ToUserExtraBean toUserExtra;

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getMsgTime() {
            return msgTime;
        }

        public void setMsgTime(String msgTime) {
            this.msgTime = msgTime;
        }

        public FromUserBean getFromUser() {
            return fromUser;
        }

        public void setFromUser(FromUserBean fromUser) {
            this.fromUser = fromUser;
        }

        public ToUserBean getToUser() {
            return toUser;
        }

        public void setToUser(ToUserBean toUser) {
            this.toUser = toUser;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public int getMsgType() {
            return msgType;
        }

        public void setMsgType(int msgType) {
            this.msgType = msgType;
        }

        public boolean isIsRead() {
            return isRead;
        }

        public void setIsRead(boolean isRead) {
            this.isRead = isRead;
        }

        public String getHostRealId() {
            return hostRealId;
        }

        public void setHostRealId(String hostRealId) {
            this.hostRealId = hostRealId;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public ExtraBean getExtra() {
            return extra;
        }

        public void setExtra(ExtraBean extra) {
            this.extra = extra;
        }

        public ToUserExtraBean getToUserExtra() {
            return toUserExtra;
        }

        public void setToUserExtra(ToUserExtraBean toUserExtra) {
            this.toUserExtra = toUserExtra;
        }

        public static class FromUserBean implements Serializable {
            private static final long serialVersionUID = 5005982276201088693L;
            private String id;
            private String name;
            private String mobile;
            private String email;
            private Object uuid;
            private String avatarUrl;
            private String signature;
            private String remark;
            private String birthday;
            private Object constellation;
            private float height;
            private float weight;
            private String sex;
            private String inviteCode;
            private List<String> hostId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public Object getUuid() {
                return uuid;
            }

            public void setUuid(Object uuid) {
                this.uuid = uuid;
            }

            public String getAvatarUrl() {
                if(avatarUrl==null){
                    avatarUrl="";
                }
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public Object getConstellation() {
                return constellation;
            }

            public void setConstellation(Object constellation) {
                this.constellation = constellation;
            }

            public float getHeight() {
                return height;
            }

            public void setHeight(float height) {
                this.height = height;
            }

            public float getWeight() {
                return weight;
            }

            public void setWeight(float weight) {
                this.weight = weight;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getInviteCode() {
                return inviteCode;
            }

            public void setInviteCode(String inviteCode) {
                this.inviteCode = inviteCode;
            }

            public List<String> getHostId() {
                return hostId;
            }

            public void setHostId(List<String> hostId) {
                this.hostId = hostId;
            }
        }

        public static class ToUserBean implements Serializable {
            private static final long serialVersionUID = -3818803669413683198L;
            private String id;
            private String name;
            private String mobile;
            private String email;
            private Object uuid;
            private String avatarUrl;
            private String signature;
            private String remark;
            private String birthday;
            private String constellation;
            private float height;
            private float weight;
            private String sex;
            private Object inviteCode;
            private List<?> hostId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public Object getUuid() {
                return uuid;
            }

            public void setUuid(Object uuid) {
                this.uuid = uuid;
            }

            public String getAvatarUrl() {
                if(avatarUrl == null){
                    avatarUrl = "";
                }
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getConstellation() {
                return constellation;
            }

            public void setConstellation(String constellation) {
                this.constellation = constellation;
            }

            public float getHeight() {
                return height;
            }

            public void setHeight(float height) {
                this.height = height;
            }

            public float getWeight() {
                return weight;
            }

            public void setWeight(float weight) {
                this.weight = weight;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public Object getInviteCode() {
                return inviteCode;
            }

            public void setInviteCode(Object inviteCode) {
                this.inviteCode = inviteCode;
            }

            public List<?> getHostId() {
                return hostId;
            }

            public void setHostId(List<?> hostId) {
                this.hostId = hostId;
            }
        }

        public static class ExtraBean implements Serializable {
            private static final long serialVersionUID = 6823282093883217752L;
            private int applyStatus;
            private int status;

            public int getApplyStatus() {
                return applyStatus;
            }

            public void setApplyStatus(int applyStatus) {
                this.applyStatus = applyStatus;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }

        public static class ToUserExtraBean implements Serializable {
            private static final long serialVersionUID = 7037545509545634560L;
            private Object permissions;
            private boolean isInBlackList;

            public Object getPermissions() {
                return permissions;
            }

            public void setPermissions(Object permissions) {
                this.permissions = permissions;
            }

            public boolean isIsInBlackList() {
                return isInBlackList;
            }

            public void setIsInBlackList(boolean isInBlackList) {
                this.isInBlackList = isInBlackList;
            }
        }
    }
}
