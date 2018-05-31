package com.boer.delos.request.gateway;

import android.content.Context;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 主机相关接口
 * create at 2016/5/13 13:35
 */
public class GatewayController extends BaseController {
    public static GatewayController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static GatewayController getInstance() {
        if (instance == null) {
            synchronized (GatewayController.class) {
                if (instance == null) {
                    instance = new GatewayController();
                }
            }
        }
        getStaticData();
        return instance;
    }

    // add by sunzhibin
    private static void getStaticData() {
        if (StringUtil.isEmpty(Constant.USERID)) {
            SharedPreferencesUtils.readLoginUserNameAndPassword(BaseApplication.getAppContext());
        }
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            SharedPreferencesUtils.readCurrentHostIdFromPreferences(BaseApplication.getAppContext());
        }
        if (StringUtil.isEmpty(Constant.LOCAL_CONNECTION_IP)
                && System.currentTimeMillis() / 3 / 1000 == 0
                && Constant.IS_LOCAL_CONNECTION) {
            UDPUtils.startUDPBroadCast(null);
            Constant.IS_LOCAL_CONNECTION = false;
        }

    }

    /**
     * 读取主机属性
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void getGatewayProperties(Context context, RequestResultListener listener) {
        getGatewayProperties(context, Constant.CURRENTHOSTID, listener);
    }

    /**
     * 读取指定主机信息
     *
     * @param context
     * @param hostId
     * @param listener
     */
    public void getGatewayProperties(Context context, String hostId, RequestResultListener listener) {
//        HashMap<String, String> maps = new HashMap<>();
//        maps.put("second", "10");
//        maps.put("hostId", hostId);
//
//        String http = Constant.IS_LOCAL_CONNECTION ? Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
//        String url = String.format("http://%s%s", http, "/host/showproperty");
//        postRequest(context, url, maps,  RequestTag.DEVICE_QUERY, listener);
        getGatewayProperties(context, hostId, Constant.LOCAL_CONNECTION_IP, Constant.IS_LOCAL_CONNECTION, listener);
    }

    /**
     * 读取已绑定的主机中指定主机信息 可以定义是否是直连方式
     * Constant.gatewayInfos  add by sunzhibin  2016/11/23
     *
     * @param context
     * @param hostId              主机ID
     * @param hostIp              主机IP
     * @param IS_LOCAL_CONNECTION 是否用直联方式连接
     * @param listener
     */
    public void getGatewayProperties(Context context, String hostId, String hostIp, boolean IS_LOCAL_CONNECTION, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("second", "10");
        maps.put("hostId", hostId);
        String http = null;
        if (IS_LOCAL_CONNECTION) {
            http = "http://" + hostIp + ":8080";
        } else {
            http = URLConfig.HTTP;
        }
//        String http = Constant.IS_LOCAL_CONNECTION ? hostIp + ":8080" : URLConfig.HTTP;
//        String url = String.format("%s%s", http, "/host/showproperty");
        String url = http + "/host/showproperty";
//        postRequest(context, url, maps, RequestTag.DEVICE_QUERY, listener);
        if (IS_LOCAL_CONNECTION) {
            postWithLocalRequest(context, url, maps, hostId, listener);
        } else {
            postWithInternetRequest(context, url, maps, hostId, listener);
//            postWithInternetRequest(context, url, maps, RequestTag.DEVICE_QUERY, listener);
        }
    }

    /**
     * 查询用户主机
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void queryUserHost(Context context, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/host/show";
        Map<String, String> map = new HashMap<>();
        postWithInternetRequest(context, url, map, RequestTag.USER_HOST, listener);
    }

    /**
     * 获取所有的主机的信息
     */
    public void queryAllHost(Context context, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/host/show1";
        Map<String, String> map = new HashMap<>();
        postWithInternetRequest(context, url, map, RequestTag.USER_HOST_ALL, listener);
    }

    /**
     * 验证管理员密码
     *
     * @param context  上下文
     * @param ip       udp接收到的ip
     * @param password 为admin
     * @param listener 回调接口
     */
    public void verifyAdminPassword(Context context, String ip, String password, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "admin");
        map.put("password", password);

        String url = null;

//        if (Constant.IS_LOCAL_CONNECTION) {
//            url = "http://" + ip + ":8080/host/verifyadminpassword";
//        } else {
//            url = URLConfig.HTTP + "/wrapper/request?cmdType=1045";
//        }
//        postRequest(context, url, map, RequestTag.VERFYADMINPASSWORD, listener);

        url = "http://" + ip + ":8080/host/verifyadminpassword";
        postWithLocalRequest(context, url, map, RequestTag.VERFYADMINPASSWORD, listener);
    }

    /**
     * 修改管理员密码-修改安全码
     *
     * @param context
     * @param password
     * @param listener
     */
    public void modifyAdminPassword(Context context, String hostId, String password, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "admin");
        map.put("hostId", hostId);
        map.put("password", password);
        String url = URLConfig.HTTP + "/wrapper/request?cmdType=1019";
        postWithInternetRequest(context, url, map, RequestTag.RESET_ADMIN_PASSWORD, listener);
//        postRequest(context, url, map, RequestTag.RESET_ADMIN_PASSWORD, listener);
    }

    /**
     * 绑定主机
     *
     * @param context  上下文
     * @param hostId   主机Id
     * @param uid      用户Id
     * @param listener 回调接口
     */
    public void bindGateway(Context context, String hostId, String uid, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId + "") || StringUtil.isEmpty(uid)) {
            return;
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("hostId", hostId);
        maps.put("uid", uid);

        String url = URLConfig.HTTP + "/host/bind";
        postWithInternetRequest(context, url, maps, RequestTag.BINDGATEWAY, listener);
    }

    /**
     * 主机切换
     *
     * @param context
     * @param hostId
     * @param listener
     */
    public void gatewayChange(Context context, String hostId, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("uid", Constant.USERID);

        String url = URLConfig.HTTP + "/host/switch";
        postWithInternetRequest(context, url, map, "gatewayChange", listener);
    }

    /**
     * 查询房间是否有灯亮
     *
     * @param context
     * @param listener
     */
    public void queryRoomLightShow(Context context, RequestResultListener listener) {
        String url = splitHttpUrl("/host/queryglobaldata", "1037", Constant.LOCAL_CONNECTION_IP, false, true, true);
        HashMap<String, String> map = new HashMap<>();
        postRequest(context, url, map, "gatewayChange", listener);
    }
}
