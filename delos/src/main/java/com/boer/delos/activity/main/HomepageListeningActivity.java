package com.boer.delos.activity.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.boer.delos.R;
import com.boer.delos.activity.camera.CameraListActivity;
import com.boer.delos.activity.camera.QRCodeListeningActivity;
import com.boer.delos.activity.healthylife.HealthLifeListeningActivity;
import com.boer.delos.activity.link.LinkManageListeningActivity;
import com.boer.delos.activity.main.adv.SlidePagerListeningActivity;
import com.boer.delos.activity.greenlive.GreenLiveListeningActivity;
import com.boer.delos.activity.personal.PersonalCenterActivity;
import com.boer.delos.activity.scene.AddBatchScanDeviceActivity;
import com.boer.delos.activity.scene.ComfortableLifeListeningActivity;
import com.boer.delos.activity.scene.DeviceManageListeningActivity;
import com.boer.delos.activity.scene.SceneDisplayListeningActivity;
import com.boer.delos.activity.scene.SceneManageListeningActivity;
import com.boer.delos.activity.settings.SystemSettingsListeningActivity;
import com.boer.delos.adapter.ComfortLiveAdapter;
import com.boer.delos.adapter.ExpressionPagerAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Advertisement;
import com.boer.delos.model.AdvertisementResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.Link;
import com.boer.delos.model.Room;
import com.boer.delos.model.User;
import com.boer.delos.model.Weather;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ad.AdController;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.CommonMethod;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.updateapp.UpdateInfo;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.MySlidingMenu;
import com.boer.delos.view.popupWindow.FamilyApplyPopUpWindow;
import com.boer.delos.view.popupWindow.ModelSelectPopUpWindow;
import com.boer.delos.widget.MyFloatRelativeLayout;
import com.boer.delos.widget.MyGridView;
import com.boer.delos.widget.NetworkImageHolderView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.linphone.squirrel.squirrelCallImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.semtec.www.nebular.SIPService;

/**
 * @author PengJiYang
 * @Description: 首页
 * create at 2016/3/10 11:53
 */
public class HomepageListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private static com.tutk.IOTC.IRegisterIOTCListener IRegisterIOTCListener;
    public static final int CAMERA_MAX_LIMITS = 6;//最多个数限制
    public static final boolean SupportOnDropbox = true;

    private PercentLinearLayout llSideSlipComfortLive;
    private PercentLinearLayout llSideSlipSecurity;
    private PercentLinearLayout llSideSlipHealthLive;
    private PercentLinearLayout llSideSlipGreenLive;
    private PercentLinearLayout llSideSlipHomepage;
    private PercentLinearLayout llSideSlipAreaManage;
    private PercentLinearLayout llSideSlipLinkageManage;
    private PercentLinearLayout llSideSlipDeviceManage;
    private PercentLinearLayout llSideSlipAreaSystemSettings;
    private PercentLinearLayout llSideSlipPersonalCenter;
    private TextView tvHomepageBloodGlucose;
    private TextView tvHomepageBloodPressure;
    private TextView tvHomepageWeight;
    private TextView tvHomepageCurrentPower;
    private TextView tvHomepageElectricityDegree;
    private TextView tvName;
//    private LinearLayout llHomepageModelSelect;

    private PercentLinearLayout llHomepageModelSelect;
    private PercentLinearLayout llHomepageHomeSecurity;
    private PercentRelativeLayout rlHomepagePushMessage;
    private PercentLinearLayout llHomepageIntelligentLock;
    private PercentLinearLayout pllHealth;
    private PercentLinearLayout pllGreenLive;
    private PercentLinearLayout llApplyAnnotation;
    private ImageView ivModelSelect;
    private TextView tvModelSelect;
    private ViewPager viewPager;
    private ConvenientBanner vPager;

    private TextView tvApplyMessage;
    private TextView tvApplyCancel;
    private TextView tvApplyAgain;

    private TextView textViewHomeOrQR;//侧边栏显示"首页"或"二维码"
    private ImageView imageViewHomeOrQR;//侧边栏显示"首页"或"二维码"
    private FrameLayout flGatewayHint;

    private ImageView ivLock;
    //天气和指纹解锁模块
    private ImageView iv_fingerprint_lock, iv_fingerprint_lock_dot, iv_weather_pic;
    private TextView tv_weather_city, tv_weather_info, tv_weather_C, tv_weather_date;

    private MySlidingMenu smHomePage;
    private BarChart bcGreen;
    private List<Room> list = new ArrayList<>();
    private List<Room> datas = new ArrayList<>();
    private User user;
    private List<String> pics = new ArrayList<>();
    //    private List<Link> modeList = new ArrayList<>();
    //门锁
    private DeviceRelate lockDeviceRelate;
    //门锁是否开着
    private Boolean isLockOpen = Boolean.FALSE;
    //SIP是否已经登录
    private Boolean isSipLogin = Boolean.FALSE;
    private squirrelCallImpl squirrelCall = null;
    //根据获取数据中是否含有门禁，决定侧边栏"首页"是否变成"二维码"
    private boolean isNeedQRCode = false;
    //门禁的SIP值
    public static String SIP = "";
    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private ProgressDialog pBar;
    //全局模式变更
    private BroadcastReceiver mGlobalModeReceiver;
    private Link currentMode;

    //百度定位
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    //测量空间用
    private final static boolean HASE_MEASURESD[] = new boolean[1];
    private final static int WH[] = new int[2];
    private static boolean isTablet; //是否是平板
    private RelativeLayout rlHomepageIntelligentLock, rlHomepageWeatherInfo;
    private TextView tv_floatButton_mode;  //横平时，可拖动的按钮上的文字和图片
    private ImageView img_floatButton_mode;
    //模式选择按钮
    MyFloatRelativeLayout myButtonBackMap;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private View mFloatLayout;
    private ModelSelectPopUpWindow popUpWindow; //模式设置PopWindow
    private static boolean isLoading = true; // 登录要加载一次，其他时间不提示
    private boolean changeHost = false; // 切换主机

    //舒适生活
