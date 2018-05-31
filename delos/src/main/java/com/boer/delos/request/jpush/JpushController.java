package com.boer.delos.request.jpush;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.User;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/5 0005.
 * 处理与极光推送有关的接口
 */
public class JpushController {

    public static JpushController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static JpushController getInstance() {
        if (instance == null) {
            synchronized (FamilyManageController.class) {
                if (instance == null) {
                    instance = new JpushController();
                }
            }
        }
        return instance;
    }

    public void updateToken(Context context, String deviceToken, String type, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            User user = new Gson().fromJson(SharedUtils.getInstance().getTagSp(SharedTag.user_login), User.class);
            Constant.USERID = user.getId();
        }
        if(StringUtil.isEmpty(deviceToken)){
            return;
        }
        String url =  URLConfig.HTTP + "/user/update_token?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("deviceToken", deviceToken);
        maps.put("type", type);
        String json = new Gson().toJson(maps);

        L.e("update_token params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.UPDATE_TOKEN, json, listener);
    }


    public void showExtend(Context context, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url =  URLConfig.HTTP + "/user/show_extend?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("uid",  Constant.USERID);
        String json = new Gson().toJson(maps);

        L.e("show_extend params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.SHOW_EXTEND, json, listener);
    }


    public void updateExtend(Context context, String tone, String vibration, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }

        String url =  URLConfig.HTTP + "/user/update_extend?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("uid", Constant.USERID);
        maps.put("tone", tone);
        maps.put("vibration", vibration);
        String json = new Gson().toJson(maps);

        L.e("update_extend params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.UPDATE_EXTEND, json, listener);
    }

    public void updateApk(Context context,String url, RequestResultListener listener){
        OKHttpRequest.RequestGet(context,url,"UPDATE_APK",listener);
    }


}
