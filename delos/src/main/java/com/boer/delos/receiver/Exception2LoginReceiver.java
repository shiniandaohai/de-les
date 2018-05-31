package com.boer.delos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.boer.delos.commen.BaseActivity;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.constant.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 针对请求异常   token
 * @CreateDate: 2017/1/4 0004 20:05
 * @Modify:
 * @ModifyDate:
 */


public class Exception2LoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        ((BaseActivity) context).toastUtils.showErrorWithStatus("请重新登录");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!ActivityCustomManager.getAppManager().isActivityStackEmpty() && ActivityCustomManager.getAppManager().getCurrentActivity() instanceof LoginActivity) {
                    return;
                }
                context.sendBroadcast(new Intent(Constant.ACTION_EXIT));
                context.startActivity(new Intent(context, LoginActivity.class));

            }
        }, 1000 * 2);
    }
}
