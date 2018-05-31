package com.boer.delos.activity.scene.devicecontrol;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhukang on 16/7/12.
 */
public class LockControlActivity extends CommonBaseActivity {

    private Boolean isLockOpen = Boolean.FALSE;
    private ImageView iv_lock, iv_finger_open, iv_finger_close, iv_light;
    private ClipDrawable cd;
    private String roomId;
    private DeviceRelate mDeviceRelate;
    private Device mDevice;

    private List<Device> devices = new ArrayList<>();
    private String tag; //标记是否是常用设备
    private Timer mTimer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                cd.setLevel(cd.getLevel() + 100);

            } else {
                cd.setLevel(cd.getLevel() - 100);
            }
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_lock_control;
    }

    protected void initView() {

        iv_lock = (ImageView) findViewById(R.id.iv_lock);
        iv_finger_open = (ImageView) findViewById(R.id.iv_finger_open);
        iv_finger_close = (ImageView) findViewById(R.id.iv_finger_close);
        iv_light = (ImageView) findViewById(R.id.iv_light);
        tlTitleLayout.setTitle(getString(R.string.lock_name));

        mTimer = new Timer();
    }

    protected void initData() {

        mDeviceRelate = (DeviceRelate) getIntent().getSerializableExtra("device");
        if (mDeviceRelate != null) {
            mDevice = mDeviceRelate.getDeviceProp();
        }
        if (!TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())) {
            tag = mDeviceRelate.getDeviceProp().getFavorite();
            if (tag.equals("1")) {
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
            } else {
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
            }
        } else {
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
            mDeviceRelate.getDeviceProp().setFavorite("0");
        }

        DeviceStatusValue value = mDeviceRelate.getDeviceStatus().getValue();
        if (value == null) {
            value = new DeviceStatusValue();
        }
        if (TextUtils.isEmpty(value.getState())
                || value.getState().equals("0")) {//关
            isLockOpen = false;
            iv_finger_open.setVisibility(View.GONE);
            iv_lock.setImageResource(R.drawable.ic_lock_close);

        } else {
            isLockOpen = true;
            iv_finger_open.setVisibility(View.VISIBLE);
            iv_lock.setImageResource(R.drawable.ic_lock_open);
        }

    }

    @Override
    protected void initAction() {
        iv_finger_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLockOpen = Boolean.FALSE;
                mTimer.cancel();
                refreshFingerButton();
            }
        });
        iv_finger_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLockOpen = Boolean.TRUE;
                mTimer.cancel();
                refreshFingerButton();
            }
        });
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
            mDevice.setFavorite(tag);
            if (!devices.contains(mDevice)) {
                devices.add(mDevice);
            }
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    /**
     * 刷新按钮
     */
    private void refreshFingerButton() {
        mTimer = new Timer();
        cd = (ClipDrawable) iv_finger_open.getDrawable();
        if (isLockOpen) {
            iv_finger_open.setVisibility(View.VISIBLE);
            //启动线程运行动画
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0x01);
                    if (cd.getLevel() >= 10000) {
                        mTimer.cancel();
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                LockControlActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isLockOpen = Boolean.FALSE;
                                        mTimer.cancel();
                                        refreshFingerButton();
                                    }
                                });
                            }
                        },5000);
                    }
                }
            }, 0, 10);
            iv_lock.setImageResource(R.drawable.ic_lock_open);
            if (mDeviceRelate != null) {
                //发送控制命令
                sendDeviceControl(mDeviceRelate, "1");
            }
        } else {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0x02);
                    if (cd.getLevel() <= 0) {
                        mTimer.cancel();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_finger_open.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }, 0, 10);
            iv_lock.setImageResource(R.drawable.ic_lock_close);
            if (mDeviceRelate != null) {
                //发送控制命令
                sendDeviceControl(mDeviceRelate, "0");
            }
        }
    }

    /**
     * 发送门锁控制命令
     *
     * @param deviceRelate
     * @param flag
     */
    private void sendDeviceControl(DeviceRelate deviceRelate, String flag) {

        List<ControlDevice> controlDevices = new ArrayList<>();

        Device device = deviceRelate.getDeviceProp();
        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        value.setSet("1");
        value.setState(flag);
        controlDevice.setValue(value);

        controlDevices.add(controlDevice);

        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("deviceControl_Json===" + Json);
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

}
