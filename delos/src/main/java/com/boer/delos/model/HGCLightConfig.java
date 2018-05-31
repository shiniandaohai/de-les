package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class HGCLightConfig {


    /**
     * response : [{"addr":"gtgtmg","channel":"1","controls":[{"addr":"ABFDC307004B12000000","type":"Light1","value":{"state":0}}],"timestamp":1478110768,"v_type":"0x1"}]
     * ret : 0
     */

    private int ret;
    /**
     * addr : gtgtmg
     * channel : 1
     * controls : [{"addr":"ABFDC307004B12000000","type":"Light1","value":{"state":0}}]
     * timestamp : 1478110768
     * v_type : 0x1
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
         * addr : ABFDC307004B12000000
         * type : Light1
         * value : {"state":0}
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
             * state : 0
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
                private int state;
                private int state2;
                private int state3;
                private int state4;

                public int getState() {
                    return state;
                }

                public void setState(int state) {
                    this.state = state;
                }

                public int getState2() {
                    return state2;
                }

                public void setState2(int state2) {
                    this.state2 = state2;
                }

                public int getState3() {
                    return state3;
                }

                public void setState3(int state3) {
                    this.state3 = state3;
                }

                public int getState4() {
                    return state4;
                }

                public void setState4(int state4) {
                    this.state4 = state4;
                }
            }
        }
    }
}
