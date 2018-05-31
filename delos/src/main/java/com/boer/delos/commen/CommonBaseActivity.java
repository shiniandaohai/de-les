package com.boer.delos.commen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.receiver.NetReceiver;
import com.boer.delos.utils.AndroidBug5497Workaround;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StatusBarUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.TitleLayout;
import com.umeng.socialize.UMShareAPI;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by gaolong on 2017/3/29.
 */

public abstract class CommonBaseActivity extends FragmentActivity implements TitleLayout.titleLayoutClick, EasyPermissions.PermissionCallbacks {
    public ToastUtils toastUtils;
    public TitleLayout tlTitleLayout;
    public FrameLayout llayoutContent;
    public LinearLayout llayoutStatus;
    private static final int PERMISSION_CODE = 1000;


    private NetReceiver mNetReceiver;
    private boolean isTranslucentStatusFitSystemWindowTrue = true;
    //add by sunzhibin
    protected Handler mHandler;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        StatusBarUtil.StatusBarLightMode(this);
        toastUtils = new ToastUtils(this);
        ActivityCustomManager.getAppManager().addActivity(this);
        mNetReceiver = new NetReceiver();
        registerReceiver(mNetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        adaptTheme(isTranslucentStatusFitSystemWindowTrue);
        init();
        AndroidBug5497Workaround.assistActivity(this);
        initView();
        initData();
        initAction();
    }

    private void init() {
        LayoutInflater inflater = getLayoutInflater().from(this);
        View father = inflater.inflate(R.layout.activity_common_base, null);
        llayoutStatus = (LinearLayout) father.findViewById(R.id.llayout_status);
        llayoutContent = (FrameLayout) father.findViewById(R.id.llayout_content);
        tlTitleLayout = (TitleLayout) father.findViewById(R.id.tl_title_layout);
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
        View child = inflater.inflate(initLayout(), null);
        llayoutContent.addView(child);
        statusBarTheme(true, getResources().getColor(R.color.layout_title_bg));
        setContentView(father);
        ButterKnife.bind(this);
    }


    protected abstract int initLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initAction();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
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
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void leftViewClick() {
        finish();
    }

    @Override
    public void rightViewClick() {

    }


    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    public void statusBarTheme(boolean display, int color) {
        if (display) {
            llayoutStatus.setVisibility(View.VISIBLE);
            llayoutStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusHeight(this)));
            llayoutStatus.setBackgroundColor(color);
        } else {
            llayoutStatus.setVisibility(View.GONE);
        }
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


}
