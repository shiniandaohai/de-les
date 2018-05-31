package com.boer.delos.activity.smartdoorbell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.AlarmMessageInfo;
import com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.CommonEventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.EventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.VisitorReceiveEventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.smartdoorbell.ICVSSUserModule;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.BatteryStateView;
import com.boer.delos.widget.BatteryViewSelf;
import com.boer.delos.widget.DrawableTextView;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.ELog;
import com.eques.icvss.utils.Method;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.boer.delos.R.id.clickVideoTalkInCom;
import static com.boer.delos.activity.smartdoorbell.SmartDoorbellAlarmInfoActivity.MSG_DEL_ALARM;
import static com.boer.delos.activity.smartdoorbell.SmartDoorbellAlarmInfoActivity.MSG_GET_ALARM_LIST;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_PIR_INFO;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_SET_PIR_INFO;
import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_DEL_ALL_VISITOR;
import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_DEL_ONE_VISITOR;
import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_GET_VISITOR_LIST;
import static com.eques.icvss.utils.Method.ATTR_DOORBELL_RINGTONE;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_CAPTURE_NUM;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_FORMAT;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_SENSE_SENSITIVITY;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_SENSE_TIME;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_VOLUME;
import static com.eques.icvss.utils.Method.METHOD_PREVIEW;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SmartDoorbellActivity extends CommonBaseActivity implements ICVSSListener {
    public static final String TAG = "SmartDoorbellActivity";
    private static ICVSSUserInstance icvss;
    private static final String DISTRIBUTE_URL = "thirdparty.ecamzone.cc:8443";
    @Bind(R.id.tvBatteryLevel)
    TextView tvBatteryLevel;
    @Bind(R.id.ivWifiLevel)
    ImageView ivWifiLevel;
    @Bind(R.id.tvAlarmCount)
    TextView tvAlarmCount;
    @Bind(R.id.llConnect)
    LinearLayout llConnect;
    @Bind(R.id.clickPlay)
    ImageView clickPlay;
    @Bind(R.id.clickGangup)
    DrawableTextView clickGangup;
    @Bind(R.id.clickPhoto)
    DrawableTextView clickPhoto;
    @Bind(R.id.clickMute)
    DrawableTextView clickMute;
    @Bind(R.id.clickTalk)
    DrawableTextView clickTalk;
    @Bind(R.id.clickVoiceTalkInCom)
    DrawableTextView clickVoiceTalkInCom;
    @Bind(R.id.surface_view)
    SurfaceView surfaceView;
    @Bind(R.id.surface_view_voice)
    SurfaceView surfaceViewVoice;
    @Bind(R.id.clickAlarm)
    FrameLayout clickAlarm;
    @Bind(R.id.clickVisitor)
    TextView clickVisitor;
    @Bind(R.id.clickPic)
    TextView clickPic;
    @Bind(R.id.clickSetup)
    TextView clickSetup;
    @Bind(R.id.rlMenu)
    RelativeLayout rlMenu;
    @Bind(R.id.llInCom)
    LinearLayout llInCom;
    @Bind(R.id.ivVideoBg)
    ImageView ivVideoBg;
    @Bind(R.id.battery_view)
    BatteryViewSelf battery_view;
    @Bind(R.id.battery_view1)
    BatteryStateView battery_view1;
    @Bind(R.id.llStatusbar)
    LinearLayout llStatusbar;
    @Bind(R.id.clickVideoTalkInCom)
    DrawableTextView mClickVideoTalkInCom;


    private String preferfsUserName = "001207c40173";
    private String preferfsAppkey = "sdk_demo";
    private String preferfsKeyId = "5d91e3b2b7fbb31c";
    private String mUid;
    private String bid;
    private String callId;
    private String callIdInComming;
    private int currVolume;
    private int screenWidthDip;
    private int screenHeightDip;
    private boolean isMuteFlag;
    private AudioManager audioManager;
    private int current;
    private int devType = 0;
    int width = 640;
    int height = 480;
    private String incomingSid;
    private String incomingUid;
    private String incomingBid;
    private int curSurfaceViewIndex;

    private boolean online;

    private DeviceRelate mDeviceRelate;
    @Override
    protected int initLayout() {
        return R.layout.activity_smart_door_bell;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        mDeviceRelate=(DeviceRelate)getIntent().getSerializableExtra("device");
        tlTitleLayout.setTitle(mDeviceRelate.getDeviceProp().getName());
        mDevice = mDeviceRelate.getDeviceProp();
        devices = new ArrayList<>();
        favoriteTag = mDevice.getFavorite();
        if (!TextUtils.isEmpty(favoriteTag))
            tlTitleLayout.setLinearRightImage(!favoriteTag.equals("1") ?
                    R.mipmap.nav_collect_nor : R.mipmap.nav_red_collect);


        registerReceiver();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        currVolume = current;

        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        icvss.equesLogin(this, DISTRIBUTE_URL, preferfsUserName, preferfsAppkey);
//        icvss.equesLogin(this, DISTRIBUTE_URL, Constant.CURRENTHOSTID, preferfsAppkey);

        clickTalk.setOnTouchListener(new MyOnTouchListener());
        clickVoiceTalkInCom.setOnTouchListener(new MyOnTouchListener());

        llConnect.setVisibility(View.GONE);
        rlMenu.setVisibility(View.VISIBLE);
        llStatusbar.setVisibility(View.VISIBLE);
        llInCom.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
        surfaceViewVoice.setVisibility(View.GONE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int arg1,
                                       int arg2, int arg3) {
            }

            public void surfaceCreated(SurfaceHolder holder) {
                if (curSurfaceViewIndex == 1) {
                    callId = icvss.equesOpenCall(mUid, holder.getSurface(), false); //视频 + 语音通话
                } else if (curSurfaceViewIndex == 2) {
                    callIdInComming = icvss.equesOpenCall(incomingUid, holder.getSurface(), false); //视频 + 语音通话
                } else if (curSurfaceViewIndex == 3) {
                    Drawable drawable = null;
                    callIdInComming = icvss.equesOpenCall(incomingUid, surfaceView, drawable, true);//纯语音通话
//                    callIdInComming = icvss.equesOpenCall(incomingUid, holder.getSurface(), true);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        surfaceViewVoice.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int arg1,
                                       int arg2, int arg3) {
            }

            public void surfaceCreated(SurfaceHolder holder) {
                if (curSurfaceViewIndex == 1) {
                    callId = icvss.equesOpenCall(mUid, holder.getSurface(), false); //视频 + 语音通话
                } else if (curSurfaceViewIndex == 2) {
                    callIdInComming = icvss.equesOpenCall(incomingUid, holder.getSurface(), false); //视频 + 语音通话
                } else if (curSurfaceViewIndex == 3) {
                    Drawable drawable = null;
                    callIdInComming = icvss.equesOpenCall(incomingUid, surfaceViewVoice, drawable, true);//纯语音通话
//                    callIdInComming = icvss.equesOpenCall(incomingUid, holder.getSurface(), true);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        setVideoSize(surfaceView);
        setVideoSize(surfaceViewVoice);
        setFourThreeView();
        boolean bo = audioManager.isWiredHeadsetOn();
        if (!bo) {
            openSpeaker();
        }

        EventBus.getDefault().register(this);
    }

    private String favoriteTag;
    private Device mDevice;
    private List<Device> devices;
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

    @Override
    public void onDisconnect(int i) {
        ToastHelper.showShortMsg("智能门铃与服务器断开连接");
        Log.d("onDisconnect", "onPingPong:" + i);
    }

    @Override
    public void onPingPong(int i) {
        Log.d("onPingPong", "onPingPong:" + i);
    }

    @Override
    public void onMeaasgeResponse(final JSONObject json) {
        Log.d("BindDoorbell", "onMeaasgeResponse:" + json);
        String method = json.optString(Method.METHOD);
        if (Method.METHOD_EQUES_SDK_LOGIN.equals(method)) {
            int code = json.optInt(Method.ATTR_ERROR_CODE);
            if (code == 4000) {
                icvss.equesGetDeviceList();
            }
        } else if (method.equals(Method.METHOD_BDYLIST)) {
            JSONArray bdys = json.optJSONArray(Method.METHOD_BDYLIST);
            for(int i=0;i<bdys.length();i++){
                if(mDeviceRelate.getDeviceProp().getAddr().equalsIgnoreCase(bdys.optJSONObject(i).optString("name"))){
                    JSONObject bdyObj = bdys.optJSONObject(i);
                    if (bdyObj != null) {
                        bid = bdyObj.optString(Method.ATTR_BUDDY_BID, null);
                        if (bid != null) {
                            ELog.e(TAG, " bid: ", bid);
                        }
                    }
                    break;
                }
            }
            JSONArray onlines = json.optJSONArray(Method.ATTR_ONLINES);
            for(int i=0;i<onlines.length();i++){
                if(onlines.optJSONObject(i).optString("bid").equalsIgnoreCase(bid)){
                    JSONObject onlinesObj = onlines.optJSONObject(i);
                    if (onlinesObj != null) {
                        online=true;
                        String uid = onlinesObj.optString(Method.ATTR_BUDDY_UID, null);
                        mUid = uid;
                        icvss.equesGetDeviceInfo(uid);
                    }
                    break;
                }
            }
        } else if (method.equals(Method.METHOD_DEVICEINFO_RESULT)) {
            Log.d("BindDoorbell", json.toString());
            if (cmdHanlder != null) {
                cmdHanlder.obtainMessage(CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO, json).sendToTarget();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int battery_level = json.optInt("battery_level");
                    int battery_status = json.optInt("battery_status");
                    int wifi_level = json.optInt("wifi_level");
                    tvBatteryLevel.setText(battery_level + "%");
                    ivWifiLevel.setImageResource(getResources().getIdentifier("ic_door_bell_wifi_" + wifi_level, "drawable", getPackageName()));
//                    battery_view.setVisibility(View.VISIBLE);
//                    battery_view.setPower(battery_level);

                    battery_view1.setVisibility(View.VISIBLE);
                    battery_view1.setPower(battery_level * 1.0f / 100);
                }
            });
        } else if (Method.METHOD_CALL.equals(method)) {
            String uid = json.optString(Method.ATTR_FROM);
            String state = json.optString(Method.ATTR_CALL_STATE);
            String to = json.optString(Method.ATTR_TO);
            String sid = json.optString(Method.ATTR_CALL_SID);
            incomingSid = sid;
            incomingUid = uid;
            if (state.equals("close")) {
                //通话挂断挂断
                onTalkClosed();
            } else if (state.equals("open")) {
                onInComming();
            }
        } else if (METHOD_PREVIEW.equals(method)) { //回调接口,门铃图片
            String inComingCallImgFid = json.optString(Method.ATTR_ALARM_FID);
        } else if (Method.METHOD_ALARM_ALMLIST.equals(method)) {
            ArrayList<AlarmMessageInfo> infos = new ArrayList<AlarmMessageInfo>();

            JSONArray alarmArray = json.optJSONArray("alarms");
            if (alarmArray != null && alarmArray.length() > 0) {
                for (int i = 0; i < alarmArray.length(); i++) {
                    AlarmMessageInfo info = new AlarmMessageInfo();

                    JSONObject jsonObjectTemp = alarmArray.optJSONObject(i);
                    long alarmTime = jsonObjectTemp.optLong("time");
                    String bid = jsonObjectTemp.optString("bid");
                    String aid = jsonObjectTemp.optString("aid");
                    int type = jsonObjectTemp.optInt("type");

                    JSONArray pvidArrayTemp = jsonObjectTemp.optJSONArray("pvid");
                    List<String> pvids = null;
                    if (pvidArrayTemp != null && pvidArrayTemp.length() > 0) {
                        pvids = new ArrayList<String>();
                        for (int j = 0; j < pvidArrayTemp.length(); j++) {
                            String alarmPvid = pvidArrayTemp.optString(j);
                            pvids.add(alarmPvid);
                        }
                    }
                    info.setPvids(pvids);

                    JSONArray fidArrayTemp = jsonObjectTemp.optJSONArray("fid");
                    List<String> fids = null;
                    if (fidArrayTemp != null && fidArrayTemp.length() > 0) {
                        fids = new ArrayList<String>();
                        for (int j = 0; j < fidArrayTemp.length(); j++) {
                            String alarmfid = fidArrayTemp.optString(j);
                            fids.add(alarmfid);
                        }
                    }
                    info.setFids(fids);
                    info.setAlarmTime(alarmTime);
                    info.setBid(bid);
                    info.setAid(aid);
                    info.setType(type);

                    infos.add(info);
                }
            }
            Message alarmList = new Message();
            alarmList.what = MSG_GET_ALARM_LIST;
            alarmList.obj = infos;
            alarmHanlder.sendMessage(alarmList);

        } else if (Method.METHOD_DB_LIGHT_ENABLE_RESULT.equals(method)) {
            int result = json.optInt("result");
            cmdHanlder.obtainMessage(CmdEventBusEntity.CMD_TYPE_DOOR_BELL_LIGHT_SWITCH, result).sendToTarget();
        } else if (Method.METHOD_ALARM_ENABLE_RESULT.equals(method)) {
            int result = json.optInt("result");
            cmdHanlder.obtainMessage(CmdEventBusEntity.CMD_TYPE_DOOR_BELL_PIR_SWITCH, result).sendToTarget();
        } else if (Method.METHOD_ALARM_GET_RESULT.equals(method)) {
            cmdHanlder.obtainMessage(CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_PIR_INFO, json).sendToTarget();
        } else if (Method.METHOD_ALARM_SET_RESULT.equals(method)) {
            cmdHanlder.obtainMessage(CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_PIR_INFO, json).sendToTarget();
        } else if (Method.METHOD_ALARM_RINGLIST.equals(method)) {
            VisitorReceiveEventBusEntity entity = new VisitorReceiveEventBusEntity();
            entity.setEventType(EVENT_TYPE_GET_VISITOR_LIST);
            entity.setEventData(json);
            EventBus.getDefault().post(entity);
        } else if (Method.METHOD_DELETE_RING.equals(method)) {
            VisitorReceiveEventBusEntity entity = new VisitorReceiveEventBusEntity();
            entity.setEventType(EVENT_TYPE_DEL_ONE_VISITOR | EVENT_TYPE_DEL_ALL_VISITOR);
            entity.setEventData(json);
            EventBus.getDefault().post(entity);
        } else if (Method.METHOD_DELETE_ALARM.equals(method)) {
            alarmHanlder.obtainMessage(MSG_DEL_ALARM, json).sendToTarget();
        }
        else if (Method.METHOD_VIDEOPLAY_STATUS_PLAYING.equals(method)) {// 视频第一帧反馈
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivVideoBg.setVisibility(View.GONE);
                }
            });
        }else if (Method.METHOD_DEVST.equals(method)) {// 设备在想状态改变
            final int status=json.optInt("status");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(status==0){
                        ToastHelper.showShortMsg("设备离线");
                        online=false;
                    }
                    else if(status==1){
                        ToastHelper.showShortMsg("设备连接");
                        online=true;
                    }
                }
            });
        }
        else if (Method.METHOD_LOCK_ALARM_NEW.equals(method)) {
            final String alarm=json.optString("alarm");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastHelper.showShortMsg(alarm);
                }
            });
        }
        else if (Method.METHOD_BATTERY_LOW.equals(method)) {
            final int param=json.optInt("param");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastHelper.showShortMsg("当前电量低："+param);
                }
            });
        }
        else if (Method.METHOD_BATTERY_STATUS.equals(method)) {
            final int level=json.optInt("level");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvBatteryLevel.setText(level + "%");
                    battery_view1.setVisibility(View.VISIBLE);
                    battery_view1.setPower(level * 1.0f / 100);
                }
            });
        }
        else if (Method.METHOD_PREVIEW.equals(method)) {//门铃来电图片
            String inComingCallImgFid = json.optString(Method.ATTR_ALARM_FID);
            String previewPicUrl = icvss.equesGetRingPictureUrl(inComingCallImgFid, incomingBid).toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
        else if (Method.METHOD_WIFI_STATUS.equals(method)) {
            final int level = json.optInt("level");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivWifiLevel.setImageResource(getResources().getIdentifier("ic_door_bell_wifi_" + level, "drawable", getPackageName()));
                }
            });
        }
    }


    private void onInComming() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llConnect.setVisibility(View.GONE);
                rlMenu.setVisibility(View.GONE);
                llStatusbar.setVisibility(View.GONE);
                llInCom.setVisibility(View.VISIBLE);
                ivVideoBg.setVisibility(View.VISIBLE);
