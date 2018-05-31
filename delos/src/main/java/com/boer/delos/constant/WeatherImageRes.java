package com.boer.delos.constant;

import com.boer.delos.model.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:天气图片
 * @CreateDate: 2017/3/31 0031 14:42
 * @Modify:
 * @ModifyDate:
 */


public class WeatherImageRes {


    public static synchronized List<Map<String, String>> dealWithWeather(Weather weather) {

        List<Weather.ResultsBean.WeatherDataBean> list = weather.getResults().get(0).getWeather_data();
        List<Map<String, String>> listMap = new ArrayList<>();
        HashMap<String, String> map;

        String city = weather.getResults().get(0).getCurrentCity(); //無錫
        String temp = weather.getResults().get(0).getWeather_data().get(0).getDate().split("实时：")[1].split("\\)")[0].split("℃")[0];
        String pm25 = weather.getResults().get(0).getPm25();
        String dateYMD = weather.getDate();//2016-12-12
        String[] dateYMDs = dateYMD.split("-");
        String date = dateYMDs[0] + "年" + dateYMDs[1] + "月" + dateYMDs[2] + "日";
        boolean isFirst = true;
        for (Weather.ResultsBean.WeatherDataBean bean : list) {
            map = new HashMap<>();

            String urlImg = bean.getDayPictureUrl();
            String dateW = bean.getDate();//周四 10月20日 （實時：20C）
            String weatherInfo = bean.getWeather();//小雨
            String temperature = bean.getTemperature();//14 ~ 10℃
            String wind = bean.getWind();              //"wind": "西北风3-4级"
            String[] dateWs = dateW.split(" ");
            String weekDay = " 星期" + dateWs[0].substring(1);

            String tes[] = temperature.split(" ");
            String tes2[] = tes[2].split("℃");

            String temperatureNew = tes[0] + "° /" + tes2[0] + "°";

            String winds[] = wind.split("风");
            String win2 = winds[1].substring(0, winds[1].length() - 1);
            if (isFirst) {
                map.put("temp", temp + "°");
                map.put("city", city);
                map.put("pm25", pm25 + "μg/m³");
                map.put("date", date);
                isFirst = false;
            }
            map.put("weather", weatherInfo);
            map.put("url", WeatherImageRes.getWeatherResIdByUrl(urlImg) + "");

            map.put("windSpeed", win2);
            map.put("windDirection", winds[0]);
            map.put("temperature", temperatureNew);
            map.put("weekDay", weekDay);

            listMap.add(map);
        }
        return listMap;

    }

    private static int getWeatherResIdByUrl(String dayPictureUrl) {
        String[] strings = dayPictureUrl.split("\\.png");
        String[] weathers = strings[0].split("/");
        String weather = weathers[weathers.length - 1];

        int resId = -1;
        switch (weather) {
            case "baoyu":
                resId = com.boer.delos.R.mipmap.weather_baoyu;
                break;
            case "baoxue":
                resId = com.boer.delos.R.mipmap.weather_baoxue;
                break;
            case "baoyuzhuandabaoyu":
                resId = com.boer.delos.R.mipmap.weather_baoyuzhuandabaoyu;
                break;
            case "dabaoyu":
                resId = com.boer.delos.R.mipmap.weather_dabaoyu;
                break;
            case "dabaoyuzhuantedabaoyu":
                resId = com.boer.delos.R.mipmap.weather_dabaoyuzhuantedabaoyu;
                break;

            case "daxue":
                resId = com.boer.delos.R.mipmap.weather_daxue;
                break;
            case "daxuezhuanbaoxue":
                resId = com.boer.delos.R.mipmap.weather_daxuezhuanbaoxue;
                break;
            case "dayu":
                resId = com.boer.delos.R.mipmap.weather_dayu;
                break;
            case "dayuzhuanbaoyu":
                resId = com.boer.delos.R.mipmap.weather_dayuzhuanbaoyu;
                break;
            case "dongyu":
                resId = com.boer.delos.R.mipmap.weather_dongyu;
                break;

            case "duoyun":
                resId = com.boer.delos.R.mipmap.weather_duoyun;
                break;
            case "fuchen":
                resId = com.boer.delos.R.mipmap.weather_fuchen;
                break;
            case "leizhenyu":
                resId = com.boer.delos.R.mipmap.weather_leizhenyu;
                break;
            case "leizhenyubanyoubingbao":
                resId = com.boer.delos.R.mipmap.weather_leizhenyubanyoubingbao;
                break;
            case "mai":
                resId = com.boer.delos.R.mipmap.weather_mai;
                break;


            case "qiangshachenbao":
                resId = com.boer.delos.R.mipmap.weather_qiangshachenbao;
                break;
            case "qing":
                resId = com.boer.delos.R.mipmap.weather_qing;
                break;
            case "shachenbao":
                resId = com.boer.delos.R.mipmap.weather_shachenbao;
                break;
            case "tedabaoyu":
                resId = com.boer.delos.R.mipmap.weather_tedabaoyu;
                break;
            case "wu":
                resId = com.boer.delos.R.mipmap.weather_wu;
                break;


            case "xiaoxue":
                resId = com.boer.delos.R.mipmap.weather_xiaoxue;
                break;
            case "xiaoxuezhuanzhongxue":
                resId = com.boer.delos.R.mipmap.weather_xiaoxuezhuanzhongxue;
                break;
            case "xiaoyu":
                resId = com.boer.delos.R.mipmap.weather_xiaoyu;
                break;
            case "xiaoyuzhuanzhongyu":
                resId = com.boer.delos.R.mipmap.weather_xiaoyuzhuanzhongyu;
                break;
            case "yangsha":
                resId = com.boer.delos.R.mipmap.weather_yangsha;
                break;


            case "yin":
                resId = com.boer.delos.R.mipmap.weather_yin;
                break;
            case "yujiaxue":
                resId = com.boer.delos.R.mipmap.weather_yujiaxue;
                break;
            case "zhenxue":
                resId = com.boer.delos.R.mipmap.weather_zhenxue;
                break;
            case "zhenyu":
                resId = com.boer.delos.R.mipmap.weather_zhenyu;
                break;
            case "zhongxue":
                resId = com.boer.delos.R.mipmap.weather_zhongxue;
                break;


            case "zhongxuezhuandaxue":
                resId = com.boer.delos.R.mipmap.weather_zhongxuezhuandaxue;
                break;
            case "zhongyu":
                resId = com.boer.delos.R.mipmap.weather_zhongyu;
                break;
            case "zhongyuzhuandayu":
                resId = com.boer.delos.R.mipmap.weather_zhongyuzhuandayu;
                break;

        }
        return resId;
    }

