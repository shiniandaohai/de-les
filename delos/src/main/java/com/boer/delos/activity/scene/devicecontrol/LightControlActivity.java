package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.L;
import com.boer.delos.view.TitleLayout;
import com.boer.delos.widget.LightControlView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.value;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/11 0011 13:46
 * @Modify:
 * @ModifyDate:
 */


public class LightControlActivity extends CommonBaseActivity {

    @Bind(R.id.lightControlView)
    LightControlView mLightControlView;
    @Bind(R.id.cbLight1)
    CheckBox mCbLight1;
    @Bind(R.id.cbLight2)
    CheckBox mCbLight2;
    @Bind(R.id.ll_light_123)
    LinearLayout mLlLight123;
    @Bind(R.id.cbLight3)
    CheckBox mCbLight3;
    @Bind(R.id.cbLight4)
    CheckBox mCbLight4;
    @Bind(R.id.ll_light_34)
    LinearLayout mLlLight34;
    @Bind(R.id.ll_light_1)
    LinearLayout mLlLight1;
    @Bind(R.id.ll_light_2)
    LinearLayout mLlLight2;
    @Bind(R.id.ll_light_3)
    LinearLayout mLlLight3;
    @Bind(R.id.ll_light_4)
    LinearLayout mLlLight4;
    @Bind(R.id.cbLight33)
    CheckBox mCbLight33;
    @Bind(R.id.ll_light_33)
    LinearLayout mLlLight33;
    @Bind(R.id.tv_light_1)
    TextView tv_light_1;
    @Bind(R.id.tv_light_2)
    TextView tv_light_2;
    @Bind(R.id.tv_light_3)
    TextView tv_light_3;
    @Bind(R.id.tv_light_34_3)
    TextView tv_light_34_3;
    @Bind(R.id.tv_light_34_4)
    TextView tv_light_34_4;

    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private List<Device> devices = new ArrayList<Device>();
    private String num;
    private String tag; //标记是否是常用设备
    private boolean isChange;//标记是否点击过右上角按钮

