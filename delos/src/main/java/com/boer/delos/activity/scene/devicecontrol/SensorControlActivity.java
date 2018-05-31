package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.widget.BatteryViewSelf;
import com.boer.delos.widget.LightControlView;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/16 0016 09:58
 * @Modify:
 * @ModifyDate:
 */


public class SensorControlActivity extends CommonBaseActivity {

    @Bind(R.id.lightControlView)
    LightControlView mControlView;
    @Bind(R.id.ivSecurityBlackPoint)
    ImageView mIvSecurityBlackPoint;
    @Bind(R.id.battery_view)
    BatteryViewSelf mBatteryView;
    @Bind(R.id.iv_lvolt)
    ImageView mIvLvolt;
    @Bind(R.id.tv_battery_percent)
    TextView mTvBatteryPercent;
    @Bind(R.id.tvGsmStatus)
    TextView mTvGsmStatus;
    @Bind(R.id.ll_gsmStatus)
    LinearLayout mLlGsmStatus;
    @Bind(R.id.ctv_choice)
    CheckedTextView mCtvChoice;
    @Bind(R.id.ll_alarm)
    LinearLayout mLlAlarm;
    @Bind(R.id.ll_battery)
    LinearLayout mLlBattery;

    private Device mDevice;
    private DeviceStatus mStatus;
    private List<Device> devices = new ArrayList<Device>();
    private List<Device> mParameterList;

