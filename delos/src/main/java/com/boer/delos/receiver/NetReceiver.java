package com.boer.delos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ad.AdController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

/**
 * Created by Administrator on 2016/5/4 0004.
 * 检测网络连接的广播
 */

public class NetReceiver extends BroadcastReceiver {


    RefreshMainFragment refreshMainFragment;
    private Context mContext;

    public NetReceiver() {
        super();
    }

    public NetReceiver(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetUtil.checkNet(context)) {
            Constant.IS_INTERNET_CONN = false;
            return;
        }
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isAvailable()) {

                /////////////网络连接
                String name = netInfo.getTypeName();

                if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    /////WiFi网络
                    //发送UDP广播
                    Constant.IS_WIFI = true;

                    UDPUtils.startUDPBroadCast(new UDPUtils.ScanCallback() {
                        @Override
                        public void callback() {
                            checkLocalConnection();
                        }
                    });

                    //测试是否能连接到外网
                    AdController.getInstance().testToInternet(BaseApplication.getAppContext(),
                            new RequestResultListener() {
                                @Override
                                public void onSuccess(String Json) {
                                    Constant.IS_INTERNET_CONN = true;
                                }

                                @Override
                                public void onFailed(String Json) {
                                    Constant.IS_INTERNET_CONN = false;
                                }
                            });

                } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    /////有线网络
                    Constant.IS_WIFI = false;
                } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    /////////3g网络
                    Constant.IS_WIFI = false;
                    Constant.IS_INTERNET_CONN = true;
                    //非本地连接
                    Constant.IS_LOCAL_CONNECTION = Boolean.FALSE;
                }
            } else {
                ////////网络断开
                ToastHelper.showShortMsg("网络不可连接!");
            }
        }
    }

    /**
     * 检查是否在一个主机
     */
    private void checkLocalConnection() {
        Constant.IS_LOCAL_CONNECTION = Boolean.FALSE;
        L.e("CURRENTHOSTID:" + Constant.CURRENTHOSTID);
        L.e("LOCAL_CONNECTION_IP:" + Constant.LOCAL_CONNECTION_IP);
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        for (GatewayInfo info : Constant.gatewayInfos) {
            if (info.getHostId().equals(Constant.CURRENTHOSTID)) {
                Constant.IS_LOCAL_CONNECTION = Boolean.TRUE;
                Constant.LOCAL_CONNECTION_IP = info.getIp();
                break;
            }
        }
    }


    public interface RefreshMainFragment {
        void refresh();

    }

    public void setRefreshMainFragment(RefreshMainFragment refreshMainFragment) {
        this.refreshMainFragment = refreshMainFragment;
    }

}
