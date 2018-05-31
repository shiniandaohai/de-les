package com.boer.delos.utils;

import java.text.DecimalFormat;

/**
 * @author wangkai
 * @Description: 计算两个坐标之间的距离
 * create at 2015/11/13 2:01
 */
public class LatXYUtil {
    private static final double EARTH_RADIUS = 6378137;
    private static String distance;
    // 默认无锡坐标
    public static double Lat = 34.547588, Lng = 120.280787;
    public static String address = "";
    // 默认无锡区号
    public static String cityCode = "0510";

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    /**
     * @param nowLat 当前经度
     * @param nowLng 当前纬度
     * @param lat1   目标经度
     * @param lng1   目标纬度
     * @return
     */
    public static String DistanceOfTwoPoints(double nowLat, double nowLng, double lat1, double lng1) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(nowLat);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(nowLng);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        DecimalFormat df = new DecimalFormat("######0.00");
        if (s < 1000) {
            distance = df.format(s) + "m";
        } else if (s >= 1000 && s < 500000) {
            distance = df.format(s * 0.001) + "km";
        } else if (s > 500000) {
            distance = ">500km";
        } else {
            distance = "";
        }
        return distance;
    }

    /**
     * 距离我的距离
     *
     * @param lat 目标经度
     * @param lng 目标纬度
     * @return
     */
    public static String DistanceWithMe(double lat, double lng) {
        return DistanceOfTwoPoints(Lat, Lng, lat, lng);
    }


}