//    private ComfortLiveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d("HomepageListeningActivity onCreate()");
        view = LayoutInflater.from(this).inflate(R.layout.activity_homepage, null);
        setContentView(view);
        if (isScreenLand() || isTablet(this)) {
            initTabletView(this);
        }
//        squirrelCall = (SquirrelCallImpl) getApplication();
        squirrelCall = squirrelCallImpl.getInstance();
        squirrelCall.squirrelSetGlobalVideo(0, 1);  //Disable local video, enable remote video

        HASE_MEASURESD[0] = false;//是否测量控件

//        adapter = new ComfortLiveAdapter(this);
        initView();
        initListener();
//        IRegisterIOTCListener = this;
        setGreenBar();

        initData();

        //更新当前模式
        updateMode();
        //APP检测自動更新
//        new UpdateApp(this);

        registerReceiver();

        isLoading = true;
    }

    private void registerReceiver() {

        //全局模式更新
        mGlobalModeReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateMode();
            }
        };
        registerReceiver(mGlobalModeReceiver, new IntentFilter(Constant.ACTION_GLOBAL_MODE_UPDATE));

        //开启定位
        //  startService(new Intent(this, LocationSvc.class));

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeHost = getIntent().getBooleanExtra("CHANGE_HOST", false); // add by sunzhibin 2016/12/7
        vPager.startTurning(5000);
        smHomePage.closeMenu();
        if (popUpWindow != null && popUpWindow.isShowing()) {
            popUpWindow.dismiss();
        }
        initProgressBar();
    }

    /**
     * 判断是否是平板
     *
     * @param context
     * @return
     */
    private boolean isTablet(Context context) {
        isTablet = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//        if (isTablet) {
//            initTabletView(context);
//
//        }
        return isTablet;
    }

    /**
     * 判斷是否是橫屏
     *
     * @return
     */
    private boolean isScreenLand() {

        Configuration cfg = getResources().getConfiguration();

        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return false;
        }
        return false;
    }

    /**
     * 初始化平板的数据
     *
     * @param context
     */
    private void initTabletView(Context context) {
//        if (isTablet(context)) {
             /*
         iv_fingerprint_lock,iv_fingerprint_lock_dot;
           tv_weather_city,tv_weather_info,iv_weather_pic,tv_weather_C,tv_weather_date;
         */
        iv_fingerprint_lock = (ImageView) findViewById(R.id.iv_fingerprint_lock);
        iv_fingerprint_lock_dot = (ImageView) findViewById(R.id.iv_fingerprint_lock_dot);
        iv_weather_pic = (ImageView) findViewById(R.id.iv_weather_pic);
        tv_weather_city = (TextView) findViewById(R.id.tv_weather_city);
        tv_weather_info = (TextView) findViewById(R.id.tv_weather_info);
        tv_weather_C = (TextView) findViewById(R.id.tv_weather);
        tv_weather_date = (TextView) findViewById(R.id.tv_weather_date);

        rlHomepageIntelligentLock = (RelativeLayout) findViewById(R.id.rlHomepageIntelligentLock);
        rlHomepageWeatherInfo = (RelativeLayout) findViewById(R.id.rlHomepageWeatherInfo);


        createFloatView();

    }

    /**
     * 获取天气信息
     */
    private void getWeatherInfo() {
        if (Constant.latitude == 0.0 && Constant.longitude == 0.0) {
            return;
        }
        DeviceController.getInstance().getWeatherTemperature(new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    if (StringUtil.isEmpty(Json)) {
                        return;
                    }

                    if (null == tv_weather_C) {
                        return;
                    }
                    Weather weather = new Gson().fromJson(Json, Weather.class);
                    if (weather.getError() != 0) {
                        return;
                    }
                    final String city = weather.getResults().get(0).getCurrentCity(); //無錫
                    String picPath = weather.getResults().get(0).getWeather_data().get(0).getDayPictureUrl();
//                weather.getResults().get(0).getWeather_data().get(0).getNightPictureUrl();
                    String dateW = weather.getResults().get(0).getWeather_data().get(0).getDate();//周四 10月20日 （實時：20C）
                    final String weatherInfo = weather.getResults().get(0).getWeather_data().get(0).getWeather(); //小雨
                    final String tempearture = weather.getResults().get(0).getWeather_data().get(0).getTemperature(); //22 ~ 19 C
                    String dateYMD = weather.getDate(); //2016-12-12

                    String[] dateYMDs = dateYMD.split("-");
                    String[] dateWs = dateW.split(" ");

                    final String date = dateYMDs[0] + "年" + dateWs[1] + " 星期" + dateWs[0].substring(1);//日期
//                ImageLoader.getInstance().displayImage( user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
                    ImageLoader.getInstance().displayImage(picPath, iv_weather_pic, BaseApplication.getInstance().displayImageOptions);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_weather_C.setText(tempearture);
                            tv_weather_city.setText(city);
                            tv_weather_date.setText(date);
                            tv_weather_info.setText(weatherInfo);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mGlobalModeReceiver);
//        unregisterComponentCallbacks();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        isLoading = true;
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    /**
     * 更新模式
     */
    private void updateMode() {
        if (Constant.GLOBAL_MODE == null) {
            return;
        }
        List<Link> modes = Constant.GLOBAL_MODE;
        for (Link link : modes) {
            if (link.getCurrent()) {
                currentMode = link;
                break;
            }
        }
        if (currentMode == null) {
            return;
        }
        //更新模式图片和名称
        updateModeView();
    }

    /**
     * 更新模式图片和名称
     */
    private void updateModeView() {
        if (currentMode == null) {
            return;
        }
        if (StringUtil.isEmpty(currentMode.getName())) {
            return;
        }

        ivModelSelect.setImageResource(Constant.modeImageWithModeName(currentMode.getName()));
        String modeName = currentMode.getTag() != null ? currentMode.getTag() : currentMode.getName();
        modeName = modeName.replace("配置", "");
        tvModelSelect.setText(modeName);

        if (null == tv_floatButton_mode || null == img_floatButton_mode) {
            return;
        }

        tv_floatButton_mode.setText(modeName);
        img_floatButton_mode.setImageResource(Constant.modeImageWithModeName(currentMode.getName()));

    }

    private void initData() {
        user = Constant.LOGIN_USER;
        getAdInfo();
//        setGatewayInfo();
        loginSipServer();
        if (user != null) {
            if (user.getName() != null) {
                this.tvName.setText(user.getName());
            }
        }
    }

    public static IRegisterIOTCListener getMultiViewActivityIRegisterIOTCListener() {
        return IRegisterIOTCListener;
    }
//    private void setHintManage(String msg) {
//        if ("主机不在线".equals(msg)) {
//            this.flGatewayHint.setVisibility(View.VISIBLE);
//        } else {
//            this.flGatewayHint.setVisibility(View.GONE);
//        }
//    }


    @Override
    protected void gatewayUpdate() {
        super.gatewayUpdate();
//        setGatewayInfo();
        initProgressBar();
    }

    @Override
    protected void deviceStatusUpdate() {
        super.deviceStatusUpdate();
        getLockDevice();
        if (!isSipLogin) {
            loginSipServer();
        }
    }

    /**
     * 获取门锁信息
     */
    private void getLockDevice() {
        if (Constant.DEVICE_RELATE == null) {
            return;
        }
        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            if (device == null) {
                return;
            }
            if (StringUtil.isEmpty(device.getType())) { // add by sunzhibin 2016/12/9
                return;
            }
            if ("Lock".equals(device.getType())) {
                if (!device.getDismiss()) {
                    lockDeviceRelate = deviceRelate;
                    DeviceStatus status = deviceRelate.getDeviceStatus();
                    DeviceStatusValue value = status.getValue();
                    if (value != null && value.getState() != null) {
                        isLockOpen = value.getState().equals("1") ? Boolean.TRUE : Boolean.FALSE;
                        updateLockView(isLockOpen);
                    }
                    break;
                }
            }
        }

    }

    /**
     * 更新门锁图标
     *
     * @param isLockOpen
     */
    private void updateLockView(Boolean isLockOpen) {
        if (isLockOpen) {
            ivLock.setImageResource(R.drawable.ic_homepage_lock_open);
            if (iv_fingerprint_lock_dot != null) {
                ivLock.setImageResource(R.drawable.ic_homepage_lock_open_land);
                iv_fingerprint_lock_dot.setImageResource(R.drawable.circle_unselect);
            }
        } else {
            ivLock.setImageResource(R.drawable.ic_homepage_lock_close);
            if (iv_fingerprint_lock_dot != null) {
                ivLock.setImageResource(R.drawable.ic_homepage_lock_close_land);
                iv_fingerprint_lock_dot.setImageResource(R.drawable.circle_unpress);
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
        if (deviceRelate == null) {
            return;
        }

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

    private void initProgressBar() {
        if (mFloatLayout != null)//悬浮按钮
            mFloatLayout.setVisibility(View.VISIBLE);
        //有网 离线 加载数据/切换主机  显示不显示进度条
        if (NetUtil.checkNet(this) &&
                (!Constant.IS_GATEWAY_ONLINE || changeHost) || isLoading) {

            toastUtils.showProgress("数据同步中...", 1);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (toastUtils == null) return;
                        if (!toastUtils.isShowing()) return; //XXX 做了修改
                        toastUtils.dismiss();
                        if (timer != null)
                            timer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000 * 15);
            isLoading = false;
            changeHost = false;
        }

        if (Constant.IS_GATEWAY_ONLINE && Constant.GATEWAY != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flGatewayHint.setVisibility(View.GONE);
                }
            });
        } else if (!Constant.IS_GATEWAY_ONLINE && !isLoading
                && flGatewayHint.getVisibility() != View.VISIBLE) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager == null) {
                        return;
                    }
                    if (toastUtils != null) toastUtils.dismiss();
                    flGatewayHint.setVisibility(View.VISIBLE);
                    viewPager.setAdapter(new ExpressionPagerAdapter(new ArrayList<View>()));
                }
            });
        }
        setGatewayInfo();
    }

    /**
     * 设定主机信息
     */
    private void setGatewayInfo() {
        Gateway gateway = Constant.GATEWAY;
        if (gateway == null) return;
        flGatewayHint.setVisibility(View.GONE);
        if (!Constant.GATEWAY.getHostId().equals(Constant.CURRENTHOSTID)) { //保证GATEWAY 更新
            return;
        }
        if (gateway.getRoom() == null) {
//            toastUtils.dismiss();
            toastUtils.showErrorWithStatus(getResources().getString(R.string.homepage_room_info_error));
        }

        if (gateway != null && gateway.getRoom() != null) {
            if (gateway.getRoom().size() > 0) {
                list.clear();
                list.addAll(gateway.getRoom());
                if (HASE_MEASURESD[0] = true) {
                    toastUtils.dismiss();
                    initComfortLiveGridView();
                }
            }
        }
    }


    /**
     * 登录门禁
     */
    private void loginSipServer() {
        boolean GuardIsExsit = false;
        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            //判断是否要门禁,如果包含门禁，则将侧滑栏中“首页”选项变成“二维码”，点击生成二维码
            if ("Guard".equals(device.getType())) {
                loginToSipServer(device.getGuardInfo().getAccount(), device.getGuardInfo().getPwd(),
                        device.getGuardInfo().getSIP());
                SIP = device.getGuardInfo().getSIP();

                GuardIsExsit = true;
                break;
            }
        }

        if (GuardIsExsit) {
            //如果门禁存在，天气显示关闭
            if (null != rlHomepageIntelligentLock && null != rlHomepageWeatherInfo) {
                rlHomepageIntelligentLock.setVisibility(View.VISIBLE);
                rlHomepageWeatherInfo.setVisibility(View.GONE);
//                rlHomepageWeatherInfo.setVisibility(View.VISIBLE);
            }

            isSipLogin = Boolean.TRUE;
            isNeedQRCode = true;
            imageViewHomeOrQR.setImageResource(R.drawable.ic_homepage_sideslip_qr);
            textViewHomeOrQR.setText("二维码");
        } else {
            //如果门禁不存在，天气显示
            if (null != rlHomepageIntelligentLock && null != rlHomepageWeatherInfo) {
                rlHomepageIntelligentLock.setVisibility(View.GONE);
                rlHomepageWeatherInfo.setVisibility(View.VISIBLE);
            }

            isNeedQRCode = false;
            imageViewHomeOrQR.setImageResource(R.drawable.ic_homepage_sideslip_homepage);
            textViewHomeOrQR.setText("首页");

            getWeatherInfo();

        }
    }


    private void setGreenBar() {
        bcGreen = (BarChart) findViewById(R.id.bcGreen);
        bcGreen.setDrawGridBackground(false);
        // no description text
        bcGreen.setDescription("");
        bcGreen.setNoDataTextDescription("");
        bcGreen.setTouchEnabled(false);
        bcGreen.setDragEnabled(false);
        bcGreen.setScaleEnabled(false);


        XAxis xAxis = bcGreen.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#9c9c9c"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = bcGreen.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(3.4f);
        leftAxis.setAxisMinValue(0);
        leftAxis.setTextColor(Color.parseColor("#9c9c9c"));
        //虚线
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        bcGreen.getAxisRight().setEnabled(false);
        // add data
        bcGreen.animateY(1500, Easing.EasingOption.Linear);
        bcGreen.getLegend().setEnabled(false);
        setData();
    }

    private void setData() {
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("");
        xVals.add("当前");
        xVals.add("");

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        yVals1.add(new BarEntry(0f, 1));
        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);
        set1.setColor(Color.parseColor("#128CE3"));
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.parseColor("#9c9c9c"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        bcGreen.setData(data);
        bcGreen.animateY(1400, Easing.EasingOption.EaseInOutQuart);

//        getGreenLiveData();// 获取绿色生活的数据
    }

    private void initView() {
        initTopBar("", null, true, true);
        smHomePage = (MySlidingMenu) findViewById(R.id.dlHomepage);
        this.llHomepageHomeSecurity = (PercentLinearLayout) findViewById(R.id.llHomepageHomeSecurity);
        this.pllHealth = (PercentLinearLayout) findViewById(R.id.pllHealth);
        this.pllGreenLive = (PercentLinearLayout) findViewById(R.id.pllGreenLive);
        this.tvHomepageElectricityDegree = (TextView) findViewById(R.id.tvHomepageElectricityDegree);
        tvHomepageElectricityDegree.setText(String.format(getString(R.string.homepage_degrees), "0.0"));
        this.tvHomepageCurrentPower = (TextView) findViewById(R.id.tvHomepageCurrentPower);
        tvHomepageCurrentPower.setText(String.format(getString(R.string.homepage_current_power), "0.0"));
        this.tvHomepageWeight = (TextView) findViewById(R.id.tvHomepageWeight);
        this.tvHomepageBloodPressure = (TextView) findViewById(R.id.tvHomepageBloodPressure);
        this.tvHomepageBloodGlucose = (TextView) findViewById(R.id.tvHomepageBloodGlucose);
        this.llSideSlipPersonalCenter = (PercentLinearLayout) findViewById(R.id.llSideSlipPersonalCenter);
        this.llSideSlipAreaSystemSettings = (PercentLinearLayout) findViewById(R.id.llSideSlipAreaSystemSettings);
        this.llSideSlipDeviceManage = (PercentLinearLayout) findViewById(R.id.llSideSlipDeviceManage);
        this.llSideSlipLinkageManage = (PercentLinearLayout) findViewById(R.id.llSideSlipLinkageManage);
        this.llSideSlipAreaManage = (PercentLinearLayout) findViewById(R.id.llSideSlipAreaManage);
        this.llSideSlipHomepage = (PercentLinearLayout) findViewById(R.id.llSideSlipHomepage);
        this.llSideSlipGreenLive = (PercentLinearLayout) findViewById(R.id.llSideSlipAddBatch);
        this.llSideSlipHealthLive = (PercentLinearLayout) findViewById(R.id.llSideSlipHealthLive);
        this.llSideSlipSecurity = (PercentLinearLayout) findViewById(R.id.llSideSlipSecurity);
        this.llSideSlipComfortLive = (PercentLinearLayout) findViewById(R.id.llSideSlipComfortLive);
        this.rlHomepagePushMessage = (PercentRelativeLayout) findViewById(R.id.rlHomepagePushMessage);
        this.vPager = (ConvenientBanner) findViewById(R.id.cBanner);
        this.llHomepageModelSelect = (PercentLinearLayout) findViewById(R.id.llHomepageModelSelect);
        this.llHomepageIntelligentLock = (PercentLinearLayout) findViewById(R.id.llHomepageIntelligentLock);
        this.llApplyAnnotation = (PercentLinearLayout) findViewById(R.id.llApplyAnnotation);
        this.tvName = (TextView) findViewById(R.id.tvName);
        this.ivModelSelect = (ImageView) findViewById(R.id.ivModelSelect);
        this.tvModelSelect = (TextView) findViewById(R.id.tvModelSelect);
        this.tvApplyMessage = (TextView) findViewById(R.id.tvApplyMessage);
        this.tvApplyCancel = (TextView) findViewById(R.id.tvApplyCancel);
        this.tvApplyAgain = (TextView) findViewById(R.id.tvApplyAgain);
        viewPager = (ViewPager) findViewById(R.id.vpComfortLive);
        this.flGatewayHint = (FrameLayout) findViewById(R.id.flGatewayHint);
        this.ivLock = (ImageView) findViewById(R.id.iv_lock);
        this.ivBack.setImageResource(R.drawable.ic_homepage_menu);
        this.ivRight.setImageResource(R.drawable.ic_homepage_message);

        textViewHomeOrQR = (TextView) findViewById(R.id.id_textViewHomeOrQR);
        imageViewHomeOrQR = (ImageView) findViewById(R.id.id_imageViewHomeOrQR);
        /*
         iv_fingerprint_lock,iv_fingerprint_lock_dot;
           tv_weather_city,tv_weather_info,iv_weather_pic,tv_weather_C,tv_weather_date;
         */

        this.ivBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    smHomePage.isOpen = !smHomePage.isOpen();
                }
                return true;
            }
        });
        this.ivBack.setOnClickListener(null);
        this.ivRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(HomepageListeningActivity.this, InformationCenterListeningActivity.class));
                return false;
            }
        });