//                showInComView(3);
            }
        });
    }

    private void onTalkClosed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (curSurfaceViewIndex == 1) {
                    llConnect.setVisibility(View.GONE);
                    rlMenu.setVisibility(View.VISIBLE);
                    llStatusbar.setVisibility(View.VISIBLE);
                    llInCom.setVisibility(View.GONE);

                    surfaceView.setVisibility(View.GONE);
                    clickPlay.setVisibility(View.VISIBLE);
                    ivVideoBg.setVisibility(View.VISIBLE);
                } else if (curSurfaceViewIndex == 2) {
                    llConnect.setVisibility(View.GONE);
                    rlMenu.setVisibility(View.VISIBLE);
                    llStatusbar.setVisibility(View.VISIBLE);
                    llInCom.setVisibility(View.GONE);
                    surfaceView.setVisibility(View.GONE);

                    surfaceViewVoice.setVisibility(View.GONE);
                    ivVideoBg.setVisibility(View.VISIBLE);
                    clickPlay.setVisibility(View.VISIBLE);
                    mClickVideoTalkInCom.setText("视频通话");
                    mClickVideoTalkInCom.setDrawable(R.drawable.ic_camera_video);
                }
                else if(curSurfaceViewIndex == 3){
                    llConnect.setVisibility(View.GONE);
                    rlMenu.setVisibility(View.VISIBLE);
                    llStatusbar.setVisibility(View.VISIBLE);
                    llInCom.setVisibility(View.GONE);
                    surfaceViewVoice.setVisibility(View.GONE);
                }
            }
        });
    }


    private void showInComView(int index) {
        curSurfaceViewIndex = index;
        ivVideoBg.setVisibility(View.VISIBLE);
        clickPlay.setVisibility(View.GONE);
        if(index==1||index==2){
            surfaceViewVoice.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
        }
        else if(index==3){
            surfaceView.setVisibility(View.GONE);
            surfaceViewVoice.setVisibility(View.GONE);
            surfaceViewVoice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        closeSpeaker();
        unRegisterReceiver();

        icvss.equesUserLogOut();
        ICVSSUserModule.getInstance(this).closeIcvss();
        icvss = null;
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.clickPlay, R.id.clickAlarm, R.id.clickVisitor, R.id.clickPic, R.id.clickSetup, R.id.clickGangup,
            R.id.clickPhoto, R.id.clickMute, R.id.clickTalk, clickVideoTalkInCom, R.id.clickVoiceTalkInCom, R.id.clickGangupInCom})
    public void onViewClicked(View view) {
        if(!(view.getId()==R.id.clickPic||view.getId()==R.id.clickGangup||view.getId()==R.id.clickGangupInCom)){
            if(!online){
                ToastHelper.showShortMsg("设备不在线");
                return;
            }
        }
        switch (view.getId()) {
            case R.id.clickPlay:
                if(llInCom.getVisibility()==View.VISIBLE){
                    mClickVideoTalkInCom.setDrawable(R.drawable.speak);
                    mClickVideoTalkInCom.setText("播放");
                    showInComView(2);
                }
                else{
                    if (StringUtils.isBlank(bid)) {
                        ToastHelper.showShortMsg("设备不在线");
                    } else {
                        rlMenu.setVisibility(View.GONE);
                        llStatusbar.setVisibility(View.GONE);
                        llInCom.setVisibility(View.GONE);
                        clickPlay.setVisibility(View.GONE);
                        llConnect.setVisibility(View.VISIBLE);
                        ivVideoBg.setVisibility(View.VISIBLE);
                        llConnect.requestLayout();
                        showInComView(1);
                    }
                }
                break;
            case R.id.clickAlarm:
                if (StringUtils.isBlank(bid)) {
                    ToastHelper.showShortMsg("设备不在线");
                } else {
                    Intent alarmList = new Intent(mContext, SmartDoorbellAlarmInfoActivity.class);
                    alarmList.putExtra("bid", bid);
                    startActivity(alarmList);
                }
                break;
            case R.id.clickVisitor:
                if (StringUtils.isBlank(bid)) {
                    ToastHelper.showShortMsg("设备不在线");
                } else {
                    startActivity(new Intent(mContext, SmartDoorbellVisitorActivity.class));
                }
                break;
            case R.id.clickPic:
                startActivity(new Intent(mContext, SmartDoorbellPicActivity.class));
                break;
            case R.id.clickSetup:
                if (StringUtils.isBlank(mUid)) {
                    ToastHelper.showShortMsg("设备不在线");
                } else {
                    Intent alarmList = new Intent(mContext, SmartDoorbellSetupActivity.class);
                    alarmList.putExtra("uid", mUid);
                    startActivity(alarmList);
                }
                break;
            case R.id.clickGangup:
                llConnect.setVisibility(View.GONE);
                rlMenu.setVisibility(View.VISIBLE);
                llStatusbar.setVisibility(View.VISIBLE);
                llInCom.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                clickPlay.setVisibility(View.VISIBLE);
                ivVideoBg.setVisibility(View.VISIBLE);
                hangUpCall();
                break;
            case R.id.clickPhoto:
                String path = getCamPath();
                boolean isCreateOk = createDirectory(path);
                if (isCreateOk) {
                    path = StringUtils.join(path, "delos" + System.currentTimeMillis(), ".jpg");
                    if (devType == BuddyType.TYPE_CAMERA_C01) {
                        height = 360;
                    }
                    icvss.equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, path);
                    ELog.showToastShort(mContext, "抓拍成功");
                } else {
                    ELog.showToastShort(mContext, "抓拍失败");
                }
                break;
            case R.id.clickMute:
                if (callId != null) {
                    isMuteFlag = !isMuteFlag;
                    setAudioMute();//设置静音
                }
                break;
            case R.id.clickTalk:
                //触摸事件处理
                break;
            case clickVideoTalkInCom:
                if(mClickVideoTalkInCom.getText().toString().equals("视屏通话")){
                    mClickVideoTalkInCom.setDrawable(R.drawable.speak);
                    mClickVideoTalkInCom.setText("播放");
                    showInComView(2);
                }
                else if(mClickVideoTalkInCom.getText().toString().equals("播放")){
                    mClickVideoTalkInCom.setDrawable(R.drawable.ic_mute);
                    mClickVideoTalkInCom.setText("静音");
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    if (callIdInComming != null) {
                        icvss.equesAudioPlayEnable(false, callIdInComming);
                        icvss.equesAudioRecordEnable(false, callIdInComming);
                    }
                }
                else if(mClickVideoTalkInCom.getText().toString().equals("静音")){
                    mClickVideoTalkInCom.setDrawable(R.drawable.speak);
                    mClickVideoTalkInCom.setText("播放");
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume*3/4, 0);
                    callSpeakerSetting(false, R.id.clickVoiceTalkInCom);
                }
                break;
            case R.id.clickVoiceTalkInCom:
