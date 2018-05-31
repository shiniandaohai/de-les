package com.boer.delos.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.boer.delos.R;
import com.boer.delos.activity.healthylife.HealthHomeActivity;
import com.boer.delos.activity.healthylife.pressure.BloodPressureActivity;
import com.boer.delos.activity.healthylife.weight.WeigthHomeActivity;
import com.boer.delos.activity.healthylife.wifiAirClean.WifiAirCleanManagerActivity;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.activity.personal.GatewayBindListeningActivity;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.activity.scene.scenemanager.SceneManagerActivity;
import com.boer.delos.activity.weather.WeatherActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.WeatherImageRes;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkResult;
import com.boer.delos.model.Weather;
import com.boer.delos.model.WeatherInfo;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ad.AdController;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.HealthPopupWindow;
import com.boer.delos.view.popupWindow.ModelSelectPopUpWindow;
import com.google.gson.Gson;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.boer.delos.R.id.rl_first_add_gateway;
import static com.boer.delos.utils.sign.MD5Utils.MD5;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends InterfaceFragment
        implements BDLocationListener,
        SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.ll_login)
    LinearLayout llLogin;
    @Bind(R.id.iv_logo)
    TextView mIvLogo;
    @Bind(R.id.iv_temperature)
    ImageView mIvTemperature;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.tv_weather)
    TextView mTvWeather;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_temperature)
    TextView mTvTemperature;

    @Bind(R.id.fl_healthy_manager)
    FrameLayout mFlHealthyManager;
    @Bind(R.id.iv_healthy_1)
    ImageView mIvHealthyWalking;
    @Bind(R.id.tv_health_walking)
    TextView mTvHealthWalking;
    @Bind(R.id.iv_healthy_2)
    ImageView mIvHealthySleeping;
    @Bind(R.id.tv_healthy_sleeping)
    TextView mTvHealthySleeping;
    @Bind(R.id.iv_healthy_3)
    ImageView mIvHealthyHeartRate;
    @Bind(R.id.tv_healthy_heartRate)
    TextView mTvHealthyHeartRate;
    @Bind(R.id.fl_home_device)
    FrameLayout mFlHomeDevice;
    @Bind(R.id.vp_home_device)
    ViewPager mVpHomeDevice;
    RelativeLayout llContent;
    @Bind(R.id.fabtn_choice_mode)
    FloatingActionButton fabtnChoiceMode;
    @Bind(R.id.tv_temp)
    TextView mTvTemp;
    @Bind(R.id.tv_pm25)
    TextView mTvPm25;
    @Bind(R.id.tv_pm25_standard)
    TextView mTvPm25Standard;
    @Bind(R.id.rl_first_add_gateway)
    RelativeLayout mRlFirstAddGateway;
    @Bind(R.id.rl_two_add_device)
    RelativeLayout mRlTwoAddDevice;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;
    @Bind(R.id.tv_host)
    TextView tvHost;
    @Bind(R.id.tvs)
    TextView tvs;
    @Bind(R.id.fl_top)
    View flTop;

    private Link currentMode;
    private ModelSelectPopUpWindow popUpWindow;
    //Baidu定位
    private double mLatitude;
    private double mLongitude;
    private List<String> refreshedList;
    private ISimpleInterfaceString mSimpleListener;
    private List<ISimpleInterfaceString> mListeners;
    private List<Fragment> mFragments = new ArrayList<>();
    private HomePageVpAdapter vpAdapter;
    private CommomDeviceFragment deviceFragment;
    private CommomFunctionFragment functionFragment;
    private CommomRoomFragment roomFragment;
    private String city;
    private String weatherStr;
    private HealthPopupWindow healthPopupWindow;
    private String ip;
    private List<Boolean> mListHealthDatas;

    private String mDeviceMD5 = "";
    private String mGatewayMD5 = "";
    private ToastUtils mToast;
    private LocationClient mLocationClient;
    private List<Link> mLinks;//全局模式

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);


        Log.v("gl", "IS_LOCAL_CONNECTION==" + Constant.IS_LOCAL_CONNECTION);
        llLogin.setVisibility(View.GONE);

        initView();
        intData();

        //启动查询服务
