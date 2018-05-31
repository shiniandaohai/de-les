package com.boer.delos.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.LightResult;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.utils.sign.MD5Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.boer.delos.constant.Constant.sRoomLighting;
import static com.boer.delos.utils.sign.MD5Utils.MD5;

/**
 * Created by zhukang on 16/7/14.
 */
public class PollService extends Service {

    private static final long TIMER_TIME = 1000 * 300;

    private Timer mTimer;
    private boolean isRunning = false;
    private boolean isFirst2USER_HOSTS = true;//add by sunzhibin 保证Constant.USER_HOSTS第一次能够赋值不为空
    private String HOST_OFF_LINE = "主机不在线";
    private String HOST_OFF_LINE2 = "网络不给力，请检查网络";

    /**
     * 5秒轮询
     */
    private void getDeviceStatusInfo() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    if (!Constant.IS_DEVICE_STATUS_UPDATE) {
                        return;
                    }
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    //判断设备信息是否有变更
                    String md5Value = MD5(Json);
                    if (!StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                            && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                            && Constant.GATEWAY != null) {
                        return;
                    }
//                    L.i("Device status changed");
                    Constant.DEVICE_RELATE = result.getResponse();
                    if (null == Constant.DEVICE_RELATE) {
                        Constant.DEVICE_RELATE = new ArrayList<>();
                    }
//                    L.i("result.getResponse():"+Constant.DEVICE_RELATE);
//                    L.i("getDeviceStatusInfo Json:"+Json);


