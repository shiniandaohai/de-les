package com.boer.delos.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

import com.boer.delos.R;
import com.boer.delos.jpush.MyReceiver;

/**
 * Created by Administrator on 2015/11/18 0018.
 * 公共方法
 */
public class PublicMethod {
    public static AlertDialog dialog = null;//提示框只显示一个
    public static String registrationID = "";

    //根据推送内容，跳转到指定界面或者弹出对话框
    public static void dealWithJPushResult(final Bundle bundle, final Context context) {
        if (!MyReceiver.message.equals("")) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.alert_dialog);
            builder.setMessage(MyReceiver.message);
            builder.setCancelable(true);
            builder.setPositiveButton("知道了!", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                        MyReceiver.message = null;
                        if (MyReceiver.mediaPlayer.isPlaying())
                            MyReceiver.mediaPlayer.stop();
                        MyReceiver.mediaPlayer.release();
                    }

                }
            });
            if (dialog == null) {
                dialog = builder.create();
                dialog.getWindow()
                        .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            } else {
                dialog.setMessage(MyReceiver.message);
            }
            if (!dialog.isShowing()) {
                dialog.show();
            }

            // MyReceiver.message = "";
        }
    }
}
