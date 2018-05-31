package com.boer.delos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.boer.delos.constant.Constant;
import com.boer.delos.model.GatewayInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by sun on 2016/11/7 0007.
 */

public class JudgeNetworkTypeUtils {
    /**
     * 得到当前的手机网络类型
     *
     * @param context
     * @return
     */
    public static String getCurrentNetType(Context context) {
        String type = "";
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null && !info.isAvailable()) {

                type = "null";
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                type = "WIFI";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                int subType = info.getSubtype();
                if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                        || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                    type = "2G";
                } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                    type = "3G";
                } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                    type = "4G";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            type = "";
        }
        return type;
    }

    /**
     * 获取手机ip地址
     *
     * @return
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {

                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 判断是否是在同一个网络下
     * @param context
     * @return
     */
    public static boolean judgeIsSameNetwork(Context context) {
        if (!getCurrentNetType(context).equals("WIFI")) {
            return false;
            //简单判断下，
        }
        Constant.IS_WIFI = true;
        checkLocalConnection();

        return Constant.IS_LOCAL_CONNECTION;

    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
    private static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 检查是否在一个主机
     */
    private static void checkLocalConnection() {
        Constant.IS_LOCAL_CONNECTION = Boolean.FALSE;
        L.e("CURRENTHOSTID:" + Constant.CURRENTHOSTID);
        L.e("LOCAL_CONNECTION_IP:" + Constant.LOCAL_CONNECTION_IP);
        if(StringUtil.isEmpty(Constant.CURRENTHOSTID)){
            return;
        }
        for (GatewayInfo info : Constant.gatewayInfos) {
            if(info.getHostId().equals(Constant.CURRENTHOSTID)){
                Constant.IS_LOCAL_CONNECTION = Boolean.TRUE;
                Constant.LOCAL_CONNECTION_IP = info.getIp();
                break;
            }
        }
    }


}
