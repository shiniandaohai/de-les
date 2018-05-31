package com.boer.delos.activity.camera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.boer.delos.R;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.camera.DatabaseManager;
import com.boer.delos.utils.camera.DeviceInfo;
import com.boer.delos.utils.camera.MyCamera;
import com.boer.delos.utils.camera.ThreadTPNS;
import com.boer.delos.utils.camera.WifiAdmin;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//此activity方便將來flow不同  搬移 mainActivity用 負責整個init的流程
@SuppressWarnings("all")
public abstract class InitCamActivity extends FragmentActivity
        implements IRegisterIOTCListener {

    public static List<MyCamera> mCameraList = new ArrayList<MyCamera>();
    public static List<Device> mDeviceList = Collections.synchronizedList(new ArrayList<Device>());
    private ResultStateReceiver resultStateReceiver;
    private IntentFilter filter;
    private ThreadReconnect m_threadReconnect = null;
    public static long startTime = 0;
    public static boolean noResetWiFi = true;
    private static String view_acc = "admin", view_pwd = "admin";

    private static boolean doWifiReconnect = false;
    private static boolean mIsAddToCloud = false;

    private boolean mIsSetWifiSeccess = false;

    private WifiAdmin WifiAdmin;
    private WifiManager mWifiManager;
    private WifiReceiver mWifiReceiver;
    private WifiConfiguration mWifiConfiguration = new WifiConfiguration();
    private static String mCurrWifiSSID = "";
    private static String mCurrWifiPWD = "";
    private static String mCandidateUID = "";
    private static String mCandidateNickName = "";
    private static String mCandidateType = "";
    private DatabaseManager mDBManager;
    private Object mIsSync = new Object();
    private String hostId;

    private static boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    protected void initView() {
        mDBManager = new DatabaseManager(getApplicationContext());
        MyCamera.init();

        WifiAdmin = new WifiAdmin(this);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_CAMERA_UPDATE);
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        resultStateReceiver = new ResultStateReceiver();
        registerReceiver(resultStateReceiver, filter);
        DatabaseManager.n_mainActivity_Status = 1;
        hostId = Constant.CURRENTHOSTID;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiReceiver);
        unregisterReceiver(resultStateReceiver);
        DatabaseManager.n_mainActivity_Status = 0;
        quit();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        DatabaseManager.n_mainActivity_Status = 1;
        super.onStart();
    }

    @Override
    protected void onStop() {
        //initDownLoad();
        DatabaseManager.n_mainActivity_Status = 0;
        super.onStop();
    }

    private void quit() {

        for (MyCamera camera : mCameraList) {
            camera.disconnect();
            camera.unregisterIOTCListener(this);
//            camera.uninit();
        }
        mCameraList.clear();
        mDeviceList.clear();

        MyCamera.uninit();

		/*int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid)*/
        ;

    }

   /* private void initdeviceList(final boolean bFirstTime) {

        WifiAirCleanController.getInstance().queryDevices(this, "Camera", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                String ret = JsonUtil.parseString(Json, "ret");
                if (ret != null && "0".equals(ret)) {
                    List<Devi±ce> tempdeviceList = JsonUtil.parseDataList(Json, Device.class, "response");
                    if (tempdeviceList == null) {
                        L.d("InitCameraActivity tempdeviceList ");
                        return;
                    }
                    if (bFirstTime) {
                        mDeviceList.clear();
                        mDeviceList.addAll(tempdeviceList);
                        initCameraList(mDeviceList);
                        return;
                    }
                    if (Constant.IS_CHANGE_HOST) {
                        Constant.IS_CHANGE_HOST = false;
                        //退出清空
                        quit();

                        mDeviceList.clear();
                        mDeviceList.addAll(tempdeviceList);
                        initCameraList(mDeviceList);
                        return;
                    }
                } else {
//                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String Json) {
                //toastUtils.showErrorWithStatus("未知异常");
            }
        });
    }
*/

    /**
     * 过滤不满足条件的摄像头
     */
    public void filterdeviceList(List<DeviceRelate> relateList) {
        List<Device> tempdeviceList = new ArrayList<Device>();
        for (DeviceRelate deviceRelate : relateList) {
            Device device = deviceRelate.getDeviceProp();
            DeviceStatus deviceStatus = deviceRelate.getDeviceStatus();

//             && deviceStatus.getOffline() != null
//                    && deviceStatus.getOffline() == 0
            if (device.getType().equals("Camera")) {
                tempdeviceList.add(device);
            }
        }
        if (tempdeviceList.size() == 0) return;

        if (isFirst || mDeviceList.size() == 0) {
            isFirst = false;
            mDeviceList.clear();
            mDeviceList.addAll(tempdeviceList);
            initCameraList(mDeviceList);
            return;
        }
        if (Constant.IS_CHANGE_HOST) {
            List<Device> tempAll = new ArrayList<>();
            tempAll.addAll(mDeviceList);
            if (tempdeviceList.retainAll(tempAll)) {
                if (tempdeviceList.size() == 0) {
                    Constant.IS_CHANGE_HOST = false;
                }
            }
            //退出清空
            quit();

            mDeviceList.clear();
            mDeviceList.addAll(tempdeviceList);
            initCameraList(mDeviceList);
            return;
        }
        //两个临时变量
        ArrayList<Device> removelist = new ArrayList<>();
        ArrayList<Device> tempAll = new ArrayList<>();
        ArrayList<Device> removeList2 = new ArrayList<>();

        tempAll.addAll(tempdeviceList);//新数据
        removelist.addAll(mDeviceList); //老数据
        removeList2.addAll(mDeviceList);

        if (removelist.retainAll(tempdeviceList)) {//交集 若retainAll返回false表示2个list相同  removelist 存交集

            tempAll.removeAll(removelist);//差集  //增加的    //tempAll 新数据

            removeList2.removeAll(removelist);//差集   // 删除的

            initCameraList(tempAll);//todo
            mCameraList.removeAll(removeList2);//todo

            initCameraList(tempAll); //增加的
            removeCameraList(removeList2);
        }

    }

    private void removeCameraList(ArrayList<Device> removeList2) {
        for (int i = 0; i < removeList2.size(); i++) {
            for (int j = 0; j < mCameraList.size(); j++) {
                if (mCameraList.get(j).getUID().equals(removeList2.get(i).getAddr())) {
                    mCameraList.get(j).disconnect();
                    mCameraList.get(j).unregisterIOTCListener(this);
                }
            }
        }
    }

    private void initCameraList(List<Device> deviceList) {
        int size = deviceList.size();
        for (int i = 0; i < size; i++) {
            Device device = deviceList.get(i);
            MyCamera camera = new MyCamera(device.getName(), device.getAddr(), view_acc, view_pwd);
            camera.registerIOTCListener(InitCamActivity.this);
            camera.connect(device.getAddr());
            camera.start(Camera.DEFAULT_AV_CHANNEL, view_acc, view_pwd);
            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                    SMsgAVIoctrlGetSupportStreamReq.parseContent());
            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                    AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                    AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
            camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                    AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
            if (HomepageListeningActivity.SupportOnDropbox) {
                camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_REQ, "0".getBytes());
            }

            camera.LastAudioMode = 1;

            mCameraList.add(camera);
        }

			/*cursor.close();
            db.close();*/
        INIT_CAMERA_LIST_OK();
    }

    public final static String getEventType(Context context, int eventType, boolean isSearch) {

        String result = "";

        switch (eventType) {
            case AVIOCTRLDEFs.AVIOCTRL_EVENT_ALL:
                result = isSearch ? context.getText(R.string.evttype_all).toString() : context.getText(R.string.evttype_fulltime_recording).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONDECT:
                result = context.getText(R.string.evttype_motion_detection).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEOLOST:
                result = context.getText(R.string.evttype_video_lost).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARM:
                result = context.getText(R.string.evttype_io_alarm).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS:
                result = context.getText(R.string.evttype_motion_pass).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEORESUME:
                result = context.getText(R.string.evttype_video_resume).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS:
                result = context.getText(R.string.evttype_io_alarm_pass).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_EXPT_REBOOT:
                result = context.getText(R.string.evttype_expt_reboot).toString();
                break;

            case AVIOCTRLDEFs.AVIOCTRL_EVENT_SDFAULT:
                result = context.getText(R.string.evttype_sd_fault).toString();
                break;
        }

        return result;
    }

    private class ResultStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String result = intent.getStringExtra("device");
