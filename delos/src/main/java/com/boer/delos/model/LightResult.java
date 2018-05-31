package com.boer.delos.model;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 查询房间的灯亮
 * @CreateDate: 2017/3/6 0006 15:59
 * @Modify:
 * @ModifyDate:
 */


public class LightResult extends BaseResult {

    /**
     * light : {"2":"on","1":"on"}
     * mode : 2
     * offline : {}
     */

    private Object response;
    private String md5;

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
