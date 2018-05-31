package com.boer.delos.activity.scene;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.IWirseUdpCMD;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.SendMsgPeriod;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpClientListener;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpMsg;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.XUdp;
import com.boer.delos.activity.scene.scenemanager.SceneManagerActivity;
import com.boer.delos.activity.scene.scenemanager.SceneTimerActivity;
import com.boer.delos.adapter.DeviceHomeAdapter;
import com.boer.delos.adapter.HostIdListPopupAdapter;
import com.boer.delos.adapter.ViewPagerAdapter;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.fragment.DeviceClassifyFuncFragment;
import com.boer.delos.fragment.DeviceClassifyRoomFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.Link;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.model.RoomModeActionResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.ModeRoomPopUpWindow;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:设备首页
 * @CreateDate: 2017/4/1 0001 09:33
 * @Modify:
 * @ModifyDate:
 */


public class DeviceHomeActivity extends CommonBaseActivity
        implements IObjectInterface<List<DeviceRelate>>, ISimpleInterfaceString {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_gateway_name)
    TextView mTvGatewayName;
    @Bind(R.id.ctv_gateway_choice)
    CheckedTextView mCtvGateway;
    @Bind(R.id.iv_add_device)
    ImageView mIvAddDevice;
    @Bind(R.id.iv_device_manager)
    ImageView mIvGatewaySetting;
    @Bind(R.id.ll_all_device)
    LinearLayout mLlAllDevice;
    @Bind(R.id.fl_content)
    FrameLayout mFlContent;

    @Bind(R.id.lv_show_device)
    PullToRefreshListView mPullToRefreshListView;
    @Bind(R.id.vp_device)
    ViewPager mVpDevice;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.ll_classify_device)
    LinearLayout mLlClassifyDevice;
    @Bind(R.id.ll_anchor)
    LinearLayout mLlAnchor;
    @Bind(R.id.tv_group_name)
    TextView mTvGroupName;
    @Bind(R.id.ctv_show)
    CheckedTextView mCtvShow;
    @Bind(R.id.fabtn_choice_mode)
    FloatingActionButton fabtn_choice_mode;


    private ListView mLvShowDevice;
    private List<DeviceRelate> mDeviceList;

    private DeviceHomeAdapter mDeviceAdapter; //所有设备的adapter

    private FragmentManager fragmentManager;
    private DeviceClassifyFuncFragment mFuncFragment; //按功能
    private DeviceClassifyRoomFragment mRoomFragment; //按房间
    private ArrayList<Fragment> fragmentList;
    private List<String> mTitleList;
    private ViewPagerAdapter mVpAdapter;

    private List<Host> mHostLists;
    private ListPopupWindow mListPopupWindow;
    private List<Observer> mListeners;
    private String mName = null;
    private String mRoomId; // roomId

    private Link currentMode;
    private ModeRoomPopUpWindow popUpWindow;
    private List<ModeAct> modeActList;

    private boolean wireOffline;//wire背影音乐离线状态、发送UDP广播来确定
    //    private boolean wireOpen;//wire背影音乐开启状态、发送UDP广播来确定
    private SendMsgPeriod sendMsgPeriod;

    @Override
    protected int initLayout() {
        return R.layout.activity_device_home;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //展示多有设备
        if (!TextUtils.isEmpty(mRoomId)) {
            fabtn_choice_mode.setVisibility(View.VISIBLE);
            queryRoomMode(mRoomId);
        } else {
            fabtn_choice_mode.setVisibility(View.GONE);
        }
        getGatewayInfo();
        queryAllDevice();

    }

    @Override
    protected void initView() {
//        StatusBarUtil.setStatusBarColor(this,R.color.layout_title_bg);
        tlTitleLayout.setTitle(getString(R.string.text_current_gateway));
        tlTitleLayout.setVisibility(View.GONE);
        mListeners = new ArrayList<>();
        List<DeviceRelate> deviceRelates = (List<DeviceRelate>) getIntent().getSerializableExtra("data");
        mName = getIntent().getStringExtra("key");
        mRoomId = getIntent().getStringExtra("roomId");
//        mRoomId = "126";


        if (!TextUtils.isEmpty(mName)) {
            updateUI(mName);

        }

        if (mDeviceList == null) mDeviceList = new ArrayList<>();
        if (deviceRelates != null) {
            mDeviceList.addAll(deviceRelates);
        }
        if (Constant.GATEWAY != null) {
            mTvGatewayName.setText(Constant.GATEWAY.getName());
        }

        mLvShowDevice = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mTitleList = new ArrayList<>();
        mTitleList.add(getString(R.string.setting_classify_functoin));
        mTitleList.add(getString(R.string.setting_classify_rooms));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式

        fragmentList = new ArrayList<>();
        mFuncFragment = DeviceClassifyFuncFragment.newInstance(mDeviceList);
        mRoomFragment = DeviceClassifyRoomFragment.newInstance(mDeviceList);
//
        fragmentList.add(mFuncFragment);
        fragmentList.add(mRoomFragment);

        mVpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, fragmentList, mTitleList);
        mVpDevice.setAdapter(mVpAdapter);
        mTabLayout.setupWithViewPager(mVpDevice);//将TabLayout和ViewPager关联起来。

        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_MODIFY_ROOM_MODE);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }


    @Override
    protected void initData() {

        modeActList = new ArrayList();
        if (TextUtils.isEmpty(mRoomId) && TextUtils.isEmpty(mName)) {
            mDeviceList.clear();
            mDeviceList.addAll(Constant.DEVICE_RELATE);
//            queryAllDevice();
        }
        mDeviceAdapter = new DeviceHomeAdapter(this, mDeviceList, R.layout.item_device_home_show);
        mLvShowDevice.setAdapter(mDeviceAdapter);

//        getGatewayInfo();
//        mPullToRefreshListView.setRefreshing();
        if (sendMsgPeriod == null) sendMsgPeriod = new SendMsgPeriod();
        sendMsgPeriod.start(new UdpClientListener.SimpleUdpClientListener() {
            @Override
            public void onReceive(XUdp client, UdpMsg udpMsg) {
                super.onReceive(client, udpMsg);
                if (!TextUtils.isEmpty(udpMsg.getSourceDataString())
                        && udpMsg.getSourceDataString().contains("BOER")) {
                    wireOffline = true;
                    if (mDeviceAdapter != null) {
                        mDeviceAdapter.setWireOffline(wireOffline);
                        mDeviceAdapter.setDatas(mDeviceList);
                    }
                }
            }

            @Override
            public void onError(XUdp client, String msg, Exception e) {
                super.onError(client, msg, e);
                wireOffline = false;
            }
        }, IWirseUdpCMD.TCPCMD_PALPITANT);
    }

    @Override
    protected void initAction() {

        mRoomFragment.setListener(this);
        mFuncFragment.setListener(this);
        mRoomFragment.setmListenerRoomId(this);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                queryAllDevice();
                getGatewayInfo();
            }
        });

    }

    @OnClick({R.id.iv_back, R.id.ll_anchor, R.id.iv_add_device, R.id.iv_device_manager
            , R.id.ll_all_device, R.id.fabtn_choice_mode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ActivityCustomManager.getAppManager().finishActivity(this);
                break;
            case R.id.ll_anchor:
                //切换主机
                if (mListPopupWindow != null) {
                    mListPopupWindow.dismiss();
                    mListPopupWindow = null;
                } else{
                    mCtvGateway.setChecked(true);
                    listPopupWindow();
                }
                break;
            case R.id.iv_add_device:
                //添加设备
                permisionAccess(1);
                break;
            case R.id.iv_device_manager:
                //设备管理
                permisionAccess(0);
                break;
            case R.id.ll_all_device:
                mRoomId = "";
                mName = "";
                fabtn_choice_mode.setVisibility(View.GONE);
                mTvGroupName.setText(getString(R.string.text_all_device));
                mCtvShow.toggle();

                if (Constant.GATEWAY == null) {
                    getGatewayInfo();
                    return;
                }
                //展示多有设备
                if (mLvShowDevice.getVisibility() == View.VISIBLE) {
                    mLlClassifyDevice.setVisibility(View.VISIBLE);
                    mLvShowDevice.setVisibility(View.GONE);

                } else {
                    mLvShowDevice.setVisibility(View.VISIBLE);
                    queryAllDevice();
                    mLlClassifyDevice.setVisibility(View.GONE);
                }
                break;
            // 房间模式管理

            case R.id.fabtn_choice_mode:
//                if (TextUtils.isEmpty(mRoomId)) {
//                    return;
//                }


                modeActList=initModeActionList(modeActList);
                showHomePageModeSelect();


//                Intent intent = new Intent(this, SceneManagerActivity.class);
//                intent.putExtra("flag", "1");
//                intent.putExtra("roomId", mRoomId);
//                startActivityForResult(intent, 200);
                break;
        }
    }


    /**
     * 处理模式列表,如果数据不足4个,补足4个
     *
     * @param list
     * @return
     */
    private List<ModeAct> initModeActionList(List<ModeAct> list) {
        List<ModeAct> modeActList = new ArrayList<>();
        modeActList.addAll(list);
        List<String> tempSerialNos = new ArrayList<>();
        for (ModeAct tempModeAct : modeActList) {
            String serialNo = tempModeAct.getSerialNo();
            tempSerialNos.add(serialNo);
        }
        for (int i = 0; i < 4; i++) {
            if (tempSerialNos.contains(String.valueOf(i))) {
                continue;
            }
            ModeAct act = new ModeAct();
            act.setRoomId(mRoomId);
            act.setSerialNo((i) + "");
            act.setDeviceList(new ArrayList<ModeDevice>());
            act.setTag("新增");
            modeActList.add(act);
        }
        //对数据做排序处理，使正常显示 add by sunzhibin
        List<ModeAct> tempModeActList = new ArrayList<>();
        tempModeActList.addAll(modeActList);
        for (ModeAct tempMA : modeActList) {
            String temp = tempMA.getSerialNo();
            int index = Integer.valueOf(temp);
            tempModeActList.remove(index);
            tempModeActList.add(index, tempMA);
        }
//        tempModeActList.add(3, tempModeActList.remove(2)); //交换 顺序变为 1、2、4、3
        modeActList.clear();
        modeActList.addAll(tempModeActList);
        return modeActList;
    }


    private void showHomePageModeSelect() {
        if (popUpWindow == null)
            popUpWindow = new ModeRoomPopUpWindow(this, modeActList,
                    new ModeRoomPopUpWindow.ModeSelectListener() {
                        @Override
                        public void result(int position) {
                            ModeAct modeAct = modeActList.get(position);

                            if (!TextUtils.isEmpty(modeAct.getModeId())) {
                                //激活模式
                                activateRoomModel(modeAct.getModeId());
                                return;
                            }
                            Bundle b = new Bundle();
                            b.putSerializable("room", modeAct);
                            b.putInt("type",1);
                            Intent intent2 = new Intent(DeviceHomeActivity.this, SceneTimerActivity.class);
                            intent2.putExtras(b);
                            startActivity(intent2);
                        }

                        @Override
                        public void modelmanager() {
                            Intent intent = new Intent(DeviceHomeActivity.this, SceneManagerActivity.class);
                            intent.putExtra("type",1);
                            intent.putExtra("flag", "1");
                            intent.putExtra("roomId", mRoomId);
                            startActivityForResult(intent, 200);

                        }
                    });
        if (popUpWindow.isShowing()) {
            popUpWindow.dismiss();
            return;
        }
        popUpWindow.refreshData(modeActList);
        popUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);

    }


    private void updateModeView() {
        if (currentMode == null) {
            return;
        }
        if (StringUtil.isEmpty(currentMode.getName())) {
            return;
        }

        fabtn_choice_mode.setImageResource(Constant.modeImageWithModeName(currentMode.getName()));
    }

    //激活房间模式
    private void activateRoomModel(String modeId) {
        if (TextUtils.isEmpty(modeId)) {
            return;
        }
        LinkManageController.getInstance().activate(this, Integer.valueOf(modeId), null,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e(Json);
                    }

                    @Override
                    public void onFailed(String json) {
                    }
                });
    }


    /**
     * 查询所有设备
     */

    public void queryAllDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                json = StringUtil.deviceStateStringReplaceMap(json);
                DeviceRelateResult result = GsonUtil.getObject(json, DeviceRelateResult.class);
                if (result.getRet() != 0) {
                    return;
                }
                if (null == Constant.DEVICE_RELATE) {
                    Constant.DEVICE_RELATE = new ArrayList<>();
                }
                Constant.DEVICE_RELATE = result.getResponse();
                if (Constant.DEVICE_RELATE == null) {
                    Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                }
                mDeviceList.clear();
                if (!TextUtils.isEmpty(mRoomId)) { //房间分类
                    for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
                        Device device = deviceRelate.getDeviceProp();
                        if (!TextUtils.isEmpty(device.getRoomId()) && !TextUtils.isEmpty(mRoomId)) {
                            if (device.getRoomId().equals(mRoomId)) {
                                mDeviceList.add(deviceRelate);
                            }
                        }
                    }
                } else if (!TextUtils.isEmpty(mName)) { // 功能分类
                    ArrayMap<String, List<DeviceRelate>> tempMap = ConstantDeviceType.groupAllDeviceByType(Constant.DEVICE_RELATE);
                    mDeviceList.addAll(tempMap.get(mName));
                } else {
                    mDeviceList.addAll(Constant.DEVICE_RELATE);
                }
