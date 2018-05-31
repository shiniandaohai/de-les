package com.boer.delos.request.health;

import android.content.Context;
import android.util.Log;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.SkinArea;
import com.boer.delos.model.SmartMirror;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * @author XieQingTing
 * @Description: 健康生活相关接口
 * create at 2016/5/27 15:19
 */
public class HealthController extends BaseController {
    public static HealthController instance = null;
    public static final String HEATHY_ALL = "0";
    public static final String HEATHY_SUGAR = "2";
    public static final String HEATHY_PERSURE = "1";
    public static final String HEATHY_WEIGHT = "3";
    public static final String HEATHY_URINE = "4";
    public static final String HEATHY_SKIN = "5";

    public static HealthController getInstance() {
        if (instance == null) {
            synchronized (HealthController.class) {
                if (instance == null) {
                    instance = new HealthController();
                }
            }
        }
        if (StringUtil.isEmpty(Constant.USERID)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(BaseApplication.getAppContext());
        }
        if (StringUtil.isEmpty(Constant.TOKEN)) {
            SharedPreferencesUtils.readTokenFromPreferences(BaseApplication.getAppContext());
        }
        return instance;
    }


    /**
     * 最近健康数据
     *
     * @param context    上下文
     * @param fromtime   起始时间
     * @param healthType 请求的数据类型
     * @param recent     最近多久
     * @param userId     用户Id
     * @param listener   回调接口
     */
    public void queryRecentHealth(Context context, String fromtime, String healthType,
                                  String recent, String userId, RequestResultListener listener) {

        String url = URLConfig.HTTP + "/health/query_recent_health?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("fromtime", fromtime);
        maps.put("healthType", healthType);
        maps.put("recent", recent);
        maps.put("userId", userId);

        String json = new Gson().toJson(maps);
        L.d("queryRecentHealth_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, "query_recent_health" + healthType, json, listener)
        ;

    }

    /**
     * 上报体重值
     * token 	string 	Y 	用户登录令牌
     * familyMemberId	string 	Y 	亲友Id，0-自己
     * measuretime 	string 	Y 	测试日期
     * weight 	string 	Y 	体重值
     * fatrate 	string 	Y 	体脂率
     * detail 	string 	Y 	其他详情
     */
    public void reportBodyweight(Context context, String familyMemberId, String measuretime, String weight, String fatrate, String detail, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/health/report_bodyweight?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("weight", weight);
        maps.put("fatrate", fatrate);
        maps.put("detail", detail);
        String json = new Gson().toJson(maps);

        L.e("report_bodyweight===" + json);
        OKHttpRequest.postWithNoKey(context, url, "report_bodyweight", json, listener);
    }

    /**
     * 上报血糖值
     *
     * @param context        上下文
     * @param familyMemberId 亲友Id，0-自己
     * @param measuretime    测量时段 早0、1中2/3晚4/5 睡前 6   餐前餐后
     * @param mesuredate     测试日期 时间戳
     * @param value          血糖测量值
     * @param detail         其他详情
     * @param listener       回掉接口
     */
    public void reportBloodSugar(Context context, String familyMemberId,
                                 String measuretime, String mesuredate,
                                 String value, String detail,
                                 String hostId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(context);
        }

        String url = URLConfig.HTTP + "/health/report_bloodsugar?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("mesuredate", mesuredate);
        maps.put("value", value);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.d("reportBloodSugar_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.REPORTBLOODSUAGR, json, listener);
    }

    /**
     * 上报血压值
     * token 	string 	Y 	用户登录令牌
     * familyMemberId	string 	Y 	亲友Id，0-自己
     * measuretime 	string 	Y 	测试日期
     * valueH 	string 	Y 	血压高值
     * valueL 	string 	Y 	血压低值
     * bpm 	string 	Y 	心率值
     * detail 	string 	Y 	其他详情
     */

    public void reportBloodpressure(Context context, String familyMemberId, String measuretime,
                                    String valueH, String valueL, String bpm,
                                    String detail,
                                    String hostId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/health/report_bloodpressure?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("valueH", valueH);
        maps.put("valueL", valueL);
        maps.put("bpm", bpm);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.e("report_bloodpressure===" + json);
        OKHttpRequest.postWithNoKey(context, url, "report_bloodpressure", json, listener);
    }

    /**
     * 上报尿检值
     * token 	string 	Y 	用户登录令牌
     * familyMemberId	string 	Y 	亲友Id，0-自己
     * measuretime 	string 	Y 	测试日期
     * detail 	string 	Y 	其他详情
     */
    public void reportUrine(Context context, String familyMemberId, String measuretime,
                            String detail,
                            String hostId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/health/report_urine?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.e("report_urine===" + json);
        OKHttpRequest.postWithNoKey(context, url, "report_urine", json, listener);
    }

    /**
     * 删除一条尿检记录
     *
     * @param context
     * @param familyMemerId "0":自己
     * @param measuretime   记录的时间戳
     * @param listener
     */
    public void deleteUrine(Context context, String familyMemerId, String measuretime, RequestResultListener listener) {

        HashMap<String, String> maps = new HashMap<>();
        maps.put("familyMemberId", familyMemerId);
        maps.put("measuretime", measuretime);

        String url = splitHttpUrl("/health/delete_urine", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "/health/delete_urine", listener);

    }

    /**
     * 删除一条体重记录
     *
     * @param context
     * @param familyMemerId "0":自己
     * @param measuretime   记录的时间戳
     * @param listener
     */
    public void deleteWeight(Context context, String familyMemerId, String measuretime, RequestResultListener listener) {

        HashMap<String, String> maps = new HashMap<>();
        maps.put("familyMemberId", familyMemerId);
        maps.put("measuretime", measuretime);

        String url = splitHttpUrl("/health/delete_bodyweight", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "/health/delete_bodyweight", listener);

    }

    /**
     * 删除一条血压记录
     *
     * @param context
     * @param familyMemerId "0":自己
     * @param measuretime   记录的时间戳
     * @param listener
     */
    public void deletePressure(Context context, String familyMemerId, String measuretime, RequestResultListener listener) {

        HashMap<String, String> maps = new HashMap<>();
        maps.put("familyMemberId", familyMemerId);
        maps.put("measuretime", measuretime);

        String url = splitHttpUrl("/health/delete_bloodpressure", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "/health/delete_bloodpressure", listener);

    }

    /**
     * 删除一条血糖记录
     *
     * @param context
     * @param familyMemerId "0":自己
     * @param measuretime   记录的时间戳
     * @param listener
     */
    public void deleteSugar(Context context, String familyMemerId, String measuretime, RequestResultListener listener) {

        HashMap<String, String> maps = new HashMap<>();
        maps.put("familyMemberId", familyMemerId);
        maps.put("measuretime", measuretime);

        String url = splitHttpUrl("/health/delete_bloodsugar", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "/health/delete_bloodsugar", listener);

    }

    public void updateSugar(Context context, String familyMemberId,
                            String measuretime, String mesuredate,
                            String value, String detail,
                            String hostId, RequestResultListener listener) {
        String url = URLConfig.HTTP + urlUpdateBloodsugar + "?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("mesuredate", mesuredate);
        maps.put("value", value);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.d("reportBloodSugar_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, urlUpdateBloodsugar, json, listener);

    }

    /**
     * 更新血压  TODO  需要修改
     *
     * @param context
     * @param familyMemberId
     * @param measuretime
     * @param valueH
     * @param valueL
     * @param bpm
     * @param detail
     * @param hostId
     * @param listener
     */
    public void updatePressure(Context context, String familyMemberId,

                               String measuretime, String valueH,
                               String valueL, String bpm, String detail,
                               String hostId, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/health/updateBloodPressure?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("valueH", valueH);
        maps.put("valueL", valueL);
        maps.put("bpm", bpm);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.e("report_bloodpressure===" + json);
        OKHttpRequest.postWithNoKey(context, url, "updateBloodPressure", json, listener);


    }

    /**
     * 尿检更新
     * @param context
     * @param familyMemberId
     * @param measuretime
     * @param detail
     * @param hostId
     * @param listener
     */
    public  void updateUrine(Context context,String familyMemberId,String measuretime,
                             String detail,String hostId,RequestResultListener listener){
        String url = URLConfig.HTTP + "/health/report_urine?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", familyMemberId);
        maps.put("measuretime", measuretime);
        maps.put("detail", detail);
        maps.put("hostId", hostId);
        String json = new Gson().toJson(maps);

        L.e("report_urine===" + json);
        OKHttpRequest.postWithNoKey(context, url, "report_urine", json, listener);

    }
    /**
     * 查询选定日期区间健康数据接口
     *
     * @param fromtime
     * @param totime
     * @param healthType
     * @param userId
     * @param listener
     */
    public void queryHealthyTime2TimeData(Context context, String fromtime, String totime,
                                          String healthType, String userId, RequestResultListener listener) {

        HashMap<String, String> maps = new HashMap<>();
        maps.put("fromtime", fromtime);
        maps.put("totime", totime);
        maps.put("healthType", healthType);
        maps.put("userId", userId);

        String url = splitHttpUrl("/health/query_health", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "/health/query_health", listener);

    }


    public void reportSkinInfo(Context context, SkinArea skinArea, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/health/report_skin?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("familyMemberId", skinArea.getFamilyMemberId());
        maps.put("measuretime", skinArea.getMeasuretime());
        maps.put("detail", new Gson().toJson(skinArea.getDetail()));
        String json = new Gson().toJson(maps);

        L.e("report_skin===" + json);
        OKHttpRequest.postWithNoKey(context, url, "report_skin", json, listener);
    }


    public void loginSmartMirror(Context context, SmartMirror smartMirror, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/auth/login_smartMirror?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("mobile", Constant.LOGIN_USER.getMobile());
        maps.put("specification", smartMirror.getSpecification());
        maps.put("model", smartMirror.getModel());
        maps.put("id", smartMirror.getId());
        maps.put("remark", smartMirror.getRemark());
        maps.put("clientId", smartMirror.getClientId());
        String json = new Gson().toJson(maps);

        OKHttpRequest.postWithNoKey(context, url, "login_smartMirror", json, listener);
    }



    public void showSmartMirrors(Context context, String  mobile, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/user/show_mirrors?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("mobile",mobile);
        String json = new Gson().toJson(maps);


        OKHttpRequest.postWithNoKey(context, url, "show_mirrors", json, listener);
    }



    public void reNameSmartMirrors(Context context, String  mobile,String  id,String  remark, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/user/update_mirror_remark?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("mobile",mobile);
        maps.put("id",id);
        maps.put("remark",remark);
        String json = new Gson().toJson(maps);


        OKHttpRequest.postWithNoKey(context, url, "update_mirror_remark", json, listener);
    }




    public void offLineSmartMirrors(Context context, String  mobile,String  id, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/auth/logout_smartMirror?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("mobile",mobile);
        maps.put("id",id);
        String json = new Gson().toJson(maps);


        OKHttpRequest.postWithNoKey(context, url, "logout_smartMirror", json, listener);
    }

    /**
     * 查询别人分享给我的健康数据
     *
     * @param context
     * @param hostId
     * @param userId
     * @param listener
     */
    public void queryShareHealth2Me(Context context, String  hostId,String  userId
            , RequestResultListener listener) {
//        if (StringUtil.isEmpty(Constant.USERID)) {
//            return;
//        }
        String url = URLConfig.HTTP + urlShareUser+"?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("hostId",hostId);
        maps.put("toUserId",userId);
        String json = new Gson().toJson(maps);

        OKHttpRequest.postWithNoKey(context, url, urlShareUser, json, listener);
    }


}
