package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.L;
import com.boer.delos.widget.RingSeekBar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by gaolong on 2017/4/12.
 */
public class LightingManualControlActivity extends CommonBaseActivity implements RingSeekBar.ITouchUpEvent {
    @Bind(R.id.ring_seekbar)
    RingSeekBar ringSeekbar;
    @Bind(R.id.cbLight)
    CheckBox cbLight;
    @Bind(R.id.btn_lighting_time)
    Button btnLightingTime;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    int second = 5;
    String toggle = "0";
    String progress = "0";
    private List<Device> devices = new ArrayList<Device>();
    private String num;
    private String tag = "0"; //标记是否是常用设备

    @Override
    protected int initLayout() {
        return R.layout.activity_lighting_manual_control;
    }

    @Override
    protected void initView() {

        initCavas(false);

        tlTitleLayout.setTitle(R.string.text_lighting_scale);
        ringSeekbar.setITouchUpEvent(this);

    }

    @Override
    protected void initData() {


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                tlTitleLayout.setTitle(mDevice.getName());
                if( !TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())
                        && mDeviceRelate.getDeviceProp().getFavorite().equals("1")){
                    tag = "1";
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
                }else
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
            }
        }
    }

    @Override
    protected void initAction() {

        cbLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if (isChecked) {
                    toggle = "1";
                } else toggle = "0";

                initCavas(isChecked);

                sendController(toggle, progress, second + "");




            }
        });


        btnLightingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSecond(second);
            }
        });

    }

    @Override
    public void rightViewClick() {
        if(tag.equals("1")){
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        }else if(tag.equals("0")){
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if(mDevice != null) {  devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }


    private void initSecond(int second) {
        if (second == 5) {
            this.second = 0;
            btnLightingTime.setBackground(getResources().getDrawable(R.mipmap.ic_light_zero));


        } else if (second == 3) {
            this.second = 5;
            btnLightingTime.setBackground(getResources().getDrawable(R.mipmap.ic_light_five));

        } else if (second == 0) {
            this.second = 3;
            btnLightingTime.setBackground(getResources().getDrawable(R.mipmap.ic_light_three));

        }


    }


    private void initCavas(boolean unlight) {

        if (unlight) {
            btnLightingTime.setEnabled(true);
            initSecond(0);

            ringSeekbar.setProgressTextColor(getResources().getColor(R.color.blue_device_control));
            ringSeekbar.setProgress(0);
            ringSeekbar.cantTouch(false);
            ringSeekbar.setIsShowProgressText(true);
        } else {
            btnLightingTime.setEnabled(false);
            initSecond(5);

            ringSeekbar.setProgress(0);
            ringSeekbar.cantTouch(true);
            ringSeekbar.setProgressTextColor(getResources().getColor(R.color.grey_device_control));
            ringSeekbar.setProgressTextSize(DensityUtil.dip2px(50));
            ringSeekbar.setProgressTextStrokeWidth(DensityUtil.dip2px(10));
            ringSeekbar.setIsShowProgressText(false);
        }

        ringSeekbar.postInvalidate();


    }


    private void sendController(String status, String coeff, String limitTime) {
        Log.v("gl", "toggle=" + status + "progress=" + coeff + "second=" + limitTime);

        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(mDevice.getRoomname());
        controlDevice.setAddr(mDevice.getAddr());
        controlDevice.setAreaName(mDevice.getAreaname());
        controlDevice.setDeviceName(mDevice.getName());
        controlDevice.setType(mDevice.getType());

        ControlDeviceValue value = new ControlDeviceValue();
        value.setCoeff(coeff);
        value.setState(status);
        value.setLightingTime(limitTime);

        controlDevice.setValue(value);
        controlDevices.add(controlDevice);
        deviceLightControl(controlDevices);

    }


    public void deviceLightControl(List<ControlDevice> controlDevices) {
        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
                        //表明控制成功
                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                L.i("LightingControlChildAdapter sendController() onSuccess()" + Json);
            }

            @Override
            public void onFailed(String json) {
                L.d("LightingControlChildAdapter sendController() onFailed():" + json);
            }
        });
    }


    @Override
    public void showProgress(String progress) {

        sendController(toggle, progress, second + "");

    }


}