//                mDeviceList.addAll(result.getResponse());
                Log.i("gwq", "getname=" + result.getResponse().get(0).getDeviceProp().getRoomname());
                mDeviceAdapter.setDatas(mDeviceList);
                updateUI(mName);
                if (mHandler != null)
                    mHandler.sendEmptyMessage(2);
            }

            @Override
            public void onFailed(String json) {
                if(json.equals("主机不在线")){
                    ToastHelper.showShortMsg("主机不在线");
                    if (Constant.DEVICE_RELATE == null) {
                        Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                    }
                    else{
                        Constant.DEVICE_RELATE.clear();
                    }
                    mDeviceList.clear();
                    if (!TextUtils.isEmpty(mRoomId)) { //房间分类
                        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
                            Device device = deviceRelate.getDeviceProp();
                            if (!TextUtils.isEmpty(device.getRoomId()) && !TextUtils.isEmpty(mRoomId)) {
                                if (device.getRoomId().equals(mRoomId)) {
                                    mDeviceList.add(deviceRelate);
                                }
                            }
                        }
                    } else if (!TextUtils.isEmpty(mName)) { // 功能分类
                        ArrayMap<String, List<DeviceRelate>> tempMap = ConstantDeviceType.groupAllDeviceByType(Constant.DEVICE_RELATE);
                        mDeviceList.addAll(tempMap.get(mName));
                    } else {
                        mDeviceList.addAll(Constant.DEVICE_RELATE);
                    }
                    mDeviceAdapter.setDatas(mDeviceList);
                    updateUI(mName);
                }
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(2, 2 * 1000);
            }
        });
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mVpDevice.getCurrentItem() == 0) {

                    }
                    break;
                case 1:
                    break;
                case 2:

                    if (mPullToRefreshListView != null) {
                        mPullToRefreshListView.onRefreshComplete();
//                        mDeviceAdapter.setDatas(mDeviceList);

                    }
                    break;
            }
        }
    };

    private HostIdListPopupAdapter mHostIdListPopupAdapter;
    private void listPopupWindow() {
        if (Constant.LOGIN_USER == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(this);
        }
        User user = Constant.LOGIN_USER;
        List<Object> listHost = user.getHostId();
        if (mHostLists == null) {
            mHostLists = new ArrayList<>();
        } else {
            mHostLists.clear();
        }
        GatewayController.getInstance().queryAllHost(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                HostResult result = new Gson().fromJson(json, HostResult.class);
                mHostLists = result.getHosts();

                if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                    mListPopupWindow = null;
                }
                mListPopupWindow = new ListPopupWindow(DeviceHomeActivity.this);
                mHostIdListPopupAdapter=null;
                mHostIdListPopupAdapter = new HostIdListPopupAdapter(DeviceHomeActivity.this, mHostLists, android.R.layout.simple_list_item_1);
                mListPopupWindow.setAdapter(mHostIdListPopupAdapter);
                mListPopupWindow.setAnchorView(mLlAnchor);

                if (mListPopupWindow == null) return;
                mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!TextUtils.isEmpty(Constant.CURRENTHOSTID)
                                && mHostLists.get(position).getHostId().equals(Constant.CURRENTHOSTID)) {
                            ToastHelper.showShortMsg("已在此主机");
                            mListPopupWindow.dismiss();
                            return;
                        }
                        Log.i("gwq", "size=" + Constant.gatewayInfos.size());
                        final String isOnline = onlineMap.get(mHostLists.get(position).getHostId());
                        if (isOnline != null || "1".equals(isOnline)) {
                            changeHost(mHostLists.get(position), position);
                        } else {
                            ToastHelper.showShortMsg("该主机不在线！");
                        }
                        mListPopupWindow.dismiss();
                    }
                });

                mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCtvGateway.setChecked(false);
                    }
                });
                mListPopupWindow.show();

                requestWithHosts(mHostLists);
            }

            @Override
            public void onFailed(String json) {
                Log.d("DeviceHomeActivity",json);
            }
        });
       /* try {
            Host host = null;
            for (Object h : listHost) {
                if (h instanceof LinkedHashMap) {
                    host = new Host();
                    host.setName((String) ((LinkedHashMap) h).get("mName"));
                    host.setId((String) ((LinkedHashMap) h).get("id"));
                    host.setHostId((String) ((LinkedHashMap) h).get("hostId"));

                    mHostLists.add(host);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * 取得主机信息
     */
    public void getGatewayInfo() {
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        GatewayController.getInstance().getGatewayProperties(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                  /*  String md5Value = MD5(Json);
                    if (md5Value == null) {
                        return;
                    }
                    if (!StringUtil.isEmpty(Constant.GATEWAY_MD5_VALUE)
                            && Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
                        return;
                    }*/
                    Constant.GATEWAY = result.getResponse();
                 /*   Constant.GATEWAY_MD5_VALUE = md5Value;
*/
                   /* if (Constant.GATEWAY != null) {
                        String mName = Constant.GATEWAY.getName();
                        updateUI(null);
                    }*/
                } catch (Exception e) {
                    L.e("getGatewayInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 切换主机
     *
     * @param host
     */
    private void changeHost(final Host host, final int pos) {
        toastUtils.showProgress(getString(R.string.progress_change_gateway_ing));
        GatewayController.getInstance().gatewayChange(this, host.getHostId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        toastUtils.showSuccessWithStatus(getString(R.string.toast_change_gateway_success));
                        //add by sunzhibin  12-27
                        Constant.IS_CHANGE_HOST = true; //摄像头有用到
                        mTvGatewayName.setText(mHostLists.get(pos).getName());

                        //更新本地常量
                        Constant.GATEWAY = null;
                        Constant.GATEWAY_MD5_VALUE = "";
                        Constant.DEVICE_MD5_VALUE = "";
                        Constant.GLOBALMODE_MD5_VALUE = "";
                        Constant.IS_GATEWAY_ONLINE = false;
                        Constant.DEVICE_RELATE = Collections.emptyList();
                        Constant.CURRENTHOSTID = host.getHostId();
                        SharedPreferencesUtils.saveCurrentHostIdToPreferences(getApplicationContext());
                        if (Constant.gatewayInfos != null && Constant.gatewayInfos.size() > pos) {
                            GatewayInfo info = Constant.gatewayInfos.get(pos);
                            Constant.LOCAL_CONNECTION_IP = info.getIp();
                            getGatewayInfo();
                            queryAllDevice();
                        }
                        //finish();
//                        setResult(RESULT_OK);
//                        startService(new Intent(getApplicationContext(), PollService.class));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastUtils.dismiss();
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils != null) {
                    toastUtils.dismiss();
                }
            }
        });
    }

    private void updateUI(String namea) {
        if (Constant.GATEWAY != null && TextUtils.isEmpty(Constant.GATEWAY.getName())) {
            mTvGatewayName.setText(Constant.GATEWAY.getName());
        }
        if (!TextUtils.isEmpty(namea)) {
            mTvGroupName.setText(namea);
        } else {
            mName = null;
            mTvGroupName.setText(getString(R.string.text_all_device));
        }
        for (Observer observer : mListeners) {
            observer.update(null, null);

        }
    }

    @Override
    public void onClickListenerOK(List<DeviceRelate> list, int pos, String tag) {
        if (mDeviceAdapter != null) {
            mLvShowDevice.setVisibility(View.VISIBLE);
            mLlClassifyDevice.setVisibility(View.GONE);

            if (pos == -1) {
                List<DeviceRelate> deviceRelates = new ArrayList<>();
                for (int i = 0; i < list.size(); i++)
                    deviceRelates.add(list.get(i));
                mDeviceList.clear();
                mDeviceList.addAll(deviceRelates);
            } else {
                mDeviceList.clear();
                mDeviceList.addAll(list);
            }
            mDeviceAdapter.setDatas(mDeviceList);
            mTvGroupName.setText(tag);
            mName = tag;

        }
    }

    public void addListeners(Observer listener) {
        mListeners.add(listener);
    }


    //房间模式
    @Override
    public void clickListener(String tag) {
        mRoomId = tag;
        //TODO 展示按钮
        fabtn_choice_mode.setVisibility(View.VISIBLE);
        queryRoomMode(mRoomId);
    }

    /************************************
     * 房间模式
     ************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            //TODO

        }
    }

    /**
     * 查询房间模式
     */
    private void queryRoomMode(String roomId) {
        RoomController.getInstance().showMode(this, null, roomId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                Log.v("gl", "Json==" + Json);
                try {
                    RoomModeActionResult result = new Gson().fromJson(Json, RoomModeActionResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    modeActList.clear();
                    modeActList.addAll(result.getResponse());
                    addMode2FourMode(modeActList);
                    Loger.d("modeActList.size() " + modeActList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
            }
        });
    }

    private void addMode2FourMode(List<ModeAct> mModeActList) {

//        if (mModeActList == null) {
        List<ModeAct> modeActList = new ArrayList<>();
//        }
        List<String> setialNo = new ArrayList<>();
        for (ModeAct modeAct : mModeActList) {
            setialNo.add(modeAct.getSerialNo());
        }
        for (int i = 0; i < 4; i++) {
            if (setialNo.contains(i + "")) {
                modeActList.add(mModeActList.get(i));

                continue;
            }
            ModeAct modeAct = new ModeAct();
            modeAct.setSerialNo(i + "");
            modeAct.setRoomId(mRoomId);
            switch (i) {
                case 0:
                    modeAct.setName(getString(R.string.model_select_morning) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_morning));
                    break;
                case 1:
                    modeAct.setName(getString(R.string.model_select_sleep) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_sleep));
                    break;
                case 2:
                    modeAct.setName(getString(R.string.model_select_read) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_read));
                    break;
                case 3:
                    modeAct.setName(getString(R.string.model_select_recreation) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_recreation));
                    break;
            }
            modeActList.add(modeAct);

        }
        mModeActList.clear();
        mModeActList.addAll(modeActList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        if (mPullToRefreshListView != null && mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
            mPullToRefreshListView = null;
        }
        if (sendMsgPeriod != null)
            sendMsgPeriod.stopAll();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && (!TextUtils.isEmpty(mName)
//                || !TextUtils.isEmpty(mRoomId))) {
//            mName = "";
//            mRoomId = "";
//            mTvGroupName.setText(getString(R.string.text_all_device));
//            mCtvShow.setChecked(false);
//            mDeviceList.clear();
//            mDeviceList.addAll(Constant.DEVICE_RELATE);
//            mDeviceAdapter.setDatas(mDeviceList);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }


    private void permisionAccess(final int type){
        if(Constant.LOGIN_USER.getName().equals("admin")){
            if(type==0){
                Intent intent = new Intent(DeviceHomeActivity.this, DeviceManagerActivity.class);
                intent.putExtra("Wire", wireOffline);
                startActivity(intent);
            }
            else if(type==1){
                startActivity(new Intent(DeviceHomeActivity.this, AddBatchScanDeviceActivity.class));
            }
            return;
        }
        FamilyManageController.getInstance().queryUserPermission(this, Constant.CURRENTHOSTID, Constant.USERID, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(json);
                    String ret=jsonObject.getString("ret");
                    if(!ret.equals("0")){
                        return;
                    }
                    String permision=jsonObject.getString("permissions");
                    int limitStatus=jsonObject.getInt("limitStatus");
                    String limitTime=jsonObject.getString("limitTime");

                    boolean isCanEnter=false;
                    if(permision.contains("设备管理")){
                        if(limitStatus==1){
                            long ms = Long.parseLong(limitTime) * 1000 - System.currentTimeMillis();
                            if(ms>0){
                                isCanEnter=true;
                            }
                            else{
                                isCanEnter=false;
                            }
                        }
                        else if(limitStatus==0||limitStatus==2){
                            isCanEnter=true;
                        }
                    }
                    else{
                        isCanEnter=false;
                    }
                    if(!isCanEnter){
                        ToastHelper.showShortMsg("请联系管理员获取权限！");
                    }
                    else{
                        if(type==0){
                            Intent intent = new Intent(DeviceHomeActivity.this, DeviceManagerActivity.class);
                            intent.putExtra("Wire", wireOffline);
                            startActivity(intent);
                        }
                        else if(type==1){
                            startActivity(new Intent(DeviceHomeActivity.this, AddBatchScanDeviceActivity.class));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                Log.d("NetGatePermision",json);
            }
        });
    }

    private int COUNT = 0;
    private Map<String, String> onlineMap = new HashMap<>();
    private void requestWithHosts(List<Host> hosts) {
        String hostIp = null;
        onlineMap.clear();
        COUNT = 0;
        for (Host host : hosts) {
            connectTestWithHostId(host.getHostId(), hostIp, false, hosts.size());
        }
    }


    private void connectTestWithHostId(final String hostId, String hostIp, boolean is_locatiion, final int SIZE) {
        L.d("FamilyManageListeningActivity connectTestWithHostId() " + hostId + "----" + hostIp);
        GatewayController.getInstance().getGatewayProperties(this, hostId, hostIp, is_locatiion, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                COUNT++;
                if (COUNT == SIZE) {
                    toastUtils.dismiss();
                }
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    L.d("FamilyManageListeningActivity connectTestWithHostId() onSuccess() ++++++++++++++++++");
                    if (result.getRet() == 0) {
                        onlineMap.put(hostId, "1");

                        mHostIdListPopupAdapter.setOnline(onlineMap);
                        mHostIdListPopupAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                COUNT++;
                if (COUNT == SIZE) {
                    if (toastUtils != null) toastUtils.dismiss();
                }
                L.d("FamilyManageListeningActivity connectTestWithHostId() onFailed() " + Json);

            }
        });
    }

    private LocalBroadcastManager mLocalBroadcastManager;
    public static String ACTION_MODIFY_ROOM_MODE ="com.boer.delos.DeviceHomeActivity.modify.room.mode";

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MODIFY_ROOM_MODE)){
                ModeAct tempModeAct=(ModeAct)intent.getSerializableExtra("modeact");
                for(ModeAct modeAct:modeActList){
                    if((modeAct.getModeId()).equals(tempModeAct.getModeId())){
                        modeAct.setTag(tempModeAct.getTag());
                        break;
                    }
                }
            }
        }
    };

}