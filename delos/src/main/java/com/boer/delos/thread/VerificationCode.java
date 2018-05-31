package com.boer.delos.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;

import java.util.Timer;

/**
 * @author PengJiYang
 * @Description: 获取验证码计时器
 * create at 2016/3/25 9:45
 */
public class VerificationCode {

    private TextView tvRegisterVerificationBtn;
    private Timer timer;
    private CountDownThread task;
    private MyHandler handler;
    private Context context;

    public VerificationCode(Context context, TextView tvRegisterVerificationBtn) {
        this.context = context;
        this.tvRegisterVerificationBtn = tvRegisterVerificationBtn;
        handler = new MyHandler();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel();
            }
        }
    }

    public void startTimerTask() {
        tvRegisterVerificationBtn.setEnabled(false);
        tvRegisterVerificationBtn.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.blue_text_delos));
        timer = new Timer();
        task = new CountDownThread(handler, 60);
        timer.schedule(task, 0, 1000);
        // TODO 发送获取验证码的请求
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                if (msg.arg1 > 0) {
                    tvRegisterVerificationBtn.setText(msg.arg1 + "s");
                } else {
                    tvRegisterVerificationBtn.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.blue_text_delos));
                    tvRegisterVerificationBtn.setText(BaseApplication.getAppContext().getString(R.string.text_get_again));
                    tvRegisterVerificationBtn.setEnabled(true);
                    cancelTimer();
                }
            }
        }
    }
}
