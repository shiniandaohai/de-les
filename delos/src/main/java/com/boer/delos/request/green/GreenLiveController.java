package com.boer.delos.request.green;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Time;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 绿色生活接口的实现类
 * create at 2016/5/18 16:05
 *
 */
public class GreenLiveController {
    public static GreenLiveController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return 当前类的实例
     */
    public static GreenLiveController getInstance() {
        if (instance == null) {
            synchronized (GreenLiveController.class) {
                if (instance == null) {
                    instance = new GreenLiveController();
                }
            }
        }
        return instance;
    }

    /**
     * 查询电能数据
     *
     * @param context    上下文
     * @param addresses  地址列表
     * @param times      时间列表
     * @param listener   回调接口
     */
    public void queryElectricity(Context context, List<Addr> addresses, List<Time> times, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url =  URLConfig.HTTP + "/energy/query_elec?uid=" + Constant.USERID;
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", Constant.TOKEN);
        map.put("addr", addresses);
        map.put("time", times);
        String json = new Gson().toJson(map);

        L.e("queryElectricity params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERY_ELECTRICITY, json, listener);
    }

    /**
     * 查询插座数据
     *
     * @param context    上下文
     * @param addresses  地址列表
     * @param times      时间列表
     * @param listener   回调接口
     */
    public void querySocket(Context context, List<Addr> addresses, List<Time> times, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url =  URLConfig.HTTP + "/energy/query_socket?uid=" + Constant.USERID;
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", Constant.TOKEN);
        map.put("addr", addresses);
        map.put("time", times);
        String json = new Gson().toJson(map);

        L.e("querySocket params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERY_SOCKET, json, listener);
    }

    /**
     * 查询水表数据
     *
     * @param context    上下文
     * @param addresses  地址列表
     * @param times      时间列表
     * @param listener   回调接口
     */
    public void queryWater(Context context, List<Addr> addresses, List<Time> times, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url =  URLConfig.HTTP + "/energy/query_water?uid=" + Constant.USERID;
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", Constant.TOKEN);
        map.put("addr", addresses);
        map.put("time", times);
        String json = new Gson().toJson(map);

        L.e("queryWater params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERY_WATER, json, listener);
    }

    /**
     * 查询气表数据
     *
     * @param context    上下文
     * @param addresses  地址列表
     * @param times      时间列表
     * @param listener   回调接口
     */
    public void queryGas(Context context, List<Addr> addresses, List<Time> times, RequestResultListener listener) {
        if(StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        String url =  URLConfig.HTTP + "/energy/query_gas?uid=" + Constant.USERID;
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", Constant.TOKEN);
        map.put("addr", addresses);
        map.put("time", times);
        String json = new Gson().toJson(map);

        L.e("queryGas params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.QUERY_GAS, json, listener);
    }
}
