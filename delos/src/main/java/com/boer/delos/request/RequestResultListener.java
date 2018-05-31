package com.boer.delos.request;

/**
 * @author wangkai
 * @Description: App请求结果的回调接口
 * create at 2015/11/4 11:31
 */
public interface RequestResultListener {
    /**
     * 请求成功
     *
     * @param json
     */
    void onSuccess(String json) ;

    /**
     * 请求失败
     * json错误信息
     */
    void onFailed(String json);

}
