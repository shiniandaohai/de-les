package com.boer.delos.activity.scene.devicecontrol.music;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.adapter.MusicModeLinkSettingAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.ModeAct;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunzhibin on 2017/8/15.
 * cmd   data                 描述
 * 0x1   0:关机 1:开机       开关机状态
 * 0x2   0-100              系统音量
 * 0x3   0：递加 1：递减      音量逐级调节
 * 0x4   0：停止1：开始2：暂停 设备播放状态
 * 0x5   0x0000-oxffff      播放指定曲目序号
 * 0x6   0：上一曲 1：下一曲   按列表顺序切换曲目
 */

public class MusicWise485Activity extends CommonBaseActivity {
    @Bind(R.id.iv_off_on)
    ImageView ivOffOn;
    @Bind(R.id.iv_album)
    ImageView ivAlbum;
    @Bind(R.id.tv_music_name)
    TextView tvMusicName;
    @Bind(R.id.tv_music_author)
    TextView tvMusicAuthor;
    @Bind(R.id.iv_voice_down)
    ImageView ivVoiceDown;
    @Bind(R.id.iv_play_bar_pre)
    ImageView ivPlayBarPre;
    @Bind(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @Bind(R.id.iv_voice_up)
    ImageView ivVoiceUp;
    @Bind(R.id.exLv_music_mode)
    ExpandableListView expandableListView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private Device mDevice;
    private DeviceStatus mDeviceStatus;
    private Device mQueryDevice;//查询用
    private ControlDeviceValue mControlValue;
    private ControlDevice mControlDevice;

    private MyHandler myHandler;
    private HashMap<String, List<ModeAct>> mModelMaps;

    private MusicModeLinkSettingAdapter mAdapter;
    private List<Device> devices;
    private String favoriteTag;
    private ObjectAnimator objectAnimator;
    private ObjectAnimator objectAnimatorLeft;

    private int volume = 1;
    private DeviceStatusValue deviceStatusValue;
    @Override
    protected int initLayout() {
        return R.layout.activity_music_485;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.music_background_music));
        tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);

        drawerLayout.setScrimColor(Color.TRANSPARENT);