//获取viewPager的宽高

        getHeightAndWidth(viewPager);


        vPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        smHomePage.setChildScroll(true);
                        if (mFloatLayout == null)
                            break;
                        break;
                }
                return false;
            }
        });

        this.rlHomepagePushMessage.setOnClickListener(this);
        this.llHomepageModelSelect.setOnClickListener(this);
        this.llHomepageHomeSecurity.setOnClickListener(this);
        this.llSideSlipAreaManage.setOnClickListener(this);
        this.llSideSlipPersonalCenter.setOnClickListener(this);
        this.pllHealth.setOnClickListener(this);
        this.tvApplyCancel.setOnClickListener(this);
        this.tvApplyAgain.setOnClickListener(this);
        this.llSideSlipComfortLive.setOnClickListener(this);
        this.llSideSlipSecurity.setOnClickListener(this);
        this.llSideSlipHealthLive.setOnClickListener(this);
        this.llSideSlipHomepage.setOnClickListener(this);
        this.llSideSlipAreaSystemSettings.setOnClickListener(this);
        this.llSideSlipDeviceManage.setOnClickListener(this);
        this.llSideSlipGreenLive.setOnClickListener(this);
        this.pllGreenLive.setOnClickListener(this);
        this.llSideSlipLinkageManage.setOnClickListener(this);
        this.llHomepageIntelligentLock.setOnClickListener(this);
    }

    private void initListener() {

    }

    private void setViewPager() {
        vPager.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, pics)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_indicators_default, R.drawable.ic_indicators_right_now})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnPageChangeListener(null);
    }

    /**
     * 获取广告信息
     */
    private void getAdInfo() {
        AdController.getInstance().getAdInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                AdvertisementResult result = new Gson().fromJson(Json, AdvertisementResult.class);
                for (Advertisement ad : result.getResponse().getData()) {
//                    String picUrl = String.format("http://%s/image/%s", URLConfig.HTTP, ad.getIphone_pic());
                    String picUrl = URLConfig.PIC_URL + ad.getIphone_pic();
                    pics.add(picUrl);
                }
                if (pics.size() != 0) {
                    setViewPager();
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    private void initComfortLiveGridView() {
        if (list.size() > 0) {
            datas.clear();
            for (Room room : list) {
                datas.add(room);
            }
        }
        //首页+
//        Room addRoom = new Room("");
//        addRoom.setType("");
//        datas.add(datas.size(), addRoom);
        //舒适生活下的viewPager

        ViewPager viewPager = (ViewPager) findViewById(R.id.vpComfortLive);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout_dot);
        int count = linear.getChildCount();
        if (count > 0) {
            linear.removeAllViews();
        }
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        smHomePage.setChildScroll(true);
                        int[] location = new int[2];
                        smHomePage.getChildAt(1).getLocationInWindow(location); //获取在当前窗口内的绝对坐标

                        smHomePage.getChildAt(1).getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标

                        break;
                }
                return false;
            }
        });
        List<View> views = new ArrayList<>();

        int size = ((datas.size() - 1) / 8) + 1;
        final List<CircleImageView> cirviewlist = new ArrayList<>(size);
        cirviewlist.clear();
        for (int i = 0; i < size; i++) {
            View gv = getGridChildView(i);
            views.add(gv);
            //添加轮播图小点
            CircleImageView indicatorView = new CircleImageView(this);
            indicatorView.setBackground(getResources().getDrawable(R.drawable.circle_unselect));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            indicatorView.setLayoutParams(lp);
            linear.addView(indicatorView);
            cirviewlist.add(indicatorView);
        }
        if (cirviewlist.size() > 1) {
            cirviewlist.get(0).setBackground(getResources().getDrawable(R.drawable.circle_unpress));
        } else if (cirviewlist.size() == 1) {
            cirviewlist.get(0).setVisibility(View.INVISIBLE);
        }
        viewPager.setAdapter(new ExpressionPagerAdapter(views));

