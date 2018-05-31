package com.boer.delos.utils;


/**
 * @author wangkai
 * @Description: LogCat工具类, 传入的数据类型任意。
 * create at 2015/10/30 11:27
 */
public class L {
    private static final String TAG = "baichun==";

    public static void e(String msg) {
        Loger.e(msg);
//        if (BuildConfig.DEBUG) {
//            if (object instanceof String) {
//                Log.e(TAG, TAG + (String) object+"\n");
//            } else if (object instanceof Integer) {
//                Log.e(TAG, TAG + (Integer) object + "\n");
//            } else if (object instanceof Boolean) {
//                Log.e(TAG, TAG + (Boolean) object + "\n");
//            } else if (object instanceof Float) {
//                Log.e(TAG, TAG + (Float) object + "\n");
//            } else if (object instanceof Long) {
//                Log.e(TAG, TAG + (Long) object + "\n");
//            } else {
//                Log.e(TAG, TAG + object.toString());
//            }
//        }
    }

    public static void d(String msg) {
        Loger.d(msg);
//        if (BuildConfig.DEBUG) {
//            if (object instanceof String) {
//                Log.d(TAG, TAG + (String) object);
//            } else if (object instanceof Integer) {
//                Log.d(TAG, TAG + (Integer) object + "");
//            } else if (object instanceof Boolean) {
//                Log.d(TAG, TAG + (Boolean) object + "");
//            } else if (object instanceof Float) {
//                Log.d(TAG, TAG + (Float) object + "");
//            } else if (object instanceof Long) {
//                Log.d(TAG, TAG + (Long) object + "");
//            } else {
//                Log.d(TAG, TAG + object.toString());
//            }
//        }
    }

    public static void w(String msg) {
        Loger.w(msg);
//        if (BuildConfig.DEBUG) {
//            if (object instanceof String) {
//                Log.w(TAG, TAG + (String) object);
//            } else if (object instanceof Integer) {
//                Log.w(TAG, TAG + (Integer) object + "");
//            } else if (object instanceof Boolean) {
//                Log.w(TAG, TAG + (Boolean) object + "");
//            } else if (object instanceof Float) {
//                Log.w(TAG, TAG + (Float) object + "");
//            } else if (object instanceof Long) {
//                Log.w(TAG, TAG + (Long) object + "");
//            } else {
//                Log.w(TAG, TAG + object.toString());
//            }
//        }
    }

    public static void i(String msg) {
        Loger.i(msg);
//        if (BuildConfig.DEBUG) {
//            if (object instanceof String) {
//                Log.i(TAG, TAG + (String) object);
//            } else if (object instanceof Integer) {
//                Log.i(TAG, TAG + (Integer) object + "");
//            } else if (object instanceof Boolean) {
//                Log.i(TAG, TAG + (Boolean) object + "");
//            } else if (object instanceof Float) {
//                Log.i(TAG, TAG + (Float) object + "");
//            } else if (object instanceof Long) {
//                Log.i(TAG, TAG + (Long) object + "");
//            } else {
//                Log.i(TAG, TAG + object.toString());
//            }
//        }
    }

    public static void v(String msg) {
        Loger.v(msg);
//        if (BuildConfig.DEBUG) {
//            if (object instanceof String) {
//                Log.v(TAG, TAG + (String) object);
//            } else if (object instanceof Integer) {
//                Log.v(TAG, TAG + (Integer) object + "");
//            } else if (object instanceof Boolean) {
//                Log.v(TAG, TAG + (Boolean) object + "");
//            } else if (object instanceof Float) {
//                Log.v(TAG, TAG + (Float) object + "");
//            } else if (object instanceof Long) {
//                Log.v(TAG, TAG + (Long) object + "");
//            } else {
//                Log.v(TAG, TAG + object.toString());
//            }
//        }
    }
}