//            if (result == null) {
//
//            } else {
//                for (int i = 0; i < mDeviceList.size(); i++) {
//                    if (mDeviceList.get(i).getAddr().equals(intent.getStringExtra("dev_uid")) == true) {
//                    }
//                }
//            }
            filterdeviceList(Constant.DEVICE_RELATE);
        }
    }

    private String getSessionMode(int mode) {

        String result = "";
        if (mode == 0)
            result = getText(R.string.connmode_p2p).toString();
        else if (mode == 1)
            result = getText(R.string.connmode_relay).toString();
        else if (mode == 2)
            result = getText(R.string.connmode_lan).toString();
        else
            result = getText(R.string.connmode_none).toString();

        return result;
    }

    private String getPerformance(int mode) {

        String result = "";
        if (mode < 30)
            result = getText(R.string.txtBad).toString();
        else if (mode < 60)
            result = getText(R.string.txtNormal).toString();
        else
            result = getText(R.string.txtGood).toString();

        return result;
    }

    public static void check_mapping_list(Context context) {
        DatabaseManager manager = new DatabaseManager(context);
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE, new String[]{"dev_uid"}, null, null, null, null, null);
        if (cursor != null) {
            SharedPreferences settings = context.getSharedPreferences("Preference", 0);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String uid = cursor.getString(0);
                settings.edit().putString(uid, uid).commit();
                ThreadTPNS ThreadTPNS = new ThreadTPNS(context, uid, 0);
                ThreadTPNS.start();
                cursor.moveToNext();

            }
            cursor.close();
        }
        db.close();
        check_remove_list(context);
    }

    public static void check_remove_list(Context context) {
        DatabaseManager manager = new DatabaseManager(context);
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.query(DatabaseManager.TABLE_REMOVE_LIST, new String[]{"uid"}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String uid = cursor.getString(0);
                ThreadTPNS thread = new ThreadTPNS(context, uid);
                thread.start();
                cursor.moveToNext();

            }
            cursor.close();
        }
        db.close();
    }

    @Override
    public void receiveFrameData(final Camera camera, int sessionChannel, Bitmap bmp) {
    }

    @Override
    public void receiveFrameInfo(final Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount,
                                 int incompleteFrameCount) {
    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());

        Message msg = handler.obtainMessage();
        msg.what = resultCode;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void receiveChannelInfo(final Camera camera, int sessionChannel, int resultCode) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
        bundle.putInt("sessionChannel", sessionChannel);

        Message msg = handler.obtainMessage();
        msg.what = resultCode;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {

        Bundle bundle = new Bundle();
        bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
        bundle.putInt("sessionChannel", sessionChannel);
        bundle.putByteArray("data", data);

        Message msg = handler.obtainMessage();
        msg.what = avIOCtrlMsgType;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String requestDevice = bundle.getString("requestDevice");

            byte[] data = bundle.getByteArray("data");
//			int i = 0;

            Device device = null;
            MyCamera camera = null;


            try {
                for (int i = 0; i < mCameraList.size(); i++) {

                    if (mCameraList.get(i).getUUID().equalsIgnoreCase(requestDevice)) {
                        camera = mCameraList.get(i);
                        if (i < mDeviceList.size()) {
                            device = mDeviceList.get(i);
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (msg.what) {

                case Camera.CONNECTION_STATE_CONNECTING:

                    if (camera != null) {
                        if (!camera.isSessionConnected() || !camera.isChannelConnected(0)) {
                            if (device != null) {
                            /*device.Status = getText(R.string.connstus_connecting).toString();
                            device.Online = false;*/

                            }
                        }
                    }
                    CONNECTION_STATE_CONNECTING(true);

                    break;

                case Camera.CONNECTION_STATE_CONNECTED:

                    if (camera != null) {
                        if (camera.isSessionConnected() && camera.isChannelConnected(0)) {
                            if (device != null) {
                            /*device.Status = getText(R.string.connstus_connected).toString();
                            device.Online = true;
							device.connect_count = 0;*/
                                if (m_threadReconnect != null) {
                                    m_threadReconnect.stopCheck = true;
                                    m_threadReconnect.interrupt();
                                    m_threadReconnect = null;
                                    //device.connect_count++;
                                }
                            /*if (device.ChangePassword) {
                                device.ChangePassword = false;
								ThreadTPNS ThreadTPNS = new ThreadTPNS(InitCameraActivity.this, device.UID, 0);
								ThreadTPNS.start();
								DatabaseManager manager = new DatabaseManager(InitCameraActivity.this);
								manager.delete_remove_list(device.UID);
							}*/

                            }
                        }
                    }
                    if (device != null)
                        CONNECTION_STATE_CONNECTED(true, device.getAddr());

                    break;

                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:

                    if (device != null) {
                    /*device.Status = getText(R.string.connstus_unknown_device).toString();
                    device.Online = false;*/

                    }
                    CONNECTION_STATE_UNKNOWN_DEVICE(true);

                    break;

                case Camera.CONNECTION_STATE_DISCONNECTED:
                    // no Use
                    if (device != null) {
                    /*device.Status = getText(R.string.connstus_disconnect).toString();
                    device.Online = false;*/

                        //if (device.connect_count < 3 && noResetWiFi) {
                        if (m_threadReconnect == null) {
                            startTime = System.currentTimeMillis();
                            m_threadReconnect = new ThreadReconnect(camera, device);
                            m_threadReconnect.start();
                            //device.connect_count++;
                        }
                        //}
                    }

                    if (camera != null) {
                        camera.disconnect();
                    }

                    CONNECTION_STATE_DISCONNECTED(true);
                    break;

                case Camera.CONNECTION_STATE_TIMEOUT:

		/*		if (device != null) {
                    device.Status = getText(R.string.connstus_disconnect).toString();
					device.Online = false;

					if (device.connect_count < 3 && noResetWiFi) {
						if (m_threadReconnect == null) {
							startTime = System.currentTimeMillis();
							m_threadReconnect = new ThreadReconnect(camera, device);
							m_threadReconnect.start();
							device.connect_count++;
						}
//						reconnect(camera,device);
					} else if (device.connect_count >= 3) {
						camera.disconnect();
					}
				}*/
                    CONNECTION_STATE_TIMEOUT(true);
                    break;

                case Camera.CONNECTION_STATE_WRONG_PASSWORD:

				/*if (device != null) {
                    device.Status = getText(R.string.connstus_wrong_password).toString();
					device.Online = false;
					ThreadTPNS ThreadTPNS = new ThreadTPNS(InitCameraActivity.this, device.UID);
					ThreadTPNS.start();
				}

				if (camera != null) {
					final MyCamera finalCam = camera;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finalCam.disconnect();
						}
					}, 1000);
				}*/
                    CONNECTION_STATE_WRONG_PASSWORD(true);
                    break;

                case Camera.CONNECTION_STATE_CONNECT_FAILED:

				/*if (device != null) {
                    device.Status = getText(R.string.connstus_connection_failed).toString();
					device.Online = false;

				}*/
                    CONNECTION_STATE_CONNECT_FAILED(true);
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT:

                    IOTYPE_USER_IPCAM_EVENT_REPORT(device, data);
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:

                    //IOTYPE_USER_IPCAM_DEVINFO_RESP(camera, device, data);
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP:

                    IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP(data);
                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_RESP:

                    if (HomepageListeningActivity.SupportOnDropbox) {
                /*	SMsgAVIoctrlGetDropbox SMsgAVIoctrlGetDropbox = new SMsgAVIoctrlGetDropbox(data);
                    device.nLinked = SMsgAVIoctrlGetDropbox.nLinked;
					device.nSupportDropbox = SMsgAVIoctrlGetDropbox.nSupportDropbox;
					if (SMsgAVIoctrlGetDropbox.szLinkUDID.equals(DatabaseManager.uid_Produce(InitCameraActivity.this))) {
						device.nLinked = 1;
					} else {
						device.nLinked = 0;
					}*/
                    }

                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:

                    if (doWifiReconnect) {
                        reconnectWifi();
                        doWifiReconnect = false;
                    }
                    break;

            }

			/*if (device != null && camera != null)
                device.Mode = camera.getSessionMode();
*/
            super.handleMessage(msg);
        }
    };

    class ThreadReconnect extends Thread {
        boolean stopCheck = true;
        Activity mActivity = null;
        Camera mReconnectCamera = null;
        Device mReconnectDevice = null;

        public ThreadReconnect(Camera camera, Device dev) {
            stopCheck = false;
            mReconnectCamera = camera;
            mReconnectDevice = dev;
        }

        public void run() {
            while (!stopCheck) {

                long endTime = System.currentTimeMillis();

                if (endTime - startTime > 30000) {

                    mReconnectCamera.disconnect();
                    mReconnectCamera.connect(mReconnectDevice.getAddr());
                    mReconnectCamera.start(Camera.DEFAULT_AV_CHANNEL, view_acc, view_pwd);
                    mReconnectCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                            AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
                    mReconnectCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                            SMsgAVIoctrlGetSupportStreamReq.parseContent());
                    mReconnectCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                            AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
                    mReconnectCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                            AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
                    if (HomepageListeningActivity.SupportOnDropbox) {
                        mReconnectCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_REQ, "0".getBytes());
                    }
                    stopCheck = true;
                }

            }
            ;
        }
    }

    public static void isAddToCloud(boolean isAddTo) {
        mIsAddToCloud = isAddTo;
    }

    public static void hasReconnect(boolean hasReconnect) {
        doWifiReconnect = hasReconnect;
    }

    public static void setCurrWifi(String ssid, String password) {
        mCurrWifiSSID = ssid;
        mCurrWifiPWD = password;
    }

    public static void setCandidate(String uid, String name, String type) {
        mCandidateUID = uid;
        mCandidateNickName = name;
        mCandidateType = type;
    }

    private void reconnectWifi() {

        mWifiConfiguration.SSID = "\"" + mCurrWifiSSID + "\"";
        mWifiConfiguration.wepKeys[0] = "\"" + mCurrWifiPWD + "\"";
        mWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        mWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        List<WifiConfiguration> ssidList = mWifiManager.getConfiguredNetworks();
        int netId = -1;
        for (WifiConfiguration wificonf : ssidList) {
            String tempSSID = wificonf.SSID.replaceAll("\"", "");
            if (tempSSID.equals(mCurrWifiSSID)) {
                netId = wificonf.networkId;
                break;
            }
        }

        mIsSetWifiSeccess = mWifiManager.enableNetwork(netId, true);
        mWifiManager.setWifiEnabled(true);
    }

    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                if (WifiAdmin.isCommect()) {
                    if (!mCandidateUID.equals("") && mIsSetWifiSeccess && mIsAddToCloud) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("cmd", "create");
                            jsonObject.put("usr", DatabaseManager.getLoginAccount());
                            jsonObject.put("pwd", DatabaseManager.getLoginPassword());
                            jsonObject.put("uid", mCandidateUID);
                            jsonObject.put("name", mCandidateNickName);
                            jsonObject.put("type", mCandidateType);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

					/*	mDeviceOnCloudClient.upload(DatabaseManager.Device_On_Cloud_URL, DatabaseManager.getLoginAccount(),
                                DatabaseManager.getLoginPassword(), jsonObject);*/

                        mCandidateUID = "";
                        mCandidateNickName = "";
                        mCandidateType = "";
                        mIsSetWifiSeccess = false;
                        mIsAddToCloud = false;
                    }
                } else {

                }
            }

        }
    }

    protected void INIT_CAMERA_LIST_OK() {
        // TODO Auto-generated method stub

    }

    protected void IOTYPE_USER_IPCAM_DEVINFO_RESP(MyCamera camera, DeviceInfo device, byte[] data) {
        // TODO Auto-generated method stub

    }

    protected void IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP(byte[] data) {
        // TODO Auto-generated method stub

    }

    protected void IOTYPE_USER_IPCAM_EVENT_REPORT(Device DeviceInfo, byte[] data) {
        // TODO Auto-generated method stub
    }

    ;

    protected void CONNECTION_STATE_CONNECTING(boolean Status) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_CONNECTED(boolean Status, String UID) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_UNKNOWN_DEVICE(boolean Status) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_DISCONNECTED(boolean Status) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_TIMEOUT(boolean Status) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_WRONG_PASSWORD(boolean Status) {
        // TODO Auto-generated method stub

    }

    protected void CONNECTION_STATE_CONNECT_FAILED(boolean Status) {
        // TODO Auto-generated method stub

    }


    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int avChannel, byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf, boolean isIframe, int codecId) {
        // TODO Auto-generated method stub

    }

    public void initDownLoad() {
    /*	mDeviceOnCloudClient = new DeviceOnCloudClient();
        mDeviceOnCloudClient.RegistrInterFace(this);*/

        DatabaseManager DatabaseManager = new DatabaseManager(getApplicationContext());
        JSONObject JSONObject = new JSONObject();
        try {
            JSONObject.put("cmd", "readall");
            JSONObject.put("usr", DatabaseManager.getLoginAccount());
            JSONObject.put("pwd", DatabaseManager.getLoginPassword());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //mDeviceOnCloudClient.download(DatabaseManager.Device_On_Cloud_URL, JSONObject);
    }

}
