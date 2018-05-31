package com.boer.delos.commen;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.CameraMainActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.HostResult;
import com.boer.delos.receiver.NetReceiver;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.utils.sign.MD5Utils;
import com.google.gson.Gson;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public abstract class BaseActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_CODE = 1000;

    public TextView tvTitle, tvRight;
    public ImageView ivBack, ivRight;
    public View vTitle;

    private static long lastClickTime = 0;

    public ToastUtils toastUtils;
    private NetReceiver mNetReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        toastUtils = new ToastUtils(this);
//        ActivityCollector.addActivity(this);
        ActivityCustomManager.getAppManager().addActivity(this);
        //切换网络监听
        mNetReceiver = new NetReceiver();
        registerReceiver(mNetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCustomManager.getAppManager().finishActivity(this);
        unregisterReceiver(mNetReceiver);
        if (toastUtils != null) {
            toastUtils.dismiss();
            toastUtils = null;
        }
        try {
            ButterKnife.unbind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()
                    && !ActivityCustomManager.getAppManager().isActivityStackEmpty()
                    && !(ActivityCustomManager.getAppManager().getCurrentActivity() instanceof CameraMainActivity)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * add by sunzhibin
     * 主动获取设备数据
     */
    protected void getDeviceStatusInfo() {
//        toastUtils.showProgress("数据加载中...");
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    Constant.DEVICE_RELATE = result.getResponse();
                    sendBroadcast(new Intent(Constant.ACTION_DEVICE_UPDATE));
                } catch (Exception e) {
                    L.e("queryDeviceRelateInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    /**
     * 重新获取当前用户主机Id
     */
    private void getCurrentHostId() {
        GatewayController.getInstance().queryUserHost(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Json = StringUtil.nullReplace(Json);
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String currentHostId = result.getCurrentHostId();
                    //保存当前主机
                    Constant.CURRENTHOSTID = currentHostId;
                    Constant.USER_HOSTS = result.getHosts();
                    SharedPreferencesUtils.saveCurrentHostIdToPreferences(BaseApplication.getAppContext());
                } catch (Exception e) {
                    L.e("getCurrentHostId:" + e);
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }


    /**
     * 主动取得主机信息
     */
    public void getGatewayInfo() {
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        GatewayController.getInstance().getGatewayProperties(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Constant.IS_GATEWAY_ONLINE = true; // add by sunzhibin
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5Utils.MD5(Json);
                    if (md5Value == null) {
                        return;
                    }

                    if (Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
                        return;
                    }
//                    L.i("Gateway is changed");
                    Constant.GATEWAY_MD5_VALUE = md5Value;
                    Constant.GATEWAY = result.getResponse();
                    sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));
                } catch (Exception e) {
                    L.d("getGatewayInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                if (json.equals("主机不在线")) {  // add by sunzhibin
                    Constant.GATEWAY = null;
                    Constant.IS_GATEWAY_ONLINE = false;
                    sendBroadcast(new Intent(Constant.ACTION_GATEWAY_UPDATE));
                }
            }
        });
    }


    /**
     * 禁用双击事件
     *
     * @return
     */
    private static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 300;
    }

    @AfterPermissionGranted(PERMISSION_CODE)
    protected boolean queryPermissionTask(String[] permissions, String hint) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (EasyPermissions.hasPermissions(this, permissions)) {
            return true;
        } else {
            // Request one permission
            if (StringUtil.isEmpty(hint)) {
                hint = getString(R.string.permission_hint_default);
            }
            EasyPermissions.requestPermissions(this, hint, PERMISSION_CODE, permissions);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


    /**
     * 隐藏键盘
     */
    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 初始化头部控件
     *
     * @param centerTitle 中间标题文字内容
     * @param rightTitle  右边TextView文字内容
     * @param leftId      返回按钮显示Tag
     * @param rightId     右边按钮显示tag
     */
    public void initTopBar(Integer centerTitle, Integer rightTitle, boolean leftId, boolean rightId) {
        initTopBar(getString(centerTitle), rightTitle, leftId, rightId);
    }

    /**
     * 初始化头部控件
     *
     * @param centerTitle 中间标题文字内容
     * @param rightTitle  右边TextView文字内容
     * @param leftId      返回按钮显示Tag
     * @param rightId     右边按钮显示tag
     */
    public void initTopBar(String centerTitle, Integer rightTitle, boolean leftId, boolean rightId) {
        vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
            vTitle.setBackgroundColor(getResources().getColor(R.color.layout_title_bg));
            vTitle.setVisibility(View.VISIBLE);
        } else {
            vTitle.setVisibility(View.GONE);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvRight = (TextView) findViewById(R.id.tvRight);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRight = (ImageView) findViewById(R.id.ivRight);

        if (centerTitle != null) tvTitle.setText(centerTitle);

        if (rightTitle != null) tvRight.setText(getString(rightTitle));

        if (leftId) ivBack.setVisibility(View.VISIBLE);
        else ivBack.setVisibility(View.GONE);

        if (rightId) ivRight.setVisibility(View.VISIBLE);
        else ivRight.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
