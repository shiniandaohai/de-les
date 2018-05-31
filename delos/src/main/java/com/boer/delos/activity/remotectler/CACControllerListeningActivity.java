package com.boer.delos.activity.remotectler;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceResult;
import com.boer.delos.model.RCAirConditionCmdData;
import com.boer.delos.model.RemoteCMatchData;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.view.popupWindow.WindDirecPopUpWindow;
import com.boer.delos.view.popupWindow.WindlevelPopUpWindow;
import com.google.gson.Gson;
import com.triggertrap.seekarc.SeekArc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/7/15.
 */
public class CACControllerListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    public static final String BUNDLE_ACITON_DEVICE_CONTROL_CMD = "cmd";

    private RCAirConditionCmdData mAcdata = new RCAirConditionCmdData();
    private Button switchbtn;
    private Button mAcCool;
    private Button mAcHot;
    private Button mAcFan;
    private Button mAcWindLevel;
    private TextView mAcWindLeveltv;
    private Button mAcAuto;
    private Button mAcWindDir;
    private TextView mAcWindDirtv, mAcTemptv;

    private WindlevelPopUpWindow windlevelPopUpWindow;
    private WindDirecPopUpWindow windDirecPopUpWindow;
    private RemoteCMatchData mRemoteInfo;
    private Device mDevice;
    private int mTempature;
    private View mView;
    private SeekArc mSeekArc;
    private TextView mSeekArcProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_remote_controller_ac, null);
        setContentView(view);

        mSeekArc = (SeekArc) findViewById(R.id.seekArc);
        mSeekArcProgress = (TextView) findViewById(R.id.seekArcProgress);
        //设置默认值
        mSeekArc.setStartAngle(90);
        mSeekArc.setSweepAngle(180);
        mSeekArc.setProgressWidth(20);
        mSeekArc.setArcWidth(20);
        mSeekArc.setMax(100);
        mSeekArc.setProgress(50);

        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                mAcdata.setTemp(mTempature);
                mAcdata.setcTemp(mTempature-16);
                updateView();
                deviceControl();
            }

            @Override
            public void onDisableTouch(SeekArc seekArc) {
                if(mAcdata.getIsOn() != 1){
                    toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
                    return;
                }
                if(mAcdata.getMode() == 4){
                    toastUtils.showErrorWithStatus("自动模式下无法调节温度");
                    return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                mSeekArcProgress.setText(String.valueOf(mTempature));
                mTempature = 19 + progress/10;
                mAcTemptv.setText(String.valueOf(mTempature));
                if(mAcdata.getIsOn() != 1){
                    mAcTemptv.setText("OFF");
                }
            }
        });

       /* Animation translateAnimation= AnimationUtils.loadAnimation(this, R.anim.out_to_up);//加载Xml文件中的动画
        mSeekArc.startAnimation(translateAnimation);*/

        //mSeekArc为整圆，现在需要显示半个圆，为了不改变源码，界面显示的时候，位移半个圆
        WindowManager wm = (WindowManager) CACControllerListeningActivity.this
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mSeekArc,"translationY",width/5);
        animator.setDuration(1);
        animator.start();

        mDevice = ((DeviceRelate) getIntent().getSerializableExtra("device")).getDeviceProp();
        if(mDevice != null && mDevice.getRcAirConditionCmdData() != null) {
            mAcdata = mDevice.getRcAirConditionCmdData();
        }
        if(mDevice != null && mDevice.getRemoteInfo() != null) {
            mRemoteInfo = mDevice.getRemoteInfo();
        }
        initView();
        initData();
        readAirState();
    }

    /**
     * 读取空调状态
     */
    private void readAirState() {
        if(mDevice == null){
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", mDevice.getName());
        map.put("addr", mDevice.getAddr());
        DeviceController.getInstance().readAirState(this, map, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try{
                    DeviceResult result = new Gson().fromJson(Json,
                            DeviceResult.class);
                    if(result.getRet() !=0){
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    mDevice = result.getResponse();
                    if(mDevice.getRcAirConditionCmdData() != null) {
                        mAcdata = mDevice.getRcAirConditionCmdData();
                    }
                    if(mDevice.getRemoteInfo() != null) {
                        mRemoteInfo = mDevice.getRemoteInfo();
                    }
                    initData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    /**
     * 更新视图
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateView() {
        if(mAcdata.getIsOn() == 1) {
            switchbtn.setBackgroundResource(R.drawable.ic_remote_ac_power_on);
            mAcTemptv.setText(String.valueOf(mAcdata.getTemp()));
        }else{
            switchbtn.setBackgroundResource(R.drawable.ic_remote_ac_power_off);
            mAcTemptv.setText("OFF");
        }

        mAcCool.setBackgroundResource(R.drawable.ic_remote_ac_cool_normal);
        mAcHot.setBackgroundResource(R.drawable.ic_remote_ac_hot_normal);
        mAcFan.setBackgroundResource(R.drawable.ic_remote_ac_fan_normal);
        mAcAuto.setBackgroundResource(R.drawable.ic_remote_ac_auto_nromal);

        switch (mAcdata.getMode()){
            //制冷
            case 1:
                mAcCool.setBackgroundResource(R.drawable.ic_remote_ac_cool_press);
                break;
            //制热
            case 2:
                mAcHot.setBackgroundResource(R.drawable.ic_remote_ac_hot_press);
                break;
            //通风
            case 3:
                mAcFan.setBackgroundResource(R.drawable.ic_remote_ac_fan_press);
                break;
            //自动
            case 4:
                mAcAuto.setBackgroundResource(R.drawable.ic_remote_ac_auto_press);
                break;
        }

        //风力
        switch (mAcdata.getWindStr()){
            case 1:
                mAcWindLevel.setBackgroundResource(R.drawable.sel_rc_ac_windlevel_weak);
                mAcWindLeveltv.setText(R.string.remote_controller_ac_weak);
                break;
            case 2:
                mAcWindLevel.setBackgroundResource(R.drawable.sel_rc_ac_windlevel_mid);
                mAcWindLeveltv.setText(R.string.remote_controller_ac_midfan);
                break;
            case 3:
                mAcWindLevel.setBackgroundResource(R.drawable.sel_rc_ac_windlevel_str);
                mAcWindLeveltv.setText(R.string.remote_controller_ac_strong);
                break;
        }

        //风向
        switch (mAcdata.getWindDir()){
            case 1:
                mAcWindDirtv.setText(getString(R.string.remote_controller_ac_shake));
                mAcWindDir.setBackgroundResource(R.drawable.sel_rc_ac_shake);
                break;
            case 2:
                mAcWindDirtv.setText(getString(R.string.remote_controller_ac_horizon));
                mAcWindDir.setBackgroundResource(R.drawable.sel_rc_ac_winddir_horizon);
                break;
            case 3:
                mAcWindDirtv.setText(getString(R.string.remote_controller_ac_vertiror));
                mAcWindDir.setBackgroundResource(R.drawable.sel_rc_ac_winddir_vertiacl);
                break;
        }

        int tempature = mAcdata.getTemp();
        //计算温度的进度条19-29
        mSeekArc.setProgress((tempature-19)*10);
        mSeekArcProgress.setText(tempature+"");
    }

    /*
   conoff ,cmode ,ctemp, cwind ,cwinddir, ckey
   1,开关（2）： 0=开，1=关
   2,运转模式（5）： 0=自动 ，1=制冷， 2=除湿， 3=送风， 4=制热
   3,温度（15）： 16-30度  0=16 。。。。 14=30
           4,风速（4）： 0=自动，1=风速1，2=风速2，3=风速3
   5,风向（5）： 0=自动，1=风向1，2=风向2，3=风向3，4=风向4
   6,键值（5）： 0=开关，1=运转模式，2=温度，3=风量，4=风向
   */
    private void initData() {
        if (mAcdata == null) {
            mAcdata = new RCAirConditionCmdData();
            /*
         * 这里初始化设置个默认值， 后面可以根据实际情况改的。
         * 获取设备属性接口暂时没有， 直接放置默认数据了。
         * UI要根据API设定的值进行显示。
         */

        /* 这个是初始状态 ， 用来显示UI的*/
            mAcdata.setIsOn(2);
            mAcdata.setMode(4);
            mAcdata.setTemp(25);
            mAcdata.setWindDir(1);
            mAcdata.setWindStr(2);
        }

        if(mAcdata.getcTemp() == 0){
            mAcdata.setcTemp(9);
        }
        if(mAcdata.getTemp() == 0){
            mAcdata.setTemp(25);
        }

        updateView();
    }

    private List<Device> devices = new ArrayList<>();

    private void initView() {
        initTopBar(mDevice.getName(), null, true, true);
        findViewById(R.id.rlTitle).setBackgroundColor(Color.parseColor("#128ce3"));
        tvTitle.setTextColor(Color.WHITE);
        ivBack.setImageResource(R.mipmap.ic_nav_back_white);
        boolean favorite = mDevice.getFavorite().equals("1");
        ivRight.setImageResource(!favorite ? R.mipmap.nav_collect_nor_white
                : R.mipmap.nav_red_collect);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = mDevice.getFavorite();
                if (tag.equals("1")) {
                    tag = "0";
                    ivRight.setImageResource(R.mipmap.nav_collect_nor_white);
                } else if (tag.equals("0")) {
                    tag = "1";
                    ivRight.setImageResource(R.mipmap.nav_red_collect);
                }
                //保存状态，并发送后台
                if (mDevice != null) {
                    devices.clear();
                    mDevice.setFavorite(tag);
                    devices.add(mDevice);
                    DeviceUpdateStatus.setCommonDevice(CACControllerListeningActivity.this, devices, toastUtils);
                }
            }
        });



        switchbtn = (Button) findViewById(R.id.bt_ac_switch);
        switchbtn.setOnClickListener(this);
        mAcTemptv = (TextView) findViewById(R.id.tv_ac_set_temp_num1);
        mAcCool = (Button) findViewById(R.id.bt_ac_rc_cool);
        mAcCool.setOnClickListener(this);
        mAcHot = (Button) findViewById(R.id.bt_ac_rc_hot);
        mAcHot.setOnClickListener(this);
        mAcFan = (Button) findViewById(R.id.bt_ac_rc_fan);
        mAcFan.setOnClickListener(this);
        mAcWindLevel = (Button) findViewById(R.id.bt_ac_rc_windlevel);
        mAcWindLevel.setOnClickListener(this);
        mAcWindLeveltv = (TextView) findViewById(R.id.tv_ac_rc_windlevel);
        mAcAuto = (Button) findViewById(R.id.bt_ac_rc_auto);
        mAcAuto.setOnClickListener(this);
        mAcWindDir = (Button) findViewById(R.id.bt_ac_rc_winddir);
        mAcWindDir.setOnClickListener(this);
        mAcWindDirtv = (TextView) findViewById(R.id.tv_ac_rc_winddir);

        findViewById(R.id.bt_ac_rc_sub).setOnClickListener(this);
        findViewById(R.id.bt_ac_rc_add).setOnClickListener(this);
    }

      /*  seekBar = (SeekBar)findViewById(R.id.seekbar_ac_rc_addsub_temp);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTempature = 19 + i/10;
                mAcTemptv.setText(String.valueOf(mTempature));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAcdata.setTemp(mTempature);
                mAcdata.setcTemp(mTempature-16);
                updateView();
                deviceControl();
            }
        });
        seekBar.setMax(100);
        seekBar.setProgress(50);
    }

    /**
     * 模式按钮
     * @param view
     */
    private void modeButtonClick(View view){
        if(mAcdata.getIsOn() != 1){
            toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
            return;
        }
        mAcdata.setcKey(1);
        switch (view.getId()){
            //制冷
            case R.id.bt_ac_rc_cool:
                mAcdata.setMode(1);
                mAcdata.setcMode(1);
                break;
            //制热
            case R.id.bt_ac_rc_hot:
                mAcdata.setMode(2);
                mAcdata.setcMode(4);
                break;
            //通风
            case R.id.bt_ac_rc_fan:
                mAcdata.setMode(3);
                mAcdata.setcMode(3);
                break;
            //自动
            case R.id.bt_ac_rc_auto:
                mAcdata.setMode(4);
                mAcdata.setcMode(0);
                break;
        }

        updateView();
        deviceControl();
    }

    /**
     * 发送控制命令
     */
    private void deviceControl(){
        if(mRemoteInfo == null){
            return;
        }
        //判断如果模式为0,设为自动
        if(mAcdata.getMode() == 0){
            mAcdata.setMode(4);
            mAcdata.setcMode(0);
        }
        if(mAcdata.getWindStr() == 0){
            //弱风
            mAcdata.setWindStr(1);
            mAcdata.setcWind(1);
        }
        if(mAcdata.getWindDir() == 0){
            //摆动
            mAcdata.setWindDir(1);
            mAcdata.setcWinddir(1);
        }
        //计算发送的index
        String keySquency = ""+mRemoteInfo.getM_key_squency();
        int index = -1;
        switch (keySquency){
            case "15000":
                index = mAcdata.getcOnoff() * 7500 + mAcdata.getcMode() * 1500 + mAcdata.getcTemp() * 100
                        + mAcdata.getcWind() * 25 + mAcdata.getcWinddir() * 5 + mAcdata.getcKey() + 1;
                break;
            case "3000":
                index = mAcdata.getcOnoff() * 1500 + mAcdata.getcMode() * 300 + mAcdata.getcTemp() * 20
                        + mAcdata.getcWind() * 5 + mAcdata.getcWinddir() + 1;
                break;
        }
        //如果没有值
        if(index < 0){
            return;
        }
        mRemoteInfo.setIndex(String.valueOf(index));

        Map<String, Object> map = new HashMap<>();
        map.put("value", mRemoteInfo);
        map.put("type", mDevice.getType());
        map.put("addr", mDevice.getAddr());
        map.put("extraCmd","newInfrared");
        map.put("AcData", mAcdata);
        map.put("deviceName", mDevice.getName());
        map.put("areaName", mDevice.getAreaname());
        map.put("roomName", mDevice.getRoomname());

//        String json = new Gson().Object2Json(map);
//        L.e("deviceControl:" + json);
        DeviceController.getInstance().deviceControl(this, (Serializable) map,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                    }

                    @Override
                    public void onFailed(String json) {

                    }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_ac_switch:
                openOrCloseClick();
                break;
            //制冷
            case R.id.bt_ac_rc_cool:
                modeButtonClick(v);
                break;
            //制热
            case R.id.bt_ac_rc_hot:
                modeButtonClick(v);
                break;
            //通风
            case R.id.bt_ac_rc_fan:
                modeButtonClick(v);
                break;
            //自动
            case R.id.bt_ac_rc_auto:
                modeButtonClick(v);
                break;
            //风力
            case R.id.bt_ac_rc_windlevel:
                windLevelClick();
                break;
            //风向
            case R.id.bt_ac_rc_winddir:
                windDirClick();
                break;
            //温度减,控制范围在19-29之间
            case R.id.bt_ac_rc_sub:
                subClick();
                break;
            //温度加
            case R.id.bt_ac_rc_add:
                addClick();
                break;
        }
    }

    /**
     * 温度+事件
     */
    private void addClick() {
        if(mAcdata.getIsOn() != 1){
            toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
            return;
        }
        if(mAcdata.getMode() == 4){
            toastUtils.showErrorWithStatus("自动模式下无法调节温度");
            return;
        }
        mAcdata.setcKey(2);
        int temp = mAcdata.getTemp();
        temp++;
        if(temp > 29) {
            temp = 29;
        }
        mAcdata.setTemp(temp);
        //设备红外发送温度
        mAcdata.setcTemp(temp-16);
        updateView();
        deviceControl();
    }

    /**
     * 温度-事件
     */
    private void subClick() {
        if(mAcdata.getIsOn() != 1){
            toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
            return;
        }
        if(mAcdata.getMode() == 4){
            toastUtils.showErrorWithStatus("自动模式下无法调节温度");
            return;
        }
        mAcdata.setcKey(2);
        int temp = mAcdata.getTemp();
        temp--;
        if(temp < 19) {
            temp = 19;
        }
        mAcdata.setTemp(temp);
        mAcdata.setcTemp(temp-16);
        updateView();
        deviceControl();
    }

    /**
     * 风向点击事件
     */
    private void windDirClick() {
        if(mAcdata.getIsOn() != 1){
            toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
            return;
        }

        windDirecPopUpWindow = new WindDirecPopUpWindow(this,
                new WindDirecPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        mAcdata.setcKey(4);
                        switch (tag) {
                            case 0:
                                mAcdata.setWindDir(1);
                                mAcdata.setcWinddir(1);
                                break;
                            case 1:
                                mAcdata.setWindDir(2);
                                mAcdata.setcWinddir(2);
                                break;
                            case 2:
                                mAcdata.setWindDir(3);
                                mAcdata.setcWinddir(3);
                                break;
                        }
                        updateView();
                        deviceControl();
                    }
                });
        windDirecPopUpWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 风速点击事件
     */
    private void windLevelClick() {
        if(mAcdata.getIsOn() != 1){
            toastUtils.showInfoWithStatus(getResources().getString(R.string.remote_controller_ac_open));
            return;
        }
        windlevelPopUpWindow = new WindlevelPopUpWindow(this, new WindlevelPopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(int tag) {
                mAcdata.setcKey(3);
                if (tag == 0) {
                    mAcdata.setWindStr(1);
                    mAcdata.setcWind(1);
                } else if (tag == 1) {
                    mAcdata.setWindStr(2);
                    mAcdata.setcWind(2);
                } else if (tag == 2) {
                    mAcdata.setWindStr(3);
                    mAcdata.setcWind(3);
                }
                updateView();
                deviceControl();
            }
        });
        windlevelPopUpWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 开或关点击事件
     */
    private void openOrCloseClick() {
        mAcdata.setcKey(0);
        //当前为开,所以参数为关
        if(mAcdata.getIsOn() == 1){
            mAcdata.setIsOn(2);
            mAcdata.setcOnoff(1);
        }
        //当前为关,所以参数为开
        else{
            mAcdata.setIsOn(1);
            mAcdata.setcOnoff(0);
        }

        //更新视图
        updateView();
        //发送命令
        deviceControl();
    }
}
