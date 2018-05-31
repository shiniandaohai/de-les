package com.boer.delos.utils;

/**
 * @author wangkai
 * @Description: 双击事件处理
 * create at 2015/12/8 11:44
 */
public class NoDoubleClickUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}