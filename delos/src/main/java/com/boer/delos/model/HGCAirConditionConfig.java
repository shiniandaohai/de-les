package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class HGCAirConditionConfig {

    /**
     * response : [{"addr":"gtgtmg","channel":"1","controls":[{"addr":"CAC_2","type":"CAC","value":{"addr":"CAC_2","controlCmd":"1","controlData":"1","controlDeviceType":"0x50","length":"1","protocol":"1"}}],"timestamp":1478097849,"v_type":"0x50"}]
     * ret : 0
     */

    private int ret;
    /**
     * addr : gtgtmg
     * channel : 1
     * controls : [{"addr":"CAC_2","type":"CAC","value":{"addr":"CAC_2","controlCmd":"1","controlData":"1","controlDeviceType":"0x50","length":"1","protocol":"1"}}]
     * timestamp : 1478097849
     * v_type : 0x50
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
         * addr : CAC_2
         * type : CAC
         * value : {"addr":"CAC_2","controlCmd":"1","controlData":"1","controlDeviceType":"0x50","length":"1","protocol":"1"}
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
            private String addr;
            private String type;
            /**
             * addr : CAC_2
             * controlCmd : 1
             * controlData : 1
             * controlDeviceType : 0x50
             * length : 1
             * protocol : 1
             */

            private ValueBean value;

            public String getAddr() {
                return addr;
            }

            public void setAddr(String addr) {
                this.addr = addr;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public ValueBean getValue() {
                return value;
            }

            public void setValue(ValueBean value) {
                this.value = value;
            }

            public static class ValueBean {
                private String addr;
                private String controlCmd;
                private String controlData;
                private String controlDeviceType;
                private String length;
                private String protocol;

                public String getAddr() {
                    return addr;
                }

                public void setAddr(String addr) {
                    this.addr = addr;
                }

                public String getControlCmd() {
                    return controlCmd;
                }

                public void setControlCmd(String controlCmd) {
                    this.controlCmd = controlCmd;
                }

                public String getControlData() {
                    return controlData;
                }

                public void setControlData(String controlData) {
                    this.controlData = controlData;
                }

                public String getControlDeviceType() {
                    return controlDeviceType;
                }

                public void setControlDeviceType(String controlDeviceType) {
                    this.controlDeviceType = controlDeviceType;
                }

                public String getLength() {
                    return length;
                }

                public void setLength(String length) {
                    this.length = length;
                }

                public String getProtocol() {
                    return protocol;
                }

                public void setProtocol(String protocol) {
                    this.protocol = protocol;
                }
            }
        }
    }
}