//        getActivity().startService(new Intent(getActivity(), PollService.class));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mSwipeLayout.setRefreshing(true);

        onRefresh();

        mNestedScrollView.smoothScrollBy(0, 0);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
    }

    private void initView() {
        mListeners = new ArrayList<>();
        mToast = new ToastUtils(getActivity());
        startLocation();

        if (Constant.LOGIN_USER != null
                && Constant.LOGIN_USER.getHostId() != null
                && Constant.LOGIN_USER.getHostId().size() != 0) {

            mVpHomeDevice.setVisibility(View.VISIBLE);
            mRlFirstAddGateway.setVisibility(View.GONE);
            mRlTwoAddDevice.setVisibility(View.GONE);
        } else {
            mVpHomeDevice.setVisibility(View.GONE);
            mRlFirstAddGateway.setVisibility(View.VISIBLE);
            mRlTwoAddDevice.setVisibility(View.VISIBLE);
        }


        refreshedList = new ArrayList<>();
        refreshedList.add("0");
        refreshedList.add("0");

        LinearLayout.LayoutParams lps=(LinearLayout.LayoutParams)flTop.getLayoutParams();
        lps.topMargin= ScreenUtils.getStatusHeight(getActivity());
        flTop.setLayoutParams(lps);
    }

    private void intData() {
        getWeatherByMobileIP();
        if (Constant.LOGIN_USER == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getContext());
        }

        if (Constant.LOGIN_USER == null) {
//            mVpHomeDevice.setVisibility(View.GONE);
        } else {
//            mVpHomeDevice.setVisibility(View.VISIBLE);
            roomFragment = new CommomRoomFragment();
            deviceFragment = new CommomDeviceFragment();
            functionFragment = new CommomFunctionFragment();

            mFragments.add(deviceFragment);
            mFragments.add(functionFragment);
            mFragments.add(roomFragment);


            vpAdapter = new HomePageVpAdapter(getChildFragmentManager(), getActivity(), mFragments);
            mVpHomeDevice.setAdapter(vpAdapter);
            mVpHomeDevice.setOffscreenPageLimit(3);

            setFragment();
            mSwipeLayout.setOnRefreshListener(this);

            Calendar calendar = Calendar.getInstance();
            mTvDate.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "日");
//            mSwipeLayout.setRefreshing(true);
////        getDeviceStatusInfo();
//            getGatewayInfo();
//            getDeviceStatusInfo();
//            getWeatherInfo(mLongitude, mLatitude);
//            getIndoorInfo();

            getGlobalMode();
        }

        for (int i = 0; i < 3; i++) {
            if (mListHealthDatas == null) mListHealthDatas = new ArrayList<>();
            mListHealthDatas.add(false);
        }

    }

    @OnClick({R.id.fl_healthy_manager, R.id.fl_home_device, R.id.ll_login,
            R.id.rl_weather_outdoor,R.id.rl_weather_indoor, rl_first_add_gateway, R.id.fabtn_choice_mode,
            R.id.rl_healthy_1, R.id.ll_healthy_2, R.id.ll_healthy_3})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_login:
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//                getActivity().finish();
                break;
            case R.id.fl_healthy_manager:

                startActivity(new Intent(getActivity(), HealthHomeActivity.class));
//                startActivity(new Intent(getActivity(), SkinChartActivity.class));
                break;
            case R.id.fl_home_device:
                if(Constant.LOGIN_USER.getName().equals("admin")){
                    startActivity(new Intent(getActivity(), DeviceHomeActivity.class));
                    break;
                }
                else
                if (Constant.LOGIN_USER.getHostId() == null
                        || Constant.LOGIN_USER.getHostId().size() == 0) {
//                    ToastHelper.showShortMsg(getString(R.string.toast_please_addhost));
                    List<String> hosts = new ArrayList<>();
                    Intent intent = new Intent(getActivity(), GatewayBindListeningActivity.class);
                    intent.putExtra("hosts", (Serializable) hosts);
                    startActivity(intent);
                    break;
                }
