package com.boer.delos.activity.healthylife.urine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.SaveUrineDataPreferences;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.google.gson.Gson;

import org.shake.bluetooth.conn.ScaleConn;
import org.shake.bluetooth.conn.UrineConn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description： 尿检  采集数据 界面
 */
public class UrineListeningActivity extends BaseUrineActivity {
    private String mDeviceBrand = "EMP-Ui"; // 设备名

    private UrineConn conn;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 10000;
    private boolean CONN = false;
    private Handler mHandler = new Handler();

    // private TextView text;
    private TranslateAnimation translateAnimation;
    private ImageView imageView;
    private RelativeLayout mLinearLayout;
    private Button mButton;
    private long measuretime = 0;//测量的时间戳

    private static String sPreUrineTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urine);

        initTopBar("采集数据", null, true, false);
        initView();
        initListener();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        boolean hasePermission = queryPermissionTask(new String[]{Manifest.permission.BLUETOOTH}, "20000");
        if (hasePermission) {
            scanLeDevice(true);
        }
        Intent gattServiceIntent = new Intent(this, UrineConn.class);
        bindService(gattServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
//        checkBluetoothPermission();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.id_imageScanning);
        mLinearLayout = (RelativeLayout) findViewById(R.id.id_linearScanning);
        mButton = (Button) findViewById(R.id.id_buttonReTest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanAnimation();

    }

    private void scanAnimation() {
        WindowManager wm = (WindowManager) UrineListeningActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        translateAnimation = new TranslateAnimation(0.0f, width, 0.0f, 0.0f);
        translateAnimation.setDuration(1200);
        translateAnimation.setRepeatCount(-1);
        //匀速
        LinearInterpolator lin = new LinearInterpolator();
        translateAnimation.setInterpolator(lin);

        imageView.startAnimation(translateAnimation);

    }

    private void initListener() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateAnimation.reset();
                imageView.setVisibility(View.VISIBLE);
                imageView.startAnimation(translateAnimation);
                mLinearLayout.setVisibility(View.GONE);
                if (CONN && aHandler != null) {
                    aHandler.sendEmptyMessage(0);
                } else {
                    scanLeDevice(true);
                }
            }
        });
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            switch (action) {
                case UrineConn.ACTION_GATT_SERVICES_DISCOVERED:

                    Loger.d("AAAABA ACTION_GATT_SERVICES_DISCOVERED " + CONN + "-----" + ScaleConn.isConn);
                    scanLeDevice(false);
                    break;

                case UrineConn.ACTION_GATT_CONNECTED:

                    CONN = true;
                    aHandler.sendEmptyMessage(0);

                    Loger.d("AAAABA ACTION_GATT_CONNECTED " + CONN + "-----" + ScaleConn.isConn);
                    break;

                case UrineConn.ACTION_GATT_DISCONNECTED:

                    CONN = false;
                    scanLeDevice(true);
                    Loger.d("AAAABA ACTION_GATT_DISCONNECTED " + CONN + "-----" + ScaleConn.isConn);
                    break;
                case UrineConn.ACTION_DATA_AVAILABLE:
                    Loger.d("AAAABA ACTION_DATA_AVAILABLE " + CONN + "-----" + ScaleConn.isConn);
                    scanLeDevice(false);
                    measuretime = TimeUtil.getCurrentstamp();
                    // 设备发送过来的数据
                    System.out.println(intent.getSerializableExtra(UrineConn.EXTRA_DATA).toString());
                    if (intent.getSerializableExtra(UrineConn.EXTRA_DATA) == null) {
                        return;
                    }
                    //解析 ->保存本地 ->上报数据
                    reportUrine(intent.getSerializableExtra(UrineConn.EXTRA_DATA).toString());
//                    String[] strings = intent.getSerializableExtra(UrineConn.EXTRA_DATA).toString().replace("  ", " ").replace(" ", ",").split(",");
                    break;

            }

        }

    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            conn = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            // TODO Auto-generated method stub
            conn = ((UrineConn.LocalBinder) service).getService();
            System.out.println("0000000");
            if (!conn.initialize()) {
                Log.e("test", "蓝牙无法初始化");
                CONN = false;
                return;
            }

        }
    };
    @SuppressLint("NewApi")
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // 查询设备
                    if (device == null || device.getName() == null) {
                        return;
                    }
                    Log.e("getName", device.getName() + "-----");
                    if (device.getName().equals(mDeviceBrand)) {
                        if (conn != null) {
                            System.out.println("-------1111");
                            conn.connect(device.getAddress());
                            Loger.d("AAAABA CONN " + CONN);
//                            if (CONN)
//                            scanLeDevice(false);
                        } else {
                            // ToastUtils.showToast(UrineListeningActivity.this,"系统蓝牙出现异常，请重新进入:UrineListeningActivity",Toast.LENGTH_SHORT);

                            //  UrineListeningActivity.this.finish();
                            return;
                        }
                    }

                }
            });

        }
    };

    protected void scanLeDevice(boolean isScan) {
        if (!isScan) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } else {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (CONN == false) {
                        translateAnimation.cancel();
                        imageView.clearAnimation();
                        imageView.setVisibility(View.GONE);
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private Handler aHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            new Times(2000, 2000).start();
        }
    };


    private class Times extends CountDownTimer {

        public Times(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            conn.getData();
        }

        @Override
        public void onTick(long arg0) {
            // TODO Auto-generated method stub

        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UrineConn.ACTION_GATT_CONNECTED);
        intentFilter
                .addAction(UrineConn.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(UrineConn.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UrineConn.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        scanLeDevice(false);
    }

    StringBuilder mStringBuilder = new StringBuilder();

    //上传尿检值
    private void reportUrine(final String result) {
        mStringBuilder.setLength(0);

        Map<String, String> resultMap = new HashMap<String, String>();
        String LEU = "urineLeu";
        String NIT = "urineNit";
        String UBG = "urineUbg";
        String PRO = "urinePro";
        String PH = "urinePh";
        String BLD = "urineBld";
        String SG = "urineSg";
        String KET = "urineKet";
        String BIL = "urineBil";
        String GLU = "urineGlu";
        String VC = "urineVC";
        //根据文档，将尿检仪获得的数据放到对应的map中，格式年 月 日  LEU BLD PH PRO UBG NIT VC GLU BIL KET SG
        try { //12 4 9 9:10  1 3 0 0 0 1 3 0 0 2 6
            String[] strings = result.replace("  ", " ").replace(" ", ",").split(",");
            String lastTime = mStringBuilder.append(strings[0]).append("/")
                    .append(strings[1]).append("/")
                    .append(strings[2]).append(" ")
                    .append(strings[3]).toString();
            /**
             * 根据时间判断是否是最新数据
             */
            if (!StringUtil.isEmpty(sPreUrineTime) && lastTime.equals(sPreUrineTime)) {

                translateAnimation.cancel();
                imageView.clearAnimation();
                imageView.setVisibility(View.GONE);

                mLinearLayout.setVisibility(View.VISIBLE);

                return;
            }
            sPreUrineTime = lastTime;
            resultMap.put(LEU, strings[4]);
            resultMap.put(BLD, strings[5]);
            resultMap.put(PH, strings[6]);
            resultMap.put(PRO, strings[7]);
            resultMap.put(UBG, strings[8]);
            resultMap.put(NIT, strings[9]);
            resultMap.put(VC, strings[10]);
            resultMap.put(GLU, strings[11]);
            resultMap.put(BIL, strings[12]);
            resultMap.put(KET, strings[13]);
            resultMap.put(SG, strings[14]);
            calculateData(resultMap);
            resultMap.put("urineTureTime", measuretime + "");

            //保存到本地
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = dateFormat.format(new Date());
            SaveUrineDataPreferences.getInstance().writeUrineDataXml(UrineListeningActivity.this, strings[4], strings[5], strings[6], strings[7], strings[8], strings[9], strings[10], strings[11], strings[12], strings[13], strings[14]
                    , urineNumber + "", date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = new Gson().toJson(resultMap);
        //上报数据
        reportData(json, result);
    }

    private void reportData(String json, final String result) {

        toastUtils.showProgress("上传中...");
        HealthController.getInstance().reportUrine(this, "0", measuretime + "", json,
                Constant.CURRENTHOSTID,new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("reportBloodSugar_Json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.dismiss();
                    BaseApplication.showToast("上传成功");
                    //TODO 修改对应的item的值
                    Intent serverIntent = new Intent(
                            UrineListeningActivity.this,
                            UrineResultListeningActivity.class);
                    serverIntent.putExtra("result", result);
                    startActivity(serverIntent);
                    finish();
                } else {
                    String msg = JsonUtil.parseString(Json, "msg");
                    BaseApplication.showToast(msg);
                    Intent serverIntent = new Intent(
                            UrineListeningActivity.this,
                            UrineResultListeningActivity.class);
                    serverIntent.putExtra("result", result);
                    startActivity(serverIntent);
                    finish();
                }
            }

            @Override
            public void onFailed(String Json) {
                BaseApplication.showToast("上传失败");
                Intent serverIntent = new Intent(
                        UrineListeningActivity.this,
                        UrineResultListeningActivity.class);
                serverIntent.putExtra("result", result);
                startActivity(serverIntent);
                finish();
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        super.onPermissionsDenied(requestCode, perms);
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        scanLeDevice(true);
    }
}
