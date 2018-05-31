package com.boer.delos.utils;

import android.widget.Toast;

import com.boer.delos.commen.BaseApplication;


/**
 * Created by shenyin on 2016/10/17.
 */
public class ToastHelper {
    private static Toast toast;

    /**
     * Using Toast way pop-up messages (display 3 seconds)
     *
     * @Title: showLongMsg
     * @param msg
     */
    public static void showLongMsg(String msg) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getAppContext(), msg,
                    Toast.LENGTH_LONG);
        }
        toast.setText(msg);
        toast.show();
    }

    /**
     * Using Toast way pop-up messages (display 2 seconds)
     *
     * @Title: showShortMsg
     * @param msg
     */
    public static void showShortMsg(String msg) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getAppContext(), msg,
                    Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

    /**
     * @see {@link Toast#cancel()}
     */
    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
