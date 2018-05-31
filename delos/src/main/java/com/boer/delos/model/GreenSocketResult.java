package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 绿色生活 电
 * @CreateDate: 2017/2/23 0023 11:37
 * @Modify:
 * @ModifyDate:
 */

public class GreenSocketResult extends BaseResult {
    /**
     * addr : F7AC0804004B12000000
     * payload : "{"addr":"F7AC0804004B12000000","timestamp":1487777494,"value":{"I":"0.42","P":"10.1","state":1,"U":"230.7","energy":"427.708","delta":"427.678"},"time":1487777494,"type":"Socket","name":"插座"}"
     * time : 1487779200
     */

    private List<ResponseBean> response;

    public List<ResponseBean> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseBean> response) {
        this.response = response;
    }


    public static class ResponseBean {
        private String addr;
        private String payload;
        private int time;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }

    public static class SocketData {
        /**
         * addr : F7AC0804004B12000000
         * timestamp : 1487777494
         * value : {"I":"0.42","P":"10.1","state":1,"U":"230.7","energy":"427.708","delta":"427.678"}
         * time : 1487777494
         * type : Socket
         * name : 插座
         */

        private String addr;
        private int timestamp;
        /**
         * I : 0.42
         * P : 10.1
         * state : 1
         * U : 230.7
         * energy : 427.708
         * delta : 427.678
         */

        private ValueBean value;
        private int time;
        private String type;
        private String name;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public ValueBean getValue() {
            return value;
        }

        public void setValue(ValueBean value) {
            this.value = value;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class ValueBean {
            private String I;
            private String P;
            private int state;
            private String U;
            private String energy;
            private String delta;

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

            public int getState() {
                return state;
            }

            public void setState(int state) {
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

            public String getDelta() {
                return delta;
            }

            public void setDelta(String delta) {
                this.delta = delta;
            }
        }
    }
}
