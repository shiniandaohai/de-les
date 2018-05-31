package com.boer.delos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.service.PollService;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/2/20 0020 10:00
 * @Modify:
 * @ModifyDate:
 */


public class OffLineActivity extends BaseActivity {
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvMessage)
    TextView tvMessage;
    @Bind(R.id.tvTime)
    TextView tvTime;
    @Bind(R.id.tvKnow)
    TextView tvKnow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_off_line_notification);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(false);
        initView();

    }

    private void initView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(new Date(System.currentTimeMillis()));
        tvTime.setText(time);

        tvKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Constant.isLogin = false;
                ActivityCustomManager.getAppManager().finishAllActivity();

                if (!ActivityCustomManager.getAppManager().isActivityStackEmpty()
                        && ActivityCustomManager.getAppManager().getCurrentActivity() instanceof LoginActivity) {
                    return;
                }
                Intent intent = new Intent(OffLineActivity.this, LoginActivity.class);
                OffLineActivity.this.startActivity(intent);
                OffLineActivity.this.finish();
            }
        });
    }

    public void logout() {

        //清理静态数据
        clearStaticData();
        //清除保存的数据
        SharedPreferencesUtils.clearAll(this);
        //停止5秒轮询
        stopService(new Intent(this, PollService.class));
        //发送退出广播
        sendBroadcast(new Intent(Constant.ACTION_EXIT));

        //主机信息清空 add by sunzhibin
        Constant.gatewayInfos.clear();
    }

    /**
     * 清理靜态数据
     */

    protected void clearStaticData() {
        Constant.DEVICE_MD5_VALUE = "";
        Constant.GATEWAY_MD5_VALUE = "";
        Constant.GLOBALMODE_MD5_VALUE = "";
        Constant.DEVICE_RELATE = new ArrayList<>();
        Constant.GATEWAY = null;
        Constant.GLOBAL_MODE = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
