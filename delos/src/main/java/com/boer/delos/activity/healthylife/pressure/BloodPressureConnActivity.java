package com.boer.delos.activity.healthylife.pressure;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.activity.healthylife.tool.SavePressureDataPreferences;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.widget.CircleProgressBarView;

import org.shake.bluetooth.conn.PressuerConn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/18 0018 10:09
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressureConnActivity extends CommonBaseActivity {
    @Bind(R.id.circleProgressBar)
    CircleProgressBarView mCircleProgressBar;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.tv_bp_H)
    TextView mTvBpH;
    @Bind(R.id.tv_bp_L)
    TextView mTvBpL;
    @Bind(R.id.tv_blood_rate)
    TextView mTvBloodRate;
    @Bind(R.id.ll_result)
    LinearLayout mLlResult;
    @Bind(R.id.btn_measure_again)
    Button mBtnMeasureAgain;
    @Bind(R.id.btn_save_data)
    Button mBtnSaveData;

    private String mBPDeviceName = "ueua-BP";
    private static final int CONN_SUCCESS = 0x00;
    private static final int CONN_FAILURE = 0x01;
    private static final int CONN_DISCONNECT = 0x02;
    private static final int CONN_RECEIVE_DATA = 0x03;
    private static final int CONN_DEVICE = 0x04;
    private static final int CONN_ERROR=0x05;

    private boolean connectStatus = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket socket;
    private PressuerConn mPressuerConn;

    private ReadThread mReadThread;
    private ClientThread mClientThread;
    private Set<BluetoothDevice> mBluetoothDeviceSet;
    private BluetoothDevice mDevice;
    private User mUser;
    //测量结果
    private long mMeasuretime;
    private String mbpH = null;
    private String mbpL = null;
    private String mHeartRate = null;

    @Override
    protected int initLayout() {
        return R.layout.activity_blood_pressure_conn;
    }

    @Override
    protected void initView() {
//        tlTitleLayout.setTitle();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }
        mCircleProgressBar.startAnimation();
        mCircleProgressBar.setHintText(getString(R.string.text_connect_ing));
        tlTitleLayout.setTitle(getString(R.string.text_blood_press_monitor));
    }

    @Override
    protected void initData() {
        mPressuerConn = new PressuerConn();
        registerListener();
        initBluetooth();
        initScenBluetooth();
    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.btn_measure_again, R.id.btn_save_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_measure_again:
                mBtnSaveData.setEnabled(true);
                mbpH = null;
                mbpL = null;
                mHeartRate = null;
                mMeasuretime = 0;
                mLlResult.setVisibility(View.GONE);
//                mCircleProgressBar.startAnimation();
                if (socket != null && socket.isConnected()) {
//                    if (mPressuerConn == null) {
//                        mPressuerConn = new PressuerConn();
//                        if (mBluetoothAdapter == null) {
//                            return;
//                        }
//                        mBluetoothAdapter.startDiscovery();
//                        return;
//                    }
//                    sendMessageHandle(mPressuerConn.start());
//                    mHandler.sendEmptyMessage(CONN_RECEIVE_DATA);
//                    mCircleProgressBar.setHintText(getString(R.string.text_measure_ing));
//                    mCircleProgressBar.startAnimation();

                    ToastHelper.showShortMsg("正在测量中...");
                } else {
                    mCircleProgressBar.startAnimation();
                    mCircleProgressBar.setHintText(getString(R.string.text_connect_ing));
                    initBluetooth();
                    initScenBluetooth();
                }
                break;
            case R.id.btn_save_data:

                SavePressureDataPreferences.getInstance().writePressureDataXml(getApplicationContext(),
                        mMeasuretime + "", mbpH, mbpL, mHeartRate, "");

                if (TextUtils.isEmpty(mbpH)
                        || TextUtils.isEmpty(mbpL)
                        || TextUtils.isEmpty(mHeartRate)) {

                    toastUtils.showInfoWithStatus(getString(R.string.toast_measure_first));
                    return;
                }

                reportBloodpressure(mMeasuretime + "",
                        mbpH, mbpL, mHeartRate);

                break;
        }
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastHelper.showShortMsg(getString(R.string.toast_bluetooth_nor));
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothDeviceSet = mBluetoothAdapter.getBondedDevices();
    }

    private void initScenBluetooth() {
        for (BluetoothDevice device : mBluetoothDeviceSet) {
            if (!TextUtils.isEmpty(device.getName()) && device.getName().contains(mBPDeviceName)) {
                mDevice = device;
                mHandler.sendEmptyMessageDelayed(CONN_DEVICE, 0);
                return;
            }
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 并重新开始
        mBluetoothAdapter.startDiscovery();
    }

    private void registerListener() {
        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册查找结束action接收器
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_SUCCESS:
                    if (mCircleProgressBar != null)
                        mCircleProgressBar.setHintText(getString(R.string.text_measure_ing));

                    break;
                case CONN_FAILURE:
                    if (mCircleProgressBar != null){
                        mCircleProgressBar.setHintText(getString(R.string.text_connect_fail));
                        mCircleProgressBar.stopAnimation();
                    }
                    shutdownClient();


//                    socket = null;
                        break;
                case CONN_DISCONNECT:
                    if (mCircleProgressBar != null) {
                        mCircleProgressBar.setHintText("连接断开");
//                        ToastHelper.showShortMsg("连接断开");
                        mCircleProgressBar.stopAnimation();
                    }
                    shutdownClient();
                    break;
                case CONN_RECEIVE_DATA:
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    receiveData(msg);
                    break;
                case CONN_DEVICE:
                    Log.d("BloodPressure","CONN_DEVICE");
                    if (socket == null || (!socket.isConnected())) {
//                    if (!connectStatus) {
                        if (mClientThread == null) {
                            mClientThread = new ClientThread(mDevice.getAddress());
                        }
                        if(!mClientThread.isAlive())
                        mClientThread.start();
//                        mBluetoothAdapter.cancelDiscovery();
                    }
                    break;
                case CONN_ERROR:
                    String errorStr=pressureError(builder.toString());
                    if(!errorStr.equals("")){
                        if(mCircleProgressBar!=null){
                            mCircleProgressBar.setHintText(errorStr);
                            mCircleProgressBar.stopAnimation();
                        }
                        shutdownClient();
                        ToastHelper.showShortMsg("此次测量数据无效，请重新测量");
                    }
                    break;

            }
        }
    };

    StringBuilder builder = new StringBuilder();

    // 消息处理队列
    private void receiveData(Message msg) {
        List<Integer> mData = (List<Integer>) msg.obj;
        Object data = mPressuerConn.getData(mData);
        builder.setLength(0);
        builder.append(data + "");
        Loger.d("血压仪 " + builder.toString());
        try {
            //返回三个数据 收缩压 舒张压 心率
            if (data.toString().contains(" ")) {
                mHandler.removeMessages(CONN_ERROR);
                String[] dataResult = data.toString().split(" ");
                mLlResult.setVisibility(View.VISIBLE);
                mBtnSaveData.setVisibility(View.VISIBLE);

                mMeasuretime = TimeUtil.getCurrentstamp();
                String date = TimeUtil.formatStamp2Time(mMeasuretime, "yyyy/MM/dd HH:mm");
                mTvDate.setText(date);

                DealWithValue2.judgeBPColor(this, mTvState, Integer.parseInt(dataResult[0]),
                        Integer.parseInt(dataResult[1]), true);
                mbpH = dataResult[0];
                mbpL = dataResult[1];
                mHeartRate = dataResult[2];
                mTvBpH.setText(dataResult[0]);
                mTvBpL.setText(dataResult[1]);
                mTvBloodRate.setText(dataResult[2]);
                mCircleProgressBar.stopAnimation();
                shutdownClient();
                mBtnSaveData.setEnabled(true);
                mCircleProgressBar.setHintText(getString(R.string.text_measure_success));
            } else {
                if (builder.toString().contains("-200")) {
//                    mBluetoothAdapter.cancelDiscovery();
//                    connectStatus = true;
//                    mCircleProgressBar.setHintText(getString(R.string.text_measure_ing));
//                    mCircleProgressBar.setHintText(builder.toString());
                } else {
//                    if (!TextUtils.isEmpty(pressureError(builder.toString()))) {
//                        mCircleProgressBar.setHintText(pressureError(builder.toString()));
//                        mCircleProgressBar.stopAnimation();
//                    } else {
//                        mCircleProgressBar.setHintText(builder.toString());
//                    }
                    if(mCircleProgressBar!=null){
                        mCircleProgressBar.setHintText(builder.toString());
                    }
                }
                mHandler.removeMessages(CONN_ERROR);
                mHandler.sendEmptyMessageDelayed(CONN_ERROR,3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送测量命令
     *
     * @param mOrder
     */
    private void sendMessageHandle(byte[] mOrder) {
        if (socket == null) {
            mHandler.sendEmptyMessage(CONN_DISCONNECT);
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(mOrder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mHandler.sendEmptyMessage(CONN_DISCONNECT);
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
                String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device1 = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
                        System.out.println("血压仪 " + device1.getName() + device1.getAddress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(device1.getName()) && device1.getName().contains(mBPDeviceName)) {// 找到设备，准备配对设备
                        mDevice = device1;
                        mBluetoothAdapter.cancelDiscovery();
                        if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                            mHandler.sendEmptyMessage(CONN_DEVICE);
                        } else if (device1.getBondState() == BluetoothDevice.BOND_NONE) {
                            //配对
                            Method createBondMethod = null;
                            try {
                                createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//
                        }
                        return;
                    }

                    Loger.d("我的天 " + (device1.getBondState() == BluetoothDevice.BOND_BONDED));
                    int state = device1.getBondState();
                    if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                        if (!TextUtils.isEmpty(device1.getName()) && device1.getName().contains(mBPDeviceName)) {// 找到设备，准备配对设备
                            mBluetoothAdapter.cancelDiscovery();
                            mDevice = device1;
                            mHandler.sendEmptyMessageDelayed(CONN_DEVICE, 0);
                        }
                    } else if (device1.getBondState() == BluetoothDevice.BOND_NONE) {
                        try {
                            if (!TextUtils.isEmpty(device1.getName()) && device1.getName().equals(mBPDeviceName)) {// 找到设备，准备配对设备
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device1);
//                                mBluetoothDeviceSet.add(device1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    if (mBluetoothDeviceSet.size() <= 0) {
////                        connectStatus = false;
//                        if (mBluetoothAdapter.isDiscovering()) {
//                            mBluetoothAdapter.cancelDiscovery();
//                        }
//
////                        toastUtils.showInfoWithStatus("连接设备失败");
//                        Loger.d("血压 再次刷新");
//                        if (socket == null || !socket.isConnected()) {
//                            initScenBluetooth();
//                        }
//                    } else {
//                        Loger.d("血压 再次刷新");
//                        if (socket == null || !socket.isConnected()) {
//                            mBluetoothAdapter.startDiscovery();
//                        }
//                    }

//                    mCircleProgressBar.setHintText("搜索完成，未发现设备");
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device==null){
                        return;
                    }
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("BluetoothAdapter", "正在配对......");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("BluetoothAdapter", "完成配对");
                            if (!TextUtils.isEmpty(device.getName()) && device.getName().equals(mBPDeviceName)) {// 找到设备，准备配对设备
                                if(mBluetoothAdapter!=null&&mBluetoothAdapter.isDiscovering()){
                                    mBluetoothAdapter.cancelDiscovery();
                                }
                                mHandler.sendEmptyMessage(CONN_DEVICE);
                            }
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.d("BluetoothAdapter", "取消配对");
                        case BluetoothAdapter.STATE_CONNECTED:
                            Log.d("BluetoothAdapter", "STATE_CONNECTED......");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Log.d("BluetoothAdapter", "STATE_CONNECTING......");
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            Log.d("BluetoothAdapter", "STATE_DISCONNECTED......");

                            break;

                        default:
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Loger.d("BluetoothAdapter.ACTION_STATE_CHANGED");
                    BluetoothDevice device2 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device2==null){
                        return;
                    }
                    switch (device2.getBondState()) {
                        case BluetoothAdapter.STATE_CONNECTED:
                            Log.d("BluetoothAdapter", "STATE_CONNECTED......");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Log.d("BluetoothAdapter", "STATE_CONNECTING......");
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            Log.d("BluetoothAdapter", "STATE_DISCONNECTED......");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Loger.d("BluetoothAdapter.ACTION_ACL_CONNECTED");
                    if (mCircleProgressBar != null) {
                        mCircleProgressBar.setHintText("连接成功");
//                        mCircleProgressBar.stopAnimation();
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Loger.d("BluetoothAdapter.ACTION_ACL_DISCONNECTED");
                    if (mCircleProgressBar != null) {
//                        mCircleProgressBar.setHintText("连接断开");
//                        ToastHelper.showShortMsg("连接断开");
                        mCircleProgressBar.stopAnimation();
                    }
                    shutdownClient();
                    break;

            }

        }
    };

    //上传血压值
    private void reportBloodpressure(String measuretime,
                                     final String valueH, final String valueL, final String bpm) {
        if (TextUtils.isEmpty(valueH) || TextUtils.isEmpty(valueH) || TextUtils.isEmpty(bpm)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_measure_first));
            return;
        }
        toastUtils.showProgress(getString(R.string.toast_update_ing));//"上传中..."
        HealthController.getInstance().reportBloodpressure(this,
                mUser.getId().equals(Constant.USERID) ? "0" : mUser.getId(),
                measuretime + "",
                valueH, valueL, bpm, "", Constant.CURRENTHOSTID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));
                            mbpH = null;
                            mbpL = null;
                            mHeartRate = null;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000 * 2);
                        } else {
                            String msg = JsonUtil.parseString(Json, "msg");
                            toastUtils.showErrorWithStatus(msg);
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null)
                            toastUtils.showErrorWithStatus(getString(R.string.toast_update_failure));
                    }
                });
    }

    private class ClientThread extends Thread {
        private BluetoothDevice device;
        public ClientThread(String address) {
            device = mBluetoothAdapter.getRemoteDevice(address);
        }

        public void run() {
            try {
//                if (socket != null && socket.isConnected()) {
////                    return;
//                }
//                else if (socket == null) {
//                    // 创建一个Socket连接：只需要服务器在注册时的UUID号
//                    socket = device.createRfcommSocketToServiceRecord(UUID
//                            .fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                    socket.connect();
//                }
//                else if(!socket.isConnected()){
//                    socket.connect();
//                }

                socket = device.createRfcommSocketToServiceRecord(UUID
                        .fromString("00001101-0000-1000-8000-00805F9B34FB"));
                socket.connect();

                // 启动接受数据
                if (mPressuerConn == null) mPressuerConn = new PressuerConn();
                if (mReadThread == null) {
                    mReadThread = new ReadThread();
                }
                if(!mReadThread.isAlive())
                mReadThread.start();
                sendMessageHandle(mPressuerConn.start());
                mHandler.sendEmptyMessage(CONN_SUCCESS);
            } catch (Exception e) {
                Log.e("connect", "", e);
                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.obj = "连接服务端异常！断开连接重试。";
                //TODO
                mHandler.sendEmptyMessage(CONN_FAILURE);

            }
        }
    }

    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {

                        byte[] buf_data = new byte[bytes];
                        StringBuffer stringBuffer = new StringBuffer();
                        List<Integer> datas = new ArrayList<Integer>();
                        for (int i = 0; i < bytes; i++) {

                            int a = buffer[i] & 0xff;
                            buf_data[i] = buffer[i];
                            datas.add(a);
                            stringBuffer.append(a + ","); // stringBuffer 未做了操作
                        }
                        Message msg = new Message();
                        msg.what = CONN_RECEIVE_DATA;
                        msg.obj = datas;
                        mHandler.sendMessage(msg);
                        Loger.d("sb  " + stringBuffer.toString());

                    }
                } catch (Exception e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        shutdownClient();
        mBluetoothAdapter.cancelDiscovery();
        mHandler.removeMessages(CONN_ERROR);
    }

    /* 停止客户端连接 */
    private void shutdownClient() {
        if(mBluetoothAdapter!=null){
            mBluetoothAdapter.cancelDiscovery();
//            mBluetoothAdapter.disable();
        }

        new Thread() {
            public void run() {
                if (mClientThread != null) {
                    mClientThread.interrupt();
                    mClientThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    socket = null;
                }
            }
        }.start();
    }

    //0x0E：ERPROM异常 0x01: 人体心跳信号太小或压力突降
    //0x02: 杂讯干扰
    //0x03: 充气时间过长
    //0x04: 测的结果异常
    //0x0C: 校正异常
    //0x0B: 电源低电压
    private int lastError;
    private String pressureError(String error) {
        Loger.d("pressureError" + error);
        String result = "";
        int e = -100;
        try {
            e = Integer.valueOf(error);
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
//        if (lastError < e) {
//            return "";
//        }
        switch (e) {
            case 0x01:
                result = "人体心跳信号太小或压力突降";
                break;
            case 0x02:
                result = "杂讯干扰";
                break;
            case 0x03:
                result = "充气时间过长";
                break;
            case 0x04:
                result = "测的结果异常";
                break;
            case 0x0C://12
                result = " 校正异常";
                break;
            case 0x0B://11
                result = "电源低电压";
                break;
            case 0x0E://14
                result = "ERPROM异常";
                break;
            default:
                result = "";
                break;
        }
        lastError = e;
        return result;
    }

}
