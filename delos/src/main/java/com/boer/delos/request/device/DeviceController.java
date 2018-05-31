package com.boer.delos.request.device;

import android.content.Context;
import android.text.TextUtils;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Addr;
import com.boer.delos.model.AirCleanDevice;
import com.boer.delos.model.AirSystemControlData;
import com.boer.delos.model.CircandianLight;
import com.boer.delos.model.Control;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.Device;
import com.boer.delos.model.Time;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangkai on 16/4/26.
 */
public class DeviceController extends BaseController {
    public static DeviceController instance = null;
    private List<Device> devices = new ArrayList<>();//查询设备用

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static DeviceController getInstance() {
        if (instance == null) {
            synchronized (DeviceController.class) {
                if (instance == null) {
                    instance = new DeviceController();
                }
            }
        }
        return instance;
    }

    /**
     * 查询设备 (查询设备某个房间或区域或某种类型的设备信息)
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void properties(Context context, RequestResultListener listener) {
        queryDeviceRelateInfo(context, listener);
//        if (StringUtil.isEmpty(Constant.USERID)) {
//            return;
//        }
//        String url =  URLConfig.HTTP + "/devices/properties?uid=" + Constant.USERID;
//        HashMap<String, String> map = new HashMap<>();
//        map.put("token", Constant.TOKEN);
//        map.put("type", "all");
//        L.e("properties params===" + new Gson().Object2Json(map) + "   userId===" + Constant.USERID);
//        OKHttpRequest.postWithNoKey(context, url, "propeties", new Gson().Object2Json(map), listener);
    }

    /**
     * 查询首页绿色生活的相关数据
     *
     * @param context 上下文
     * @param addr    地址数组
     * @param time    时间戳数组* @param listener  回调接口
     */
    public void greenLive(Context context, String[] addr, String[] time, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
//        String url =  URLConfig.HTTP + "/energy/query_elec?uid=" + Constant.USERID + "&token=" + Constant.TOKEN;
        String url = URLConfig.HTTP + urlQueryElec + "?uid=" + Constant.USERID + "&token=" + Constant.TOKEN;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("addr", new Gson().toJson(addr));
        maps.put("time", new Gson().toJson(time));
        String json = new Gson().toJson(maps);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.MAIN_GREENLIVE, json, listener);
    }

    /**
     * 控制设备(设备控制，控制如灯、窗帘等的开/关)
     *
     * @param context  上下文
     * @param devices  要控制的设备信息，状态等
     * @param listener 回调接口
     */
    public void deviceControl(Context context, List<ControlDevice> devices, RequestResultListener listener) {
        //发送控制命令时,设备状态不更新
        deviceControl(context, devices, true, listener);
    }


    /**
     * 控制设备(设备控制，控制如灯、窗帘等的开/关)
     *
     * @param context  上下文
     * @param devices  要控制的设备信息，状态等
     * @param delay    延迟
     * @param listener 回调接口
     */
    public void deviceControl(Context context, List<ControlDevice> devices, boolean delay, RequestResultListener listener) {
        //发送控制命令时,设备状态不更新
        Constant.IS_DEVICE_STATUS_UPDATE = !delay;

        HashMap<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);
        L.e("deviceControl:" + new Gson().toJson(maps));
        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/device/cmd");
        url = splitHttpUrl(urlDeviceCmd, null, Constant.LOCAL_CONNECTION_IP, false, true, false);
        postRequest(context, url, maps, RequestTag.DEVICE_CONTROL, listener);
    }

    /**
     * 控制设备
     *
     * @param context
     * @param cmd
     * @param listener
     */
    public void deviceControl(Context context, Serializable cmd, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        List<Serializable> list = new ArrayList<>();
        list.add(cmd);
        maps.put("devices", list);

//        L.e("deviceControl:" + new Gson().Object2Json(maps));
        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/device/cmd");
        postRequest(context, url, maps, RequestTag.DEVICE_UPDATE_PROP, listener);
    }


    /**
     * 更新设备关联(设备的关联，如灯的开关关联)
     *
     * @param context  上下文
     * @param dbId     数据库id
     * @param controls 需要关联的设备的信息
     * @param listener 回调接口
     */
    public void updateDeviceRelevance(Context context, int dbId, List<Control> controls, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/wrapper/request?cmdType=1040&uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("addr", "");// TODO 当前设备mac地址
        maps.put("controls", new Gson().toJson(controls));
        maps.put("dbId", dbId + "");
        maps.put("timestamp", System.currentTimeMillis() + "");
        String json = new Gson().toJson(maps);

        L.e("updateDeviceRelevance_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.UPDATE_DEVICE_RELEVANCE, json, listener);
    }

    /**
     * 查询设备关联信息(查询设备关联表，设备关联信息的获取)
     *
     * @param context  上下文
     * @param channel  设备通道
     * @param listener 回调接口
     */
    public void queryDeviceRelevance(Context context, String channel, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url = URLConfig.HTTP + "/wrapper/request?cmdType=1041?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("addr", "");// TODO 当前设备mac地址
        maps.put("channel", channel);
        String json = new Gson().toJson(maps);

        L.e("queryDeviceRelevance_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERY_DEVICE_CONTROL, json, listener);
    }

    /**
     * 删除设备(将已经扫描添加成功的设备删除。PS：需要先解绑，然后才能删除，删除后需要重新扫描该设备)
     *
     * @param context  上下文
     * @param devices  设备信息
     * @param listener 回调接口
     */
    public void removeDevice(Context context, List<Device> devices, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
//        String url = String.format("%s%s", http, "/device/remove");
        String url = String.format("%s%s", http, urlDeviceRemove);
        postRequest(context, url, maps, RequestTag.DEVICE_REMOVE, listener);
    }

    /**
     * 解绑设备(将已经绑定的设备从一个房间解绑，解绑后可将该设备重新绑定到另一个房间或者删除该设备)
     *
     * @param context  上下文
     * @param device   设备信息
     * @param listener 回调接口
     */
    public void dismissDevice(Context context, Device device, RequestResultListener listener) {
        List<Map<String, String>> devices = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("addr", device.getAddr());
        map.put("dismiss", "true");
        map.put("name", device.getName());
        devices.add(map);
        Map<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
//        String url = String.format("%s%s", http, "/device/dismiss");
        String url = String.format("%s%s", http, urlDeviceDismiss);
        postRequest(context, url, maps, RequestTag.DEVICE_DISMISS, listener);
    }

    /**
     * 添加设备或者修改设备属性
     *
     * @param context  上下文
     * @param device   设备信息
     * @param update   是否是更新配置标记，false-添加设备；true-更新设备；默认为false
     * @param listener 回调接口
     */
    public void updateProp(Context context, Device device, String update, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("device", device);
        maps.put("update", update);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
//        String url = String.format("%s%s", http, "/device/updateprop");
        String url = String.format("%s%s", http, urlDeviceUpdateProp);
        postRequest(context, url, maps, RequestTag.DEVICE_UPDATE_PROP, listener);
        String json = new Gson().toJson(maps);

        L.e("updateProp_params===" + json);
    }


    /**
     * 查询某个房间或区域或某种类型的设备信息
     *
     * @param context  上下文
     * @param type     设备类型，根据要查询的设备确定，如：Lock表示门锁,all表示所有设备
     * @param listener 回调接口
     */
    public void queryDevices(Context context, String type, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("type", type);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
//        String url = String.format("%s%s", http, "/devices/properties");
        String url = String.format("%s%s", http, urlDevicesProperties);
        postRequest(context, url, maps, RequestTag.DEVICE_QUERY, listener);
    }

    /**
     * 查询设备状态(查询设备某个房间或区域或某种类型的设备信息)
     *
     * @param context  上下文
     * @param devices  设备信息
     * @param listener 回调接口
     */
    public void queryDevicesStatus(Context context, List<Device> devices, RequestResultListener listener) {

//        String url = URLConfig.HTTP + "/device/status?uid=" + Constant.USERID;
//        String url = URLConfig.HTTP + urlDeviceStatus + "?uid=" + Constant.USERID;
        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);
        String url = String.format("%s%s", http, "/device/status");
        postRequest(context, url, maps, RequestTag.DEVICE_STATUS, listener);
    }

    /**
     * 查询单个设备状态
     *
     * @param context
     * @param device
     * @param listener
     */
    public void queryDevicesStatus(Context context, Device device, RequestResultListener listener) {
        devices.clear();
        devices.add(device);
        queryDevicesStatus(context, devices, listener);
    }

    /**
     * 扫描设备(查询某个房间或区域或某种类型的设备信息)
     *
     * @param context  上下文
     * @param type     设备类型
     * @param listener 回调接口
     */
    public void scanDevice(Context context, String ip, String type, RequestResultListener listener) {
        String url = "http://" + ip + ":8080/device/scan";
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);

        postWithLocalRequest(context, url, map, RequestTag.DEVICE_SCAN, listener);
    }

    /**
     * 查询某一设备的属性
     *
     * @param context  上下文
     * @param addr     设备地址
     * @param name     设备名称
     * @param listener 回调接口
     */
    public void queryOneProp(Context context, String addr, String name, RequestResultListener listener) {

    }

    /**
     * 查询设备的属性及状态（1054)
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void queryDeviceRelateInfo(Context context, RequestResultListener listener) {
        queryDeviceRelateInfo(context, "all", listener);
    }

    /**
     * 查询指定类型相关设备
     *
     * @param context
     * @param deviceType
     * @param listener
     */
    public void queryDeviceRelateInfo(Context context, String deviceType, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", deviceType);
        map.put("userPhone", StringUtil.isEmpty(Constant.CURRENTPHONE) ? "admin" : Constant.CURRENTPHONE);

        String url;
        //判断是否直连
        if (Constant.IS_LOCAL_CONNECTION && !TextUtils.isEmpty(Constant.LOCAL_CONNECTION_IP)) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/device/queryAllDevices";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1054";
        }
        postRequest(context, url, map, RequestTag.DEVICE_QUERY, listener);
    }


    /**
     * 查询门禁
     *
     * @param context
     * @param listener
     */
    public void queryDeviceGuardInfo(Context context, RequestResultListener listener) {
        queryDeviceRelateInfo(context, "Guard", listener);
    }

    /**
     * 查询设备关联信息(1041)
     *
     * @param context  上下文
     * @param channel  设备通道
     * @param addr     设备Mac地址
     * @param listener 回调接口
     */
    public void queryRelateDeviceInfo(Context context, String channel, String addr, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("channel", channel);
        map.put("addr", addr);

//        String url = URLConfig.HTTP + "/wrapper/request?cmdType=1041";
        String url = splitHttpUrl("/device/querylink", "1041", Constant.LOCAL_CONNECTION_IP, false, true, true).trim();
        postRequest(context, url, map, RequestTag.QUERY_RELATE_DEVICE_INFO, listener);
    }

    /**
     * 更新设备关联(1040)
     *
     * @param context  上下文
     * @param map
     * @param listener 回调
     */
    public void updateRelateDevice(Context context, Map<String, Object> map, RequestResultListener listener) {
        if (map == null) {
            return;
        }
        String url = splitHttpUrl("/device/link", "1040", Constant.LOCAL_CONNECTION_IP, false, true, true);
//        String url = URLConfig.HTTP + "/wrapper/request?cmdType=1040";
        postRequest(context, url, map, RequestTag.UPDATE_RELATE_DEVICE, listener);
    }

    /**
     * 读取空调状态
     *
     * @param context
     * @param map
     * @param listener
     */
    public void readAirState(Context context, Map<String, String> map, RequestResultListener listener) {
        if (map == null) {
            return;
        }

        String url;
        //判断是否直连
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/device/queryOneProp";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1053";
        }
        postRequest(context, url, map, "readAirState", listener);
    }


    //获取天气预报当天室外温度
    public void getWeatherTemperature(final RequestResultListener listener) {
        getWeatherTemperature(Constant.longitude, Constant.latitude, listener);
    }

    public void getWeatherTemperature(double longitude, double latitude, final RequestResultListener listener) {
        String location = longitude + "," + latitude;
        String url = "http://api.map.baidu.com/telematics/v3/weather?location="
                + location + "&output=json&ak=lg15x5WG2gAsXFr17N08zge8pwhSLU2L";
        Request request = new Request.Builder().url(url).build();
        BaseApplication.getOKHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                BaseApplication.getDelivery().post(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFailed("");
                        }
                    }
                });

            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final String jsonString = response.body().string();
