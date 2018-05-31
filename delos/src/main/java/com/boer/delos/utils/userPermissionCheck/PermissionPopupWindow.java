package com.boer.delos.utils.userPermissionCheck;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseActivity;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 弹出框，提示用户获取权限的用途
 * @CreateDate: 2016/12/7 0007 11:02
 * @Modify:
 * @ModifyDate:
 */

public class PermissionPopupWindow extends PopupWindow implements View.OnClickListener{
    private View view;
    private Context mContext;
    private LayoutInflater inflater;
    private String permissionName;
    private TextView tvMsg;
    private TextView tvExitCancel;
    private TextView tvExitConfirm;
    private int PERMISSION_REQUEST_CODE = 200;

    public PermissionPopupWindow(Context mContext, String msg,String permisssionName) {
        this.mContext = mContext;
        this.permissionName = permisssionName;
        inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.popup_permission_mananger, null);
        setContentView(view);
        initView(msg);
        setProperty();
//        startIntent2SettingPermission(permisssionName);

    }
    private void initView(String msg) {
        tvMsg = (TextView) view.findViewById(R.id.tvMsg);
        tvExitCancel = (TextView) view.findViewById(R.id.tvExitCancel);
        tvExitConfirm = (TextView) view.findViewById(R.id.tvExitConfirm);

        tvMsg.setText(msg);
    }
    private void setProperty() {
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void startIntent2SettingPermission(String permissionName) {
        ActivityCompat.requestPermissions(((BaseActivity)mContext), new String[]{permissionName}, PERMISSION_REQUEST_CODE);
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
        }
        return localIntent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvExitCancel:
                startIntent2SettingPermission(permissionName);
                break;
         case R.id.tvExitConfirm:
             mContext.startActivity(getAppDetailSettingIntent());
                break;
        }
    }
}