//        toastUtils.dismiss(); //销毁提示框

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (cirviewlist.size() == 1) {
                    cirviewlist.get(0).setVisibility(View.INVISIBLE);
                    return;
                }
                for (int i = 0; i < cirviewlist.size(); i++) {
                    CircleImageView circle = cirviewlist.get(i);
                    if (i == position)
                        circle.setBackground(getResources().getDrawable(R.drawable.circle_unpress));
                    else
                        circle.setBackground(getResources().getDrawable(R.drawable.circle_unselect));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 舒适生活GridView
     */
    public View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.layout_home_life, null);
        MyGridView gv = (MyGridView) view.findViewById(R.id.gvLife);

        gv.setPadding(8, 8, 8, 10);
        gv.isCanMove = false;
        final List<Room> tempList = new ArrayList<>();

        if (datas.size() > 0 && datas.size() <= 8) {
            tempList.addAll(datas);
        } else if (datas.size() - (8 * i) <= 8) {
            tempList.addAll(datas.subList((i * 8), datas.size()));
        } else {
            tempList.addAll(datas.subList(i * 8, 8 * (i + 1)));
        }
        ComfortLiveAdapter adapter = new ComfortLiveAdapter(this);

        if (WH[1] != 0) {
            int height = WH[1];//viewPager 高度
            //viewpager高度 - 两个图片的高度
            height = (height - 2 * DensityUitl.dip2px(this, 70) - DensityUitl.dip2px(this, 10)) / 2;

            if (height > 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, height / 2, 0, height / 2);
                gv.setLayoutParams(params);

                gv.setVerticalSpacing(height);
            }
        }


        adapter.setDatas(tempList);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Room room = tempList.get(position);
                if ("".equals(room.getName())) {
                    Intent intent = new Intent(HomepageListeningActivity.this, SceneManageListeningActivity.class);
                    intent.putExtra("tag", 1);
                    startActivity(intent);
                } else {
                   /* if(!isOPen(HomepageListeningActivity.this)){

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("提示");
                        builder.setMessage("请打开gps定位,否则获取不到室外温度!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                              turnGPSOn();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                                Intent intent = new Intent(mContext, SceneDisplayListeningActivity.class);
                                intent.putExtra("RoomObject", room);
//                    intent.putExtra("roomName", room.getName());
                                startActivity(intent);
                            }
                        });
                        builder.create().show();
                    }else {
                        Intent intent = new Intent(mContext, SceneDisplayListeningActivity.class);
                        intent.putExtra("RoomObject", room);
//                    intent.putExtra("roomName", room.getName());
                        startActivity(intent);
                    }*/
                    Intent intent = new Intent(HomepageListeningActivity.this, SceneDisplayListeningActivity.class);
                    intent.putExtra("RoomObject", room);
//                    intent.putExtra("roomName", room.getName());
                    startActivity(intent);
                }
            }
        });


        return view;
    }

    /**
     * 判断是否有权限
     *
     * @param menuItemName
     * @return
     */
    private boolean setSldingMenuClick(String menuItemName) {
        String permissions = Constant.PERMISSIONS;
        if (menuItemName.equals("区域管理")) {/*显示时场景管理 权限是区域管理*/
            menuItemName = "场景管理"; // 这里容错处理
            if (permissions.contains(menuItemName) || permissions.contains("区域管理")) {
                return true;
            } else {
                toastUtils.showInfoWithStatus(getResources().getString(R.string.no_permission));
                return false;
            }
        }
        if (!permissions.contains(menuItemName)) {
            toastUtils.showInfoWithStatus(getResources().getString(R.string.no_permission));
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llHomepageModelSelect:
                if (Constant.GLOBAL_MODE == null) {
                    return;
                }
                showHomePageModeSelect();
                break;
            case R.id.rlHomepagePushMessage:
//                FamilyApplyPopUpWindow familyApplyPopUpWindow = new FamilyApplyPopUpWindow(HomepageListeningActivity.this);
//                familyApplyPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                startActivity(new Intent(HomepageListeningActivity.this, SlidePagerListeningActivity.class));
                break;
//            case R.id.llHomepageHomeSecurity:
////                if (setSldingMenuClick(readString(R.string.homepage_home_security))) {
////                CommonMethod.startCommonActivity(mContext, CameraListListeningActivity.class);
//                CommonMethod.startCommonActivity(this, CameraListActivity.class);//摄像头
////                }
//                break;
            case R.id.llSideSlipAreaManage:
                //区域管理界面
                boolean areaManagePermission = setSldingMenuClick(getResources().getString(R.string.homepage_area_manage));
                if (areaManagePermission) {
                    startActivity(new Intent(HomepageListeningActivity.this, SceneManageListeningActivity.class));    //?
                }
                break;
            case R.id.llSideSlipPersonalCenter:
                //个人中心界面
                startActivity(new Intent(HomepageListeningActivity.this, PersonalCenterActivity.class));
                break;
            case R.id.pllHealth:
            case R.id.llSideSlipHealthLive:
                /*正确跳转*/
                CommonMethod.startCommonActivity(this, HealthLifeListeningActivity.class);

                break;
            case R.id.tvApplyCancel:
                // TODO 首页的 "取消" 或 "取消等待" 按钮
                break;
            case R.id.tvApplyAgain:
                // TODO 首页的 "重新申请" 按钮
                break;
            case R.id.ivBack:
                break;
            //舒适生活
            case R.id.llSideSlipComfortLive:
                CommonMethod.startCommonActivity(this, ComfortableLifeListeningActivity.class);
                break;
            case R.id.llHomepageHomeSecurity:
            case R.id.llSideSlipSecurity:
                //跳转到摄像头列表界面
                if (setSldingMenuClick(getString(R.string.homepage_home_security))) {
                    CommonMethod.startCommonActivity(this, CameraListActivity.class);
                }
                break;
            case R.id.llSideSlipHomepage:
                if (!isNeedQRCode) {//显示首页
                    smHomePage.closeMenu();
                } else {//显示二维码，点击进入生成二维码界面
                    Intent intent = new Intent(HomepageListeningActivity.this, QRCodeListeningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("SIP", SIP);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                break;
            case R.id.llSideSlipAreaSystemSettings:
////                //没有权限
//                boolean systemSetPermission = setSldingMenuClick(getResources().readString(R.string.homepage_system_settings));
////                if (systemSetPermission) {

//
                startActivity(new Intent(HomepageListeningActivity.this, SystemSettingsListeningActivity.class));
//                }
                break;
            case R.id.llSideSlipDeviceManage:
                //设备管理界面
                boolean deviceManagePermission = setSldingMenuClick(getResources().getString(R.string.homepage_device_manage));
                if (deviceManagePermission) {
                    startActivity(new Intent(HomepageListeningActivity.this, DeviceManageListeningActivity.class));
                }
                break;
            case R.id.llSideSlipAddBatch:
//                BaseApplication.showToastShort("暂未开发完成，敬请等待！");
                //批量添加
                startActivity(new Intent(HomepageListeningActivity.this, AddBatchScanDeviceActivity.class));

                break;
            case R.id.pllGreenLive:
                boolean green2 = setSldingMenuClick(getResources().getString(R.string.homepage_green_live));
                if (green2) {
                    startActivity(new Intent(HomepageListeningActivity.this, GreenLiveListeningActivity.class));
//                BaseApplication.showToastShort("暂未开发完成，敬请等待！");
                }
                break;

            case R.id.llSideSlipLinkageManage:
                boolean linkManagePermission = setSldingMenuClick(getResources().getString(R.string.homepage_link_manage));
                if (linkManagePermission) {
                    startActivity(new Intent(HomepageListeningActivity.this, LinkManageListeningActivity.class));
                }
                break;
            //智能门锁
            case R.id.llHomepageIntelligentLock:
//                //门禁可见 执行下一步操作
                if (null == rlHomepageIntelligentLock || rlHomepageIntelligentLock.getVisibility() != View.VISIBLE) {

                    return;
                }

                if (isLockOpen) {
                    isLockOpen = Boolean.FALSE;

                    sendDeviceControl(lockDeviceRelate, "0");
                } else {
                    isLockOpen = Boolean.TRUE;
                    sendDeviceControl(lockDeviceRelate, "1");
                }
                updateLockView(isLockOpen);
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        vPager.stopTurning();
//        if (mFloatLayout != null) {
//            //移除悬浮窗口
//            mWindowManager.removeView(mFloatLayout);
//        }
        if (mFloatLayout != null)
            mFloatLayout.setVisibility(View.GONE);
    }

    /**
     * 是否有用户申请消息
     */
    private void hasApplyMessage() {
        // TODO 调用接口,查询是否有申请信息，如果有，则弹出对话框；如果没有，则不弹出
        FamilyApplyPopUpWindow familyApplyPopUpWindow = new FamilyApplyPopUpWindow(HomepageListeningActivity.this);
        familyApplyPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 获取绿色生活的数据
     */
    private void getGreenLiveData() {
        String[] addr = {"1", "1"};
        String[] time = {"1452748741", "1452162844"};
        DeviceController.getInstance().greenLive(this, addr, time, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
//
//                L.e("首页getGreenLiveData===" + Json);
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 激活（切换）指定的联动预案或模式
     */
    private void activateModel() {
        if (currentMode == null) {
            return;
        }
        LinkManageController.getInstance().activate(this, currentMode.getModeId(), null,
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
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
    /*    final Intent intent = new Intent(this, LongRunningService.class);
        stopService(intent);*/
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    //  startService(intent);
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            logout();
            //停止SIP服务
            stopSipService();
            finish();
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }


    /**
     * 停止SIP服务
     */
    public void stopSipService() {
        try {
           /* new Thread() {
                @Override
                public void run() {
                    super.run();

                    //退出呼叫库
                    squirrelCall.squirrelUninit();
                }
            }.start();*/
            logoutFromSipServer();
            //关闭服务
            stopService(new Intent(HomepageListeningActivity.this, SIPService.class));
            // stopService(new Intent(this, LocationSvc.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * SIP服务退出
     */
    protected void logoutFromSipServer() {
        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String username = preferences.getString("login_username", "");
        if (StringUtil.isEmpty(username)) {
            return;
        }
        squirrelCall.squirrelAccountExit(squirrelCallImpl.servername,
                squirrelCallImpl.serverport, username);
    }

    //强制打开gps定位，用于获取当地天气预报
    public void turnGPSOn() {
        LocationManager alm = (LocationManager) HomepageListeningActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
    }
   /* //判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }
*/

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 60 * 10;//十分钟
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Locatio

            Constant.latitude = location.getLatitude();
            Constant.longitude = location.getLongitude();

            getWeatherInfo();

            L.e("Constant.latitude:" + Constant.latitude);
            L.e("Constant.longitude:" + Constant.longitude);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 用来在onCreat方法中获取控件的宽高
     *
     * @param v
     */
    private void getHeightAndWidth(final View v) {
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (HASE_MEASURESD[0] == false) {

                    int height = v.getMeasuredHeight();
                    int width = v.getMeasuredWidth();
                    L.i("AAAABA" + height + " = = " + width);
                    WH[0] = width;
                    WH[1] = height;
                    HASE_MEASURESD[0] = true;
                }

                return true;
            }

        });
    }

    /**
     * 浮动按钮初始化
     */
    private void createFloatView() {
        if (null == rlHomepageIntelligentLock) {
            return;
        }

        //获取LayoutParams对象
        wmParams = new WindowManager.LayoutParams();
        //获取的是CompatModeWrapper对象
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGB_565;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗口至左上角
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 10;
        int y = ScreenUtils.getScreenHeight(this);
        y *= 0.6599f; //高度
//        y += DensityUitl.getWindowsStatusBarHeight(HomepageListeningActivity.this);
        y -= DensityUitl.dip2px(this, 60);
        wmParams.y = y;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = getLayoutInflater();//LayoutInflater.from(getApplication());

        mFloatLayout = inflater.inflate(R.layout.layout_homepage_floatbutton, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        tv_floatButton_mode = (TextView) mFloatLayout.findViewById(R.id.tv_floatButton_mode);
        img_floatButton_mode = (ImageView) mFloatLayout.findViewById(R.id.img_floatButton_mode);

        myButtonBackMap = (MyFloatRelativeLayout) mFloatLayout.findViewById(R.id.myfloatbutton);

        //绑定触摸移动监听
        myButtonBackMap.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0.0f;
            float startY = 0.0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();

                switch (eventAction) {
                    case MotionEvent.ACTION_MOVE:
                        wmParams.x = (int) event.getRawX() - mFloatLayout.getWidth() / 2;
                        //25为状态栏高度
                        wmParams.y = (int) event.getRawY() - mFloatLayout.getHeight() / 2 - DensityUitl.getWindowsStatusBarHeight(HomepageListeningActivity.this);
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        L.i("TAG" + "点击///ACTION_DOWN");
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float lastX = event.getX();
                        float lastY = event.getY();
                        if (lastX - startX == 0 && lastY - startY == 0) {
                            startX = 0;
                            startY = 0;
                            showHomePageModeSelect();
                        }
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }

    /**
     * 模式选择PopWindow 弹出模式选择
     */
    private void showHomePageModeSelect() {
        if (popUpWindow == null)
            popUpWindow = new ModelSelectPopUpWindow(this, Constant.GLOBAL_MODE,
                    new ModelSelectPopUpWindow.ModelSelectListener() {
                        @Override
                        public void result(int position) {
                            switch (position) {
                                case 1:
                                    currentMode = Constant.getTagFromModeName("回家模式配置");
                                    // TODO 向服务器发送请求，修改当前模式
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
                            }
                        }
                    });
        if (popUpWindow.isShowing()) {
            popUpWindow.dismiss();
            return;
        }
        popUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);

    }

    private void followSldding() {
        final View pllMain = smHomePage.getChildAt(1);
        ViewTreeObserver vto = pllMain.getViewTreeObserver();
        final int[] location = {0, 0};
        vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int tempX = location[0];
                int tempY = location[1];
                pllMain.getLocationOnScreen(location); //获取在当前窗口内的绝对坐标
                if (tempX != location[0] || tempY != location[1]) { //说明View移动
                    wmParams.x = location[0];
                    mFloatLayout.setVisibility(View.GONE);
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    mFloatLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        pllMain.setOnTouchListener(new View.OnTouchListener() {
            int[] location = {0, 0};

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (smHomePage.isOpen) {
                            wmParams.x = (int) (DensityUitl.dip2px(HomepageListeningActivity.this, 150) - ScreenUtils.getScreenWidth(HomepageListeningActivity.this) * 0.1f);
                        } else {
                            wmParams.x = 10;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            }
                        });
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int tempX = location[0];
                        int tempY = location[1];
                        pllMain.getLocationOnScreen(location); //获取在当前窗口内的绝对坐标
                        if (tempX != location[0] || tempY != location[1]) { //说明View移动
                            wmParams.x = location[0] - mFloatLayout.getHeight() / 2;
//                    wmParams.y += location[1] - mFloatLayout.getHeight() / 2 - DensityUitl.getWindowsStatusBarHeight(HomepageListeningActivity.this);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                                }
                            });
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        if (smHomePage.isOpen) {
                            wmParams.x = (int) (DensityUitl.dip2px(HomepageListeningActivity.this, 150) - ScreenUtils.getScreenWidth(HomepageListeningActivity.this) * 0.1f);
                        } else {
                            wmParams.x = 10;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            }
                        });
                        break;
                }


                return false;
            }
        });
        GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
            final int[] location = {0, 0};
            int tempX = location[0];
            int tempY = location[1];

            @Override
            public boolean onDown(MotionEvent e) {
                pllMain.getLocationOnScreen(location); //获取在当前窗口内的绝对坐标
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {


                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        };

    }

}