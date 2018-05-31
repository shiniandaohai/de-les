package com.boer.delos.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Calendar;

/**
 * @author wangkai
 * @Description: 通用方法工具类
 * create at 2015/11/13 1:57
 */
public class CommonMethod {


    /**
     * 通用启动activity
     *
     * @param context 上下文
     * @param cls     需要跳转到页面
     */
    public static void startCommonActivity(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, cls);
        context.startActivity(intent);
    }


    /**
     * 拨打电话
     *
     * @param context         上下文
     * @param moibleNumberStr 电话号码
     */
    public static void ActionCall(Context context, String moibleNumberStr) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + moibleNumberStr));
        context.startActivity(intent);
    }

    /**
     * 获取年龄
     *
     * @param birthYear 出生年份
     * @return
     */
    public static int getAge(int birthYear) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - birthYear;
        return age;
    }
}
