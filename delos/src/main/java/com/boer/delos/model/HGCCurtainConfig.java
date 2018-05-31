package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class HGCCurtainConfig {


    /**
     * ret : 0
     * response : [{"timestamp":1478110775,"v_type":"0x4","addr":"gtgtmg","channel":"1","controls":[{"type":"CurtainSensor","addr":"1234567891123456789"}]},{"timestamp":1478110779,"v_type":"0x4","addr":"gtgtmg","channel":"2","controls":[{"type":"Curtain","addr":"gttgtp","value":{"state":"1"}}]}]
     */

    private int ret;
    /**
     * timestamp : 1478110775
     * v_type : 0x4
     * addr : gtgtmg
     * channel : 1
     * controls : [{"type":"CurtainSensor","addr":"1234567891123456789"}]
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
        private int timestamp;
        private String v_type;
        private String addr;
        private String channel;
        /**
         * type : CurtainSensor
         * addr : 1234567891123456789
         */

        private List<ControlsBean> controls;

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

        public List<ControlsBean> getControls() {
            return controls;
        }

        public void setControls(List<ControlsBean> controls) {
            this.controls = controls;
        }

        public static class ControlsBean {
            private String type;
            private String addr;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getAddr() {
                return addr;
            }

            public void setAddr(String addr) {
                this.addr = addr;
            }
        }
    }
}
