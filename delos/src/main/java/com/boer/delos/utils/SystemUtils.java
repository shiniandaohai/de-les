package com.boer.delos.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by zhukang on 16/7/23.
 */
public class SystemUtils {

    /**
     * 打开手机屏幕
     * @param context
     */
    public static void screenOn(Context context){
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);// init powerManager
        final PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK, "target"); // this target for tell OS which app control screen
        mWakelock.acquire(); // Wake up Screen and keep screen lighting
        //5秒后息屏
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWakelock.release(); // release control.stop to keep screen lighting
            }
        }, 5000);
    }

    /**
     * 打开手机震动
     * @param context
     */
    public static void vibratorOn(Context context){
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(5000);             //震动5秒
    }

    /**
     * 手机解锁
     */
    public static void keyguardUnLock(Context context){
        // 键盘锁管理器对象
        KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        // 这里参数”kale”作为调试时LogCat中的Tag
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("kale");
        if (km.inKeyguardRestrictedInputMode()) {
            // 解锁键盘
            kl.disableKeyguard();
        }
    }

    /**
     * 系统声音播放
     * @param context
     */
    public static void soundPlay(Context context){
        Uri systemDefaultRing = RingtoneManager.getActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_ALARM);
        MediaPlayer mMediaPlayer = MediaPlayer.create(context, systemDefaultRing);
        mMediaPlayer.setLooping(false);
        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }
    /**
     * 获得版本号
     */
    public static int getVerCode(Context context){
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo("com.boer.delos", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("版本号获取异常", e.getMessage());
        }
        return verCode;
    }

    /**
     * 获得版本名称
     */
    public static String getVerName(Context context){
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo("com.boericasa.smartmirror.lanucher", 0).versionName;
            Log.d("VERSION","version:"+verName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("版本名称获取异常", e.getMessage());
        }
        return verName;
    }
}