    @Override
    protected int initLayout() {
        return R.layout.activity_device_control_light;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                tlTitleLayout.setTitle(mDevice.getName());
                if (!TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())) {
                    tag = mDeviceRelate.getDeviceProp().getFavorite();
                    if (tag.equals("1")) {
                        tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
                    } else {
                        tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
                    }
                }
            }
        }
    }

    @Override
    protected void initData() {
        initUI();
        upDateUI();
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else if (tag.equals("0")) {
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if (mDevice != null) {
            devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

  /*  @Override
    protected void onStop() {
        super.onStop();
        //保存状态，并发送后台
        if(mDevice != null)
            mDevice.setFavorite(tag);
        devices.add(mDevice);
        DeviceUpdateStatus.setCommonDevice(this,devices,toastUtils);
    }*/

    @OnClick({R.id.cbLight1, R.id.cbLight2, R.id.cbLight3, R.id.cbLight4, R.id.cbLight33})
    public void onClick(View view) {
        if (mDeviceRelate.getDeviceStatus() != null && mDeviceRelate.getDeviceStatus().getOffline() == 1) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_device_offline));
            mCbLight1.setChecked(false);
            mCbLight2.setChecked(false);
            mCbLight3.setChecked(false);
            mCbLight33.setChecked(false);
            mCbLight4.setChecked(false);
            return;
        }
        String flag = "0";
        int position = 0;
        switch (view.getId()) {

            case R.id.cbLight1:
                if (mCbLight1.isChecked()) {
                    mLightControlView.startLight(0);
                    flag = "1";
                } else {
                    mLightControlView.closeLight(0);
                }
                break;
            case R.id.cbLight2:
                position = 1;
                if (mCbLight2.isChecked()) {
                    mLightControlView.startLight(1);
                    flag = "1";
                } else {
                    mLightControlView.closeLight(1);
                }
                break;
            case R.id.cbLight33:
                position = 2;
                if (mCbLight33.isChecked()) {
                    mLightControlView.startLight(2);
                    flag = "1";
                } else {
                    mLightControlView.closeLight(2);
                }
                break;
            case R.id.cbLight3:
                position = 2;
                if (mCbLight3.isChecked()) {
                    mLightControlView.startLight(2);
                    flag = "1";
                } else {
                    mLightControlView.closeLight(2);
                }
                break;
            case R.id.cbLight4:
                position = 3;
                if (mCbLight4.isChecked()) {
                    mLightControlView.startLight(3);
                    flag = "1";
                } else {
                    mLightControlView.closeLight(3);
                }
                break;
        }
        sendController(position, flag, mDevice.getType(), null);
    }

    /**
     * /**
     * 发送控制命令
     *
     * @param position 控制几号灯
     * @param flag     开关： 0、 1
     * @param type     作为判断是否是调光灯或者灯1
     * @param coeff    调光灯值
     */
    private void sendController(int position, String flag, String type, String coeff) {
        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(mDevice.getRoomname());
        controlDevice.setAddr(mDevice.getAddr());
        controlDevice.setAreaName(mDevice.getAreaname());
        controlDevice.setDeviceName(mDevice.getName());
        controlDevice.setType(mDevice.getType());

        ControlDeviceValue value = new ControlDeviceValue();
        if (position == 0) {
            value.setState(flag);

        } else if (position == 1) {
            value.setState2(flag);
        } else if (position == 2) {
            value.setState3(flag);
        } else if (position == 3) {
            value.setState4(flag);
        } else if (type.equals("LightAdjust")) {
            value.setCoeff(coeff);
            value.setState(flag);
        }

        controlDevice.setValue(value);
        controlDevices.add(controlDevice);
        deviceLightControl(controlDevices);

    }

    public void deviceLightControl(List<ControlDevice> controlDevices) {
        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Log.i("gwq", "json=" + Json);
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
                        //表明控制成功
                    } else {
                        initUI();
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


    private void initUI() {
        if (mDevice != null && !TextUtils.isEmpty(mDevice.getType()) && mDevice.getType().contains("Light")) {
            try {
                num = mDevice.getType().substring(mDevice.getType().length() - 1, mDevice.getType().length());
                mLightControlView.settingLightNum(Integer.valueOf(num));
                switch (num) {
                    case "1":
                        mLlLight2.setVisibility(View.GONE);
                        mLlLight3.setVisibility(View.GONE);
                        mLlLight4.setVisibility(View.GONE);
                        mLlLight33.setVisibility(View.GONE);

                        mLlLight1.setVisibility(View.VISIBLE);
                        tv_light_1.setText(getString(R.string.text_light_toggle));
                        mLlLight123.setPadding(0, 0, 0, DensityUtil.dip2px(70));

                        break;
                    case "2":
                        mLlLight3.setVisibility(View.GONE);
                        mLlLight4.setVisibility(View.GONE);
                        mLlLight33.setVisibility(View.GONE);

                        mLlLight1.setVisibility(View.VISIBLE);
                        mLlLight2.setVisibility(View.VISIBLE);
                        mLlLight123.setPadding(0, 0, 0, DensityUtil.dip2px(70));

                        if(mDevice.getLightName()!=null){
                            tv_light_1.setText(TextUtils.isEmpty(mDevice.getLightName().get("name1"))?"灯1":
                                    mDevice.getLightName().get("name1"));
                            tv_light_2.setText(TextUtils.isEmpty(mDevice.getLightName().get("name2"))?"灯2":
                                    mDevice.getLightName().get("name2"));
                        }

                        break;
                    case "3":
                        mLlLight3.setVisibility(View.GONE);
                        mLlLight4.setVisibility(View.GONE);

                        mLlLight1.setVisibility(View.VISIBLE);
                        mLlLight2.setVisibility(View.VISIBLE);
                        mLlLight33.setVisibility(View.VISIBLE);
                        mLlLight123.setPadding(0, 0, 0, DensityUtil.dip2px(70));

                        if(mDevice.getLightName()!=null){
                            tv_light_1.setText(TextUtils.isEmpty(mDevice.getLightName().get("name1"))?"灯1":mDevice.getLightName().get("name1"));
                            tv_light_2.setText(TextUtils.isEmpty(mDevice.getLightName().get("name2"))?"灯2":mDevice.getLightName().get("name2"));
                            tv_light_3.setText(TextUtils.isEmpty(mDevice.getLightName().get("name3"))?"灯3":mDevice.getLightName().get("name3"));
                        }

                        break;
                    case "4":
                        mLlLight33.setVisibility(View.GONE);

                        mLlLight1.setVisibility(View.VISIBLE);
                        mLlLight2.setVisibility(View.VISIBLE);
                        mLlLight3.setVisibility(View.VISIBLE);
                        mLlLight4.setVisibility(View.VISIBLE);
                        mLlLight34.setVisibility(View.VISIBLE);

                        if(mDevice.getLightName()!=null){
                            tv_light_1.setText(TextUtils.isEmpty(mDevice.getLightName().get("name1"))?"灯1":mDevice.getLightName().get("name1"));
                            tv_light_2.setText(TextUtils.isEmpty(mDevice.getLightName().get("name2"))?"灯2":mDevice.getLightName().get("name2"));
                            tv_light_34_3.setText(TextUtils.isEmpty(mDevice.getLightName().get("name3"))?"灯3":mDevice.getLightName().get("name3"));
                            tv_light_34_4.setText(TextUtils.isEmpty(mDevice.getLightName().get("name4"))?"灯4":mDevice.getLightName().get("name4"));
                        }

                        break;

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void upDateUI() {
        String type = mDevice.getType().substring(mDevice.getType().length() - 1, mDevice.getType().length());
        DeviceStatus state = mDeviceRelate.getDeviceStatus();
        DeviceStatusValue value = state.getValue();
        if (value == null) {
            return;
        }
        switch (type) {
            case "1":
                mCbLight1.setChecked(value.getState().equals("1"));

                mLightControlView.startLight(mCbLight1.isChecked() ? 0 : -1);
                break;
            case "2":
                mCbLight1.setChecked(value.getState().equals("1"));
                mCbLight2.setChecked(value.getState2().equals("1"));

                mLightControlView.startLight(mCbLight1.isChecked() ? 0 : -1);
                mLightControlView.startLight(mCbLight2.isChecked() ? 1 : -1);
                break;
            case "3":
                mCbLight1.setChecked(value.getState().equals("1"));
                mCbLight2.setChecked(value.getState2().equals("1"));
                mCbLight33.setChecked(value.getState3().equals("1"));

                mLightControlView.startLight(mCbLight1.isChecked() ? 0 : -1);
                mLightControlView.startLight(mCbLight2.isChecked() ? 1 : -1);
                mLightControlView.startLight(mCbLight33.isChecked() ? 2 : -1);
                break;
            case "4":
                mCbLight1.setChecked(value.getState().equals("1"));
                mCbLight1.setChecked(value.getState2().equals("1"));
                mCbLight3.setChecked(value.getState3().equals("1"));
                mCbLight4.setChecked(value.getState4().equals("1"));

                mLightControlView.startLight(mCbLight1.isChecked() ? 0 : -1);
                mLightControlView.startLight(mCbLight2.isChecked() ? 1 : -1);
                mLightControlView.startLight(mCbLight3.isChecked() ? 2 : -1);
                mLightControlView.startLight(mCbLight4.isChecked() ? 3 : -1);
                break;
        }
    }
}
