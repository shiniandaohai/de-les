package com.boer.delos.activity.scene.devicecontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.music.MusicWise485Activity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/8/30.
 */

public class LaserEggActivity extends CommonBaseActivity {
    private DeviceStatus mDeviceStatus;
    private Device mDevice;
    @Bind(R.id.tv_aqi)
    TextView tvAqi;
    @Bind(R.id.tv_aqi_des)
    TextView tvAqiDes;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.v_temp)
    View vTemp;
    @Bind(R.id.tv_hum)
    TextView tvHum;
    @Bind(R.id.v_hum)
    View vHum;
    @Bind(R.id.tv_pm_two_five)
    TextView tvPm25;
    @Bind(R.id.v_pm_two_five)
    View vPm25;
    @Bind(R.id.tv_pm_one_zero)
    TextView tvPm10;
    @Bind(R.id.v_pm_one_zero)
    View vPm10;
    @Bind(R.id.tv_tvoc)
    TextView tvTvoc;


    private MyHandler myHandler;
    private Device mQueryDevice;
    private List<Device> devices;
    private String favoriteTag;
    @Override
    protected int initLayout() {
        return R.layout.activity_laser_egg;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.device_laserEggFilter));
        tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDeviceStatus=deviceRelate.getDeviceStatus();
        mDevice = deviceRelate.getDeviceProp();
        devices = new ArrayList<>();
        favoriteTag = mDevice.getFavorite();
        if (!TextUtils.isEmpty(favoriteTag))
            tlTitleLayout.setLinearRightImage(!favoriteTag.equals("1") ?
                    R.mipmap.nav_collect_nor : R.mipmap.nav_red_collect);

        myHandler = new MyHandler();
        mQueryDevice = new Device();
        mQueryDevice.setAddr(mDevice.getAddr());
        mQueryDevice.setType(mDevice.getType());
        updateUI();
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        if (favoriteTag.equals("1")) {
            favoriteTag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else if (favoriteTag.equals("0")) {
            favoriteTag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if (mDevice != null) {
            devices.clear();
            mDevice.setFavorite(favoriteTag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    private void updateUI(){
        tvAqi.setText(mDeviceStatus.getValue().getAqiPm25()+"");
        String[] aqiDeses = getResources().getStringArray(R.array.aqi_des);
        String[] aqiColors = getResources().getStringArray(R.array.aqi_color);
        int levelAqi=judgeAQILevel(mDeviceStatus.getValue().getAqiPm25());
        tvAqiDes.setText(aqiDeses[levelAqi]);
        GradientDrawable myGrad = (GradientDrawable)tvAqiDes.getBackground();
        myGrad.setColor(Color.parseColor(aqiColors[levelAqi]));

        int levelPm25=judgeAQILevel(mDeviceStatus.getValue().getPm25());
        tvPm25.setText(mDeviceStatus.getValue().getPm25()+"");
        vPm25.setBackgroundColor(Color.parseColor(aqiColors[levelPm25]));

        int levelPm10=judgeAQILevel(mDeviceStatus.getValue().getPm10());
        tvPm10.setText(mDeviceStatus.getValue().getPm10()+"");
        vPm10.setBackgroundColor(Color.parseColor(aqiColors[levelPm10]));


        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        tvTemp.setText(decimalFormat.format(Double.valueOf(mDeviceStatus.getValue().getTemp())));
        tvHum.setText(Double.valueOf(mDeviceStatus.getValue().getHumidity()).intValue()+"");
        String rtvoc=mDeviceStatus.getValue().getRtvoc();
        int rtvocInt=0;
        try{
            rtvocInt= Integer.parseInt(rtvoc);
        }
        catch(NumberFormatException e){

        }
        DecimalFormat decimalFormat1=new DecimalFormat("0.00");
        tvTvoc.setText(decimalFormat1.format(rtvocInt*0.0012));
    }

    private int judgeAQILevel(int value) {
        if (value <= 50) {
            return 0;
        } else if (value > 50 && value <= 100) {
            return 1;
        } else if (value > 100 && value <= 150) {
            return 2;
        } else if (value > 150 && value <= 200) {
            return 3;
        } else if (value > 200 && value <= 300) {
            return 4;
        } else {
            return 5;
        }
    }


    private void queryDeviceStatus() {
        DeviceController.getInstance().queryDevicesStatus(this, mQueryDevice, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("ret") != 0) {
                        return;
                    }
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("devices");
                    jsonObject = jsonArray.getJSONObject(0);
                    DeviceStatus status = GsonUtil.getObject(jsonObject.toString(), DeviceStatus.class);
                    mDeviceStatus.setValue(status.getValue());
                    mDeviceStatus.setOffline(status.getOffline());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateUI();

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            queryDeviceStatus();
            myHandler.sendEmptyMessageDelayed(0, 60*1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.removeCallbacksAndMessages(null);

    }
}
