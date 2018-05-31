package com.boer.delos.activity.healthylife.sugar;

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
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.activity.healthylife.tool.SaveSugarDataPreferences;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.CircleProgressBarView;

import org.shake.bluetooth.conn.SugarConn;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/18 0018 21:39
 * @Modify:
 * @ModifyDate:
 */


public class SugarConnActivity extends CommonBaseActivity {
    @Bind(R.id.circleProgressBar)
    CircleProgressBarView mCircleProgressBar;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.tv_blood_sugar)
    TextView mTvBloodSugar;
    @Bind(R.id.iv_sugar_standard)
    ImageView mIvSugarStandard;
    @Bind(R.id.ll_measure_done)
    LinearLayout mLlMeasureDone;
    @Bind(R.id.btn_measure_again)
    Button mBtnMeasureAgain;
    @Bind(R.id.btn_save_data)
    Button mBtnSaveData;
    @Bind(R.id.ctv_before)
    CheckedTextView mCtvBefore;
    @Bind(R.id.ctv_after)
    CheckedTextView mCtvAfter;

    private static final int CONN_SUCCESS = 0x00;
    private static final int CONN_FAILURE = 0x01;
    private static final int CONN_DISCONNECT = 0x02;
    private static final int CONN_RECEIVE_DATA = 0x03;
    private static final int CONN_DEVICE = 0x04;
    private static final String DEVICE_BRAND = "UeUa-DM"; //默认搜索这个名字的设备


    private User mUser;
    private SugarConn mSugarConn;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mBluetoothDeviceSet;
    private String isAfter;

    private Thread mClientThread;
    private Thread mReadThread;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private long measuretime;
    private boolean connectStatus;
    private String mValue;

    @Override
    protected int initLayout() {
        return R.layout.activity_sugar_connect;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        mCircleProgressBar.setHintText(getString(R.string.text_connect_ing));
    }

    @Override
    protected void initData() {
        mSugarConn = new SugarConn();
        initDate();
        initBluetooth();
        initScenBluetooth();

    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.iv_sugar_standard, R.id.btn_measure_again, R.id.btn_save_data,
            R.id.ll_eat_after, R.id.ll_eat_before, R.id.ctv_before, R.id.ctv_after})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sugar_standard:

                break;
            case R.id.btn_measure_again:
                if (connectStatus) {
                    mHandler.sendEmptyMessage(CONN_RECEIVE_DATA);
                } else {
                    initScenBluetooth();
                }
                break;
            case R.id.btn_save_data:

                reportBloodSugar(measuretime + "", mValue);

                break;
            case R.id.ll_eat_after:
            case R.id.ctv_after:
            case R.id.ll_eat_before:
            case R.id.ctv_before:
                mCtvBefore.toggle();
                mCtvAfter.toggle();
                changeTextViews();
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

    }

    private void initScenBluetooth() {
        mBluetoothDeviceSet = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : mBluetoothDeviceSet) {

            if (!TextUtils.isEmpty(device.getName()) && device.getName().equals(device.getName())) {
                mDevice = device;
                mHandler.sendEmptyMessageDelayed(CONN_DEVICE, 1000);
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

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

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
                    if (mCircleProgressBar != null)
                        mCircleProgressBar.setHintText(getString(R.string.text_connect_fail));
                    mSocket = null;
                    break;
                case CONN_DISCONNECT:
                    if (toastUtils != null)
                        toastUtils.showInfoWithStatus(getString(R.string.text_disconnect));
                    break;
                case CONN_RECEIVE_DATA:
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    getDataFromBluetoothDevice(msg);
                    break;
                case CONN_DEVICE:
                    if (!connectStatus) {
                        mClientThread = new ClientThread(mDevice.getAddress());
                        mClientThread.start();
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    break;

            }
        }
    };

    /**
     * 时间的初始化
     */
    private void initDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        String date = dateFormat.format(new Date());
        int time = Integer.parseInt(date);
        if (time < 10) {
            isAfter = "0";
        } else if (time > 10 && time < 16) {
            isAfter = "2";
        } else if (time > 16 && time < 24) {
            isAfter = "4";
        }
        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册查找结束action接收器
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.EXTRA_CONNECTION_STATE);
        registerReceiver(mReceiver, filter);

    }

    private void changeTextViews() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        String date = dateFormat.format(new Date());
        int time = Integer.parseInt(date);
        //"餐前"按钮点击
        if (mCtvBefore.isChecked()) {
            if (time < 10) {
                isAfter = "0";
            } else if (time > 10 && time < 16) {
                isAfter = "2";
            } else if (time > 16 && time < 24) {
                isAfter = "4";
            }

        } else {
            if (time < 10) {
                isAfter = "1";
            } else if (time > 10 && time < 16) {
                isAfter = "3";
            } else if (time > 16 && time < 24) {
                isAfter = "5";
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device1 = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (StringUtil.isEmpty(device1.getName())) {
                        Loger.d("SugarConn2Activity " + device1.getBondState() + "-----------" + device1.getAddress());
                        return;
                    }
                    Loger.d("SugarConn2Activity() " + device1.getName() + "++++++" + device1.getBondState() + "++++" + device1.getAddress());
                    if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                        if (device1.getName().contains(DEVICE_BRAND)) {// 找到设备，准备配对设备
                            Loger.d("SugarConn2Activity() onReceive() " + device1.getName() + "++++++" + device1.getBondState() + "++++" + device1.getAddress());
//                                mIdTextViewConnect.setText("测量中...");
                            mHandler.sendEmptyMessageDelayed(CONN_DEVICE, 1000);
                        }
                    } else {
                        try {
                            if (!StringUtil.isEmpty(device1.getName()) && device1.getName().contains(DEVICE_BRAND)) {// 找到设备，准备配对设备
                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (mBluetoothDeviceSet.size() <= 0) {
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }

                    }
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    Loger.d("SugarConn2Activity onReceive() BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED");
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_CONNECTED) {
                        Loger.d("SugarConn2Activity onReceive() BluetoothAdapter.STATE_CONNECTED");

                    }
                    if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                        Loger.d("SugarConn2Activity onReceive() BluetoothAdapter.STATE_DISCONNECTED");
                    }
                    break;

                case BluetoothAdapter.EXTRA_CONNECTION_STATE:

                    Loger.d("SugarConn2Activity onReceive() BluetoothAdapter.EXTRA_CONNECTION_STATE");

                    break;
            }
        }

    };

    private class ClientThread extends Thread {
        private BluetoothDevice device;

        public ClientThread(String address) {
            device = mBluetoothAdapter.getRemoteDevice(address);
        }

        public void run() {
            try {
                if (mSocket == null)
                    // 创建一个Socket连接：只需要服务器在注册时的UUID号
                    mSocket = device.createRfcommSocketToServiceRecord(UUID
                            .fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if (!mSocket.isConnected())
                    mSocket.connect();
                if (mReadThread != null) mReadThread = null;
                mReadThread = new ReadThread();
                mReadThread.start();
                mBluetoothAdapter.cancelDiscovery();
            } catch (IOException e) {
                Log.e("AAAABBBB", "", e);
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
                mmInStream = mSocket.getInputStream();
                Loger.d("AAAABBBB  " + mmInStream.toString());
                while (true) {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {

                        byte[] buf_data = new byte[bytes];
                        StringBuffer stringBuffer = new StringBuffer();
                        List<Integer> datas = new ArrayList<Integer>();
                        for (int i = 0; i < bytes; i++) {

                            int a = buffer[i] & 0xff;
                            buf_data[i] = buffer[i];
                            datas.add(a);
                            stringBuffer.append(a + ",");
                        }
                        Message msg = new Message();
                        msg.what = CONN_RECEIVE_DATA;
                        msg.obj = datas;
                        mHandler.sendMessage(msg);
                        Loger.d("AAAABBBB  " + stringBuffer.toString());
                    }

                }
            } catch (IOException e) {
                try {
                    mmInStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 接收血糖仪的数据
     *
     * @param msg
     */
    private StringBuilder builder = new StringBuilder();

    private void getDataFromBluetoothDevice(Message msg) {
        List<Integer> mData = (List<Integer>) msg.obj;
        Object data = mSugarConn.getData(mData);
        builder.delete(0, builder.length());
        builder.append(data + "");
        Loger.d("AAAABBBB() " + builder.toString());
        if (builder.toString().equals("-200")) {
            connectStatus = true;
            mTvBloodSugar.setText(builder.toString());
            mBluetoothAdapter.cancelDiscovery();
            return;
        }
        mTvBloodSugar.setText(mValue);

        if (!builder.toString().contains(".")) { // 接收到double 类型数据 表示接收成功
            return;
        }
        mLlMeasureDone.setVisibility(View.VISIBLE);
        mValue = builder.toString();
        measuretime = TimeUtil.getCurrentstamp();
        if (mValue == null) {
            return;
        }
        mTvDate.setText(TimeUtil.formatStamp2Time(measuretime, "yyyy/MM/dd HH:mm " + isAfter));
        DealWithValue2.judgeBSColor(this, mTvState, Integer.valueOf(mValue), false);

        //保存到本地
        SaveSugarDataPreferences.getInstance().writeSugarDataXml(SugarConnActivity.this,
                builder.toString(), null);


    }


    /**
     * 上传血糖 上报血糖
     */
    private void reportBloodSugar(String measuretime, String value) {
        toastUtils.showProgress(getString(R.string.toast_update_ing));//"上传中..."
        HealthController.getInstance().reportBloodSugar(this, "0", isAfter, measuretime,
                value, "", Constant.CURRENTHOSTID,new RequestResultListener() {

                    @Override
                    public void onSuccess(String Json) {
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));

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

    /* 停止客户端连接 */
    private void shutdownClient() {
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
                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    mSocket = null;
                }
            }

            ;
        }.start();
    }

    /**
     * 关闭程序掉用处理部分
     */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        shutdownClient();

    }

}
