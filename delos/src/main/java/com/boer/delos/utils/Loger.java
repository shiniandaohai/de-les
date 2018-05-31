package com.boer.delos.utils;

import android.util.Log;

import com.boer.delos.commen.BaseApplication;

/**
 * Created by shenyin on 2016/10/11.
 */
public class Loger {
    private static boolean LOGER = BaseApplication.LOGER;

    public static void v(String msg) {
        if (!LOGER) return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.v(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(), ste.getLineNumber(), msg));
    }

    public static void d(String msg) {
        if (!LOGER) return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.d(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(), ste.getLineNumber(), msg));
    }

    public static void i(String msg) {
        if (!LOGER) return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.i(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(), ste.getLineNumber(), msg));
    }

    public static void w(String msg) {
        if (!LOGER) return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.w(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(), ste.getLineNumber(), msg));
    }

    public static void e(String msg) {
        if (!LOGER) return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.e(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(), ste.getLineNumber(), msg));
    }

}
