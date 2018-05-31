package com.boer.delos.activity.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.camera.InitCamActivity;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.smartdoorbell.SmartDoorbellActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.constant.Constant;
import com.boer.delos.fragment.DiscoveryFragment;
import com.boer.delos.fragment.MainFragment;
import com.boer.delos.fragment.MallFragment;
import com.boer.delos.fragment.MineFragment;
import com.boer.delos.model.Apk;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.receiver.NetReceiver;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.jpush.JpushController;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StatusBarUtil;
import com.boer.delos.utils.SystemUtils;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.popupWindow.ApkPopupWindow;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainTabActivity extends InitCamActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.main_home)
    RadioButton mainHome;
    @Bind(R.id.main_mall)
    RadioButton mainMall;
    @Bind(R.id.main_discovery)
    RadioButton mainDiscovery;
    @Bind(R.id.main_mine)
    RadioButton mainMine;
    @Bind(R.id.main_bottom_tabs)
    RadioGroup mainBottomTabs;

    private final String TAG = "MainTabActivity";
    @Bind(R.id.llayout_status)
    LinearLayout llayoutStatus;
    @Bind(R.id.main_content)
    FrameLayout mainContent;
    private NetReceiver mNetReceiver;

    private FragmentManager fragmentManager;
    public MainFragment mainFragment;
    public MallFragment mallFragment;
    public DiscoveryFragment discoveryFragment;
    public MineFragment mineFragment;
    public Fragment currentFragment;


    //Login>>>>MainTab
    String tabPos = "-1";
    //更新
    private ApkPopupWindow mPopupApk;
    private String newVerName = "";// 新版本名称
    private double newVerCode = -1;// 新版本号
    private Apk apk;
    private static final int TASK_UPDATE_SHOW = 0x02;

    public ToastUtils toastUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.StatusBarLightMode(this);
//        StatusBarUtil.setStatusBarColor(this,R.color.layout_title_bg);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ActivityCustomManager.getAppManager().addActivity(this);

        Log.v("gl", "IS_LOCAL_CONNECTION==" + Constant.IS_LOCAL_CONNECTION);

        setContentView(R.layout.activity_main_tab);
        ButterKnife.bind(this);

//        adaptTheme(true);
//        statusBarTheme(false, getResources().getColor(R.color.white));


        Intent intent = getIntent();
        tabPos = intent.getStringExtra("tabPos");
        Log.v("gl", "tabPos==" + tabPos);


        fragmentManager = getSupportFragmentManager();

//        mainDiscovery.setChecked(true);
        mainBottomTabs.setOnCheckedChangeListener(this);
        L.i("height=1" + mainBottomTabs.getHeight() + "," + mainBottomTabs.getMeasuredHeight());


        if (TextUtils.isEmpty(tabPos)){
            RadioButton childRadioBtn = (RadioButton) mainBottomTabs.getChildAt(2);
            childRadioBtn.setChecked(true);
            changeFragment(2);
        }
        else {
            RadioButton childRadioBtn = (RadioButton) mainBottomTabs.getChildAt(Integer.parseInt(tabPos));
            childRadioBtn.setChecked(true);
            changeFragment(Integer.parseInt(tabPos));
        }

        checkVersion();

        registerNetworkStatusReceiver();

