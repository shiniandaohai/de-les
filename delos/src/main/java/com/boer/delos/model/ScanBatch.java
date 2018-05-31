package com.boer.delos.model;

/**
 * Created by gaolong on 2017/4/6.
 */
public class ScanBatch {


    /**
     * response : {"msg":"主机已处于扫描状态"}
     * ret : 50006
     * md5 : 223da53ae1bb4a53d46fd3c84dbe1757
     */

    private ResponseBean response;
    private int ret;
    private String md5;

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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public static class ResponseBean {
        /**
         * msg : 主机已处于扫描状态
         */

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
