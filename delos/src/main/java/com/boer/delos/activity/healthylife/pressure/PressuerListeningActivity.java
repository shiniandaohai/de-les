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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.activity.healthylife.tool.SavePressureDataPreferences;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;

import org.shake.bluetooth.conn.PressuerConn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @description:血压仪 界面
 */
public class PressuerListeningActivity extends CommonBaseActivity {


    @Bind(R.id.id_textViewConnect)
    TextView mIdTextViewConnect;
    @Bind(R.id.id_textViewPressure)
    TextView mIdTextViewPressure;//血压
    @Bind(R.id.id_textViewHeartRate)
    TextView mIdTextViewHeartRate;
    @Bind(R.id.id_textViewSystolicPressure)
    TextView mIdTextViewSystolicPressure;//收缩压
    @Bind(R.id.id_textViewDiastolePressure)
    TextView mIdTextViewDiastolePressure;//舒张压
    @Bind(R.id.id_textViewValue)
    TextView mIdTextViewValue;
    @Bind(R.id.id_buttonSave)
    Button mIdButtonSave;
    @Bind(R.id.id_buttonReset)
    Button mIdButtonReset;
    @Bind(R.id.id_linear1)
    LinearLayout mIdLinear1;
    @Bind(R.id.id_linear2)
    LinearLayout mIdLinear2;
    @Bind(R.id.id_linear3)
    LinearLayout mIdLinear3;
    @Bind(R.id.id_linear4)
    LinearLayout mIdLinear4;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄
    private readThread mreadThread = null;
    private clientThread clientConnectThread = null;
    public int isScan = 0;
    private List<BluetoothDevice> mDevices = new ArrayList<>();
    private PressuerConn conn;
    private long measuretime = 0;//测量的时间戳
    private Timer mTimerTextView = new Timer();

    //接口传值
    private String valueH = "0";
    private String valueL = "0";
    private String bpm = "0";

    private String mNeedDeviceName = "ueua-BP";

    private Handler handlerTextView = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (mIdTextViewConnect == null || mIdTextViewConnect.getText() == null) {
                    return;
                }
                String s = mIdTextViewConnect.getText().toString();
                StringBuilder stringBuilder = new StringBuilder(s);
                stringBuilder.append(".");
                if (s.contains("...")) {
                    mIdTextViewConnect.setText(s.split("\\.\\.\\.")[0]);
                } else {
                    mIdTextViewConnect.setText(stringBuilder);
                }
            }
            if (msg.what == 0) {
                mIdTextViewConnect.setText("测量中");
            }
            if (msg.what == 1) {
                mIdTextViewConnect.setText("正在连接中");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressuer);
        ButterKnife.bind(this);
//        initTopBar("血压仪", null, true, false);
        conn = new PressuerConn();

        getDevice();
        registerListener();
        initListener();
        connDevice();
    }

    @Override
    protected int initLayout() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    private void connDevice() {
        for (BluetoothDevice d : mDevices) {
            clientConnectThread = new clientThread(
                    d.getAddress());
            clientConnectThread.start();
            if (mBtAdapter != null && mBtAdapter.isDiscovering())
                mBtAdapter.cancelDiscovery();
            return;
        }
    }

    private void initListener() {
        //定时器
        mTimerTextView.schedule(new TimerTask() {
            @Override
            public void run() {
                handlerTextView.sendEmptyMessage(0x123);
            }
        }, 0, 500);

        mIdButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportBloodpressure();
            }
        });

        mIdButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mTimerTextView.cancel();
                mIdLinear1.setVisibility(View.INVISIBLE);
                mIdLinear2.setVisibility(View.INVISIBLE);
                mIdLinear3.setVisibility(View.INVISIBLE);
                mIdLinear4.setVisibility(View.INVISIBLE);
                mIdTextViewDiastolePressure.setText("");
                mIdTextViewSystolicPressure.setText("");
                mIdTextViewHeartRate.setText("");
                mIdTextViewPressure.setText("");
                mIdTextViewValue.setText("");
                mIdTextViewConnect.setText("测量中");
