package com.boer.delos.request.link;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.LinkPlan;
import com.boer.delos.model.ModeAct;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 联动管理相关接口
 * create at 2016/5/25 21:22
 */
public class LinkManageController extends BaseController {
    public static LinkManageController instance = null;

    public static LinkManageController getInstance() {
        if (instance == null) {
            synchronized (LinkManageController.class) {
                if (instance == null) {
                    instance = new LinkManageController();
                }
            }
        }
        return instance;
    }

    /**
     * 查询全局联动模式
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void queryGlobalModes(Context context, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/plan/queryGlobalModes?uid=" + Constant.USERID;

        L.e("queryGlobalModes_url===" + url);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERYGLOBALMODES, null, listener);
    }

    /**
     * 查询全局模式（Cloud）
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void requestGlobalModes(Context context, RequestResultListener listener) {
        String url;
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/plan/queryGlobalModes";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1038";
        }

        HashMap<String, String> map = new HashMap<>();
        postRequest(context, url, map, RequestTag.REQUESTGLOBALMODES, listener);
    }

    /**
     * 激活（切换）指定的联动预案或模式
     *
     * @param context  上下文
     * @param modeId   联动模式的id
     * @param listener 回调接口
     */
    public void activate(Context context, Integer modeId, Integer roomId, RequestResultListener listener) {
        Map<String, Object> map = new HashMap<>();
        map.put("modeId", modeId);
        if (roomId != null) {
            map.put("roomId", roomId);
        }

//        String url;
        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.
                format("http://%s%s", http, "/room/activemode");
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" + "/room/activemode";
        } else {
            url = URLConfig.HTTP + "/room/activemode";
        }
        postRequest(context, url, map, RequestTag.ACTIVATE, listener);
    }

    /**
     * 查询房间模式信息
     *
     * @param context  上下文
     * @param name     全局模式名称
     * @param roomId   房间ID
     * @param listener 回掉接口
     */
    public void showRoomModel(Context context, String name, String roomId, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        if (!StringUtil.isEmpty(name)) {
            map.put("name", name);
        } else if (!StringUtil.isEmpty(roomId)) {
            map.put("roomId", roomId);
        }

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/showmode");
        postRequest(context, url, map, RequestTag.SHOWROOMMODEL, listener);
    }


    /**
     * 新增或修改房间模式
     *
     * @param context  上下文
     * @param model    模式详细信息
     * @param listener 回掉接口
     */
    public void updateRoomMode(Context context, ModeAct model, RequestResultListener listener) {
        if (model == null) {
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("mode", model);
//        String json = GsonUtil.Object2Json(model);
//        Map<String, Object> map = GsonUtil.json2Map(json);
//        map.remove("deviceList");
//        Loger.d(json);
//        if (true) {
//            return;
//        }
        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/updatemode");
        postRequest(context, url, map, RequestTag.UPDATEROOMMODE, listener);
    }

    /**
     * 更改模式名称
     *
     * @param context  上下文
     * @param modeId   模式id
     * @param tag      模式名称
     * @param serialNo TODO 未确定
     * @param listener 回调接口
     */
    public void modifyModelName(Context context, String modeId, String tag, String serialNo, RequestResultListener listener) {

        HashMap<String, String> map = new HashMap<>();
        map.put("modeId", modeId);
        map.put("tag", tag);
        map.put("serialNo", serialNo);

        String url;
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/plan/modifyModeName";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1029";
        }
        postRequest(context, url, map, RequestTag.UPDATEROOMMODE, listener);
    }

    /**
     * 查询指定的联动预案或模式
     *
     * @param context  上下文
     * @param addr     设备Mac地址
     * @param listener 回调接口
     */
    public void planShow(Context context, String addr, String deviceType, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("addr", addr);
        map.put("deviceType", deviceType);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/plan/show");
        postRequest(context, url, map, RequestTag.PLANSHOW, listener);
    }

    /**
     * 新增或修改联动计划
     *
     * @param context  上下文
     * @param plan     联动预案信息列表
     * @param listener 回调接口
     */
    public void planUpdate(Context context, LinkPlan plan, RequestResultListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mode", plan);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/plan/update");
        postRequest(context, url, map, RequestTag.PLANUPDATE, listener);
    }

    /**
     * 添加(或更新)定时  为情景模式添加定时任务
     *
     * @param context
     * @param modeid      模式id
     * @param type        定时类型 delay reo
     * @param triggerTime
     * @param repeat
     * @param OFF
     */
    public void planSetTimeTask(Context context, String modeid, String type, String triggerTime, List repeat, String OFF, RequestResultListener listener) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("modeId", modeid);
        map.put("type", type);
        if (triggerTime != null)
            map.put("triggerTime", triggerTime);
        if (repeat.size() != 0)
            map.put("repeat", repeat);
        map.put("switch", OFF);
//        String temp = new Gson().Object2Json(map);
        String url;
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/plan/setTimeTask";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1062";
        }
        postRequest(context, url, map, RequestTag.SETTING_TIME_TASK, listener);
    }

    /**
     * 定时任务的开启和关闭
     *
     * @param context
     * @param id       定时任务ID
     * @param modeid   模式ID
     * @param OFF      on or off
     * @param listener
     */
    public void switchTimeTask(Context context, String id, String modeid, String OFF, RequestResultListener listener) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("modeid", modeid);
        map.put("id", id);
        map.put("switch", OFF);
        String url;
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/plan/switchTimeTask ";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1063";
        }
        postRequest(context, url, map, RequestTag.SWITCH_TIME_TASK, listener);
    }


    /**
     * 查询当前主机下的所有的模式信息
     *
     * @param context
     * @param sparam
     * @param listener
     */
    public void queryAllMode2CunrrentGateWay(Context context, String sparam, RequestResultListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sparam", "");
        String url = "";
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/plan/allModes ";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1066";
        }
        postRequest(context, url, map, RequestTag.AREAUPDATE, listener);
    }


}
