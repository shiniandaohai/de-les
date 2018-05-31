package com.boer.delos.utils.updateapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.utils.ExternalStorageUtils;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreTag;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/27 0027.
 * 检测app版本信息，提示是否更新
 */
public class UpdateApp {
    private static final int NOTIFICATION_ID = 1000;
    private Context mContext;
    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private ProgressDialog pBar;

    private volatile boolean mIsExitThread = false;
    private int apkLength;

    public UpdateApp(Context context) {
        mContext = context;
        updateSoft();
    }

    //更新方法
    private void updateSoft() {
        // 自动检查有没有新版本 如果有新版本就提示更新
        new Thread() {
            public void run() {
                try {
                    UpdateInfoService updateInfoService = new UpdateInfoService(
                            mContext);
                    info = updateInfoService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /**
     * app自动更新功能
     */

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // 如果有更新就提示
            if (isNeedUpdate() && Constant.IS_WIFI) {

                if (mContext != null && ((BaseActivity) mContext).toastUtils.isShowing()) {
                    ((BaseActivity) mContext).toastUtils.dismiss();
                }
                showUpdateDialog();
                //发送通知
//                BaseApplication.getAppContext().sendBroadcast(new Intent());
            }
            //WIFI 下自动更新
        }

        ;
    };


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("检测到APP新版本v" + info.getVersion());
        builder.setTitle("检测到APP新版本,请你及时更新");
        if (info.getDescription().contains("测试")) {
            builder.setMessage("新版本功能升级，性能体验提升,请您及时升级");
        } else
            builder.setMessage(info.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile(info.getUrl());     //在下面的代码段
                } else {
                    Toast.makeText(mContext, "SD卡不可用，请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesUtils.saveString(BaseApplication.getAppContext(), SharedPreTag.APP_VERSION, getVersion());
            }

        });
        builder.create().show();
    }

    private boolean isNeedUpdate() {
        if (info == null) {
            return false;
        }
        String newVersion = info.getVersion(); // 最新版本的版本号
        String currentVersion = getVersion();

        String saveVersion = SharedPreferencesUtils.readString(BaseApplication.getAppContext(),
                SharedPreTag.APP_VERSION, currentVersion);

        if (newVersion.equals(currentVersion)
                || (!StringUtil.isEmpty(saveVersion) && newVersion.equals(saveVersion))) {
            return false;
        } else {
            return true;
        }
    }

    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    mContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    private void downFile(final String url) {
        pBar = new ProgressDialog(mContext);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();
        File file = new File(ExternalStorageUtils.CACHE_DIR + "/app");
        if (!file.exists()) {
            file.mkdirs();
        }

        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                if (!mIsExitThread) {
                    try {
                        response = client.execute(get);
                        HttpEntity entity = response.getEntity();
                        apkLength = (int) entity.getContentLength();   //获取文件大小
                        pBar.setMax(apkLength);                            //设置进度条的总长度
                        InputStream is = entity.getContent();
                        FileOutputStream fileOutputStream = null;
                        if (is != null) {
                            File file = new File(
                                    ExternalStorageUtils.CACHE_DIR + "/app",
                                    "家卫士.apk");
                            Loger.d("APk file length " + file.length());
                            if (file.exists() && file.length() == apkLength) {
                                down();
                                return;
                            }

                            fileOutputStream = new FileOutputStream(file);
                            byte[] buf = new byte[1024];
                            int ch = -1;
                            int process = 0;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);
                                process += ch;
                                pBar.setProgress(process);
                            }
                            fileOutputStream.flush();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        down();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
    }

    private void down() {
        handler1.postDelayed(new Runnable() {
            public void run() {
                pBar.cancel();
                mIsExitThread = false;
                update();
            }
        }, 1000);
    }

    //安装文件，一般固定写法
    void update() {
        File apkfile = new File(ExternalStorageUtils.CACHE_DIR + "/app", "家卫士.apk");
        if (!apkfile.exists() || apkfile.length() != apkLength) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(ExternalStorageUtils.CACHE_DIR + "/app", "家卫士.apk")),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

    }
}
