package com.boer.delos.view.popupWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Apk;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ToastHelper;


public class ApkPopupWindow extends PopupWindow implements OnClickListener {

    private static final int TASK_UPDATE_DOWN_FINISH = 0x04;
    private static final int TASK_UPDATE_ALREADY = 0x05;
    private static final int TASK_UPDATE_PROGRESS = 0x06;
    private static final int TASK_UPDATE_ERROR = 0x07;
    private Context mContext;
    private View mRoot;
    private View show;
    private Apk apk;
    private SeekBar updateSeekbar;
    private long apkCount;
    private long apkDownCount;
    private boolean update = true;//正在下载
    private boolean downed = false;
    private String currentUrl;
    private String currentApkName;
    private View.OnKeyListener mOnKeyListener;

    public ApkPopupWindow(Activity context, View root) {
        super(context);
        this.mContext = context;
        this.mRoot = root;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        show = inflater.inflate(R.layout.popupwindow_update, null);
        updateSeekbar = (SeekBar) show.findViewById(R.id.updateSeekbar);
        updateSeekbar.setVisibility(View.GONE);
        show.findViewById(R.id.update).setOnClickListener(this);
        Button btn_exit = (Button) show.findViewById(R.id.exit);
        btn_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                update = false;
                downed = false;
                handler.sendEmptyMessage(TASK_UPDATE_DOWN_FINISH);
                try {
                    if (downThread != null) downThread.interrupt();
                    downThread = null;
                } catch (Exception e) {
                }
                dismiss();
            }
        });


        this.setContentView(show);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);

        mOnKeyListener = new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                Log.v("gl","keyCode=="+keyCode);
                if(keyCode == KeyEvent.KEYCODE_BACK){

                }
                return true;
            }
        };
        show.setOnKeyListener(mOnKeyListener);
        show.setFocusable(true);
        show.setFocusableInTouchMode(true);



        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TASK_UPDATE_ALREADY:
                    if (apk != null) {
                        ((TextView) show.findViewById(R.id.txtUpdateVersion)).setText(apk.getVersionName());
                        ((TextView) show.findViewById(R.id.txtUpdateDescription)).setText(apk.getDescription());
                        ((TextView) show.findViewById(R.id.txtUpdateFunction)).setText(Html.fromHtml(apk.getNew_function()));
                    }
                    break;
                case TASK_UPDATE_PROGRESS:                //更新进度条
                    double bai = ((double) apkDownCount / (double) apkCount * 100);
                    updateSeekbar.setSecondaryProgress((int) bai);
                    updateSeekbar.setProgress((int) bai);
                    Log.v("gl", "bai==" + bai);
                    this.sendEmptyMessageDelayed(TASK_UPDATE_PROGRESS, 50);
                    break;
                case TASK_UPDATE_DOWN_FINISH:            //下载应用
                    Log.v("gl", "apk.getFileUrlList().size()==" + apk.getFileUrlList().size());
                    if (apk.getFileUrlList().size() > 0) {
                        currentUrl = apk.getFileUrlList().removeLast();
                        Log.v("gl", "currentUrl==" + currentUrl);
                        downed = true;
                    } else {
                        dismiss();
                        downed = false;
                        handler.removeMessages(TASK_UPDATE_PROGRESS);
                    }
                    break;
                case TASK_UPDATE_ERROR:
                    Toast.makeText(mContext, "更新应用出错，请重试!", Toast.LENGTH_SHORT).show();
                    update = false;
                    downed = false;
                    handler.sendEmptyMessage(TASK_UPDATE_DOWN_FINISH);
                    try {
                        if (downThread != null) downThread.interrupt();
                        downThread = null;
                    } catch (Exception e) {
                    }
                    dismiss();
                    break;
            }
        }
    };

    public void setApk(Apk apk) {
        this.apk = apk;
        handler.sendEmptyMessage(TASK_UPDATE_ALREADY);
    }

    public Apk getApk() {
        return this.apk;
    }

    public boolean getUpdate() {
        return update;
    }

    /**
     * 安装应用
     */
    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Constant.UPDATE_FILE_PATH, currentApkName))
                , "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.update:
                if (apk != null) {        //开启更新
                    v.setEnabled(false);
                    v.setBackgroundResource(R.drawable.shape_btn_yellow2);
                    updateSeekbar.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessage(TASK_UPDATE_DOWN_FINISH);
                    if (!downThread.isAlive()) downThread.start();
                } else {
                    ToastHelper.showLongMsg("file not found!");
                }
                break;
        }
    }

    /**
     *
     */
    Thread downThread = new Thread(new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                if (downed) {
                    downed = false;
                    L.e("down-------------------------------------------------->");
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response;
                    try {
                        handler.sendEmptyMessage(TASK_UPDATE_PROGRESS);
                        HttpGet get = new HttpGet(currentUrl);
                        response = client.execute(get);
                        HttpEntity entity = response.getEntity();
                        apkCount = entity.getContentLength();
                        InputStream is = entity.getContent();
                        FileOutputStream fileOutputStream = null;
                        if (is != null) {
                            currentApkName = currentUrl.substring(currentUrl.lastIndexOf("/"), currentUrl.length());
                            File file = new File(Constant.UPDATE_FILE_PATH, currentApkName);
                            fileOutputStream = new FileOutputStream(file);
                            byte[] b = new byte[1024];
                            int charb = -1;
                            while ((charb = is.read(b)) != -1) {
                                if (update) {
                                    fileOutputStream.write(b, 0, charb);
                                    apkDownCount += charb;
                                }
                            }
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        update();
                        handler.sendEmptyMessage(TASK_UPDATE_DOWN_FINISH);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.v("gl", "Exception=====" + e.getMessage());
                        e.printStackTrace();
//						apkDownCount = 0;
                        downed = true;
                        handler.sendEmptyMessage(TASK_UPDATE_ERROR);
                    }
                }
            }
        }

    });


    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                //do something...
                return false;
            } else {
                return true;
            }
        }
    };
}
