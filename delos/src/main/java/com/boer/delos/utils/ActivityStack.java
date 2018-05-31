package com.boer.delos.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangkai
 * @Description: 堆栈管理
 * create at 2015/10/30 16:40
 */
public class ActivityStack {
    public static ActivityStack instance = null;
    public static List<Activity> mActivities = null;

    public ActivityStack() {
        super();
    }

    /**
     * 实例化
     *
     * @return
     */
    public static ActivityStack getInstance() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }

    /**
     * 将当前Activity推入栈中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivities == null) {
            mActivities = new ArrayList<Activity>();
        }
        mActivities.add(activity);
    }

    /**
     * 结束掉当前堆栈
     *
     * @param
     */
    public void finishActivities() {
        if (mActivities != null) {
            try {
                for (Activity mActivity : mActivities) {
                    if (mActivity != null)
                        mActivity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 结束掉当前堆栈，排除不需要关闭的页面
     *
     * @param
     */
    public void finishActivities(String[] retainActivities) {
        if (mActivities != null) {
            try {
                for (Activity mActivity : mActivities) {
                    if (mActivity != null) {
                        for (int i = 0; i < retainActivities.length; i++) {
                            if (!mActivity.toString().contains(retainActivities[i])) {
                                mActivity.finish();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 退出当前应用
     */
    public void exitApplication() {
        if (mActivities != null) {
            for (Activity mActivity : mActivities) {
                if (mActivity != null)
                    mActivity.finish();
            }
        }
        System.exit(0);
    }
}
