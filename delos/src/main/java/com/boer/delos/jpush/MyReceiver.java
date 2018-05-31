package com.boer.delos.jpush;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ShowExtend;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.jpush.JpushController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.L2;
import com.boer.delos.utils.PublicMethod;
import com.boer.delos.utils.sharedPreferences.SoundAndVibratorPreferences;

import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 * <p>
 * <p>
 * 如果app没有在运行，从起始页开始，进行初始化数据，进入主界面之后，根据推送类型跳转到指定界面，
 * 用户在指定界面点击退出，就回到主界面。如果程序在运行，就直接跳转到指定界面，用户退出，
 * 回到点击推送之前的操作界面
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    public static String jpushType = "";//推送类型
    public static String message = "";//推送显示内容
    public static Bundle sBundle = null;
    public static MediaPlayer mediaPlayer = null;//播放音乐
    private Vibrator vibrator = null;//震动

    @Override
    public void onReceive(final Context context, Intent intent) {
        sBundle = intent.getExtras();
        jpushType = "";
        L2.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(sBundle));


        //是否在运行
        String MY_PKG_NAME = "com.boer.delos";
        boolean isAppRunning = false;//
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(MY_PKG_NAME) && info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                    isAppRunning = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = sBundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            if (!Constant.TOKEN.equals("")) {
                updateToken(context, regId);
            }

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            L2.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + sBundle.getString(JPushInterface.EXTRA_MESSAGE));


            String tmp = sBundle.getString(JPushInterface.EXTRA_MESSAGE);
            L2.d(TAG, "接受到推送下来的通知" + tmp);

            if (tmp == null) {
                tmp = sBundle.getString(JPushInterface.EXTRA_ALERT);
            }
            if (message != null) {
                if (message.equals(tmp)) {//如果上一次接收到的推送消息跟本次一样，则不推送
//                    L2.d(TAG, "与上次推送内容一致，不推送");
                    return;
                } else {
                    message = tmp;
                }
            }

//            showExtend(context);


            //获取到推送，读取本地配置，播放声音以及设置是否震动
            String[] result = new SoundAndVibratorPreferences().readSoundAndVibratorXml(context).split(",");
            try {
                playMusicAndSetVibrator(context, Integer.parseInt(result[0]), Integer.parseInt(result[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message != null) {
                if (isAppRunning)
                    PublicMethod.dealWithJPushResult(sBundle, context);
            }


        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {


        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

//            String tmp = sBundle.getString(JPushInterface.EXTRA_MESSAGE);
//
//            L2.d(TAG, "[MyReceiver] 用户点击打开了通知" + tmp);
//
//            if (tmp == null) {
//                tmp = sBundle.getString(JPushInterface.EXTRA_ALERT);
//            }
//            if (message != null) {
//                if (message.equals(tmp)) {//如果上一次接收到的推送消息跟本次一样，则不推送
////                    L2.d(TAG, "与上次推送内容一致，不推送");
//                    return;
//                } else {
//                    message = tmp;
//                }
//            }
            //程序不在运行中，点击通知，启动app，进入首页弹出窗口
            //如果app退出，关闭所有Activity，保留极光推送服务，点开推送，打开登录界面

//            L.v("gl===" + ActivityCustomManager.getAppManager().isActivityStackEmpty());

//            if (ActivityCustomManager.getAppManager().isActivityStackEmpty()) {
//                Intent i = new Intent(context, MainTabActivity.class);
//                i.putExtras(sBundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);
//            }
            if(Constant.LOGIN_USER==null){
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }
            String tmp = sBundle.getString(JPushInterface.EXTRA_ALERT);
            Intent i = new Intent(context, MainTabActivity.class);
            i.putExtras(sBundle);
            if(tmp.contains("门铃")){
                i.putExtra("flag","flag_push_door");
            }
            else{
                i.putExtra("flag","flag_push_info");
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction()))

        {
            L2.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + sBundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction()))

        {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            L2.d(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else

        {
            L2.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    /**
     * 更新推送设备码请求
     * jpush_android
     */
    private void updateToken(Context mContext, String deviceToken) {
        JpushController.getInstance().updateToken(mContext,
                deviceToken, "jpush", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
//                        L.e("updateToken json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            L.e("成功");
                            // toastUtils.showSuccessWithStatus("分享成功");
                        } else {
                            L.e("失败");
                            // toastUtils.showInfoWithStatus(JsonUtil.ShowMessage(Json));
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        //toastUtils.showErrorWithStatus(readString(R.string.unknow_exception));
                    }
                });
    }

    /**
     * 显示铃声和震动请求
     */
    private void showExtend(final Context mContext) {
        JpushController.getInstance().showExtend(mContext,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
//                        L.e("showExtend json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            L.e("成功");
                            if (Json.contains("user_extend")) {
                                try {
                                    JSONObject jo = new JSONObject(Json);
                                    JSONObject ja = jo.getJSONObject("user_extend");
                                    ShowExtend showExtend = GsonUtil.getObject(ja.toString(), ShowExtend.class);
                                    if (showExtend.getTone() == null || showExtend.getVibration() == null) {

                                    } else {
                                        new SoundAndVibratorPreferences().writeSoundAndVibratorDataXml(mContext, showExtend.getTone(), showExtend.getVibration());
                                        //保存到本地
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            L.e("失败");

                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        //toastUtils.showErrorWithStatus(readString(R.string.unknow_exception));
                    }
                });
    }


    //根据系统设置，播放指定音乐,设置是否震动
    private void playMusicAndSetVibrator(Context context, int musicIndex, int isVibrator) {
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        try {
            if (vibrator.hasVibrator()) {
                //0：无震动，1：震动
                if (isVibrator == 0) {
                    //关闭震动
                    vibrator.cancel();
                } else {
                    //关闭震动
                    vibrator.cancel();
                    //启动震动，并持续指定的时间
                    vibrator.vibrate(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            switch (musicIndex) {
                case 0:
                    mediaPlayer = MediaPlayer.create(context, R.raw.qingxin);
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(context, R.raw.dingdong);
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(context, R.raw.gundong);
                    break;
                case 3:
                    mediaPlayer = MediaPlayer.create(context, R.raw.menghuan);
                    break;
                case 4:
                    mediaPlayer = MediaPlayer.create(context, R.raw.shangwu);
                    break;
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.start();//播放音乐
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