    @Override
    protected int initLayout() {

        return R.layout.activity_device_control_sensor;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DeviceRelate mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            mDevice = mDeviceRelate.getDeviceProp();
        }

    }



    @Override
    protected void initData() {
        if (mDevice != null) {
            mParameterList = new ArrayList<>();
            Device device = new Device();
            device.setAddr(mDevice.getAddr());
            mParameterList.add(device);

            mHandler.sendEmptyMessage(0);
        }
        settingUI(mDevice);

    }

    @Override
    protected void initAction() {

    }

    @OnClick(R.id.ctv_choice)
    public void onClick() {

        toggleClickistener(mStatus);

    }

    /**
     * 查询设备状态
     *
     * @param list
     */
    private void queryDeviceStatus(List<Device> list) {
        DeviceController.getInstance().queryDevicesStatus(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    DeviceStatusResult relateResult = GsonUtil.getObject(json, DeviceStatusResult.class);
                    if (relateResult.getRet() != 0) {
                        return;
                    }
                    mStatus = relateResult.getResponse().getDevices().get(0);
                    /*离线状态*/
                    if (mStatus.getOffline() == 1) {
                        mControlView.closeLight(0);
                        mBatteryView.setPower(100);
                        mCtvChoice.setChecked(false);
                        mCtvChoice.setEnabled(false);
                        return;
                    }

                    sensorSetting(mStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void updateUI(String type) {
        switch (type) {

        }
    }

    protected void settingUI(Device device) {
        if (device == null) return;
        tlTitleLayout.setTitle(device.getName());

        mLlAlarm.setVisibility(View.VISIBLE); //默认展示
        mLlGsmStatus.setVisibility(View.GONE);
        mBatteryView.setVisibility(View.VISIBLE);
        // 设置数据

        switch (device.getType()) {
            case "Exist":
                mLlAlarm.setVisibility(View.VISIBLE);
                break;
            case "Fall":
//                mLlAlarm.setVisibility(View.GONE);
                break;
            case "Water":
                mLlAlarm.setVisibility(View.VISIBLE);
//                mCtvChoice.setVisibility(View.GONE);
//                findViewById(R.id.tv_1).setVisibility(View.GONE);
                break;
            case "Sov":
                mLlBattery.setVisibility(View.GONE);
                mLlAlarm.setVisibility(View.GONE);
                mControlView.setSensor(false);
                break;
            case "SOS":
                mLlAlarm.setVisibility(View.VISIBLE);
                mLlBattery.setVisibility(View.GONE);
                mCtvChoice.setVisibility(View.GONE);
                findViewById(R.id.tv_1).setVisibility(View.GONE);
                break;
            case "Gsm": //门窗磁
                mLlAlarm.setVisibility(View.VISIBLE);
                mLlGsmStatus.setVisibility(View.VISIBLE); //打开关闭
//                mLlBattery.setVisibility(View.VISIBLE); //电量
                break;
            case "CurtainSensor":
                mLlAlarm.setVisibility(View.VISIBLE);
                break;
        }
    }

    protected void toggleClickistener(DeviceStatus deviceStatus) {
        if (deviceStatus == null) {
            return;
        }
        DeviceStatusValue value = deviceStatus.getValue();
        if (value == null) {
            value = new DeviceStatusValue();

            value.setSet(1);
            value.setState("0");
            value.setLvolt("0");
            value.setPosition("0");
            value.setPowerPercent("100");
//            if (!value.set) value.set = @"1";
//            if (!value.state) value.state = @"0";
//            if (!value.lvolt) value.lvolt = @"0";
//            if (!value.position) value.position = @"0";
//            if ([@[@"Exist", @"CurtainSensor", @"Water", @"Gsm"] containsObject:self.device.deviceProp.type]) {
//                if (!value.powerPercent) value.powerPercent = @"100";
//            }
        }
        /////存在传感器,set是开关,value是状态
        if ("Exist".equals(deviceStatus.getType())) {
            if (deviceStatus.getValue().getSet() != null) {
//                if (1 == deviceStatus.getValue().getSet()) {
                if (mCtvChoice.isChecked()) {

                    deviceStatus.getValue().setSet(0);
                    mControlView.closeLight(0);
                } else {
                    deviceStatus.getValue().setSet(1);
                    mControlView.startLight(0);
                }
            } else {
                value.setSet(1);
                mControlView.startLight(0);
                deviceStatus.setValue(value);
            }
        } else {
            if (deviceStatus.getValue() != null && deviceStatus.getValue().getSet() != null) {
//                if (1 == deviceStatus.getValue().getSet()) {
                if (mCtvChoice.isChecked()) {
                    deviceStatus.getValue().setSet(0);
                    mControlView.closeLight(0);
                } else {
                    deviceStatus.getValue().setSet(1);
                    mControlView.startLight(0);
                }
            } else {
                if (value.getState().equals("0")) {
                    value.setState("1");
                    mControlView.startLight(0);
                } else {
                    value.setState("0");
                    mControlView.closeLight(0);
                }
                deviceStatus.setValue(value);
            }
        }
        mCtvChoice.toggle();

        DeviceRelate deviceRelate = new DeviceRelate();
        deviceRelate.setDeviceProp(mDevice);
        deviceRelate.setDeviceStatus(deviceStatus);
        //开关设备
        sendDeviceControl(deviceRelate);
    }


    protected void sensorSetting(DeviceStatus deviceStatus) {
        //布防
        if (deviceStatus != null && deviceStatus.getValue() == null) {
            mIvSecurityBlackPoint.setBackgroundResource(R.mipmap.ic_alarm_off);
            mTvGsmStatus.setText(getString(R.string.sensor_off));
            mTvBatteryPercent.setText("100%");
            mBatteryView.setPower(100);
            mControlView.closeLight(0);
            mIvLvolt.setVisibility(View.GONE);
            return;
        }
        //  布防
        if (deviceStatus.getValue().getSet() != null) {
            if (1 == deviceStatus.getValue().getSet()) {
//                    battery_view.setColor(Color.GREEN);
                mCtvChoice.setChecked(true);
                mControlView.startLight(0);
            } else {
//                    battery_view.setColor(Color.GRAY);
                mCtvChoice.setChecked(false);
                mControlView.closeLight(0);
            }
        } else {
            //报警
            if (!StringUtil.isEmpty(deviceStatus.getValue().getState())
                    && deviceStatus.getValue().getState().equals("0")) {
                mCtvChoice.setChecked(true);
                mControlView.startLight(0);
            } else {
                mCtvChoice.setChecked(false);
                mControlView.closeLight(0);
            }

        }
        //报警
        if (!StringUtil.isEmpty(deviceStatus.getValue().getState())) {
            boolean alarm = deviceStatus.getValue().getState().equals("1");
            Log.d("我的", "报警：" + alarm);
            mIvSecurityBlackPoint.setBackgroundResource(alarm
                    ? R.mipmap.ic_alarm_on : R.mipmap.ic_alarm_off);
        } else {
            mIvSecurityBlackPoint.setBackgroundResource(R.mipmap.ic_alarm_off);
        }

        //状态
        if (!StringUtil.isEmpty(deviceStatus.getValue().getPosition())) {
            mTvGsmStatus.setText(deviceStatus.getValue().getPosition().equals("1") ?
                    getString(R.string.sensor_on) : getString(R.string.sensor_off));
        }
        //  电量
        // lvolt 和 powerPercent同时为零 强电
        if (!StringUtil.isEmpty(deviceStatus.getValue().

                getLvolt())
                && deviceStatus.getValue().

                getLvolt().

                equals("1"))

        {
//            mIvLvolt.setVisibility(View.VISIBLE);
            mBatteryView.setVisibility(View.VISIBLE);
            mTvBatteryPercent.setText("100%");
            mBatteryView.setPower(100);
        } else if (!StringUtil.isEmpty(deviceStatus.getValue().

                getLvolt())
                && !StringUtil.isEmpty(deviceStatus.getValue().

                getLvolt())
                && deviceStatus.getValue().

                getLvolt().

                equals("0")
                && deviceStatus.getValue().

                getLvolt().

                equals("0"))

        {
            mTvBatteryPercent.setText(deviceStatus.getValue().getPowerPercent()+"%");
            mBatteryView.setPower(Integer.valueOf(deviceStatus.getValue().getPowerPercent()));

        } else if (!StringUtil.isEmpty(deviceStatus.getValue().

                getLvolt()))

        {
            mTvBatteryPercent.setText(deviceStatus.getValue().getPowerPercent() + "%");
            mBatteryView.setPower(Integer.valueOf(deviceStatus.getValue().getPowerPercent()));
        }

    }


    /**
     * 发送插座控制命令
     *
     * @param deviceRelate
     */

    private void sendDeviceControl(DeviceRelate deviceRelate) {

        List<ControlDevice> controlDevices = new ArrayList<>();

        Device device = deviceRelate.getDeviceProp();
        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        DeviceStatusValue statusValue = deviceRelate.getDeviceStatus().getValue();
        if (statusValue.getSet() != null) {
            value.setSet(String.valueOf(statusValue.getSet()));
        }
        value.setState(statusValue.getState());
        controlDevice.setValue(value);

        controlDevices.add(controlDevice);

        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                if (result.getRet() == 0) {

                } else {
                    mCtvChoice.toggle();
                    if (!mCtvChoice.isChecked()) {
                        mControlView.startLight(0);
                    } else
                        mControlView.closeLight(0);

                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void onRefresh() {
        queryDeviceStatus(mParameterList);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onRefresh();
            mHandler.sendEmptyMessageDelayed(0, 1000 * 3);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
