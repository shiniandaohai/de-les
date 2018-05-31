package com.boer.delos.request.system;

import android.content.Context;

import com.boer.delos.request.RequestResultListener;

/**
 * @author PengJiYang
 * @Description: 用户信息相关的接口, 如登录、注册等
 * create at 2016/3/24 9:14
 */
public interface SystemInterface {

    // 一键备份
    void oneTouchBackup(Context context, RequestResultListener listener);

    //一键还原
    void vistaGhost(Context context, String currentHostId, RequestResultListener listener);
}
