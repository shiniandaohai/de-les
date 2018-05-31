package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class HGCSceneConfig {

    /**
     * response : [{"addr":"gtgtmg","channel":"2","controls":[{"modeId":"6"}],"timestamp":1478103546,"v_type":"0xF"},{"addr":"gtgtmg","channel":"5","controls":[{"modeId":"5"}],"timestamp":1478114097,"v_type":"0xF"}]
     * ret : 0
     */

    private int ret;
    /**
     * addr : gtgtmg
     * channel : 2
     * controls : [{"modeId":"6"}]
     * timestamp : 1478103546
     * v_type : 0xF
     */

    private List<ResponseBean> response;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<ResponseBean> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseBean> response) {
        this.response = response;
    }

    public static class ResponseBean {
        private String addr;
        private String channel;
        private int timestamp;
        private String v_type;
        /**
         * modeId : 6
         */

        private List<ControlsBean> controls;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getV_type() {
            return v_type;
        }

        public void setV_type(String v_type) {
            this.v_type = v_type;
        }

        public List<ControlsBean> getControls() {
            return controls;
        }

        public void setControls(List<ControlsBean> controls) {
            this.controls = controls;
        }

        public static class ControlsBean {
            private String modeId;

            public String getModeId() {
                return modeId;
            }

            public void setModeId(String modeId) {
                this.modeId = modeId;
            }
        }
    }
}
