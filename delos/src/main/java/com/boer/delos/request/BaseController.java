package com.boer.delos.request;

import android.content.Context;
import android.util.Log;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukang on 16/7/13.
 */
public class BaseController implements Api {

    /**
     * 发送请求
     *
     * @param context
     * @param url
     * @param map
     * @param tag
     * @param listener
     */
    protected void postRequest(Context context, String url, Map map, String tag, RequestResultListener listener) {
        String json = new Gson().toJson(map);
        String localUrl;
        Loger.v("postRequest+++params====" + json);
        if (Constant.IS_LOCAL_CONNECTION) {
//            localUrl = splitHttpUrl(url,null,Constant.LOCAL_CONNECTION_IP,false,true,false);

            postWithLocalRequest(context, url, map, tag, listener);
        } else {
            postWithInternetRequest(context, url, map, tag, listener);
        }
    }

    /**
     * 发送请求 ，判断是否直连
     *
     * @param context
     * @param isNeedLoacation
     * @param url
     * @param map
     * @param tag
     * @param listener
     */
    protected void postRequest(Context context, boolean isNeedLoacation, String url, Map map, String tag, RequestResultListener listener) {
        if (isNeedLoacation && Constant.IS_LOCAL_CONNECTION) {
            postWithLocalRequest(context, url, map, tag, listener);
        } else {
            postWithInternetRequest(context, url, map, tag, listener);
        }
    }

    /**
     * 本地直连请求
     *
     * @param context
     * @param url
     * @param map
     * @param tag
     * @param listener
     */
    protected void postWithLocalRequest(Context context, String url, Map map, String tag, RequestResultListener listener) {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("sparam", map);
        String json = new Gson().toJson(postMap);
        L.i(url + ":------:" + json);
        OKHttpRequest.postWithNoKeyNonEncrypted(context, url, tag, json, listener);
    }


    /**
     * 网络请求
     *
     * @param context
     * @param url
     * @param map
     * @param tag
     * @param listener
     */
    protected void postWithInternetRequest(Context context, String url, Map map, String tag, RequestResultListener listener) {
        context = context.getApplicationContext();
        if (StringUtil.isEmpty(Constant.USERID)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(context);
        }
        if (StringUtil.isEmpty(Constant.TOKEN)) {
            SharedPreferencesUtils.readTokenFromPreferences(context);
        }
        if (url.contains("?")) {
            url = String.format("%s&uid=%s", url, Constant.USERID);
//            String[] tempUrl = url.split("\\?");
//            url = String.format("%s?uid=%s", tempUrl[0], Constant.USERID);
//            url = String.format("%s&%s", url, tempUrl[1]);
        } else {
            url = String.format("%s?uid=%s", url, Constant.USERID);
        }
        if (map == null) {
            map = new HashMap();
        }
        map.put("token", Constant.TOKEN);
        String json = new Gson().toJson(map);

        Log.v("gl", "postWithInternetRequest++params===" + json + "======url======" + url + "======getClass======" + context.getClass().getName());
//        L.i(url + ":------:" + json);

        OKHttpRequest.postWithNoKey(context, url, tag, json, listener);


    }

    /**
     * 拼接Url
     *
     * @param api                     inteface Api
     * @param cmd                     云端连接 cmdType= <int>
     * @param isOnlyCmdType           是否直接cmdType云端 true 直接云端 https+wrapper
     * @param isNeedJudgeLocationConn 是否要判断本地直连 不判断直接 https+api 判断 http+ip+8080+api / https+wrapper/http+api
     * @param isCmdType               判断直连是选择云端方式  https+wrapper/http+api
     * @return
     */
    public String splitHttpUrl(String api, String cmd, String ip, boolean isOnlyCmdType,
                               boolean isNeedJudgeLocationConn, boolean isCmdType) {
        String url = "";
        /*直接走云端CmdType*/
        if (isOnlyCmdType) {
            url = String.format("%s%s%s%s", URLConfig.HTTP, wrapper_request, "cmdType=", cmd);
            return url;
        }
        /*直连走云端http+api*/
        if (!isNeedJudgeLocationConn) {
            url = String.format("%s%s", URLConfig.HTTP, api);
            return url;
        }
        /*判断本地直连 ip+api : http+api/http+CmdTpe*/
        if (Constant.IS_LOCAL_CONNECTION && !StringUtil.isEmpty(ip)) {
            url = String.format("http://%s%s%s", ip, ":8080", api);
        } else {
            url = isCmdType ? String.format("%s%s%s%s", URLConfig.HTTP, wrapper_request, "?cmdType=", cmd)
                    : String.format("%s%s", URLConfig.HTTP, api);
        }
        return url;
    }

    public String justLocationConn(String ip, String api) {
        return String.format("http://%s%s%s", ip, ":8080", api);
    }
}
