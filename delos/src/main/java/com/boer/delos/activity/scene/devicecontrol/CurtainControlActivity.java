package com.boer.delos.activity.scene.devicecontrol;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.CurtainControlAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 窗帘界面
 * create at 2016/5/5 16:44
 */
public class CurtainControlActivity extends CommonBaseActivity {

    private ListView lvCurtainList;
    private CurtainControlAdapter adapter;
    private List<DeviceRelate> curtainList;
    private ImageView imageView_left;
    private ImageView imageView_right;
    private int screen_width;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private String roomId;
    private List<Device> devices = new ArrayList<Device>();
    private String tag; //标记是否是常用设备

    @Override
    protected int initLayout() {
        return R.layout.activity_curtain_control;
    }

    protected void initView() {
        this.lvCurtainList = (ListView) findViewById(R.id.lvCurtainList);
        imageView_left = (ImageView) findViewById(R.id.imageView_left);
        imageView_right = (ImageView) findViewById(R.id.imageView_right);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                tlTitleLayout.setTitle(mDevice.getName());
                tag = mDeviceRelate.getDeviceProp().getFavorite();
                if (!TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())
                        && tag.equals("1")) {
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
                } else
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
            }
        }
    }

    protected void initData() {
        adapter = new CurtainControlAdapter(this);
        curtainList = new ArrayList<>();
        curtainList.add(mDeviceRelate);
        adapter.setList(curtainList, new CurtainControlAdapter.ClickResultListener() {
            @Override
            public void ClickResult(int position, int tag) {
                DeviceRelate deviceRelate = (DeviceRelate) adapter.getItem(position);
                Device device = deviceRelate.getDeviceProp();
                switch (tag) {
                    case 0://开始
                        openCurtain();
                        controlCurtain(device, "1");
                        break;
                    case 1://暂停
                        stopCurtain();
                        controlCurtain(device, "3");
                        break;
                    case 2://关闭
                        closeCurtain();
                        controlCurtain(device, "2");
                        break;
                }
            }


        });
        this.lvCurtainList.setAdapter(adapter);
        screen_width = ScreenUtils.getScreenWidth(this);
//        setCurtainInfo();
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
        if (mDevice != null) {  devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    /**
     * 设定窗帘信息
     */
    private void setCurtainInfo() {
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<DeviceRelate> filterList = filterDismissDevice(deviceRelateList);
        if (filterList.size() > 0) {
            curtainList.clear();
            curtainList.addAll(filterList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 过滤解绑和非当间房间设备
     *
     * @param list
     * @return
     */
    private List<DeviceRelate> filterDismissDevice(List<DeviceRelate> list) {
        //过滤当前房间设备
        List<DeviceRelate> filterList = new ArrayList<>();
        for (DeviceRelate deviceRelate : list) {
            Device device = deviceRelate.getDeviceProp();
            if (device.getType().contains("Curtain")) {
                filterList.add(deviceRelate);
            }
        }
        return filterList;
    }

    /**
     * 关闭窗帘
     */
    @TargetApi(19)
    private void closeCurtain() {
        //计算需要用时间
        int duration = (int) (screen_width - Math.abs(imageView_left.getX())) * 5;
        imageView_left.animate()
                .translationXBy(imageView_left.getX())
                .translationX(0)
                .setDuration(duration)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (imageView_left.getX() == 0) {
                            imageView_left.animate().cancel();
                        }
                    }
                });
        imageView_right.animate()
                .translationXBy(imageView_right.getX())
                .translationX(0)
                .setDuration(duration)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        if (imageView_right.getX() == 0) {
                            imageView_right.animate().cancel();
                        }
                    }
                });
    }

    /**
     * 暂停窗帘
     */
    private void stopCurtain() {
        imageView_left.animate().cancel();
        imageView_right.animate().cancel();
    }

    /**
     * 打开窗帘
     */
    @TargetApi(19)
    private void openCurtain() {
        int duration = (int) (screen_width - Math.abs(imageView_left.getX())) * 5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            imageView_left.animate()
                    .translationXBy(imageView_left.getX())
                    .translationX(-screen_width / 2)
                    .setDuration(duration)
                    .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            if (imageView_left.getX() == -screen_width / 2) {
                                imageView_left.animate().cancel();
                            }
                        }
                    });
            imageView_right.animate()
                    .translationXBy(imageView_right.getX())
                    .translationX(screen_width / 2)
                    .setDuration(duration)
                    .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            if (imageView_right.getX() == screen_width) {
                                imageView_right.animate().cancel();
                            }
                        }
                    });
        } else {
            ObjectAnimator oa = ObjectAnimator.ofFloat(imageView_left, View.TRANSLATION_X, imageView_left.getX())
                    .setDuration(duration);
            oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (imageView_left.getX() == -screen_width / 2) {
                        imageView_left.animate().cancel();
                    }
                }
            });
            oa.start();

            ObjectAnimator oaR = ObjectAnimator.ofFloat(imageView_right, View.TRANSLATION_X, imageView_right.getX())
                    .setDuration(duration);
            oaR.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (imageView_right.getX() == screen_width) {
                        imageView_right.animate().cancel();
                    }
                }
            });
            oaR.start();
        }
    }


    /**
     * 控制窗帘
     *
     * @param device
     * @param state
     */
    private void controlCurtain(Device device, String state) {
        List<ControlDevice> devices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        value.setState(state);
        controlDevice.setValue(value);
        devices.add(controlDevice);

        DeviceController.getInstance().deviceControl(this, devices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("controlCurtain:" + Json);
            }

            @Override
            public void onFailed(String json) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageView_left != null)
            imageView_left.clearAnimation();
        if (imageView_right != null)
            imageView_right.clearAnimation();


    }
}
