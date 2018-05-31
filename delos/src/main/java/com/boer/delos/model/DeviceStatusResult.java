package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/16 0016 15:54
 * @Modify:
 * @ModifyDate:
 */


public class DeviceStatusResult extends BaseResult {

    private ResponseBean response;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * addr : JJJ
         * name : 水浸
         * offline : 0
         * time : 1492303017
         * type : Water
         */

        private List<DeviceStatus> devices;

        public List<DeviceStatus> getDevices() {
            return devices;
        }

        public void setDevices(List<DeviceStatus> devices) {
            this.devices = devices;
        }
    }
}