//                showInComView(3);
                break;
            case R.id.clickGangupInCom:
                if (incomingSid == null) {
                } else {
                    icvss.equesCloseCall(incomingSid);
                }
                llConnect.setVisibility(View.GONE);
                rlMenu.setVisibility(View.VISIBLE);
                llStatusbar.setVisibility(View.VISIBLE);
                llInCom.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                surfaceViewVoice.setVisibility(View.GONE);
                ivVideoBg.setVisibility(View.VISIBLE);
                clickPlay.setVisibility(View.VISIBLE);

                mClickVideoTalkInCom.setText("视频通话");
                mClickVideoTalkInCom.setDrawable(R.drawable.ic_camera_video);
                break;
        }
    }

    private void setVideoSize(SurfaceView sv) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidthDip = dm.widthPixels;
        screenHeightDip = dm.heightPixels;
        if (screenWidthDip == 1812) {
            screenWidthDip = 1920;
        }
        setAudioMute(); //设置是否静音
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sv.getHolder().setFixedSize(screenWidthDip, screenHeightDip);
        } else {
            getVerticalPixel(sv);
        }
    }

    private void setFourThreeView(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if (screenWidth == 1812) {
            screenWidth = 1920;
        }
        int verticalHeight;
        if (devType == BuddyType.TYPE_CAMERA_C01) {
            verticalHeight = (screenWidth * 9) / 16;
        } else {
            verticalHeight = (screenWidth * 3) / 4;
        }
        FrameLayout.LayoutParams layoutParams=(FrameLayout.LayoutParams)ivVideoBg.getLayoutParams();
        layoutParams.width=screenWidth;
        layoutParams.height=verticalHeight;
        ivVideoBg.setLayoutParams(layoutParams);
    }

    public void closeSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC, currVolume,
                            0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSpeakerSetting(boolean f,int viewId) {
        DrawableTextView tempView=null;
        String tempCallId=null;
        if(viewId==R.id.clickTalk){
            tempView=clickTalk;
            tempCallId=callId;
        }
        else if(viewId==R.id.clickVoiceTalkInCom){
            tempView=clickVoiceTalkInCom;
            tempCallId=callIdInComming;
        }
        if (f) {
            tempView.setText("松开结束");
            if (tempCallId != null) {
                icvss.equesAudioRecordEnable(true, tempCallId);
                icvss.equesAudioPlayEnable(false, tempCallId);
            }
            closeSpeaker();
        } else {
            tempView.setText("按住说话");
            if (tempCallId != null) {
                icvss.equesAudioPlayEnable(true, tempCallId);
                icvss.equesAudioRecordEnable(false, tempCallId);
            }
            openSpeaker();
        }
    }

    private void getVerticalPixel(SurfaceView sv) {
        int verticalHeight;
        if (devType == BuddyType.TYPE_CAMERA_C01) {
            verticalHeight = (screenWidthDip * 9) / 16;
        } else {
            verticalHeight = (screenWidthDip * 3) / 4;
        }
        sv.getHolder().setFixedSize(screenWidthDip, verticalHeight);
    }

    private class MyOnTouchListener implements View.OnTouchListener {

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    callSpeakerSetting(true,v.getId());
                    break;

                case MotionEvent.ACTION_UP:
                    callSpeakerSetting(false,v.getId());
                    break;
            }
            return true;
        }
    }

    public void openSpeaker() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume,
                        0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAudioMute() {
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMuteFlag);
        if (isMuteFlag) {
            if (callId != null) {
                icvss.equesAudioPlayEnable(false, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
            clickMute.setText("静音");
            clickMute.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_mute,0,0);
        } else {
            int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume*3/4, 0);
            callSpeakerSetting(false,R.id.clickTalk);
            clickMute.setText("播放");
            clickMute.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.speak,0,0);
        }
    }

    private void hangUpCall() {
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
    }

    public boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/";
        }
    }

    public String getCamPath() {
        String rootPath = getRootFilePath();
        String camPicPath = rootPath + "delos" + File.separator;
        return camPicPath;
    }

    public boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Method.METHOD_VIDEOPLAY_STATUS_PLAYING);
        registerReceiver(videoCallReceiver, intentFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(videoCallReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        if (null != videoCallReceiver) {
            unregisterReceiver(videoCallReceiver);
            videoCallReceiver = null;
        }
    }

    private BroadcastReceiver videoCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Method.METHOD_VIDEOPLAY_STATUS_PLAYING)) {
                //视频画面开始了,隐藏加载动画
                Toast.makeText(mContext, "收到第一帧视频画面", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onBackPressed() {
        hangUpCall();
        finish();
    }

    protected void onPause() {
        super.onPause();
//        onTalkClosed();
        hangUpCall();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler alarmHanlder;

    public void onEventMainThread(EventBusEntity entity) {
        if (entity.getAction().equals(EventBusEntity.GET_ALARM_LIST)) {
            long startTime = entity.getStartTime();
            long endTime = entity.getEndTime();
            String bidTemp = entity.getBid();
            alarmHanlder = entity.getmHandler();
            // 获取从当前时间到24小时之间的报警记录
            icvss.equesGetAlarmMessageList(bidTemp, startTime, endTime, 10);
        }
    }

    public void onEventMainThread(CommonEventBusEntity entity) {
        if (entity.getEventType() == MSG_DEL_ALARM) {
            alarmHanlder = entity.getmHandler();
            String[] aids = (String[]) entity.getEventData();
            icvss.equesDelAlarmMessage(bid, aids, 0);
        }
    }

    private Handler cmdHanlder;

    public void onEventMainThread(CmdEventBusEntity entity) {
        if (entity.getCmdType() == CMD_TYPE_DOOR_BELL_GET_PIR_INFO) {
            cmdHanlder = entity.getmHandler();
            icvss.equesGetDevicePirInfo(mUid);
        } else if (entity.getCmdType() == CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO) {
            cmdHanlder = entity.getmHandler();
            icvss.equesGetDeviceInfo(mUid);
        } else if (entity.getCmdType() == CMD_TYPE_DOOR_BELL_SET_PIR_INFO) {
            try {
                String json = entity.getCmdStr();
                JSONObject jsonObject = new JSONObject(json);
                icvss.equesSetDevicePirInfo(mUid, jsonObject.optInt(ATTR_SETTINGS_SENSE_TIME),
                        jsonObject.optInt(ATTR_SETTINGS_SENSE_SENSITIVITY),
                        jsonObject.optInt(ATTR_DOORBELL_RINGTONE),
                        jsonObject.optInt(ATTR_SETTINGS_VOLUME),
                        jsonObject.optInt(ATTR_SETTINGS_CAPTURE_NUM),
                        jsonObject.optInt(ATTR_SETTINGS_FORMAT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (entity.getCmdType() == CmdEventBusEntity.CMD_TYPE_DOOR_BELL_LIGHT_SWITCH) {
            cmdHanlder = entity.getmHandler();
            icvss.equesSetDoorbellLight(mUid, entity.getCmd());
        } else if (entity.getCmdType() == CmdEventBusEntity.CMD_TYPE_DOOR_BELL_PIR_SWITCH) {
            cmdHanlder = entity.getmHandler();
            icvss.equesSetPirEnable(mUid, entity.getCmd());
        }
    }

    public void onEventMainThread(VisitorSendEventBusEntity entity) {
        if (entity.getEventType() == EVENT_TYPE_GET_VISITOR_LIST) {
            long[] times = (long[]) entity.getEventData();
            icvss.equesGetRingRecordList(bid, times[0], times[1], 10);
        } else if (entity.getEventType() == EVENT_TYPE_DEL_ONE_VISITOR) {
            String[] fids = (String[]) entity.getEventData();
            icvss.equesDelRingRecord(bid, fids, 0);
        } else if (entity.getEventType() == EVENT_TYPE_DEL_ALL_VISITOR) {
            icvss.equesDelRingRecord(bid, null, 2);
        }
    }
}