                    //如果是无外网本地连接
                    if (Constant.IS_LOCAL_CONNECTION && !Constant.IS_INTERNET_CONN) {
//                        L.i("getDeviceStatusInfo alarmList:" + result.getNewAlarmList());
                        //取本地通知
                        List<String> list = result.getNewAlarmList();
                        if (list.size() > 0) {
                            Intent intent = new Intent();
                            intent.putExtra("alarmList", (Serializable) list);
                            intent.setAction(Constant.ACTION_ALARM);
                            sendBroadcast(intent);
                        }
                    }
                    if (!BaseApplication.isForeground) {
                        return;
                    }
                    //发送设备变更的广播
                    Constant.DEVICE_MD5_VALUE = md5Value;
                    sendBroadcast(new Intent(Constant.ACTION_DEVICE_UPDATE));
                } catch (Exception e) {
                    L.e("queryDeviceRelateInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    /**
     * 取得主机信息
     */
    public void getGatewayInfo() {
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        GatewayController.getInstance().getGatewayProperties(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5(Json);
                    if (md5Value == null) {
                        return;
                    }

                    Constant.IS_GATEWAY_ONLINE = true; // add by sunzhibin
                    if (!StringUtil.isEmpty(Constant.GATEWAY_MD5_VALUE)
                            && Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
                        return;
                    }
                    Constant.GATEWAY = result.getResponse();

                    if (!BaseApplication.isForeground) {
                        return;
                    }
                    Loger.d("getGatewayInfo()");

                    Constant.GATEWAY_MD5_VALUE = md5Value;
                    sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));
                } catch (Exception e) {
                    L.e("getGatewayInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
//                if (json.equals("网络不给力，请检查网络")) {
//                    Constant.GATEWAY = null;
//                    sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));
//                }
                try {

                    if (json.equals(HOST_OFF_LINE) || json.equals(HOST_OFF_LINE2)) {  // add by sunzhibin
                        Constant.GATEWAY = null;
                        Constant.GATEWAY_MD5_VALUE = "";
                        Constant.IS_GATEWAY_ONLINE = false;
                        if (BaseApplication.isForeground)
                            sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));
                    }
                    L.e("getGatewayInfo() onFailed():" + json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void queryLight() {
        GatewayController.getInstance().queryRoomLightShow(this, new RequestResultListener() {

            @Override
            public void onSuccess(String json) {
                Loger.d("queryLight " + json);
                try {
                    LightResult baseResult = GsonUtil.getObject(json, LightResult.class);
                    if(baseResult == null)
                        return;
                    if (baseResult.getRet() != 0) {
                        return;
                    }
                    if (!BaseApplication.isForeground) {
                        return;
                    }
                    String md5 = "";

                    if (StringUtil.isEmpty(baseResult.getMd5())) {
                        md5 = MD5Utils.MD5(json);
                    } else
                        md5 = baseResult.getMd5();
//                    LightResult result = GsonUtil.getObject(json, LightResult.class);
                    if (!StringUtil.isEmpty(Constant.sRoomLightingMd5)
                            && md5.equals(Constant.sRoomLightingMd5)) {
                        return;
                    } else if (StringUtil.isEmpty(Constant.sRoomLightingMd5)) {
                        Constant.sRoomLightingMd5 = md5;
                    }

                    JSONObject jsonObject = new JSONObject(json);

                    jsonObject = jsonObject.getJSONObject("response");

                    jsonObject = jsonObject.getJSONObject("light");

                    Iterator<String> iterator = jsonObject.keys();
                    sRoomLighting.clear();
                    while (iterator.hasNext()) {
                        String key = iterator.next() + "";
                        sRoomLighting.put(key, jsonObject.get(key));
                    }
                    Loger.d("queryLight()");
                    Constant.sRoomLightingMd5 = md5;
                    sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                getListMaps(json);
            }

            @Override
            public void onFailed(String json) {
                Loger.d("queryLight " + json);

            }
        });
    }

    /**
     * 获取全局模式
     */
    public void getGlobalMode() {
        LinkManageController.getInstance().requestGlobalModes(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    LinkResult result = new Gson().fromJson(Json, LinkResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5(Json);
                    if (Constant.GLOBALMODE_MD5_VALUE.equals(md5Value)) {
                        return;
                    }
//                    L.i("Global mode changed");
                    Constant.GLOBAL_MODE = result.getResponse();
                    if (Constant.GLOBAL_MODE == null) {
                        Constant.GLOBAL_MODE = new ArrayList<Link>();
                    }
                    if (!BaseApplication.isForeground) {
                        return;
                    }
                    Loger.d("getGlobalMode()");

                    Constant.GLOBALMODE_MD5_VALUE = md5Value;
                    sendBroadcast(new Intent(Constant.ACTION_GLOBAL_MODE_UPDATE));
                } catch (Exception e) {
                    L.e("getGlobalMode:" + e);
                }
            }

            @Override
            public void onFailed(String Json) {
                L.e("getGlobalMode:" + Json);
            }
        });
    }

    /**
     * 获取当前用户主机Id
     */
    private void getCurrentHostId() {
        GatewayController.getInstance().queryUserHost(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Json = StringUtil.nullReplace(Json);
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String currentHostId = result.getCurrentHostId();
//                        /*主机ID为空，请求一次 change by sunzhibin 17/2/4*/
//                    getGatewayInfo();
//                    getGlobalMode();
//                    getDeviceStatusInfo();

                    if (isFirst2USER_HOSTS) {
                        Constant.USER_HOSTS = result.getHosts();//应该对他赋值一次
                        isFirst2USER_HOSTS = false;
                    }
                    if (!StringUtil.isEmpty(Constant.CURRENTHOSTID)
                            && Constant.CURRENTHOSTID.equals(currentHostId)) {
                        return;
                    }
                    //保存当前主机
                    Constant.CURRENTHOSTID = currentHostId;
                    Constant.USER_HOSTS = result.getHosts();//应该对他赋值一次
                    SharedPreferencesUtils.saveCurrentHostIdToPreferences(PollService.this);
                } catch (Exception e) {
                    L.e("getCurrentHostId:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                L.e("getCurrentHostId:" + json);
            }
        });
    }

    /**
     * 查询用户权限
     *
     * @param
     * @param userId
     * @param hostId
     * @param
     */
    private void queryUserPermission(String userId, String hostId) {
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(hostId)) {
            return;
        }
        FamilyManageController.getInstance().queryUserPermission(this, hostId, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    Constant.PERMISSIONS = JsonUtil.parseString(json, "permissions");
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        Loger.d("PollService onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            return super.onStartCommand(intent, flags, startId);
        }
        isRunning = true;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                getCurrentHostId();
                queryLight();
                getGlobalMode();
                getDeviceStatusInfo();
                queryUserPermission(Constant.USERID, Constant.CURRENTHOSTID);
                getGatewayInfo();

            }
        }, 0, TIMER_TIME);
        return super.onStartCommand(intent, flags, startId);
    }

}
