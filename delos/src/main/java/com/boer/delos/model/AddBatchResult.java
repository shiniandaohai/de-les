package com.boer.delos.model;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/29 0029 10:49
 * @Modify:
 * @ModifyDate:
 */


public class AddBatchResult extends BaseResult {
    /**
     * response : [{"areaId":"-1","keyId":180,"hardwareVer":"1.1","addr":"E38xxxxxxxxxx2000000","timestamp":1490341327,"dismiss":true,"softwareVer":"1.4","roomId":"-1","type":"Light2","id":"E38xxxxxxxxxx2000000","name":"二联灯"},{"areaId":"-1","keyId":187,"hardwareVer":"1.1","addr":"3D9xxxxxxxxxx2000000","timestamp":1490341344,"dismiss":true,"softwareVer":"1.1","roomId":"-1","type":"Socket","id":"3D9xxxxxxxxxx2000000","name":"插座"}]
     * md5 : 17d2ac96db91e7567504f6b7c6b36f71
     */

    private String md5;
    /**
     * areaId : -1
     * keyId : 180
     * hardwareVer : 1.1
     * addr : E38xxxxxxxxxx2000000
     * timestamp : 1490341327
     * dismiss : true
     * softwareVer : 1.4
     * roomId : -1
     * type : Light2
     * id : E38xxxxxxxxxx2000000
     * name : 二联灯
     */
    private List<Device> response;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<Device> getResponse() {
        return response;
    }

    public void setResponse(List<Device> response) {
        this.response = response;
    }
}