//                    L.d("天气预报:" + response.body().string());
                    BaseApplication.getDelivery().post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onSuccess(jsonString);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 配置中控
     *
     * @param context
     * @param channel
     * @param addr
     * @param listener
     */
    public void configHGC(Context context, String channel, String addr,
                          String v_type, Map<String, Object> controls, RequestResultListener listener) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("channel", channel);
        map.put("addr", addr);
        map.put("v_type", v_type);
        map.putAll(controls);
//        L.i(new Gson().Object2Json(map));

        String url;
        //判断是否直连
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/device/configHgc";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1044";
        }
        postRequest(context, url, map, RequestTag.QUERY_RELATE_HGC, listener);
    }

    /**
     * 查询中控
     *
     * @param context
     * @param md5
     * @param addr
     * @param listener
     */
    public void queryHGC(Context context, String addr, String v_type, String md5, RequestResultListener listener) {

        HashMap<String, String> map = new HashMap<>();
        map.put("md5", md5);
        map.put("addr", addr);
        map.put("v_type", v_type);
        String url;
        //判断是否直连
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/device/queryHgcConfig";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1046";
        }
        postRequest(context, url, map, RequestTag.SETTING_RELATE_HGC, listener);
    }

    /**
     * 删除中控
     *
     * @param context
     * @param channel
     * @param HGCaddr
     * @param listener
     */
    public void deleteConfigHGC(Context context, String channel, String HGCaddr, String v_type, String controls, RequestResultListener listener) {

        HashMap<String, String> map = new HashMap<>();
        map.put("channel", channel);
        map.put("addr", HGCaddr);
        map.put("v_type", v_type);
//        map.put("controls", controls);
        String url;
        //判断是否直连
        if (Constant.IS_LOCAL_CONNECTION) {
            url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/device/deleteHgcConfig";
        } else {
            url = URLConfig.HTTP + "/wrapper/request?cmdType=1047";
        }
        postRequest(context, url, map, RequestTag.DELETE_RELATE_HGC, listener);
    }

    /**
     * 开始批量添加设备
     *
     * @param context
     * @param listener
     */
    public void startAddBatchScanDevice(Context context, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("op", "scan");
        url = String.format("http://%s%s%s", ip, ":8080", urlStartScanBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }


    public void startAddBatchScanN4Device(Context context, String ip, String n4SerialNo, RequestResultListener listener) {
        String url = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "CircadianLight");
        map.put("n4SerialNo", n4SerialNo);
        url = String.format("http://%s%s%s", ip, ":8080", urlStartScanBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }

    public void saveAddBatchScanN4Device(Context context, List<CircandianLight> list, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, Object> map = new HashMap<>();
        map.put("devices", list);
        map.put("isX2", "1");
        url = String.format("http://%s%s%s", ip, ":8080", urlSaveDeviceBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }


    /**
     * 结束批量添加设备
     *
     * @param context
     * @param listener
     */
    public void stopAddBatchScanDevice(Context context, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("op", "stop");
        url = String.format("http://%s%s%s", ip, ":8080", urlStopScanBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }

    /**
     * 查询设备批量添加设备
     *
     * @param context
     * @param listener
     */
    public void queryDeviceBatch(Context context, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, String> map = new HashMap<>();
        url = String.format("http://%s%s%s", ip, ":8080", urlQueryDeviceBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }

    /**
     * 保存批量添加设备
     *
     * @param context
     * @param listener
     */
    public void saveAddBatchScanDevice(Context context, List<Device> list, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, Object> map = new HashMap<>();
        map.put("devices", list);
        url = String.format("http://%s%s%s", ip, ":8080", urlSaveDeviceBatch);
        postWithLocalRequest(context, url, map, url, listener);
    }

    public void AddCommonDevice(Context context, List<Device> devices, String ip, RequestResultListener listener) {
        String url = "";
        HashMap<String, Object> map = new HashMap<>();
        map.put("devices", devices);
//        url = String.format("http://%s%s%s", ip, ":8080", urlSaveDeviceBatch);
        url = splitHttpUrl(urlCommonDevice, "1071", ip, false, true, true);
        postRequest(context, url, map, url, listener);
    }


    /**
     * @param context  上下文
     * @param devices  要控制的设备信息，状态等
     * @param listener 回调接口
     */
    public void deviceAirCleanControl(Context context, List<AirCleanDevice> devices, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);
        L.e("deviceControl:" + new Gson().toJson(maps));
        String url = splitHttpUrl(urlDeviceCmd, null, Constant.LOCAL_CONNECTION_IP, false, true, false);
        postRequest(context, url, maps, RequestTag.DEVICE_CONTROL, listener);
    }

    /**
     * @param context  上下文
     * @param devices  要控制的设备信息，状态等
     * @param listener 回调接口
     */
    public void deviceAirSystemControl(Context context, List<AirSystemControlData> devices, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("devices", devices);
        L.e("deviceControl:" + new Gson().toJson(maps));
        String url = splitHttpUrl(urlDeviceCmd, null, Constant.LOCAL_CONNECTION_IP, false, true, false);
        postRequest(context, url, maps, RequestTag.DEVICE_CONTROL, listener);
    }



    public void deviceAirCleanHistoryData(Context context, List<Time> time, List<Addr> addr, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();

        maps.put("time", time);
        maps.put("addr", addr);
        L.e("deviceControl:" + new Gson().toJson(maps));

        String url = splitHttpUrl("/energy/query_airFilter", "", "", false, false, false);
        postWithInternetRequest(context, url, maps, "query_airFilter", listener);
    }

    public void deviceTableWaterHistoryData(Context context, List<Addr> addr, List<Time> time, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();

        maps.put("time", time);
        maps.put("addr", addr);
        L.e("deviceControl:" + new Gson().toJson(maps));

        String url = splitHttpUrl(urlQueryTableWaterFilter, "", "", false, false, false);
        postWithInternetRequest(context, url, maps, urlQueryTableWaterFilter, listener);
    }

    public void deviceFloorWaterHistoryData(Context context, List<Addr> addr, List<Time> time, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();

        maps.put("time", time);
        maps.put("addr", addr);
        L.e("deviceControl:" + new Gson().toJson(maps));

        String url = splitHttpUrl(urlQueryFloorWaterFilter, "", "", false, false, false);
        postWithInternetRequest(context, url, maps, urlQueryFloorWaterFilter, listener);
    }


    /**
     * 查询华尔斯背景音乐的歌曲列表
     *
     * @param context
     * @param from     歌曲 index
     * @param to
     * @param listener
     */
    public void queryWiseMediaList(Context context, int from, int to, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("from", from);
        maps.put("to", to);
        String url = String.format("http://%s%s%s", Constant.LOCAL_CONNECTION_IP, ":8080", urlQueryWiseMediaList);
        postWithLocalRequest(context, url, maps, urlQueryWiseMediaList, listener);

    }
}
