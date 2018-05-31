package com.boer.delos.commen;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SIPs;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.receiver.Exception2LoginReceiver;
import com.boer.delos.receiver.ExitReceiver;
import com.boer.delos.service.PollService;
import com.boer.delos.utils.StatusBarUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.SystemUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.AlarmPopUpWindow;
import com.boer.delos.view.popupWindow.OffLinePopUpWindow;

import org.linphone.squirrel.squirrelCallImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangkai
 * @Description: 基类
 * create at 2016/3/25 11:36
 */
public abstract class BaseListeningActivity extends BaseActivity {

    public OffLinePopUpWindow offLinePopUpWindow;
    private ExitReceiver mExitReceiver;
    private squirrelCallImpl squirrelCall = squirrelCallImpl.getInstance();
    private SharedPreferences preferences;
    private SharedPreferences.Editor pref_editor;
    private BroadcastReceiver mDeviceUpdateReceiver;
    private BroadcastReceiver mGateWayUpdateReceiver;
    private BroadcastReceiver mAlarmReceiver;
    protected View view;

    private static boolean isStartService = false;
    private Exception2LoginReceiver exceptionReceiver;

    private List<PopupWindow> popupWindowList = new ArrayList<>();
    private AlarmPopUpWindow alarmPopUpWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        pref_editor = preferences.edit();

        //退出监听
        mExitReceiver = new ExitReceiver(this);
        registerReceiver(mExitReceiver, new IntentFilter(Constant.ACTION_EXIT));

        /**网络请求异常广播 针对token过期*/
        exceptionReceiver = new Exception2LoginReceiver();
        registerReceiver(exceptionReceiver, new IntentFilter(Constant.ACTION_EXCEPTION));

