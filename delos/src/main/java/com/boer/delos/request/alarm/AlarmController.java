package com.boer.delos.request.alarm;

import android.content.Context;
import android.text.TextUtils;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.MsgSettings;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 报警相关接口
 * create at 2016/5/30 17:53
 */
public class AlarmController extends BaseController {
    private static AlarmController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static AlarmController getInstance() {
        if (instance == null) {
            synchronized (AlarmController.class) {
                if (instance == null) {
                    instance = new AlarmController();
                }
            }
        }
        return instance;
    }

    /**
     * 查询报警消息
     *
     * @param context   上下文
     * @param host      主机ID
     * @param alarmTime 报警时间
     * @param size      每页大小
     * @param start     开始页数
     * @param type      类型,“”(全部),“非法入侵”,“火灾报警”,“水浸报警”,“环境污染”,“跌倒报警”
     * @param listener  回调接口
     */
    public void getAlarm(Context context, String host, String alarmTime, String size, String start, String type, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/alarm/show1?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("host", host);
        maps.put("alarmTime", alarmTime);
        maps.put("size", size);
        maps.put("start", start);
        maps.put("type", type);
        String json = new Gson().toJson(maps);

        L.e("getAlarm_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.ALARM, json, listener);
    }

    /**
     * 查询系统消息
     *
     * @param context    上下文
     * @param hostId     主机ID
     * @param recordTime 报警时间
     * @param size       每页大小
     * @param start      开始页数
     * @param type       类型,“”(全部),“非法入侵”,“火灾报警”,“水浸报警”,“环境污染”,“跌倒报警”
     * @param listener   回调接口
     */
    public void getSystemMessage(Context context, String hostId, String recordTime, String size, String start, String type, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
//        String url = URLConfig.HTTP + "/systemMessage/show?uid=" + Constant.USERID;
        String url = URLConfig.HTTP + urlSystemMessageShow + "?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("hostId", hostId);
        maps.put("recordTime", recordTime);
        maps.put("size", size);
        maps.put("start", start);
        maps.put("type", type);
        maps.put("userId", Constant.USERID);
        String json = new Gson().toJson(maps);

        L.e("getAlarm_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.ALARM, json, listener);
    }

    /**
     * 删除单条或多条报警消息
     *
     * @param context
     * @param alarmId  报警Id数组
     * @param listener
     */

    public void deleteAlarm(Context context, String[] alarmId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        List<Map<String, String>> list = new ArrayList();
        for (int i = 0; i < alarmId.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("alarmId", alarmId[i]);
            list.add(map);
        }

//        String url = URLConfig.HTTP + "/alarm/delete1?uid=" + Constant.USERID;
        String url = URLConfig.HTTP + urlAlarmDelete + "?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("alarmId", list);
        String json = new Gson().toJson(maps);
        L.e("deleteAlarm_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.ALARM, json, listener);
    }

    /**
     * \
     * 删除单条或多条系统消息
     *
     * @param context
     * @param msgId    消息Id数组
     * @param listener
     */

    public void deleteSystemMessage(Context context, String[] msgId, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }

        List<Map<String, String>> list = new ArrayList();
        for (int i = 0; i < msgId.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("msgId", msgId[i]);
            list.add(map);
        }

//        String url = URLConfig.HTTP + "/systemMessage/remove?uid=" + Constant.USERID;
        String url = URLConfig.HTTP + urlSystemMessageDelete + "?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("msgId", list);
        String json = new Gson().toJson(maps);
        L.e("deleteSystemMessage_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.ALARM, json, listener);
    }


    public void getMsgSettings(Context context, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + urlgetMsgSettings + "?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("userId", Constant.USERID);
        String json = new Gson().toJson(maps);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.FIND_SETTINGS_MSG, json, listener);
    }


    public void setMsgSettings(Context context, MsgSettings settings, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + urlsetMsgSettings + "?uid=" + Constant.USERID;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("userId", Constant.USERID);

        maps.put("unDisturb", settings.getUnDisturb());
        if (!TextUtils.isEmpty(settings.getUnDisturbStartTime()))
            maps.put("unDisturbStartTime", settings.getUnDisturbStartTime());

        if (!TextUtils.isEmpty(settings.getUnDisturbEndTime()))
            maps.put("unDisturbEndTime", settings.getUnDisturbEndTime());
        maps.put("receiveSystemMessage", settings.getReceiveSystemMessage());
        maps.put("receiveAlarmMessage", settings.getReceiveAlarmMessage());


        String json = new Gson().toJson(maps);
        L.v("gl==" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.SAVE_SETTINGS_MSG, json, listener);
    }


    public void pushNotification(Context context, String message, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + urlsetPushMsg + "?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("userId", Constant.USERID);
        maps.put("message", message);
        maps.put("event", Constant.NotificationEvent.NotificationEventNormal + "");
        String json = new Gson().toJson(maps);

        L.v("gl++push====" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.FIND_SETTINGS_MSG, json, listener);
    }


}