//                getDevice();
                mTimerTextView.cancel();
                mTimerTextView = new Timer();
                mTimerTextView.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handlerTextView.sendEmptyMessage(0x123);
                    }
                }, 0, 500);

                sendMessageHandle(conn.start());
            }
        });
    }

    private void registerListener() {
        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // 注册查找结束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


    }

    private void getDevice() {
        try {
            if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
            }
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            if (mBtAdapter.isDiscovering()) {
                mBtAdapter.cancelDiscovery();
            }
            // 并重新开始
            mBtAdapter.startDiscovery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (isScan == 0) {
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device1 = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        System.out.println("血压仪 " + device1.getName() + device1.getBondState());
                        if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                            if (!StringUtil.isEmpty(device1.getName()) && device1.getName().equals(mNeedDeviceName)) {// 找到设备，准备配对设备
//                            mDevices.add(device1.getAddress());
                                clientConnectThread = new clientThread(
                                        device1.getAddress());
                                clientConnectThread.start();
                                mBtAdapter.cancelDiscovery();
                            }
                        }

                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if (mDevices.size() <= 0) {
                            if (mBtAdapter.isDiscovering()) {
                                mBtAdapter.cancelDiscovery();
                            }
                            // 并重新开始
                            if (socket != null && !socket.isConnected()) {
                                // 并重新开始
                                mBtAdapter.startDiscovery();
                            }
                        }
                        break;
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                        Loger.d("BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED");
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        Loger.d("BluetoothAdapter.ACTION_STATE_CHANGED");
                        break;
                }


            }
        }

    };

    private class clientThread extends Thread {

        public clientThread(String address) {
            device = mBtAdapter.getRemoteDevice(address);
        }

        public void run() {
            try {
                // 创建一个Socket连接：只需要服务器在注册时的UUID号
                if (socket != null) {
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(UUID
                        .fromString("00001101-0000-1000-8000-00805F9B34FB"));
                socket.connect();
                Message msg = new Message();
                msg.what = 0;
                handlerTextView.sendMessage(msg);
                // 启动接受数据
                if (conn == null) conn = new PressuerConn();
                sendMessageHandle(conn.start());
                mreadThread = new readThread();
                mreadThread.start();
            } catch (Exception e) {
                socket = null;
                Log.e("connect", "", e);
                Message msg = new Message();
                msg.what = 1;
                handlerTextView.sendMessage(msg);

                // LinkDetectedHandler.sendMessage(msg);
            }
        }
    }

    private class readThread extends Thread {
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
                        msg.obj = datas;
                        handler.sendMessage(msg);
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

    StringBuilder builder = new StringBuilder();
    // 消息处理队列
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Integer> mData = (List<Integer>) msg.obj;

            Object data = conn.getData(mData);
            builder.append(data + "\n");
            if (!StringUtil.isEmpty(pressuerFail(builder.toString()))) {
                toastUtils.showErrorWithStatus(pressuerFail(builder.toString()));
                return;
            }
            Loger.d("血压仪 " + builder.toString());
            try {
                //返回三个数据 收缩压 舒张压 心率
                if (data.toString().contains(" ")) {
                    String[] dataResult = data.toString().split(" ");
                    mIdLinear1.setVisibility(View.VISIBLE);
                    mIdLinear2.setVisibility(View.VISIBLE);
                    mIdLinear3.setVisibility(View.VISIBLE);
                    mIdLinear4.setVisibility(View.VISIBLE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String date = dateFormat.format(new Date());
                    mIdTextViewConnect.setText(date);
                    mTimerTextView.cancel();
                    judgeStateLabelWithHighValueAndLowValue(Integer.parseInt(dataResult[0]), Integer.parseInt(dataResult[1]));
                    judgeHeartStateLabelAndTraingleArrow(Integer.parseInt(dataResult[2]));
                    mIdTextViewValue.setText(dataResult[2]);
                    mIdTextViewDiastolePressure.setText("舒张压:" + dataResult[1]);
                    mIdTextViewSystolicPressure.setText("收缩压:" + dataResult[0]);
                    measuretime = TimeUtil.getCurrentstamp();

                    valueH = dataResult[0];
                    valueL = dataResult[1];
                    bpm = dataResult[2];
                    SavePressureDataPreferences.getInstance().writePressureDataXml(PressuerListeningActivity.this, measuretime + "", dataResult[0], dataResult[1], dataResult[2], date);
                } else {
                    if (builder.toString().contains("-200")) {
                        mIdTextViewValue.setText("0");
                    } else
                        mIdTextViewValue.setText(data + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 发送测量命令
     *
     * @param mOrder
     */
    private void sendMessageHandle(byte[] mOrder) {
        if (socket == null) {
            Toast.makeText(PressuerListeningActivity.this, "没有连接", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(mOrder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            socket = null;
//            handler.sendEmptyMessage();
            getDevice();
            e.printStackTrace();
        }
    }


    //根据高压和低压来设置控件颜色和控件值
    private void judgeStateLabelWithHighValueAndLowValue(int highValue, int lowValue) {
        if (highValue == 0 && lowValue == 0) {
            return;
        }
        int i;
        if (highValue > 140 && lowValue < 90) {
            i = 0;
        } else if (highValue > 180 || lowValue > 110) {
            i = 1;
        } else if ((highValue >= 160 && highValue < 180) || (lowValue >= 100 && lowValue < 110)) {
            i = 2;
        } else if ((highValue >= 140 && highValue < 160) || (lowValue >= 90 && lowValue < 100)) {
            i = 3;
        } else if ((highValue >= 120 && highValue < 140) || (lowValue >= 80 && lowValue < 90)) {
            i = 4;
        } else if ((highValue >= 90 && highValue < 120) || (lowValue >= 60 && lowValue < 80)) {
            i = 5;
        } else {
            i = 6;
        }
        try {
            DealWithValues dealWithValues = DealWithValues.getInstance();
            ArrayList<Map> arrayList = dealWithValues.dealWithPressure();
            int textColor = Integer.parseInt(arrayList.get(i).get("color").toString());

            mIdTextViewPressure.setTextColor(textColor);
            mIdTextViewPressure.setText(arrayList.get(i).get("title").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据心率值设置控件颜色和控件值
    private void judgeHeartStateLabelAndTraingleArrow(int heartRateNum) {
        if (heartRateNum == 0) {
            return;
        }
        int i = DealWithValues.judgeHeartRateState(heartRateNum);

        DealWithValues dealWithValues = DealWithValues.getInstance();
        ArrayList<Map> arrayList = dealWithValues.dealWithHeartRate();
        int textColor = Integer.parseInt(arrayList.get(i).get("color").toString());

        mIdTextViewHeartRate.setTextColor(textColor);
        mIdTextViewHeartRate.setText(arrayList.get(i).get("title").toString());
    }

    //上传血压值
    private void reportBloodpressure() {

        toastUtils.showProgress("上传中...");
        HealthController.getInstance().reportBloodpressure(this, "0", measuretime + "", valueH,
                valueL, bpm, "", Constant.CURRENTHOSTID,new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("reportBloodpressure_Json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.dismiss();
                    toastUtils.showSuccessWithStatus("上传成功");
                    //TODO 修改对应的item的值

                } else {
                    String msg = JsonUtil.parseString(Json, "msg");
                    toastUtils.showErrorWithStatus(msg);
                }
            }

            @Override
            public void onFailed(String Json) {
                toastUtils.showErrorWithStatus("上传失败");
            }
        });
    }

    // 关闭程序掉用处理部分
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        shutdownClient();
        Loger.d("血压 " + "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }

    /* 停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            public void run() {
                if (clientConnectThread != null) {
                    clientConnectThread.interrupt();
                    clientConnectThread = null;
                }
                if (mreadThread != null) {
                    mreadThread.interrupt();
                    mreadThread = null;
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

            ;
        }.start();
    }


    private String pressuerFail(String result) {
        String resultString = "";
        int ret = Integer.valueOf(builder.toString());
        switch (ret) {
            case 0x0E:
//                resultString = "ERPROM异常";
            case 0x02:
//                resultString = "杂讯干扰";
            case 0x03:
//                resultString = "充气时间过长";
            case 0x0C:
//                resultString = "校正异常";
            case 0x04:
                resultString = "测的结果异常";
                break;
            case 0x01:
                resultString = "人体心跳信号太小或压力";
                break;
            case 0x0B:
                resultString = "电源低电压";
                break;
            default:
                resultString = "测的结果异常";
                break;
        }
        return resultString;

    }
}
