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
import com.boer.delos.activity.healthylife.pressure.BloodPressureConnActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.activity.healthylife.tool.SaveSugarDataPreferences;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.SugarResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.widget.CircleProgressBarView;

import org.shake.bluetooth.conn.SugarConn;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/6/21 0021.
 *
 * @Description：血糖仪 界面
 */
public class SugarConn2Activity extends CommonBaseActivity {

    @Bind(R.id.circleProgressBar)
    CircleProgressBarView mCircleProgressBar;
    @Bind(R.id.ctv_before)
    CheckedTextView mCtvBefore;
    @Bind(R.id.ctv_after)
    CheckedTextView mCtvAfter;

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

    private BluetoothSocket socket = null;
    private BluetoothDevice mDevice = null;

    private BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄
    private ReadThread mReadThread = null;
    private ClientThread mClientThread = null;
    public int isScan = 0;
    private List<String> mBluetoothDeviceSet = new ArrayList<String>();
    private SugarConn conn;
    private boolean isDinnerBeforeClick = true;//“餐前”按钮是否点击，如果点击，“餐后”按钮改变颜色和背景
    private String dinner = "餐前";//饭前测试还是饭后测试
    private String isAfter;//接口传值
    private long measuretime = 0;//测量的时间戳
    private Timer mTimerTextView = new Timer();
    private Set<BluetoothDevice> pairedDevices;
    private static final String DEVICE_BRAND = "UeUa-DM"; //默认搜索这个名字的设备
    private static final int ERROR = 96;
    private User mUser;