        //设备信息更新
        mDeviceUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try{
                    deviceStatusUpdate();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(mDeviceUpdateReceiver, new IntentFilter(Constant.ACTION_DEVICE_UPDATE));

        //主机信息更新
        mGateWayUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                gatewayUpdate();
            }
        };
        registerReceiver(mGateWayUpdateReceiver, new IntentFilter(Constant.ACTION_GATEWAY_UPDATE));

        //报警接收
        mAlarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<String> alarmList = (List<String>) intent.getSerializableExtra("alarmList");
                for (String message : alarmList) {
                    popupAlarmWindow(message);
                }
            }
        };
        registerReceiver(mAlarmReceiver, new IntentFilter(Constant.ACTION_ALARM));

        //轮询服务
        startService(new Intent(this, PollService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceRunning(this, PollService.class.getName())) {
            startService(new Intent(this, PollService.class));
        }

    }

    private boolean isServiceRunning(Context context, String service_Name) {
        try {
            ActivityManager manager =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (service_Name.equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void readXMLInfo() {
        if (Constant.GATEWAY == null) {

        }
    }

    /**
     * 报警功能
     */
    private void alarmOn() {
        //亮屏
        SystemUtils.screenOn(this);
        //解锁
        SystemUtils.keyguardUnLock(this);
        //震动
        SystemUtils.vibratorOn(this);
        //报警播放
        SystemUtils.soundPlay(this);
    }


    /**
     * 主机信息更新
     */
    protected void gatewayUpdate() {
    }

    /**
     * 设备状态更新
     */
    protected void deviceStatusUpdate() {
        loginOrLoginOutSipServer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mExitReceiver);
        unregisterReceiver(mDeviceUpdateReceiver);
        unregisterReceiver(mGateWayUpdateReceiver);
        unregisterReceiver(mAlarmReceiver);
        unregisterReceiver(exceptionReceiver);
//        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            hideInput();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 弹出报警框
     *
     * @param message
     */
    protected void popupAlarmWindow(String message) {
        if (view == null) {
            return;
        }
        //开启报警功能
        alarmOn();
        //只有一个弹窗
        if (alarmPopUpWindow != null && alarmPopUpWindow.isShowing()) {
            alarmPopUpWindow.dismiss();
        }
        alarmPopUpWindow = new AlarmPopUpWindow(this, null);
        alarmPopUpWindow.setTextViewContent(message);
//        if (popupWindowList.size() != 0) {
//            alarmPopUpWindow.setPopAlarmBg();
//        }
        alarmPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

//        popupWindowList.add(alarmPopUpWindow);

    }

    /**
     * 改OfflineActivity 下线通知
     */
    public void offLine() {
        try {
            //如果当前窗口显示,就不再显示
            if (offLinePopUpWindow != null && offLinePopUpWindow.isShowing()) {
                return;
            }
            offLinePopUpWindow = new OffLinePopUpWindow(this, new OffLinePopUpWindow.ClickResultListener() {
                @Override
                public void ClickResult(int tag) {
                    offLinePopUpWindow.dismiss();

                    logout();
                    if (!ActivityCustomManager.getAppManager().isActivityStackEmpty()
                            && ActivityCustomManager.getAppManager().getCurrentActivity() instanceof LoginActivity) {
                        return;
                    }
                    Intent intent = new Intent(BaseListeningActivity.this, LoginActivity.class);
                    intent.putExtra("ActivityName", "OKHttpRequest");
                    BaseListeningActivity.this.startActivity(intent);
                }
            });
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String time = dateFormat.format(new Date(System.currentTimeMillis()));
            offLinePopUpWindow.tvTime.setText(time);
            offLinePopUpWindow.showAtLocation(vTitle, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 用户退出
     */
    public void logout() {

        //清理静态数据
        clearStaticData();
        //清除保存的数据
        SharedPreferencesUtils.clearAll(this);
        //停止5秒轮询
        stopService(new Intent(this, PollService.class));
        //发送退出广播
        sendBroadcast(new Intent(Constant.ACTION_EXIT));

        //主机信息清空 add by sunzhibin
        Constant.gatewayInfos.clear();

        ActivityCustomManager.getAppManager().finishAllActivity();
    }

    /**
     * 清理靜态数据
     */

    protected void clearStaticData() {
        Constant.DEVICE_MD5_VALUE = "";
        Constant.GATEWAY_MD5_VALUE = "";
        Constant.GLOBALMODE_MD5_VALUE = "";
        Constant.DEVICE_RELATE = new ArrayList<>();
        Constant.GATEWAY = null;
        Constant.GLOBAL_MODE = new ArrayList<>();
    }


    /**
     * 登录Sip服务
     *
     * @param account
     * @param passwd
     * @param SIP
     */
    protected void loginToSipServer(String account, String passwd, String SIP) {

        /**测试数据
         * 服务器获取的数据
         * "account": "051008010300010105011",
         "SIP": "075500000100010100000",
         "pwd": "080103",
         "hostID": "001207C40173"
         需要生成的数据
         * roomID 0001010501
         passwd 080103
         zoneID 05100801
         userName 051008010300010105011
         */


        if (account.length() != 21) {
            return;
        } else {
            String zoneID = account.substring(0, 8);
            String roomID = account.substring(account.length() - 1 - 10, account.length() - 1);
            //   String username = zoneID + "03" + roomID + "1";
            //保存在本地
            pref_editor.putString("roomID", roomID);
            pref_editor.putString("password", passwd);
            pref_editor.putString("zoneID", zoneID);
            pref_editor.putString("login_username", account);
            pref_editor.putString("SIP", SIP);
            pref_editor.commit();
            int protocol = 1;
            squirrelCallImpl.login_state = 0;
            if (passwd != null)
                if (SIPs.getSIPs().contains(SIP)) {
                    squirrelCall.squirrelAccountLogin(squirrelCallImpl.servername, squirrelCallImpl.serverport, protocol, null, account, passwd, null, 1);
                } else
                    squirrelCall.squirrelAccountLogin(squirrelCallImpl.servername_BOER, squirrelCallImpl.serverport, protocol, null, account, passwd, null, 1);

        }
    }

    /**
     * SIP服务退出
     */
    protected void logoutFromSipServer() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String username = preferences.getString("login_username", "");
        if (StringUtil.isEmpty(username)) {
            return;
        }
        squirrelCall.squirrelAccountExit(squirrelCallImpl.servername,
                squirrelCallImpl.serverport, username);
    }


    /**
     * 登录或登出门禁
     */
    protected void loginOrLoginOutSipServer() {
        boolean GuardIsExsit = false;
        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            if (device == null) return;
            if (StringUtil.isEmpty(device.getType())) {
                return;
            }
            //判断是否有门禁，如果有，并且未登录，则登录
            if ("Guard".equals(device.getType())) {
                if (squirrelCallImpl.login_state == 0) {

                    if (device.getGuardInfo() == null)
                        return;
                    if(Constant.IS_INTERNET_CONN)
                    loginToSipServer(device.getGuardInfo().getAccount(), device.getGuardInfo().getPwd(),
                            device.getGuardInfo().getSIP());
                }
                GuardIsExsit = true;
                break;
            }
        }

        if (!GuardIsExsit) {
            if (squirrelCallImpl.login_state == 1) {
                logoutFromSipServer();
            }
        }
    }

    private long backTime = 0;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (popupWindowList.size() != 0) {
//                if (popupWindowList.get(0) != null && popupWindowList.get(0).isShowing()) {
//
//                    if (Calendar.getInstance().getTimeInMillis() - backTime > 1000) {
//                        backTime = Calendar.getInstance().getTimeInMillis();
////                        toastUtils.showInfoWithStatus("双击，退出所有弹窗");
//                        popupWindowList.remove(popupWindowList.size() - 1).dismiss();
//                        if (Calendar.getInstance().getTimeInMillis() - backTime < 300) {
//                            ToastHelper.showShortMsg("双击，退出所有弹窗");
//                        }
//                    } else {
//                        for (PopupWindow pop : popupWindowList) {
//                            if (pop != null && pop.isShowing()) {
//                                pop.dismiss();
//                            }
//                        }
//                        popupWindowList.clear();
//                    }
//                    return true;
//
//                }
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
