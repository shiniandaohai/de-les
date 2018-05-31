package com.boer.delos.jpush;

import android.app.Notification;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.boer.delos.commen.BaseActivity;
import com.boer.delos.utils.L2;
import com.boer.delos.utils.PublicMethod;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by dell on 2015/10/12.
 */
public class JPushInit extends BaseActivity {
    private static final String TAG = "JPush";
    private static Context context;
    private static JPushHandler jPushHandler;

    private static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    /**
     * JPush配置
     */

    public JPushInit(Context context, String tagValue, String aliasValue) {
        jPushHandler = new JPushHandler();
        this.context = context;
        //初始化JPush
        JPushInterface.init(context);
//        JPushInterface.setDebugMode(true);

        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);

        builder.notificationDefaults = Notification.DEFAULT_LIGHTS;  // 设置呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);

        JPushInterface.setDefaultPushNotificationBuilder(builder);
        //调用JPush API设置Tag,为安装了应用程序的用户，打上标签。其目的主要是方便开发者根据标签，来批量下发 Push 消息。
        String tag = tagValue;
        // 检查 tag 的有效性
        if (!TextUtils.isEmpty(tag)) {
            // ","隔开的多个 转换成 Set
            String[] sArray = tag.split(",");
            Set<String> tagSet = new LinkedHashSet<String>();
            for (String sTagItme : sArray) {
                if (!isValidTagAndAlias(sTagItme)) {
                    return;
                }
                tagSet.add(sTagItme);
            }
            jPushHandler.sendMessage(jPushHandler.obtainMessage(MSG_SET_TAGS, tagSet));
        }

        //调用JPush API设置Alias,为安装了应用程序的用户，取个别名来标识。以后给该用户 Push 消息时，就可以用此别名来指定。
        String alias = aliasValue;

        if (!TextUtils.isEmpty(alias)) {
            if (!isValidTagAndAlias(alias)) {
                jPushHandler.sendMessage(jPushHandler.obtainMessage(MSG_SET_ALIAS, alias));
            }
        }

        //集成了 JPush SDK 的应用程序在第一次成功注册到 JPush 服务器时，JPush 服务器会给客户端返回一个唯一的该设备的标识 - RegistrationID。
        // JPush SDK 会以广播的形式发送 RegistrationID 到应用程序。应用程序可以把此 RegistrationID 保存以自己的应用服务器上，
        // 然后就可以根据 RegistrationID 来向设备推送消息或者通知。
        PublicMethod.registrationID = JPushInterface.getRegistrationID(context);
        // L.e("registrationID", StaticParameter.registrationID);
        L2.e("registrationID", JPushInterface.getRegistrationID(context));
    }


    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]");
        Matcher m = p.matcher(s);
        return m.matches();
    }


    private static class JPushHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    L2.d(TAG, "Set alias in handler.");
                    try {
                        JPushInterface.setAliasAndTags(context, (String) msg.obj, null, mAliasCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case MSG_SET_TAGS:
                    L2.d(TAG, "Set tags in handler.");
                    try {
                        JPushInterface.setAliasAndTags(context, null, (Set<String>) msg.obj, mTagsCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    L2.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    }
   /* private final Handler jPushHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(context, (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(context, null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default_charge:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };*/


    private static TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    L2.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    L2.i(TAG, logs);
                    if (isConnected(context)) {
                        jPushHandler.sendMessageDelayed(jPushHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    L2.i(TAG, logs);
            }

        }

    };

    private static TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    L2.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    L2.i(TAG, logs);
                    if (isConnected(context)) {
                        jPushHandler.sendMessageDelayed(jPushHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    L2.i(TAG, logs);
            }

        }

    };

}