    private static final int CONN_SUCCESS = 0x00;
    private static final int CONN_FAILURE = 0x01;
    private static final int CONN_DISCONNECT = 0x02;
    private static final int CONN_RECEIVE_DATA = 0x03;
    private static final int CONN_DEVICE = 0x04;

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
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }

        tlTitleLayout.setTitle(getString(R.string.text_blood_sugar_monitor));
    }

    @Override
    protected void initData() {
        conn = new SugarConn();
        initDate();
        initListener();
        buttonSettingShow(100);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ToastHelper.showShortMsg(getString(R.string.toast_bluetooth_nor));
            return;
        }
        connect2BluetoothDevice();
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initListener() {


    }

    /**
     * 时间的初始化
     */
    private void initDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        String date = dateFormat.format(new Date());
        int time = Integer.parseInt(date);
        if (time < 10) {
            isAfter = "0";
        } else if (time >= 10 && time < 16) {
            isAfter = "2";
        } else if (time >= 16 && time < 24) {
            isAfter = "4";
        }
        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 注册查找结束action接收器
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.EXTRA_CONNECTION_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
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
            } else if (time >= 10 && time < 16) {
                isAfter = "2";
            } else if (time >= 16 && time < 24) {
                isAfter = "4";
            }

        } else {
            if (time < 10) {
                isAfter = "1";
            } else if (time >= 10 && time < 16) {
                isAfter = "3";
            } else if (time >= 16 && time < 24) {
                isAfter = "5";
            }
        }
    }

    private void getDevice() {

        pairedDevices = mBluetoothAdapter.getBondedDevices(); //没用到
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 并重新开始
        mBluetoothAdapter.startDiscovery();
    }

    private void connect2BluetoothDevice() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        if (mCircleProgressBar != null) {
            mCircleProgressBar.startAnimation();
            mCircleProgressBar.setHintText(getString(R.string.text_connect_ing));
        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();  //返回已绑定的蓝牙设备集
        for (BluetoothDevice tempDevice : bondedDevices) {
            if (tempDevice.getName().equals(DEVICE_BRAND)) {
                mBluetoothDeviceSet.add(tempDevice.getAddress());
                if (mClientThread == null) {
                    mClientThread = new ClientThread(
                            tempDevice.getAddress());
                    mClientThread.start();
                }
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                return;
            }
        }
        getDevice();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            L.d("SugarConn2Activity mReceiver action " + action);
            if (isScan == 0) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device1 = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (StringUtil.isEmpty(device1.getName())) {
                            L.d("SugarConn2Activity " + device1.getBondState() + "-----------" + device1.getAddress());
                            return;
                        }
                        L.d("SugarConn2Activity() " + device1.getName() + "++++++" + device1.getBondState() + "++++" + device1.getAddress());

                        if (TextUtils.isEmpty(device1.getName()) || !device1.getName().contains(DEVICE_BRAND)){
                            return;
                        }

                        if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                            if (!TextUtils.isEmpty(device1.getName()) && device1.getName().contains(DEVICE_BRAND)) {// 找到设备，准备配对设备
                                mBluetoothAdapter.cancelDiscovery();
                                mDevice = device1;
                                mHandler.sendEmptyMessageDelayed(CONN_DEVICE, 0);
                            }
                        } else if (device1.getBondState() == BluetoothDevice.BOND_NONE){
                            try {
                                if (!TextUtils.isEmpty(device1.getName()) && device1.getName().equals(DEVICE_BRAND)) {// 找到设备，准备配对设备
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

//                        if (socket == null || !socket.isConnected()) {
//                            // 并重新开始
//                            mBluetoothAdapter.startDiscovery();
//                        }
                        break;
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(device==null){
                            return;
                        }
                        if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                            if (!TextUtils.isEmpty(device.getName()) && device.getName().equals(DEVICE_BRAND)) {// 找到设备，准备配对设备
                                if(mBluetoothAdapter!=null&&mBluetoothAdapter.isDiscovering()){
                                    mBluetoothAdapter.cancelDiscovery();
                                }
                                mHandler.sendEmptyMessage(CONN_DEVICE);
                            }
                        }



//                        L.d("SugarConn2Activity onReceive() BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED");
//                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
//                        if (state == BluetoothAdapter.STATE_CONNECTED) {
//                            L.d("SugarConn2Activity onReceive() BluetoothAdapter.STATE_CONNECTED");
//
//                        }
//                        if (state == BluetoothAdapter.STATE_DISCONNECTED) {
//                            L.d("SugarConn2Activity onReceive() BluetoothAdapter.STATE_DISCONNECTED");
//                        }
                        break;

                    case BluetoothAdapter.EXTRA_CONNECTION_STATE:

                        L.d("SugarConn2Activity onReceive() BluetoothAdapter.EXTRA_CONNECTION_STATE");

                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:

                        L.d("SugarConn2Activity onReceive() ACTION_ACL_CONNECTED");
                        if (mCircleProgressBar != null) {
                            mCircleProgressBar.setHintText("连接成功");
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (mCircleProgressBar != null) {
//                            mCircleProgressBar.setHintText("连接断开");
//                            ToastHelper.showShortMsg("连接断开");
                            mCircleProgressBar.stopAnimation();
                        }
                        shutdownClient();
                        break;
                }
            }
        }

    };

    @OnClick({R.id.ctv_before, R.id.ll_eat_before, R.id.ctv_after, R.id.ll_eat_after, R.id.iv_sugar_standard, R.id.btn_measure_again, R.id.btn_save_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ctv_before:
            case R.id.ll_eat_before:
            case R.id.ctv_after:
            case R.id.ll_eat_after:

                mCtvBefore.toggle();
                mCtvAfter.toggle();
                changeTextViews();

                break;
            case R.id.iv_sugar_standard:
                Intent intent = new Intent(getApplication(), BloodSugarMajorActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_measure_again:
                buttonSettingShow(0);
                if (socket == null || !socket.isConnected()) {     //断开连接
                    mTvBloodSugar.setText("");
                    mTvDate.setText("");
                    mTvState.setText("");
                    connect2BluetoothDevice();
                } else {
                    ToastHelper.showShortMsg("正在测量中...");
                }
                break;
            case R.id.btn_save_data:

                reportBloodSugar();

                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_SUCCESS:
                    if (mCircleProgressBar != null)
                        mCircleProgressBar.setHintText(getString(R.string.text_measure_ing));
//                    if (mBluetoothAdapter != null)
//                        mBluetoothAdapter.cancelDiscovery();
                    break;
                case CONN_FAILURE:
                    if (mCircleProgressBar != null){
                        mCircleProgressBar.setHintText(getString(R.string.text_connect_fail));
                        mCircleProgressBar.stopAnimation();
                    }
                    shutdownClient();

//                    socket = null;
//                        connect2BluetoothDevice();

                    break;
                case CONN_DISCONNECT:
//                    if (toastUtils != null) {
//                        toastUtils.showInfoWithStatus(getString(R.string.text_disconnect));
//                    }
                    if (mCircleProgressBar != null)
                        mCircleProgressBar.setHintText(getString(R.string.text_disconnect));
                    break;
                case CONN_RECEIVE_DATA:
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    getDataFromBluetoothDevice(msg);
                    break;
                case CONN_DEVICE:
                    if (socket == null || !socket.isConnected()) {
                        if (mClientThread == null) {
                            mClientThread = new ClientThread(mDevice.getAddress());
//                            mClientThread.start();
                        }
                        if(!mClientThread.isAlive())
                            mClientThread.start();
//                        mBluetoothAdapter.cancelDiscovery();
                    }

                    break;

            }
        }
    };

    private class ClientThread extends Thread {

        public ClientThread(String address) {
            mDevice = mBluetoothAdapter.getRemoteDevice(address);
        }

        public void run() {
            try {
//                if (socket != null && socket.isConnected()) {
//                    return;
//                }
                // 创建一个Socket连接：只需要服务器在注册时的UUID号
                socket = mDevice.createRfcommSocketToServiceRecord(UUID
                        .fromString("00001101-0000-1000-8000-00805F9B34FB"));
                socket.connect();
                if (mReadThread == null) {
                    mReadThread = new ReadThread();
//                    mReadThread.start();
                }
                if(!mReadThread.isAlive())
                    mReadThread.start();

                mHandler.sendEmptyMessage(CONN_SUCCESS);
            } catch (IOException e) {
                Log.e("connect", "", e);
                try {
                    if (socket != null){
                        socket.close();
                        socket=null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                socket = null;
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
                        L.d("SugarConn2Activity StringBuffer " + stringBuffer.toString() + "+++++" + datas.size());
                        Log.d("SugarConn",stringBuffer.toString());
                        Message msg = new Message();
                        msg.what = CONN_RECEIVE_DATA;
                        msg.obj = datas;
                        mHandler.sendMessage(msg);
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

    StringBuilder builder = new StringBuilder();

    /**
     * 接收血糖仪的数据
     *
     * @param msg
     */
    private void getDataFromBluetoothDevice(Message msg) {
        List<Integer> mData = (List<Integer>) msg.obj;
        Object data = conn.getData(mData);
        builder.delete(0, builder.length());
        builder.append(data + "");
        Log.d("SugarConn==",builder.toString());
        L.d("SugarConn2Activity getDataFromBluetoothDevice() " + builder.toString());
        if (builder.toString().equals("-200")) {
            return;
        }
        if (!builder.toString().contains(".")) { // 接收到double 类型数据 表示接收成功
            return;
        }
        shutdownClient();
        mCircleProgressBar.stopAnimation();
        mCircleProgressBar.setHintText(getString(R.string.text_measure_success));

        mTvBloodSugar.setText(builder.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = dateFormat.format(new Date());
        mTvDate.setText(date + " " + (Integer.valueOf(isAfter) % 2 == 0 ? "餐前" : "餐后"));
        String value = builder.toString();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        buttonSettingShow(100);
        DealWithValue2.judgeBSColor(this, mTvState, Float.valueOf(value), true);
        //保存到本地
        SaveSugarDataPreferences.getInstance().writeSugarDataXml(SugarConn2Activity.this,
                builder.toString(), date);
        measuretime = TimeUtil.getCurrentstamp();
    }

    /**
     * 上传血糖 上报血糖
     */
    private void reportBloodSugar() {
        if (mTvBloodSugar.getText().toString().equals("")) {
            BaseApplication.showToast(getString(R.string.text_upload_failure));
            return;
        }
        toastUtils.showProgress(getString(R.string.toast_update_ing));
        HealthController.getInstance().reportBloodSugar(this, mUser.getId().equals(Constant.USERID)
                        ? "0" : mUser.getId(),
                isAfter, measuretime + "",
                mTvBloodSugar.getText().toString(), "", Constant.CURRENTHOSTID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("reportBloodSugar_Json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            toastUtils.showSuccessWithStatus(getString(R.string.save_success));
                            //TODO 修改对应的item的值
                            startActivity(new Intent(SugarConn2Activity.this,BloodSugarActivity.class));
                            finish();
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

    /**
     * 停止客户端连接
     */
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
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            }
        }.start();
    }

    /**
     * @param position -1 隐藏 100 展示 0 :测量 1 :上报
     */
    private void buttonSettingShow(int position) {
        switch (position) {
            case -1:
                mBtnMeasureAgain.setVisibility(View.GONE);
                mBtnSaveData.setVisibility(View.GONE);
                break;
            case 0:
                mBtnMeasureAgain.setVisibility(View.VISIBLE);
                mBtnSaveData.setVisibility(View.VISIBLE);
                break;
            case 1:
                mBtnMeasureAgain.setVisibility(View.GONE);
                mBtnSaveData.setVisibility(View.VISIBLE);
                break;
            case 100:
                mBtnMeasureAgain.setVisibility(View.VISIBLE);
                mBtnSaveData.setVisibility(View.VISIBLE);
                break;
        }
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
