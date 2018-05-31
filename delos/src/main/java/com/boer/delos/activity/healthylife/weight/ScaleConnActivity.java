package com.boer.delos.activity.healthylife.weight;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.SaveScaleDataPreferences;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.WeightBean;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.CircleProgressBarView;
import com.google.gson.Gson;

import org.shake.bluetooth.conn.ScaleConn;
import org.shake.bluetooth.tools.AbDataAnalysis;
import org.shake.bluetooth.tools.HealthGattAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/6/21 0021.
 *
 * @Descrip 体脂称 体重 界面
 */
public class ScaleConnActivity extends CommonBaseActivity {

    @Bind(R.id.circleProgressBar)
    CircleProgressBarView mCircleProgressBar;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.iv_major_info)
    ImageView mIvMajorInfo;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.tv_weight_data)
    TextView mTvWeightKg;
    @Bind(R.id.tv_fat_rate_data)
    TextView mTvFatRateData;
    @Bind(R.id.tv_muscle)
    TextView mTvMuscle;
    @Bind(R.id.tv_water)
    TextView mTvWater;
    @Bind(R.id.tv_bone)
    TextView mTvBone;
    @Bind(R.id.tv_bmr)
    TextView mTvBmr;
    @Bind(R.id.tv_bmi)
    TextView mTvBmi;

    @Bind(R.id.btn_measure_again)
    Button mBtnMeasureAgain;
    @Bind(R.id.btn_save_data)
    Button mBtnSaveData;

    @Bind(R.id.tv_weight_info)
    TextView tv_weight_info;

    private ScaleConn conn;
    private BluetoothAdapter mBluetoothAdapter;
    private long measuretime = 0;//测量的时间戳
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 98;

    private String tempReslt; //返回的结果的过程数据
    private String DEVICE_BRAND = "BT-RS100"; //默认选择这个设备名字的设备
    private boolean IS_RECEIVE_DATA = true; //广播的数据是否接收，默认接收

    private String[] mMeasureResult;
    private boolean isDeviceConn = false;

    @Override
    protected int initLayout() {
        return R.layout.activity_weight_connect;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.health_weight));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void initData() {
//        //原有sdk中应该有个错误，这两个值不一样，得不到结果，修改成一样的就可以得到结果
//        HealthGattAttributes.SCALE_BLE_TX = HealthGattAttributes.SCALE_BLE_RX;
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        Intent gattServiceIntent = new Intent(this, ScaleConn.class);
        bindService(gattServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
//        checkBluetoothPermission();
        scanLeDevice(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        buttonSettingShow(100);
    }

    @Override
    protected void initAction() {

    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("ScaleConn","onReceive==="+action);
            if (ScaleConn.ACTION_GATT_CONNECTED.equals(action)) {
                scanLeDevice(false);
                startTest();
                isDeviceConn=true;
                mCircleProgressBar.setHintText("设备连接成功");
            } else if (ScaleConn.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                if (mCircleProgressBar != null){
                    mCircleProgressBar.setHintText(getString(R.string.text_disconnect));
                }
                isDeviceConn = false;
                buttonSettingShow(0);
                scanLeDevice(true);
            } else if (ScaleConn.ACTION_DATA_AVAILABLE
                    .equals(action)) {
                try {
                    //根据返回值，来判断是6个数据还是一个数据
                    //当 返回只有一项数值时，返回为：测量过程中显示的体重；
                    // 当返回有6项数据是，返回的是最终的测量结果，数据格式为： 体重 脂肪比例 BMI 肌肉率 水分 骨质
                    Log.d("ScaleConn==","IS_RECEIVE_DATA:"+IS_RECEIVE_DATA);
                    String data="-1";
                    if(intent.getSerializableExtra(ScaleConn.EXTRA_DATA)==null){
                        data="null";
                    }
                    else{
                        data = AbDataAnalysis.getScale((byte[])intent.getSerializableExtra(ScaleConn.EXTRA_DATA));
                    }
                    Log.d("ScaleConn==","data"+data);
                    if (!IS_RECEIVE_DATA) return;
//                    mIdTextViewConnect.setText(readString(R.string.scale_conn_activity_measureing));
                    if (intent.getSerializableExtra(ScaleConn.EXTRA_DATA) == null) { //确定后会发出最后数据后，然后发出一个 null
                        sendUserData();
                    }
                    if (intent.getSerializableExtra(ScaleConn.EXTRA_DATA) != null) {
//                        String result = intent.getSerializableExtra(ScaleConn.EXTRA_DATA).toString();
//                        final String result = (String) intent.getSerializableExtra(ScaleConn.EXTRA_DATA);
                        final String result = AbDataAnalysis.getScale((byte[]) intent.getSerializableExtra(ScaleConn.EXTRA_DATA));
                        Log.d("ScaleConnAct","data"+result);
                        if (result.contains(" ")) {//6个数据
                            buttonSettingShow(100);
                            if (mCircleProgressBar != null) {
                                mCircleProgressBar.setHintText(getString(R.string.text_measure_success));
                                mCircleProgressBar.stopAnimation();
                            }
                            mReceiveDataHandler.removeMessages(0);
                            IS_RECEIVE_DATA = false;
                            mMeasureResult = result.split(" ");

                            final String[] ss = mMeasureResult;
                            if (ss[0].equals(0.0 + "")) {
                                mTvWeightKg.setText(" " + (tempReslt==null?"0":tempReslt) + " kg");
                            } else {
                                mTvWeightKg.setText(" " + (ss[0]==null?"0":ss[0]) + " kg");
                            }
                            mTvFatRateData.setText(" " + ss[1] + " %");
                            mTvBmi.setText(ss[2] + "kg/m³");

                            mTvMuscle.setText(ss[3] + "%");
                            mTvWater.setText(ss[4] + "%");
                            mTvBone.setText(ss[5] + "%");
                            mTvBmr.setText(ss[6] + "Kcal");

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String date = dateFormat.format(new Date());
                            mTvDate.setText(date);
                            measuretime = TimeUtil.getCurrentstamp();

                            //保存在本地，如果有网络，则上传
                            SaveScaleDataPreferences.getInstance().writeScaleDataXml(ScaleConnActivity.this, ss[0], ss[2], ss[1], ss[3]
                                    , ss[4], ss[5], ss[6], date);
                            // text.setText("体重:" + ss[0] + " 脂肪比例:" + ss[1] + " BMI:" + ss[2] + " 肌肉率:" + ss[3] + " 水分:" + ss[4] + " 骨质:" + ss[5]);

                        } else {
                            if (result == null) return;
                            mTvWeightKg.setText(" " + (tempReslt==null?"":tempReslt) + " kg");
                            tempReslt = result;
                            if(!result.equals("0.0")){
                                mCircleProgressBar.setHintText("测量中...");
                                mReceiveDataHandler.removeMessages(0);
                                mReceiveDataHandler.sendEmptyMessageDelayed(0,2000);
                            }
                            else{
                                mCircleProgressBar.setHintText("请测量");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            conn = null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            // TODO Auto-generated method stub
            conn = ((ScaleConn.LocalBinder) service).getService();
            System.out.println("0000000");
            if (!conn.initialize()) {
                Log.e("test", "蓝牙无法初始化");
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
            if(device==null){
                Log.d("ScaleConn","onLeScan===device=null");
            }
            else{
                if(device.getName()!=null){
                    Log.d("ScaleConn","onLeScan===device.getName="+device.getName());
                }
                else{
                    Log.d("ScaleConn","onLeScan===device.getName=null");
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 查询设备
                    if (device == null || device.getName() == null) {
                        return;
                    }
                    if (device.getName().equals(DEVICE_BRAND)) {
                        Log.d("ScaleConn","onLeScan===device.getName="+device.getName()+"=====DEVICE_BRAND="+DEVICE_BRAND);
                        conn.connect(device.getAddress());
                        if(mCircleProgressBar!=null){
                            mCircleProgressBar.setHintText(getString(R.string.text_connect_ing));
                        }
                    }
                }
            });

        }
    };

    @SuppressLint("NewApi")
    protected void scanLeDevice(boolean isScan) {
        try {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
//            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
//            startActivity(intent);
//        }
        if (!isScan) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } else {
            UUID[] serviceUuids = new UUID[]{ScaleConn.UUID_HEART_RATE_MEASUREMENT,
                    UUID.fromString(HealthGattAttributes.SCALE_BLE_SERVICE)};
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            if(mCircleProgressBar!=null){
                mCircleProgressBar.startAnimation();
                mCircleProgressBar.setHintText("搜索设备中...");
            }
        }
    }

    @OnClick({R.id.iv_major_info, R.id.btn_measure_again, R.id.btn_save_data, R.id.tv_weight_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_weight_info:
            case R.id.iv_major_info:
                if (mMeasureResult==null||(mMeasureResult[3].equals("")||mMeasureResult[3].equals("0.0"))||
                        (mMeasureResult[4].equals("")||mMeasureResult[4].equals("0.0"))) {
                    startActivity(new Intent(this, WeightMajorInfoActivity.class));
                    return;
                }
                if (mMeasureResult.length < 7) {
                    startActivity(new Intent(this, WeightMajorInfoActivity.class));
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("muscle", mMeasureResult[3]);
                params.put("water", mMeasureResult[4]);
                params.put("bone", mMeasureResult[5]);
                params.put("Kcal", mMeasureResult[6]);
                params.put("BMI", mMeasureResult[2]);
                String json = new Gson().toJson(params);
                WeightBean weightBean=new WeightBean();
                weightBean.setWeight(Float.valueOf(mMeasureResult[0]));
                weightBean.setFatrate(Float.valueOf(mMeasureResult[1]));
                weightBean.setDetail(json);
                startActivity(new Intent(this, WeightMajorInfoActivity.class).putExtra("weight",weightBean));
                break;
            case R.id.btn_measure_again:
                IS_RECEIVE_DATA = true;
                if (isDeviceConn) {
                    tempReslt="0";
                    mTvWeightKg.setText(" " + 0 + " kg");
                    mTvFatRateData.setText(" " + 0 + " %");
                    mTvBmi.setText("" + 0 + "kg/m³");
                    mTvMuscle.setText(0 + "%");
                    mTvWater.setText(0 + "%");
                    mTvBone.setText(0 + "%");
                    mTvBmr.setText(0 + "Kcal");
                    mMeasureResult=null;
                    if(mCircleProgressBar!=null){
                        mCircleProgressBar.startAnimation();
                        mCircleProgressBar.setHintText("重新测量");
                    }
                    buttonSettingShow(-1);
                    startTest();
                } else {
//                    ToastHelper.showShortMsg("设备尚未连接");
//                    scanLeDevice(true);
                }
                break;
            case R.id.btn_save_data:
                reportBodyweight();
                break;

        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScaleConn.ACTION_GATT_CONNECTED);
        intentFilter
                .addAction(ScaleConn.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(ScaleConn.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ScaleConn.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);


        return intentFilter;
    }

    //计算年龄
    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                } else {
                    //do nothing
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        } else {
            //monthNow<monthBirth
            //donothing
        }

        return age;
    }

    //上传体重值
    private void reportBodyweight() {
        if (mMeasureResult==null||(mMeasureResult[3].equals("")||mMeasureResult[3].equals("0.0"))||
                (mMeasureResult[4].equals("")||mMeasureResult[4].equals("0.0"))) {
            BaseApplication.showToast(getString(R.string.text_invalid));
            return;
        }
        if (mMeasureResult.length < 7) {
            ToastHelper.showShortMsg(getString(R.string.text_invalid));
            return;
        }
        // text.setText("体重:" + ss[0] + " 脂肪比例:" + ss[1] + " BMI:" + ss[2] +
        // " 肌肉率:" + ss[3] + " 水分:" + ss[4] + " 骨质:" + ss[5]);


        Map<String, String> params = new HashMap<>();
        params.put("muscle", mMeasureResult[3]);
        params.put("water", mMeasureResult[4]);
        params.put("bone", mMeasureResult[5]);
        params.put("Kcal", mMeasureResult[6]);
        params.put("BMI", mMeasureResult[2]);
        String json = new Gson().toJson(params);
        toastUtils.showProgress(getString(R.string.toast_update_ing));
        HealthController.getInstance().reportBodyweight(this, "0", measuretime + "",
                mMeasureResult[0], mMeasureResult[1],
                json, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("reportBloodSugar_Json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_upload_success));
                            //TODO 修改对应的item的值
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    finish();
                                }
                            }, 1000 * 1);
                        } else {
                            String msg = JsonUtil.parseString(Json, "msg");
                            toastUtils.showErrorWithStatus(msg);
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null)
                            toastUtils.showErrorWithStatus(getString(R.string.text_upload_failure));
                    }
                });
    }

    private void sendUserData() {
        // TODO Auto-generated method stub
        int sex = 0;//性别
        int age = 0;//年纪
        if (Constant.LOGIN_USER == null) {
            toastUtils.showInfoWithStatus("请重新登录");
            conn.getData(0, 0, 0); //极端情况
            return;
        }
        if (StringUtil.isEmpty(Constant.LOGIN_USER.getBirthday())) {
            ToastHelper.showShortMsg(getString(R.string.scale_conn_activity_toast_show_advice));
        }
        if (StringUtil.isEmpty(Constant.LOGIN_USER.getSex()) || Constant.LOGIN_USER.getSex().equals("")) {
            sex = 0;//默认值
        } else {
            try {
                sex = Integer.parseInt(Constant.LOGIN_USER.getSex());
            } catch (NumberFormatException e) {
                // sex  性别：0:男，1:女
                switch (Constant.LOGIN_USER.getSex()) {
                    case "女":
                        sex = 1;
                        break;
                    case "男":
                        sex = 0;
                        break;
                    default:
                        sex = 0;
                        break;

                }
            }
        }
        if (StringUtil.isEmpty(Constant.LOGIN_USER.getBirthday()) || Constant.LOGIN_USER.getBirthday().equals("")) {
            age = 0;//默认值
        } else {
            if (Constant.LOGIN_USER.getBirthday().contains("年")) {
                DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
                try {
                    age = getAge(df.parse(Constant.LOGIN_USER.getBirthday()));
                } catch (Exception e) {
                    e.printStackTrace();
                    age = 0;
                }
            }

        }
        float hight = Constant.LOGIN_USER.getHeight();
        if (age == 0) {
            age = 20;
        }
        if (hight == 0) {
            hight = 175;
        }
        L.e("体脂称:" + sex + "," + age + "," + Constant.LOGIN_USER.getHeight());
        conn.getData(sex, age, (int) hight);
    }

    /**
     * 校验蓝牙权限
     */

    protected void checkBluetoothPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            //校验是否已具有模糊定位权限

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},

                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {

                //具有权限

                scanLeDevice(true);
            }

        } else {
            //系统不高于6.0直接执行
            //具有权限

            scanLeDevice(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    protected void doNext(int requestCode, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //同意权限

                scanLeDevice(true);
            } else {

                // 权限拒绝
                // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
//                denyPermission();
                Loger.d("体重 获取蓝牙权限 ");
//                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
//                startActivity(intent);
//                finish();
            }

        }
    }

    /**
     * @param position -1 隐藏 100 展示 0 测量 1 上报
     */
    private void buttonSettingShow(int position) {
        switch (position) {
            case -1:
                mBtnMeasureAgain.setEnabled(false);
                mBtnSaveData.setEnabled(false);
                break;
            case 0:
                mBtnMeasureAgain.setEnabled(true);
                mBtnSaveData.setEnabled(true);
                break;
            case 100:
                mBtnMeasureAgain.setEnabled(true);
                mBtnSaveData.setEnabled(true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        scanLeDevice(false);
        if (mCircleProgressBar != null) {
            mCircleProgressBar.stopAnimation();
        }
        conn.disconnect();
        mReceiveDataHandler.removeMessages(0);
    }


    private void startTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendUserData();
            }
        }).start();
    }

    private Handler mReceiveDataHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mCircleProgressBar.setHintText("请测量");
        }
    };
}
