package com.boer.delos.request.ad;

import android.content.Context;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Weather;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhukang on 16/7/14.
 */
public class AdController extends BaseController {

    private static AdController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static AdController getInstance() {
        if (instance == null) {
            synchronized (AdController.class) {
                if (instance == null) {
                    instance = new AdController();
                }
            }
        }
        return instance;
    }

    /**
     * 获取广告数据
     *
     * @param context
     * @param listener
     */
    public void getAdInfo(Context context, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("recent", String.valueOf(4));

        String url = URLConfig.HTTP + "/notification/query_recent_notification";
//        String url =  URLConfig.HTTP + query_recent_notification;
        postWithInternetRequest(context, url, map, RequestTag.ADVERTISEMENT, listener);
    }

    /**
     * 连接到外网
     *
     * @param context
     * @param listener
     */
    public void testToInternet(final Context context, final RequestResultListener listener) {
//        String url = "http://api.map.baidu.com/telematics/v3/weather?location=无锡&output=json&ak=jhgIhZw702LslfPBUaPuUFdr";
        String url = "http://api.map.baidu.com/telematics/v3/weather?location=无锡&output=json&ak=lg15x5WG2gAsXFr17N08zge8pwhSLU2L";
//        String url = "https://www.baidu.com";
        final Request request = new Request.Builder().url(url).build();
        BaseApplication.setTimeOut(5000);
        BaseApplication.getOKHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                BaseApplication.getDelivery().post(new Runnable() {

                    @Override
                    public void run() {
//                        BaseApplication.setTimeOut(10000);
                        if (listener != null) {
                            listener.onFailed("");
                        }
                    }

                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String result = response.body().string();
                BaseApplication.getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        Weather weather = GsonUtil.getObject(result, Weather.class);
                        if (weather == null || StringUtil.isEmpty(weather.getStatus())) {
                            if (listener != null) {
                                listener.onFailed("");
                            }
                        } else {
                            if (listener != null) {
                                listener.onSuccess("");
                            }
//
                        }
                    }
                });
            }
        });
        BaseApplication.setTimeOut(10000);
    }

    public void getWeatheInfo(Context context,String city, RequestResultListener listener){
        String url = "http://wthrcdn.etouch.cn/WeatherApi?city="+city;
        OKHttpRequest.RequestGet(context,url,"WEATHER",listener);
    }

    public void getWeatherInfo(Context context,String ip,RequestResultListener listener) {
        String host = "http://ali-weather.showapi.com";
        String path = "ip-to-weather";//"gps-to-weather";//
        String method = "GET";
        String appcode = "e9cf08db587d468b8a6da999c5f047f4";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("ip", ip);
        querys.put("need3HourForcast", "0");
        querys.put("needAlarm", "0");
        querys.put("needHourData", "0");
        querys.put("needIndex", "1");
        querys.put("needMoreDay", "1");

        try {
            StringBuilder tempParams = new StringBuilder();

            int pos = 0;
            for (String key : querys.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(querys.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s?%s", host, path, tempParams.toString());

            OKHttpRequest.RequestGet(context,requestUrl,"APPCODE " + appcode,"WEATHER",listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