//        processIntent();

        toastUtils = new ToastUtils(this);
        if (!NetUtil.checkNet(this)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
        }
    }

    public void checkVersion() {
        Log.d(TAG, "check version");
        mPopupApk = new ApkPopupWindow(MainTabActivity.this,
                findViewById(R.id.main_content));


        JpushController.getInstance().updateApk(this, Constant.UPDATE_URL, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.v("gl", "json==" + json);

                parseUpdateXml(json);

            }

            @Override
            public void onFailed(String json) {

            }
        });


    }


    private void parseUpdateXml(String json) {

        try {
            // TODO Auto-generated method stub

            JSONObject jsonObject = XML.toJSONObject(json);
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(jsonObject.toString());
            JsonObject jsonContent = el.getAsJsonObject();

            JsonObject jsonProduct = jsonContent
                    .get("boericasa").getAsJsonObject().get("product").getAsJsonObject();

            Log.d(TAG, "get product");
            JsonObject jsonFileList = jsonProduct.get(
                    "filelist").getAsJsonObject();
            // Log.d(TAG, jsonFileList.toString());
            if (jsonFileList.get("file").isJsonObject()) {
                JsonObject jsonFile = jsonFileList.get("file")
                        .getAsJsonObject();
                apk = new Apk();
                Log.d(TAG, "check apk");

                if (("\"" + Constant.UPDATE_FILE_TYPE + "\"")
                        .equals(jsonFile.get("type").toString())) {
                    Log.d(TAG, "IF");
                    if (apk.getFileUrlList().size() <= 0) {
                        apk.setVersion(jsonFile.get("version")
                                .toString());
                        apk.setVersionName(jsonFile.get(
                                "versionName").toString());
                        Log.d(TAG,
                                "versionName = "
                                        + apk.getVersionName());
                        apk.setFile_url(jsonFile
                                .get("file_url").toString());
                        apk.setDescription(jsonFile.get(
                                "description").toString());
                        apk.setName(jsonFile.get("name")
                                .toString());
                        apk.setNew_function(jsonFile.get(
                                "new_function").toString());
                        apk.setType(jsonFile.get("type")
                                .toString());
                        newVerCode = Double
                                .parseDouble(jsonFile.get(
                                        "version").toString());
                        if (newVerCode > SystemUtils
                                .getVerCode(MainTabActivity.this)) { // 更新版本
                            mPopupApk.setApk(apk);

                            mHandler.sendEmptyMessage(TASK_UPDATE_SHOW);
                        }
                    }
                    String url = jsonFile.get("file_url")
                            .toString();
                    apk.getFileUrlList().addLast(
                            url.replace("\"", ""));
                }
                // Log.d(TAG, "ELSE");

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_UPDATE_SHOW:

                    if (mPopupApk != null && mPopupApk.getUpdate()) {
                        mPopupApk.showAtLocation(
                                findViewById(R.id.main_content),
                                Gravity.CENTER, 0, 0);
//                        mPopupApk.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    }

                    break;
            }
        }
    };


    private void registerNetworkStatusReceiver() {
        mNetReceiver = new NetReceiver(this);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mFilter);

        mNetReceiver.setRefreshMainFragment(new NetReceiver.RefreshMainFragment() {
            @Override
            public void refresh() {
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                mainFragment.setArguments(bundle);
                beginTransaction.replace(R.id.main_content, mainFragment);
                beginTransaction.commit();

            }
        });
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_home:
                changeFragment(0);
                break;
            case R.id.main_mall:
                changeFragment(1);
                break;
            case R.id.main_discovery:
                changeFragment(2);
                break;
            case R.id.main_mine:
                changeFragment(3);
                break;
            default:
                break;
        }
    }

    private void full(boolean enable) {
        if (enable) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            adaptTheme(true);
            statusBarTheme(false, Color.TRANSPARENT);
            StatusBarUtil.StatusBarDarkMode(this);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            adaptTheme(true);
            statusBarTheme(true, getResources().getColor(R.color.layout_title_bg));
            StatusBarUtil.StatusBarLightMode(this);
//            StatusBarUtil.setStatusBarColor(this,R.color.layout_title_bg);
        }
    }

    public void changeFragment(int index)//ͬ切换fragment
    {
        if(index==0){
            full(true);
        }
        else{
            full(false);
        }

        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);
        switch (index) {
            case 0:
                if (!Constant.isLogin) {
                    startLoginActvity("0");
                    return;
                }

                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    Bundle bundle = new Bundle();
                    mainFragment.setArguments(bundle);
                    beginTransaction.add(R.id.main_content, mainFragment);
                } else {
                    beginTransaction.show(mainFragment);
                }
                currentFragment = mainFragment;
                break;
            case 1:
                if (mallFragment == null) {
                    mallFragment = new MallFragment();
                    beginTransaction.add(R.id.main_content, mallFragment);
                } else {
                    beginTransaction.show(mallFragment);
                }
                currentFragment = mallFragment;
                break;
            case 2:
                if (discoveryFragment == null) {
                    discoveryFragment = new DiscoveryFragment();
                    beginTransaction.add(R.id.main_content, discoveryFragment);
                } else {
                    beginTransaction.show(discoveryFragment);
                }
                currentFragment = discoveryFragment;
                break;
            case 3:
                if (!Constant.isLogin) {
                    startLoginActvity("3");
                    return;
                }

                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    beginTransaction.add(R.id.main_content, mineFragment);
                } else {
                    beginTransaction.show(mineFragment);
                }
                currentFragment = mineFragment;
                break;

            default:
                break;
        }
        beginTransaction.commitAllowingStateLoss();//执行  chage by sunzhibin
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null)
            transaction.hide(mainFragment);
        if (mallFragment != null)
            transaction.hide(mallFragment);
        if (discoveryFragment != null)
            transaction.hide(discoveryFragment);
        if (mineFragment != null)
            transaction.hide(mineFragment);
    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (currentFragment instanceof MallFragment) {

                mallFragment.onBackPressed();

            } else if (currentFragment instanceof DiscoveryFragment) {

                discoveryFragment.onBackPressed();

            }


            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    protected static Boolean isExit = false;

    protected void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {

            ActivityStack.getInstance().finishActivities();
            Constant.isLogin = false;
            System.exit(0);
        }
    }


    public void resetFragment() {
        mainFragment = null;
        mallFragment = null;
        discoveryFragment = null;
        mineFragment = null;


    }

    //主机
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.v("gl", "onNewIntent");

        setIntent(intent);
        processIntent();

    }

    private void processIntent(){
        Intent intent=getIntent();
        String flag=intent.getStringExtra("flag");
        if(flag!=null&&flag.equals("flag_push_info")){
            startActivity(new Intent(this, InformationCenterListeningActivity.class));
        }
        else if(flag!=null&&flag.equals("flag_push_door")){
            queryEnvDevice();
        }
        else{
            RadioButton childRadioBtn = (RadioButton) mainBottomTabs.getChildAt(0);
            childRadioBtn.setChecked(true);
            changeFragment(0);
        }
    }

    private void queryEnvDevice() {
        toastUtils.showProgress("");
        DeviceController.getInstance().queryDeviceRelateInfo(this,"Doorbell",new RequestResultListener(){
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();
                Log.d("json", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("ret") != 0) {
                        return;
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    jsonObject = jsonArray.getJSONObject(0);
                    DeviceRelate device = GsonUtil.getObject(jsonObject.toString(), DeviceRelate.class);
                    startActivity(new Intent(MainTabActivity.this, SmartDoorbellActivity.class).putExtra("device",device));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
            }
        });
    }

    private void startLoginActvity(String tabPos) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("tabPos", tabPos);
        startActivity(intent);

    }



    public void statusBarTheme(boolean display, int color) {
        if (display) {
            llayoutStatus.setVisibility(View.VISIBLE);
            llayoutStatus.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusHeight(this)));
            llayoutStatus.setBackgroundColor(color);
        } else {
            llayoutStatus.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void adaptTheme(final boolean isTranslucentStatusFitSystemWindowTrue) {
        if (isTranslucentStatusFitSystemWindowTrue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
        }
        if (toastUtils != null) {
            toastUtils.dismiss();
            toastUtils = null;
        }
    }
}
