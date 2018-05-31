package com.boer.delos.activity.scene;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.ScanBatch;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.CustomeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.R.id.btn_scan;

/**
 * Created by gaolong on 2017/3/29.
 */
public class AddBatchScanDeviceActivity extends CommonBaseActivity {


    @Bind(R.id.customeView)
    CustomeView customeView;
    @Bind(R.id.lv_scan_devices)
    ListView lvScanDevices;
    @Bind(btn_scan)
    Button btnScan;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.tv4)
    TextView tv4;
    @Bind(R.id.tv5)
    TextView tv5;
    @Bind(R.id.tv6)
    TextView tv6;

    @Bind(R.id.ll_device_tip)
    LinearLayout llDevicetip;
    @Bind(R.id.tv_device_1)
    TextView tvDevice1;
    @Bind(R.id.tv_device_2)
    TextView tvDevice2;
    @Bind(R.id.tv_device_3)
    TextView tvDevice3;

    private boolean scaning;
    private List<Device> mDeviceLists;// 扫描的设备
    private Timer mTimer;
    private static final long TIMER_TIME = 1000 * 3;
    ScaleAnimation animation;
    private int REQ_SCAN_RESULT = 1000;


    @Override
    protected int initLayout() {
        return R.layout.activity_add_batch_device;
    }

    protected void initView() {
        tlTitleLayout.setTitle(R.string.text_add_device_some);
        tlTitleLayout.setRightText(getString(R.string.text_add_device_one));

        btnScan.setTextColor(getResources().getColor(R.color.blue_btn_bg));
        btnScan.setText(getString(R.string.text_start_scan));
        btnScan.setBackground(getResources().getDrawable(R.drawable.btn_full_circle_corner));
    }

    @Override
    protected void initData() {

        mDeviceLists = new ArrayList<>();
        animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(TIMER_TIME);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setFillAfter(true);
    }

    @Override
    protected void initAction() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scaning) {
                    stopScanDevice("scan");
                } else {
                    checkNetGate("scan");
                }
            }
        });
    }

    @Override
    public void rightViewClick() {
        checkNetGate("skip");//跳转页面
    }


    private void checkNetGate(String flag) {


        Log.v("gl", "sameNetGate==" + sameNetGate());


        if (flag.equals("skip")) {
            handler.sendEmptyMessage(2);
        } else {

            if (sameNetGate()) {

                handler.sendEmptyMessage(1);

            } else {

                handler.sendEmptyMessage(0);

            }
        }
    }


    private boolean sameNetGate() {
        if(NetUtil.isNetWorkMobileConnect(this)){
            return false;
        }
        for (GatewayInfo gatewayInfo : Constant.gatewayInfos) {
            if (gatewayInfo.getHostId().equals(Constant.CURRENTHOSTID)) {
                return true;
            }
        }
        return false;
    }


    private void startScanDevice(final String ip) {

        DeviceController.getInstance().startAddBatchScanDevice(this, ip, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                ScanBatch result = GsonUtil.getObject(json, ScanBatch.class);
//                ToastHelper.showShortMsg(result.getResponse().getMsg());
                queryAddBatchDevice(ip);

            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });

    }

    private void stopScanDevice(final String buttonFlag) {

        if (scaning)
            changeScanBtn("end");

        String ip = Constant.LOCAL_CONNECTION_IP;

        DeviceController.getInstance().stopAddBatchScanDevice(this, ip, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {

                if (buttonFlag.equals("scan")) {
                    if(mDeviceLists.size()==0){
                        llDevicetip.setVisibility(View.GONE);
                        ToastHelper.showShortMsg("未扫描到任何设备");
                    }
                    else{
                        Intent intent = new Intent(AddBatchScanDeviceActivity.this, AddBatchScanResultActivity.class);
                        intent.putExtra("list", (Serializable) mDeviceLists);
                        startActivityForResult(intent, REQ_SCAN_RESULT);
                        finish();
                    }
                } else if (buttonFlag.equals("skip")) {
                    startActivity(new Intent(AddBatchScanDeviceActivity.this, AddDeviceListeningActivity.class));
                    finish();
                }

            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                if (buttonFlag.equals("skip")) {
                    Intent intent = new Intent(AddBatchScanDeviceActivity.this, AddDeviceListeningActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void queryAddBatchDevice(String ip) {
        DeviceController.getInstance().queryDeviceBatch(this, ip, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                String response = "";
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    response = jsonObject.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                List<Device> devices = gson.fromJson(response, new TypeToken<List<Device>>() {
                }.getType());


                mDeviceLists.clear();
                mDeviceLists.addAll(devices);
                showScanDevices();

                Loger.v("mDeviceLists==" + mDeviceLists.size());
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void showScanDevices() {

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1);
//        for (Device device : mDeviceLists)
//            adapter.add(device.getName());
//
//        lvScanDevices.setAdapter(adapter);
//
//        adapter.notifyDataSetChanged();


        llDevicetip.setVisibility(View.VISIBLE);
        int count1=0;
        int count2=0;
        int count3=0;
        for (Device device : mDeviceLists){
            if(device.getType().contains("Light")){
                count1++;
            }
            if(device.getType().equals("Socket")){
                count2++;
            }
            if(device.getType().equals("Curtain")){
                count3++;
            }
        }
        tvDevice1.setText("已经扫描到"+count1+"个开关");
        tvDevice2.setText("已经扫描到"+count2+"个插座");
        tvDevice3.setText("已经扫描到"+count3+"个窗帘");

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 0) {

                changeScanBtn("end");

                toastUtils.showErrorWithStatus(getString(R.string.gateway_bind_hint));

            } else if (msg.what == 1) {


                changeScanBtn("start");

                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (scaning) {
                            startScanDevice(Constant.LOCAL_CONNECTION_IP);
                        }

                    }
                }, 0, TIMER_TIME);
            } else if (msg.what == 2) {


                stopScanDevice("skip");

            }

        }

    };

    private void changeScanBtn(String status) {

        if (status.equals("start")) {

//            tv6.startAnimation(animation);
            customeView.startAnimation();
            mTimer = new Timer();

            scaning = true;
            btnScan.setTextColor(getResources().getColor(R.color.gray_text_delete));
            btnScan.setText(getString(R.string.text_end_scan));
            btnScan.setBackground(getResources().getDrawable(R.drawable.btn_red_full_circle_corner));


        } else if (status.equals("end")) {
            customeView.stopAnimation();
//            tv6.clearAnimation();

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            scaning = false;
            btnScan.setTextColor(getResources().getColor(R.color.blue_btn_bg));
            btnScan.setText(getString(R.string.text_start_scan));
            btnScan.setBackground(getResources().getDrawable(R.drawable.btn_full_circle_corner));

        } else if (status.equals("addone")) {
            customeView.stopAnimation();
//            tv6.clearAnimation();

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            scaning = false;
            btnScan.setTextColor(getResources().getColor(R.color.blue_btn_bg));
            btnScan.setText(getString(R.string.text_start_scan));
            btnScan.setBackground(getResources().getDrawable(R.drawable.btn_full_circle_corner));

            stopScanDevice("addone");

        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SCAN_RESULT && resultCode == RESULT_OK) {

            checkNetGate("scan");

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

