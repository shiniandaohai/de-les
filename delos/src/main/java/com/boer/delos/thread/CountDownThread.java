package com.boer.delos.thread;

import android.os.Handler;
import android.os.Message;

import java.util.TimerTask;

/**
 * @author wangkai
 * @Description: 倒计时线程
 * create at 2015/11/13 16:57
 */
public class CountDownThread extends TimerTask {
    private Handler handler;
    private int number = 30;

    public CountDownThread(Handler handler, int number) {
        this.handler = handler;
        this.number = number;
    }

    @Override
    public void run() {
        Message message = new Message();
        message.what = 0;
        message.arg1 = number;
        handler.sendMessage(message);
        number -= 1;
    }
}
