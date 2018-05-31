package com.boer.delos.request.wifiAirClean;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangkai on 16/4/26.
 */
public class WifiAirCleanController extends BaseController {
    public static WifiAirCleanController instance = null;
    private static final String ROOTURL="http://iot.ucheer.com/KJ260FV2/userController/";
    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static WifiAirCleanController getInstance() {
        if (instance == null) {
            synchronized (WifiAirCleanController.class) {
                if (instance == null) {
                    instance = new WifiAirCleanController();
                }
            }
        }
        return instance;
    }

    public void thirdReg(Context context, String phone, String userId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = ROOTURL+"thirdReg";
        HashMap<String, String> maps = new HashMap<>();
        maps.put("phone", phone);
        maps.put("id", userId);
        url+=getUrlParam(maps);
        String json = "";
        OKHttpRequest.commonPostWithNoKeyNonEncrypted(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    public void bingDevice(Context context, String sn, String Uid,String nickName, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = ROOTURL+"bingDevice";
        HashMap<String, String> maps = new HashMap<>();
        maps.put("sn", sn);
        maps.put("UId", Uid);
        maps.put("nickName", nickName);
        url+=getUrlParam(maps);
        String json = "";
        OKHttpRequest.commonPostWithNoKeyNonEncrypted(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    public void cancelBingDevice(Context context, String sn, String Uid, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = ROOTURL+"cancelBingDevice";
        HashMap<String, String> maps = new HashMap<>();
        maps.put("sn", sn);
        maps.put("UId", Uid);
        url+=getUrlParam(maps);
        String json = "";
        OKHttpRequest.commonPostWithNoKeyNonEncrypted(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    public void userGetBingDevice(Context context,String Uid, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = ROOTURL+"userGetBingDevice";
        HashMap<String, String> maps = new HashMap<>();
        maps.put("UId", Uid);
        url+=getUrlParam(maps);
        String json = "";
        OKHttpRequest.commonPostWithNoKeyNonEncrypted(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    public void deviceState(Context context,String sn, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = ROOTURL+"deviceState";
        HashMap<String, String> maps = new HashMap<>();
        maps.put("sn", sn);
        url+=getUrlParam(maps);
        String json = "";
        OKHttpRequest.commonPostWithNoKeyNonEncrypted(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    private String getUrlParam(HashMap<String, String> map){
        StringBuilder result=new StringBuilder("");
        Iterator iter = map.entrySet().iterator();
        if(iter.hasNext()){
            result.append("?");
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key =(String) entry.getKey();
            String val = (String)entry.getValue();
            result.append(key).append("=").append(val);
            result.append("&");
        }
        if(result.length()>0){
            result.deleteCharAt(result.length()-1);
        }
        return result.toString();
    }
}
