package com.boer.delos.utils.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.User;
import com.boer.delos.utils.Base64;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ToastHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by zhukang on 16/7/13.
 */
public class SharedPreferencesUtils extends SharedPreTag {
    /**
     * 保存调光灯数值
     *
     * @param context
     */
    public static void saveLightAdjustToPreferences(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences("LightAdjust", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("LightAdjustValue", value);
        editor.commit();
    }

    /**
     * 读取调光灯数值
     *
     * @param context
     */
    public static int readLightAdjustFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("LightAdjust", Context.MODE_PRIVATE);
        return preferences.getInt("LightAdjustValue", 0);
    }

    /**
     * 保存用户信息
     *
     * @param context
     */
    public static void saveUserInfoToPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(Constant.LOGIN_USER);
            // 将字节流编码成base64的字符窜
            String user_Base64 = new String(Base64.encode(baos
                    .toByteArray()));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user", user_Base64);

            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        L.i("saveUserInfoToPreferences success");
    }

    /**
     * 读取用户信息
     *
     * @param context
     */
    public static void readUserInfoFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String user_Base64 = preferences.getString("user", "");

        //读取字节
        byte[] base64 = Base64.decode(user_Base64);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);

            //读取对象
            User user = (User) bis.readObject();
            if (user != null) {
                Constant.LOGIN_USER = user;
                Constant.USERID = user.getId();
                Constant.CURRENTPHONE = user.getMobile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.i("readUserInfoFromPreferences success");
    }


    /**
     * 保存令牌
     *
     * @param context
     */
    public static void saveTokenToPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", Constant.TOKEN);
        editor.commit();
    }

    /**
     * 读取令牌
     *
     * @param context
     */
    public static void readTokenFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Constant.TOKEN = preferences.getString("token", "");
    }

    /**
     * 保存加密Key
     *
     * @param context
     */
    public static void saveKeyToPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("key", Base64.encode(Constant.KEY));
        editor.commit();
    }

    /**
     * 读取加密的Key
     *
     * @param context
     */
    public static void readKeyFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String key_Base64 = preferences.getString("key", "");
        Constant.KEY = Base64.decode(key_Base64);
    }


    /**
     * 保存用户当前主机Id
     *
     * @param context
     */
    public static void saveCurrentHostIdToPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentHostId", Constant.CURRENTHOSTID);
        editor.commit();
    }

    /**
     * 读取用户使用的主机Id
     *
     * @param context
     */
    public static void readCurrentHostIdFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Constant.CURRENTHOSTID = preferences.getString("currentHostId", "");
    }

    /**
     * 移除所有值 保存用户登录名和密码
     *
     * @param context
     */
    public static void clearAll(Context context) {
        Constant.KEY = null;
        Constant.TOKEN = "";
        Constant.USERID = "";
        Constant.LOGIN_USER = null;
        Constant.CURRENTHOSTID = "";

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("password", "");
        editor.commit();
    }

    /**
     * 移除所有值
     *
     * @param context
     */
    public static void clear(Context context) {
//        Constant.KEY = null;
//        Constant.TOKEN = "";
//        Constant.USERID = "";
//        Constant.LOGIN_USER = null;
//        Constant.CURRENTHOSTID = "";

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
//        String password = preferences.readString("password", "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
////        //将原来的用户名保留下来
        editor.putString("username", username);
//        editor.saveString("password", password);
        editor.commit();
    }

    /**
     * 保存用户登录账号
     *
     * @param context
     */
    public static void saveLoginUserNameAndPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", Constant.LOGIN_USERNNME);
        editor.putString("password", Constant.LOGIN_PASSWORD);
        editor.commit();
    }

    /**
     * 读取用户登录账号
     *
     * @param context
     */
    public static void readLoginUserNameAndPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Constant.LOGIN_USERNNME = preferences.getString("username", "");
        Constant.LOGIN_PASSWORD = preferences.getString("password", "");
    }


    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int readInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long readLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key,defaultValue);
    }

    public static final long VERIFY_TIME=
            24*60*60*1000;
//            180*1000;
    public static final int VERIFY_COUNT=
            10;
//            2;
    public static boolean isCanSendVerifyCode(Context context,String type){
        int count=SharedPreferencesUtils.readInt(context,type+"VerifyCodeCount",0);
        long timestamp=SharedPreferencesUtils.readLong(context,type+"VerifyCodeTimestamp",System.currentTimeMillis());
        if(System.currentTimeMillis()-timestamp>=VERIFY_TIME){
            count=0;
        }
        if(count<VERIFY_COUNT){
            if(count==0){
                SharedPreferencesUtils.saveLong(context,type+"VerifyCodeTimestamp",System.currentTimeMillis());
            }
            count++;
            SharedPreferencesUtils.saveInt(context,type+"VerifyCodeCount",count);
            return true;
        }
        else{
            return false;
        }
    }
}
