package com.boer.delos.utils;

import android.os.Handler;

public class DataHandlerTimer {
    private Handler mHandler;
    private Runnable mRunnable;
    private int mTimeInterval;

    public DataHandlerTimer() {
        this(1000);
    }

    public DataHandlerTimer(int timeInterval) {
        mTimeInterval = timeInterval;
        mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
                onHandlerTimeDo();
                mHandler.postDelayed(this, mTimeInterval);
            }
        };
    }

    //子类实现
    public void onHandlerTimeDo() {
        mHandler.postDelayed(mRunnable, mTimeInterval);

    }

    public void startHandlerTimer() {

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, mTimeInterval);
    }

    public void stopHandlerTimer() {
        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
    }

    public void setTimeInterval(int timeInterval) {
        mTimeInterval = timeInterval;
    }

    public int getTimeInterval() {
        return mTimeInterval;
    }

}