    public static int getWeatherResIdByValue(String weather) {

        int resId = -1;
        switch (weather) {
            case "暴雨":
                resId = com.boer.delos.R.mipmap.weather_baoyu;
                break;
            case "暴雪":
                resId = com.boer.delos.R.mipmap.weather_baoxue;
                break;
            case "暴雨转大暴雨":
                resId = com.boer.delos.R.mipmap.weather_baoyuzhuandabaoyu;
                break;
            case "大暴雨":
                resId = com.boer.delos.R.mipmap.weather_dabaoyu;
                break;
            case "大暴雨转特大暴雨":
                resId = com.boer.delos.R.mipmap.weather_dabaoyuzhuantedabaoyu;
                break;

            case "大雪":
                resId = com.boer.delos.R.mipmap.weather_daxue;
                break;
            case "大雪转暴雪":
                resId = com.boer.delos.R.mipmap.weather_daxuezhuanbaoxue;
                break;
            case "大雨":
                resId = com.boer.delos.R.mipmap.weather_dayu;
                break;
            case "大雨转暴雨":
                resId = com.boer.delos.R.mipmap.weather_dayuzhuanbaoyu;
                break;
            case "冻雨":
                resId = com.boer.delos.R.mipmap.weather_dongyu;
                break;

            case "多云":
                resId = com.boer.delos.R.mipmap.weather_duoyun;
                break;
            case "浮尘":
                resId = com.boer.delos.R.mipmap.weather_fuchen;
                break;
            case "雷阵雨":
                resId = com.boer.delos.R.mipmap.weather_leizhenyu;
                break;
            case "雷阵雨伴有冰雹":
                resId = com.boer.delos.R.mipmap.weather_leizhenyubanyoubingbao;
                break;
            case "霾":
                resId = com.boer.delos.R.mipmap.weather_mai;
                break;


            case "强沙尘暴":
                resId = com.boer.delos.R.mipmap.weather_qiangshachenbao;
                break;
            case "晴":
                resId = com.boer.delos.R.mipmap.weather_qing;
                break;
            case "沙尘暴":
                resId = com.boer.delos.R.mipmap.weather_shachenbao;
                break;
            case "特大暴雨":
                resId = com.boer.delos.R.mipmap.weather_tedabaoyu;
                break;
            case "雾":
                resId = com.boer.delos.R.mipmap.weather_wu;
                break;


            case "小雪":
                resId = com.boer.delos.R.mipmap.weather_xiaoxue;
                break;
            case "小雪转中雪":
                resId = com.boer.delos.R.mipmap.weather_xiaoxuezhuanzhongxue;
                break;
            case "小雨":
                resId = com.boer.delos.R.mipmap.weather_xiaoyu;
                break;
            case "小雨转中雨":
                resId = com.boer.delos.R.mipmap.weather_xiaoyuzhuanzhongyu;
                break;
            case "扬沙":
                resId = com.boer.delos.R.mipmap.weather_yangsha;
                break;


            case "阴":
                resId = com.boer.delos.R.mipmap.weather_yin;
                break;
            case "雨夹雪":
                resId = com.boer.delos.R.mipmap.weather_yujiaxue;
                break;
            case "阵雪":
                resId = com.boer.delos.R.mipmap.weather_zhenxue;
                break;
            case "阵雨":
                resId = com.boer.delos.R.mipmap.weather_zhenyu;
                break;
            case "中雪":
                resId = com.boer.delos.R.mipmap.weather_zhongxue;
                break;


            case "中雪转大雪":
                resId = com.boer.delos.R.mipmap.weather_zhongxuezhuandaxue;
                break;
            case "中雨":
                resId = com.boer.delos.R.mipmap.weather_zhongyu;
                break;
            case "中雨转大雨":
                resId = com.boer.delos.R.mipmap.weather_zhongyuzhuandayu;
                break;

        }
        return resId;
    }
}
