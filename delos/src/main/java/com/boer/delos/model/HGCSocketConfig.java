package com.boer.delos.model;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class HGCSocketConfig{
    /**
     * ret : 0
     * response : [{"timestamp":1478106689,"v_type":"0x2","addr":"gtgtmg","channel":"1","controls":[{"type":"Socket","addr":"42894801004B12000000","value":{"I":"0.00","P":"0.0","state":"0","U":"0.1","energy":"0.001"}}]},{"timestamp":1478106691,"v_type":"0x2","addr":"gtgtmg","channel":"2","controls":[{"type":"Socket","addr":" swim","value":{"state":"0"}}]}]
     */

    private int ret;
    /**
     * timestamp : 1478106689
     * v_type : 0x2
     * addr : gtgtmg
     * channel : 1
     * controls : [{"type":"Socket","addr":"42894801004B12000000","value":{"I":"0.00","P":"0.0","state":"0","U":"0.1","energy":"0.001"}}]
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
         * type : Socket
         * addr : 42894801004B12000000
         * value : {"I":"0.00","P":"0.0","state":"0","U":"0.1","energy":"0.001"}
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
            /**
             * I : 0.00
             * P : 0.0
             * state : 0
             * U : 0.1
             * energy : 0.001
             */

            private ValueBean value;

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

            public ValueBean getValue() {
                return value;
            }

            public void setValue(ValueBean value) {
                this.value = value;
            }

            public static class ValueBean {
                private String I;
                private String P;
                private String state;
                private String U;
                private String energy;

                public String getI() {
                    return I;
                }

                public void setI(String I) {
                    this.I = I;
                }

                public String getP() {
                    return P;
                }

                public void setP(String P) {
                    this.P = P;
                }

                public String getState() {
                    return state;
                }

                public void setState(String state) {
                    this.state = state;
                }

                public String getU() {
                    return U;
                }

                public void setU(String U) {
                    this.U = U;
                }

                public String getEnergy() {
                    return energy;
                }

                public void setEnergy(String energy) {
                    this.energy = energy;
                }
            }
        }
    }
}