//                if(Constant.DEVICE_RELATE.size()==0){
//                    startActivity(new Intent(getActivity(), AddHostActvity.class));
//                }
//                else{
//                    startActivity(new Intent(getActivity(), DeviceHomeActivity.class));
//                }
                startActivity(new Intent(getActivity(), DeviceHomeActivity.class));
                break;
            case R.id.rl_weather_outdoor:
                if (TextUtils.isEmpty(mTvLocation.getText().toString())) {
                    startLocation();
                    return;
                }

                Intent weatherIn = new Intent(getActivity(), WeatherActivity.class);
                weatherIn.putExtra("type",0);
                weatherIn.putExtra("weather", weatherStr);
                startActivity(weatherIn);
                break;
            case R.id.rl_weather_indoor:
                startActivity(new Intent(getActivity(), WeatherActivity.class).putExtra("weather", weatherStr).putExtra("type",1));
                break;
            case R.id.rl_first_add_gateway:
                //加入主机，
                startActivity(new Intent(getActivity(), GatewayBindListeningActivity.class));
                break;
            case R.id.rl_two_add_device:
                //加入主机，添加设备后 viewPager 展示

//                mVpHomeDevice.setVisibility(View.VISIBLE);
                break;
            case R.id.fabtn_choice_mode:

                showHomePageModeSelect();

                break;
            case R.id.rl_healthy_1:
                healthPopupwindow(0);

                break;
            case R.id.ll_healthy_2:
