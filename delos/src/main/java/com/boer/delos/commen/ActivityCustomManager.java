package com.boer.delos.commen;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.utils.Loger;

import java.util.NoSuchElementException;
import java.util.Stack;

public class ActivityCustomManager {

    // Activity栈 
    public static Stack<Activity> activityStack;
    // 单例模式 
    public static ActivityCustomManager instance;

    private ActivityCustomManager() {
    }

    /**
     * 单一实例
     */
    public static ActivityCustomManager getAppManager() {
        if (instance == null) {
            instance = new ActivityCustomManager();
        }
        return instance;
    }

    public boolean isActivityStackEmpty() {
        return activityStack.empty();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
        Log.v("gl","activityStack=="+ activityStack.size());
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        try {
            Activity activity = activityStack.lastElement();
            if (activity instanceof LoginActivity) {
                return;
            }
            finishActivity(activity);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取指定类名的Activity
     *
     * @param cls
     * @return
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        try {
            if (activity != null) {
                activityStack.remove(activity);
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
                activity.finish();
                activity = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityStack) {
            activity.finish();
        }
        activityStack.clear();
        Log.v("gl","finishall++activityStack"+ activityStack.size());

    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            Log.v("gl","mActivities=="+   ActivityCustomManager.activityStack.size());
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}