//        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDevice = deviceRelate.getDeviceProp();
        mDeviceStatus = deviceRelate.getDeviceStatus();
        if(mDeviceStatus.getValue()==null){
            deviceStatusValue=new DeviceStatusValue();
            deviceStatusValue.setVolume(0);
            deviceStatusValue.setCurrNo(0);
            deviceStatusValue.setPlayState(0);
            deviceStatusValue.setState("0");
            mDeviceStatus.setValue(deviceStatusValue);
        }
        volume = mDeviceStatus.getValue().getVolume();

        mQueryDevice = new Device();
        mQueryDevice.setAddr(mDevice.getAddr());
        mQueryDevice.setType(mDevice.getType());

        mControlDevice = new ControlDevice();
        mControlDevice.setAddr(mDevice.getAddr());
        mControlDevice.setAreaName(mDevice.getAreaname());
        mControlDevice.setRoomName(mDevice.getRoomname());
        mControlDevice.setDeviceName(mDevice.getName());
        mControlDevice.setType(mDevice.getType());
        mControlDevice.setBrand(mDevice.getBrand());

        mControlValue = new ControlDeviceValue();
        mControlValue.setDataLen("1");
        mControlValue.setBrand(mDevice.getBrand());
        mControlDevice.setValue(mControlValue);
        mControlValue.setBrand(mDevice.getBrand());

        updateUI();
        myHandler = new MyHandler();
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        mAdapter = new MusicModeLinkSettingAdapter(this, mModelMaps);
        expandableListView.setAdapter(mAdapter);
        expandableListView.setGroupIndicator(null);
        queryAllModesInfo();

        devices = new ArrayList<>();
        favoriteTag = mDevice.getFavorite();
        if (!TextUtils.isEmpty(favoriteTag))
            tlTitleLayout.setLinearRightImage(!favoriteTag.equals("1") ?
                    R.mipmap.nav_collect_nor : R.mipmap.nav_red_collect);

    }

    @Override
    protected void initAction() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ModeAct modeAct = mAdapter.getChild(groupPosition, childPosition);
                String modeId = modeAct.getModeId();
                mControlValue.setCmd("5");
                mControlValue.setData(mDeviceStatus.getValue().getCurrNo() + "");
                mControlValue.setModeId(modeId);
                mControlValue.setDataLen("2");
                mControlValue.setVolume(mDeviceStatus.getValue().getVolume().toString());
                mControlDevice.setValue(mControlValue);
                controlMusic(mControlDevice);
                return true;
            }
        });
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

    @OnClick({R.id.iv_off_on, R.id.iv_album, R.id.iv_voice_down, R.id.iv_play_bar_pre,
            R.id.iv_play_bar_play, R.id.iv_play_bar_next, R.id.iv_voice_up, R.id.iv_music_link})
    public void onViewClicked(View view) {
        int i = -1;
        switch (view.getId()) {
            case R.id.iv_off_on:
                i = 5;
                break;
            case R.id.iv_music_link:
                drawerLayout.openDrawer(GravityCompat.END);
                return;
            case R.id.iv_voice_down:
                i = 0;
                break;
            case R.id.iv_play_bar_pre:
                i = 1;
                break;
            case R.id.iv_play_bar_play:
                i = 2;
                break;
            case R.id.iv_play_bar_next:
                i = 3;
//                tranceXAnimator(false);
                break;
            case R.id.iv_voice_up:
                i = 4;
                break;
        }
        String data = "";
        String cmd = "";
        if (i == 0) {//音量-
            data = "1";
            cmd = "3";

        } else if (i == 1) {//上一首
            data = "1";
            cmd = "6";

        } else if (i == 2) {//暂停播放按钮
//            data = ivPlayBarPlay.isSelected() ? "1" : "2";
            data = mDeviceStatus.getValue().getPlayState() != 1 ? "1" : "2";
            cmd = "4";

        } else if (i == 3) {//下一首
            data = "2";
            cmd = "6";
        } else if (i == 4) {//音量+
            data = "2";
            cmd = "3";
        } else if (i == 5) {
            data = !ivOffOn.isSelected() ? "1" : "0";
//            data = "0"; //"1"
            cmd = "1";
        }
        mControlValue.setCmd(cmd);
        mControlValue.setData(data);
        mControlDevice.setValue(mControlValue);
        mControlValue.setModeId("");
        mControlValue.setVolume("");
        mControlValue.setDataLen("1");
        controlMusic(mControlDevice);

    }


    private void controlMusic(final ControlDevice controlDevice) {

        DeviceController.getInstance().deviceControl(this, controlDevice, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    if (ret != 0) {
                        toastUtils.showInfoWithStatus(getString(R.string.music_play_fail));
                    }
                    if (!TextUtils.isEmpty(controlDevice.getValue().getVolume())
                            && mControlValue.getDataLen().equals("2")) {
                        toastUtils.showSuccessWithStatus(getString(R.string.music_link_success));
                        rotateImageView(ivPlayBarPlay.isSelected());
                    }//音量-
                    if (mControlValue.getCmd().equals("3")
                            && mControlValue.getData().equals("1")) {
                        volume--;
                        if (volume <= 1) volume = 1;
//                        ToastHelper.showShortMsg(volume + "");
                        return;
                    }//上一首
                    if (mControlValue.getCmd().equals("6")
                            && mControlValue.getData().equals("1")) {
                        tranceXAnimator(true);

                        return;
                    }
                    //播放暂停
                    if (mControlValue.getCmd().equals("4")) {
                        ivPlayBarPlay.setSelected(!ivPlayBarPlay.isSelected());
                        rotateImageView(ivPlayBarPlay.isSelected());
                        return;
                    }
                    //下一首
                    if (mControlValue.getCmd().equals("6")
                            && mControlValue.getData().equals("2")) {
                        tranceXAnimator(false);

                        return;
                    }
                    //音量+
                    if (mControlValue.getCmd().equals("3")
                            && mControlValue.getData().equals("2")) {
                        volume++;
                        if (volume >= 30) volume = 30;
//                        ToastHelper.showShortMsg(volume + "");
                        return;
                    }
                    //开关
                    if (mControlValue.getCmd().equals("1")) {
                        ivOffOn.setSelected(!ivOffOn.isSelected());
                        if (!ivOffOn.isSelected()) {
                            rotateImageView(false);
                            ivPlayBarPlay.setSelected(false);
                        }
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void queryDeviceStatus() {
        DeviceController.getInstance().queryDevicesStatus(this, mQueryDevice, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("c", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("ret") != 0) {
                        return;
                    }
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("devices");
                    jsonObject = jsonArray.getJSONObject(0);
                    DeviceStatus status = GsonUtil.getObject(jsonObject.toString(), DeviceStatus.class);
                    mDeviceStatus.setValue(status.getValue());
                    if(mDeviceStatus.getValue()==null){
                        deviceStatusValue=new DeviceStatusValue();
                        deviceStatusValue.setVolume(0);
                        deviceStatusValue.setCurrNo(0);
                        deviceStatusValue.setPlayState(0);
                        deviceStatusValue.setState("0");
                        mDeviceStatus.setValue(deviceStatusValue);
                    }
                    mDeviceStatus.setOffline(status.getOffline());
                    volume = mDeviceStatus.getValue().getVolume();
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

    private void updateUI() {
        //0表示停止播放、1表示开始播放、2表示暂停播放
        int statePlay = mDeviceStatus.getValue().getPlayState();
        ivPlayBarPlay.setSelected(statePlay == 1);
        rotateImageView(statePlay == 1);
        String stateOFF = mDeviceStatus.getValue().getState();
        ivOffOn.setSelected(stateOFF.equals("1"));
        Log.d("json", "" + ivOffOn.isSelected());


    }

    private void queryAllModesInfo() {
        LinkManageController.getInstance().queryAllMode2CunrrentGateWay(this, null, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
//                toastUtils.dismiss();

                try {
                    int ret = JsonUtil.parseInt(json, "ret");
                    if (ret != 0) {
                        return;
                    }

                    String temp = JsonUtil.parseString(json, "response");
                    if (!StringUtil.isEmpty(temp)) parseJson(temp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    //TODO 模式的配置
    private void parseJson(String Json) {
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        String key = null;
        try {
            JSONObject jsonObject = new JSONObject(Json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                key = iterator.next();
                List<ModeAct> modeActList = JsonUtil.parseDataList(Json, ModeAct.class, key);
                mModelMaps.put(key, modeActList);
            }
            mAdapter.setmListData(mModelMaps, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void rotateImageView(boolean play) {
//        RotateAnimation an = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        an.setInterpolator(new LinearInterpolator());//不停顿
//        an.setRepeatCount(1);//重复次数
//        an.setFillAfter(true);//停在最后
//        an.setDuration(3600);
//        ivAlbum.startAnimation(an);
//        && TextUtils.isEmpty((String) ivAlbum.getTag())
        if (play && (objectAnimator == null || !objectAnimator.isRunning())) {
            objectAnimator = ObjectAnimator.ofFloat(ivAlbum, "rotation", 0, 360);
            objectAnimator.setDuration(1000 * 5);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(-1);
            objectAnimator.start();

        } else if (!play) {
            if (objectAnimator != null)
                objectAnimator.cancel();
        }
    }

    private void tranceXAnimator(boolean leftOut) {
        if (objectAnimatorLeft == null) {
            objectAnimatorLeft = ObjectAnimator.ofFloat(ivAlbum, "translationX", 0, 0);
            objectAnimatorLeft.setDuration(1000);
            objectAnimatorLeft.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    objectAnimatorAlpha();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (leftOut) {
            if (objectAnimator != null)
                objectAnimator.cancel();
            objectAnimatorLeft.setFloatValues(0, -1000);
            objectAnimatorLeft.start();

        } else {
            if (objectAnimator != null)
                objectAnimator.cancel();
            objectAnimatorLeft.setFloatValues(0, 1000);
            objectAnimatorLeft.start();
        }
    }

    private void objectAnimatorAlpha() {
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.music_play);
        anim.setTarget(ivAlbum);
        anim.start();
    }


    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            queryDeviceStatus();
            myHandler.sendEmptyMessageDelayed(0, ConstantDeviceType.DEVICE_QUERY_PERIOY);
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