//                healthPopupwindow(1);
                startNewActivity(WifiAirCleanManagerActivity.class);
                break;
            case R.id.ll_healthy_3:
                healthPopupwindow(2);

                break;

        }
    }

    private void healthPopupwindow(int position) {
        // "血压仪":
        // "血糖仪":
        // "体脂称":
        // "尿常规":
        // "皮肤检测":
        if (mListHealthDatas.get(position)) {
            switch (position) {
                case 0:
                    startNewActivity(BloodPressureActivity.class);
                    break;
                case 1:
                    startNewActivity(WifiAirCleanManagerActivity.class);
                    break;
                case 2:
                    startNewActivity(WeigthHomeActivity.class);
                    break;
            }

            return;
        }

        if (healthPopupWindow != null && healthPopupWindow.isShowing()) {
            healthPopupWindow.dismiss();
            healthPopupWindow = null;
        }

        healthPopupWindow = new HealthPopupWindow(getActivity(), R.layout.popup_healthy_home2);

        switch (position) {
            case 0:
                healthPopupWindow.popShow(getString(R.string.blood_pressure_device));
                break;
            case 1:
                healthPopupWindow.popShow(getString(R.string.blood_sugar_device));
                break;
            case 2:
                healthPopupWindow.popShow(getString(R.string.health_weight));
                break;
        }

    }

    //开启定位
    private void startLocation() {
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(this);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setPriority(LocationClientOption.NetWorkFirst); //设置网络优先
        int span = 1000 * 60 * 5;//1分钟
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null) {
            //TODO 定位失败
            return;
        }

       /* MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();*/
        mLatitude = bdLocation.getLatitude();
        mLongitude = bdLocation.getLongitude();
        Constant.latitude = mLatitude;
        Constant.longitude = mLongitude;
        //打印出当前城市
        Log.i("gwq", "latitude=" + mLatitude + "，longi=" + mLongitude);
        Log.i("gwq", "location.getCity()=" + bdLocation.getCity());
        //getWeatherInfo(mLongitude, mLatitude);
        city = bdLocation.getCity();
        if (StringUtil.isEmpty(city))
            city = "无锡";
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mTvLocation != null)
                    mTvLocation.setText(city);
            }
        });
        /*MyHand myHand = new MyHand();
        myHand.sendEmptyMessage(0);*/

        getWeather(city);
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.stop();
    }

    @Override
    public void onRefresh() {
        if (TextUtils.isEmpty(Constant.CURRENTHOSTID)) {
            getCurrentHostId();
        } else {
            getGatewayInfo();
            getDeviceStatusInfo();
            getIndoorInfo();
            getGlobalMode();
        }

        if (TextUtils.isEmpty(mTvLocation.getText().toString())
                || mTvLocation.getText().toString().equals("--")) {

            startLocation();
        } else {
            //getWeatherInfo(Constant.longitude, Constant.latitude);
            getWeather(city);
        }
//        updateFragment();
        setFragment();
        if (mListHealthDatas.contains(false)) {
            queryHealthData();
        }
    }

    class MyHand extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (mTvLocation != null) { // 空指针
                mTvLocation.setText(city);
            }
        }
    }

    //更新设备显示布局
    private void updateFragment() {
        if(Constant.DEVICE_RELATE.size()==0){
            mFragments.clear();
            mFragments.add(functionFragment);
            vpAdapter.notifyDataSetChanged();
        }
        else{
            SharedPreferences sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
            String fragStr = sp.getString("fragmentlist", "");
            List<String> list = GsonUtil.getList(fragStr, String.class);
            if (list == null || list.size() == 0) {
                return;
            }

            mFragments.clear();
            int count = list.size();
            for (int i = 0; i < count; i++) {
                String name = list.get(i);
                if (name.equals(getString(R.string.setting_classify_functoin))) {
                    mFragments.add(functionFragment);
                } else if (name.equals(getString(R.string.setting_classify_devices))) {
                    mFragments.add(deviceFragment);
                } else
                    mFragments.add(roomFragment);
            }
            vpAdapter.notifyDataSetChanged();
            mVpHomeDevice.setOffscreenPageLimit(3);
        }
    }

    static class HomePageVpAdapter extends FragmentStatePagerAdapter {
        private Context mContext;
        private List<Fragment> mFragmentList;

        public HomePageVpAdapter(FragmentManager fm, Context context, List<Fragment> fragmentList) {
            super(fm);
            mContext = context;
            mFragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    /**
     * 取得主机信息
     */
    public void getGatewayInfo() {
        tvHost.setText("");
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            refreshedList.set(0, "1");
            stopRefresh(null, null);
            return;
        }
        GatewayController.getInstance().getGatewayProperties(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    refreshedList.set(0, "1");
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
//                        mToast.showErrorWithStatus(TextUtils.isEmpty(result.getMsg())?getString(R.string.toast_query_data_failure):result.getMsg());
                        stopRefresh(null, null);
                        return;
                    }
                 /*   String md5Value = MD5(Json);
                    if (md5Value == null) {
                        return;
                    }

                    if (!StringUtil.isEmpty(Constant.GATEWAY_MD5_VALUE)
                            && Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
                        return;
                    }*/
                    Constant.GATEWAY = result.getResponse();
                    stopRefresh(null, result.getMd5());
                    tvHost.setText(result.getResponse().getName());
//                    Constant.GATEWAY_MD5_VALUE = md5Value;
                } catch (Exception e) {
                    L.e("getGatewayInfo:" + e);
//                    stopRefresh();
                }
            }

            @Override
            public void onFailed(String json) {
                Log.d("AAA", "getGatewayInfo isVisible " + isVisible + " isVisible() " + isVisible() + " isHidden() " + isHidden());

                if(json.equals("主机不在线")){
                    Constant.GATEWAY = null;
                    tvHost.setText("");
                }

                refreshedList.set(0, "1");
                stopRefresh(null, null);
            }
        });
    }

    private void getDeviceStatusInfo() {
        DeviceController.getInstance().queryDeviceRelateInfo(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    refreshedList.set(1, "1");
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);

                    if(result.getRet()==20005){
                        if (Constant.DEVICE_RELATE == null) {
                            Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                        }
                        else{
                            Constant.DEVICE_RELATE.clear();
                        }
                        if(Constant.DEVICE_RELATE.size()==0){
                            tvs.setText("我的主机");
                            fabtnChoiceMode.setVisibility(View.GONE);
                        }
                        else{
                            tvs.setText("我的主机：");
                            fabtnChoiceMode.setVisibility(View.VISIBLE);
                        }

                        setFragment();
                        ((MainTabActivity) getActivity()).filterdeviceList(Constant.DEVICE_RELATE);
                        return;
                    }

                    if (result.getRet() != 0) {
                        stopRefresh(null, null);
                        return;
                    }
                    List<DeviceRelate> mDeviceRelate = new ArrayList<>();
                    mDeviceRelate.addAll(result.getResponse());
//                    //判断设备信息是否有变更
//                    String md5Value = MD5(Json);
//                    if (!StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
//                            && Constant.DEVICE_MD5_VALUE.equals(md5Value)
//                            && Constant.GATEWAY != null) {
//                        return;
//                    }
                    Constant.DEVICE_RELATE = result.getResponse();
                    if (null == Constant.DEVICE_RELATE) {
                        Constant.DEVICE_RELATE = new ArrayList<>();
                    }

                    if(Constant.DEVICE_RELATE.size()==0){
                        tvs.setText("我的主机");
                        fabtnChoiceMode.setVisibility(View.GONE);
                    }
                    else{
                        tvs.setText("我的主机：");
                        fabtnChoiceMode.setVisibility(View.VISIBLE);
                    }

                    setFragment();
                    getIndoorInfo();
                    if (TextUtils.isEmpty(mDeviceMD5) || !result.getMd5().equals(mDeviceMD5)) {
                        ((MainTabActivity) getActivity()).filterdeviceList(Constant.DEVICE_RELATE);

                    }
                    stopRefresh(result.getMd5(), null);

                    //如果是无外网本地连接
                    if (Constant.IS_LOCAL_CONNECTION && !Constant.IS_INTERNET_CONN) {
//                        L.i("getDeviceStatusInfo alarmList:" + result.getNewAlarmList());
                        //取本地通知
                        List<String> list = result.getNewAlarmList();
                        if (list.size() > 0) {
                            Intent intent = new Intent();
                            intent.putExtra("alarmList", (Serializable) list);
                            intent.setAction(Constant.ACTION_ALARM);
                        }
                    }
                } catch (Exception e) {
                    stopRefresh(null, null);
                }
            }

            @Override
            public void onFailed(String json) {
                Log.d("AAA", "getDeviceStatusInfo isVisible " + isVisible + " isVisible() " + isVisible() + " isHidden() " + isHidden());

                if(json.equals("主机不在线")){
                    ToastHelper.showShortMsg("主机不在线");
                    if (Constant.DEVICE_RELATE == null) {
                        Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                    }
                    else{
                        Constant.DEVICE_RELATE.clear();
                    }

                    if(Constant.DEVICE_RELATE.size()==0){
                        tvs.setText("我的主机");
                        fabtnChoiceMode.setVisibility(View.GONE);
                    }
                    else{
                        tvs.setText("我的主机：");
                        fabtnChoiceMode.setVisibility(View.VISIBLE);
                    }

                    setFragment();
                    ((MainTabActivity) getActivity()).filterdeviceList(Constant.DEVICE_RELATE);
                }

                refreshedList.set(1, "1");
                stopRefresh(null, null);
            }
        });
    }


    private void showHomePageModeSelect() {
        if (popUpWindow == null)
            popUpWindow = new ModelSelectPopUpWindow(getActivity(), Constant.GLOBAL_MODE,
                    new ModelSelectPopUpWindow.ModelSelectListener() {
                        @Override
                        public void result(int position) {
                            switch (position) {
                                case 1:
                                    currentMode = Constant.getTagFromModeName("回家模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 2:
                                    currentMode = Constant.getTagFromModeName("会客模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 3:
                                    currentMode = Constant.getTagFromModeName("离家模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 4:
                                    currentMode = Constant.getTagFromModeName("就餐模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 5:
                                    currentMode = Constant.getTagFromModeName("撤防模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 6:
                                    currentMode = Constant.getTagFromModeName("布防模式配置");
                                    updateModeView();
                                    activateModel();
                                    break;
                                case 7:
                                    permisionAccess();
                                    break;
                            }
                        }
                    });
        if (popUpWindow.isShowing()) {
            popUpWindow.dismiss();
            return;
        }

        popUpWindow.updataUI(Constant.GLOBAL_MODE);
        popUpWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);

    }


    private void updateModeView() {
        if (currentMode == null) {
            return;
        }
        if (StringUtil.isEmpty(currentMode.getName())) {
            return;
        }

        fabtnChoiceMode.setImageResource(Constant.modeImageWithModeName(currentMode.getName()));
    }


    private void activateModel() {
        if (currentMode == null) {
            return;
        }
        LinkManageController.getInstance().activate(getActivity(), currentMode.getModeId(), null,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        BaseResult baseResult = GsonUtil.getObject(Json, BaseResult.class);
                        if (baseResult.getRet() == 0) {
                            mToast.showSuccessWithStatus(currentMode.getTag() + getString(R.string.model_activate));
                        }
                        L.e(Json);

                    }

                    @Override
                    public void onFailed(String json) {
                    }
                });
    }

    /**
     * 获取天气信息
     */
    private void getWeatherInfo(double longitude, double latitude) {
        if (latitude == 0.0 && longitude == 0.0) {
            return;
        }
        DeviceController.getInstance().getWeatherTemperature(longitude, latitude, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    if (StringUtil.isEmpty(Json)) {
                        return;
                    }
                    Weather weather = GsonUtil.getObject(Json, Weather.class);
                    if (weather.getError() != 0) {
                        return;
                    }
                    updateUI(weather);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    //获取天气信息
    private void getWeather(String city) {
        AdController.getInstance().getWeatheInfo(getActivity(), city, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                weatherStr = json;
                Document dom = null;
                try {
                    dom = DocumentHelper.parseText(json);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                Element root = dom.getRootElement();

                mTvTemperature.setText(root.elementText("wendu"));
                Element foreElement = root.element("forecast");
                List<Element> elements1 = foreElement.elements("weather");
                if (elements1 != null && elements1.size() > 0) {
                    Element element = elements1.get(0);
                    Element dayElement = element.element("day");
                    String type = dayElement.elementText("type");
                    mTvWeather.setText(type);
                    mIvTemperature.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                }
//                Element envElement = root.element("environment");
//                mTvTemp.setText("22");
//                mTvPm25.setText(envElement.elementText("pm25"));
//                mTvPm25Standard.setText(envElement.elementText("quality"));

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    public void getWeatherByMobileIP() {
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://ip.chinaz.com/getip.aspx");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String result = readInStream(in);
                    if (!result.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            ip = jsonObject.getString("ip");
                            if (!ip.isEmpty())
                                AdController.getInstance().getWeatherInfo(getActivity(), ip, new RequestResultListener() {
                                    @Override
                                    public void onSuccess(String json) {
                                        JSONObject jo = null;
                                        try {
                                            jo = new JSONObject(json);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        String weather = null;
                                        try {
                                            weather = jo.getString("showapi_res_body");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        WeatherInfo weatherInfo = new Gson().fromJson(weather, WeatherInfo.class);
                                        Log.i("gwq", "v=" + weatherInfo.getTime());
                                    }

                                    @Override
                                    public void onFailed(String json) {
                                        Log.i("gwq", "j=" + json);
                                    }
                                });
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        }.start();

    }

    private String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void updateUI(Weather weather) {
        List<Map<String, String>> mapList = WeatherImageRes.dealWithWeather(weather);
        Map<String, String> map = mapList.get(0);

        mTvTemperature.setText(map.get("temp"));
        mIvTemperature.setImageResource(Integer.valueOf(map.get("url")));
        mTvLocation.setText(map.get("city"));
        mTvWeather.setText(map.get("weather"));
        mTvDate.setText(map.get("date"));
    }

    private void getIndoorInfo() {
        Device device=null;
        for(DeviceRelate deviceRelate:Constant.DEVICE_RELATE){
            if(deviceRelate.getDeviceProp().getType().equals("LaserEgg")){
                device=deviceRelate.getDeviceProp();
                break;
            }
        }
        if(device==null){
            return;
        }
        DeviceController.getInstance().queryDevicesStatus(getActivity(), device, new RequestResultListener() {
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
                    DecimalFormat decimalFormat=new DecimalFormat("0.0");
                    mTvTemp.setText(decimalFormat.format(Double.valueOf(status.getValue().getTemp())));
                    mTvPm25.setText(status.getValue().getAqiPm25()+"");

                    String[] aqiDeses = getResources().getStringArray(R.array.aqi_des);
                    int levelAqi=judgeAQILevel(status.getValue().getAqiPm25());
                    mTvPm25Standard.setText(aqiDeses[levelAqi]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String json) {
                Log.d("json:",json);
            }
        });
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

    /**
     * 获取当前用户主机Id
     */
    private void getCurrentHostId() {
        GatewayController.getInstance().queryUserHost(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

                try {
                    Json = StringUtil.nullReplace(Json);
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String currentHostId = result.getCurrentHostId();
//                   if (StringUtil.isEmpty(currentHostId)
//                            && Constant.CURRENTHOSTID.equals(currentHostId)) {
//                        return;
//                    }
                    //保存当前主机
                    Constant.CURRENTHOSTID = currentHostId;
                    Constant.USER_HOSTS = result.getHosts();//应该对他赋值一次
                    Constant.LOGIN_USER.getHostId().clear();
                    Constant.LOGIN_USER.getHostId().addAll(Constant.USER_HOSTS == null
                            ? new ArrayList<>() : Constant.USER_HOSTS);

                    initView();
                    if (TextUtils.isEmpty(Constant.CURRENTHOSTID)) {
                        tvHost.setText("");
                        Constant.DEVICE_RELATE = Collections.emptyList();
                        if(Constant.DEVICE_RELATE.size()==0){
                            tvs.setText("我的主机");
                            fabtnChoiceMode.setVisibility(View.GONE);
                        }
                        else{
                            tvs.setText("我的主机：");
                            fabtnChoiceMode.setVisibility(View.VISIBLE);
                        }
                        setFragment();
//                        stopRefresh("null","null");
                        mSwipeLayout.setRefreshing(false);
//                        ToastHelper.showShortMsg(getString(R.string.toast_please_addhost));
                        return;
                    }
                    getGatewayInfo();
                    getDeviceStatusInfo();
                    getGlobalMode();
                } catch (Exception e) {
                    L.e("getCurrentHostId:" + e);
                    mSwipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailed(String json) {
                L.e("getCurrentHostId:" + json);
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    private void stopRefresh(String deviceMD5, String gatewayMD5) {
        try {
            if (!refreshedList.contains("0")) {

                if (mSwipeLayout != null && mSwipeLayout.isRefreshing()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeLayout.setRefreshing(false);
                        }
                    });
                }
                if (TextUtils.isEmpty(deviceMD5) && TextUtils.isEmpty(gatewayMD5)) {
                    updateFragmentData(false);
                    return;
                }
//                if (!TextUtils.isEmpty(deviceMD5)
//                        && !TextUtils.isEmpty(mDeviceMD5)
//                        && mDeviceMD5.equals(deviceMD5)) {
//                    return;
//                }
//                if (!TextUtils.isEmpty(gatewayMD5)
//                        && !TextUtils.isEmpty(mGatewayMD5)
//                        && mDeviceMD5.equals(deviceMD5)) {
//                    return;
//                }
//            if (TextUtils.isEmpty(deviceMD5)
//                    && !TextUtils.isEmpty(mGatewayMD5)
//                    && !TextUtils.isEmpty(gatewayMD5)
//                    && mGatewayMD5.equals(gatewayMD5)) {
//                mGatewayMD5 = gatewayMD5;
//                return;
//            }
//            if (TextUtils.isEmpty(gatewayMD5)
//                    && !TextUtils.isEmpty(deviceMD5)
//                    && !TextUtils.isEmpty(mDeviceMD5)
//                    && mDeviceMD5.equals(deviceMD5)) {
//                mDeviceMD5 = deviceMD5;
//                return;
//            }
                //更新Fragment
                updateFragmentData(true);
                if (!TextUtils.isEmpty(gatewayMD5)) {
                    mGatewayMD5 = gatewayMD5;
                }
                if (!TextUtils.isEmpty(deviceMD5)) {
                    mDeviceMD5 = deviceMD5;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void addSimpleListener(ISimpleInterfaceString simpleListener) {
        if (simpleListener == null)
            throw new NullPointerException();
        if (!mListeners.contains(simpleListener)) {
            mListeners.add(simpleListener);
        }
    }

    public synchronized void deleteObserver(ISimpleInterfaceString o) {
        mListeners.remove(o);
    }


    public void getGlobalMode() {
        LinkManageController.getInstance().requestGlobalModes(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    LinkResult result = new Gson().fromJson(Json, LinkResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5(Json);
                    if (Constant.GLOBALMODE_MD5_VALUE.equals(md5Value)) {
                        return;
                    }
//                    L.i("Global mode changed");
                    Constant.GLOBAL_MODE = result.getResponse();
                    if (Constant.GLOBAL_MODE == null) {
                        Constant.GLOBAL_MODE = new ArrayList<Link>();
                    }
                    if (!BaseApplication.isForeground) {
                        return;
                    }
                    Loger.d("getGlobalMode()");

                    Constant.GLOBALMODE_MD5_VALUE = md5Value;
                } catch (Exception e) {
                    L.e("getGlobalMode:" + e);
                }
            }

            @Override
            public void onFailed(String Json) {
                L.e("getGlobalMode:" + Json);
            }
        });
    }

    /**************************查询健康数据，如果没有弹出pop选择************************************/
    /**
     * 查询所有数据
     */
    private void queryHealthData() {
        long fromTime = TimeUtil.getCurrentstamp();

        HealthController.getInstance().queryRecentHealth(getActivity(), fromTime + "", "0", "1",
                Constant.USERID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        Loger.d(json);
                        /**
                         * TODO 皮肤
                         */
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getInt("ret") != 0) {
                                return;
                            }
                            object = object.getJSONObject("response");
                            JSONObject jsonObject = null;
                            JSONArray jsonArray = null;
                            try {
                                //血压
                                jsonObject = object.getJSONObject("blood_pressure");
                                jsonArray = jsonObject.getJSONArray("data");
//                        PressureResult.PressureBean pressureBean = GsonUtil.getObject(jsonArray.getString(0), PressureResult.PressureBean.class);
                                if (!jsonArray.isNull(0)) {
                                    mListHealthDatas.set(0, true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                // 血糖
                                jsonObject = object.getJSONObject("blood_glucose");
                                jsonArray = jsonObject.getJSONArray("data");
//                        SugarResult.SugarBean sugarBean = GsonUtil.getObject(jsonArray.getString(0), SugarResult.SugarBean.class);
                                if (!jsonArray.isNull(0)) {
                                    mListHealthDatas.set(1, true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                // 尿检
                                jsonObject = object.getJSONObject("urine");
                                jsonArray = jsonObject.getJSONArray("data");
//                        UrineResult.UrineBean urineBean = GsonUtil.getObject(jsonArray.getString(0), UrineResult.UrineBean.class);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {

                                // 体重
                                jsonObject = object.getJSONObject("body_weight");
                                jsonArray = jsonObject.getJSONArray("data");
//                        WeightBean weightBean = GsonUtil.getObject(jsonArray.getString(0), WeightBean.class);
                                if (!jsonArray.isNull(0)) {
                                    mListHealthDatas.set(2, true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                //TODO
                                //皮肤

                                jsonObject = object.getJSONObject("skin");
                                jsonArray = jsonObject.getJSONArray("data");
//                        SkinArea skinArea = GsonUtil.getObject(jsonArray.getString(0), SkinArea.class);
                                if (!jsonArray.isNull(0)) {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d(json);
                    }
                });

    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        Bundle bundle = new Bundle();
        if (Constant.LOGIN_USER == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getActivity());
        }
        bundle.putSerializable("user", Constant.LOGIN_USER);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 是否通知刷新数据
     *
     * @param isTrue
     */
    private void updateFragmentData(boolean isTrue) {
        for (ISimpleInterfaceString interf : mListeners) {

            if (interf != null) {
                interf.clickListener(null);
            }
        }
    }

    private void permisionAccess(){
        if(Constant.LOGIN_USER.getName().equals("admin")){
            Intent intent = new Intent(getActivity(), SceneManagerActivity.class);
            intent.putExtra("mFlag", "0");
            startActivity(intent);
            return;
        }
        FamilyManageController.getInstance().queryUserPermission(getActivity(), Constant.CURRENTHOSTID, Constant.USERID, new RequestResultListener() {
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
                    if(permision.contains("场景管理")){
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
                        Intent intent = new Intent(getActivity(), SceneManagerActivity.class);
                        intent.putExtra("mFlag", "0");
                        startActivity(intent);
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

    private LocalBroadcastManager mLocalBroadcastManager;
    public static String ACTION_DELETE_HOST ="com.boer.delos.MainFragment.delete.host";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_DELETE_HOST);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_DELETE_HOST)){
                onRefresh();
            }
        }
    };

    private void setFragment(){
        if(Constant.DEVICE_RELATE.size()==0){
            mFragments.clear();
            mFragments.add(functionFragment);
            vpAdapter.notifyDataSetChanged();
        }
        else{
//            mFragments.clear();
//            mFragments.add(deviceFragment);
//            mFragments.add(functionFragment);
//            mFragments.add(roomFragment);
//            vpAdapter.notifyDataSetChanged();
//            mVpHomeDevice.setOffscreenPageLimit(3);



            SharedPreferences sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
            String fragStr = sp.getString("fragmentlist", "");
            List<String> list = GsonUtil.getList(fragStr, String.class);
            if (list == null || list.size() == 0) {
                mFragments.clear();
                mFragments.add(deviceFragment);
                mFragments.add(functionFragment);
                mFragments.add(roomFragment);
            }
            else{
                mFragments.clear();
                int count = list.size();
                for (int i = 0; i < count; i++) {
                    String name = list.get(i);
                    if (name.equals(getString(R.string.setting_classify_functoin))) {
                        mFragments.add(functionFragment);
                    } else if (name.equals(getString(R.string.setting_classify_devices))) {
                        mFragments.add(deviceFragment);
                    } else
                        mFragments.add(roomFragment);
                }
            }
            vpAdapter.notifyDataSetChanged();
            mVpHomeDevice.setOffscreenPageLimit(3);
        }
    }

}
