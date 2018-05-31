package com.boer.delos.activity.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.view.TitleLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class PhotoViewerActivity extends CommonBaseActivity
        implements GestureDetector.OnGestureListener, View.OnClickListener {
    private static final int FLING_MIN_DISTANCE = 20;
    private static final int FLING_MIN_VELOCITY = 0;
    private GestureDetector mGestureDetector;
    private String mFileName; //加绝对路径的文件名
    private String filename;
    private String[] filenames;//文件名集合
    private List<String> IMAGE_FILES;//加绝对路径文件名集合
    private List<String> namelist;
    private int mPosition;
    private int mSize;
    private Bitmap bm;
    private ImageView iv;
    private ImageButton saveImg, deleteImg;
    private TitleLayout titleLayout;

    @Override
    protected int initLayout() {
        return R.layout.activity_photoview;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        //BitmapFactory.Options bfo = new BitmapFactory.Options();
        //bfo.inSampleSize = 2;
        filenames = extras.getStringArray("filenames");
        IMAGE_FILES = extras.getStringArrayList("files");
        mPosition = extras.getInt("pos");
        mFileName = IMAGE_FILES.get(mPosition);
        filename = filenames[mPosition];
        mSize = IMAGE_FILES.size();
        namelist = Arrays.asList(filenames);
        saveImg = (ImageButton) findViewById(R.id.btn_save);
        deleteImg = (ImageButton) findViewById(R.id.btn_delete);
        iv = (ImageView) findViewById(R.id.iv_photo);
        mGestureDetector = new GestureDetector(this);
        bm = BitmapFactory.decodeFile(mFileName);// ,bfo);
        iv.setImageBitmap(bm);
        titleLayout = (TitleLayout) findViewById(R.id.title);
        saveImg.setOnClickListener(this);
        deleteImg.setOnClickListener(this);
    }

    @Override
    protected void initAction() {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling left
            if (mPosition == (mSize - 1)) {
                mPosition = 0;
                mFileName = IMAGE_FILES.get(mPosition);
                bm = BitmapFactory.decodeFile(mFileName);
                iv.setImageBitmap(bm);
//		        setContentView(iv);
            } else {
                mPosition += 1;
                mFileName = IMAGE_FILES.get(mPosition);
                bm = BitmapFactory.decodeFile(mFileName);
                iv.setImageBitmap(bm);
                //setContentView(iv);
            }
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling right
            if (mPosition == 0) {
                mPosition = mSize - 1;
                mFileName = IMAGE_FILES.get(mPosition);
                bm = BitmapFactory.decodeFile(mFileName);
                iv.setImageBitmap(bm);
                //setContentView(iv);
            } else {
                mPosition -= 1;
                mFileName = IMAGE_FILES.get(mPosition);
                bm = BitmapFactory.decodeFile(mFileName);
                iv.setImageBitmap(bm);
                //setContentView(iv);
            }
        }
        filename = namelist.get(mPosition);
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                copyfile(mFileName, filename);

                break;
            case R.id.btn_delete:
                File file = new File(mFileName);

                IMAGE_FILES.remove(mPosition);
                mSize = IMAGE_FILES.size();
                if (mSize == 0) {
                    this.finish();
                    break;
                }
                if (mPosition == mSize) {
                    mPosition = 0;
                    mFileName = IMAGE_FILES.get(mPosition);
                    bm = BitmapFactory.decodeFile(mFileName);
                    iv.setImageBitmap(bm);
//		        setContentView(iv);
                } else {
                    mPosition += 1;
                    mFileName = IMAGE_FILES.get(mPosition);
                    bm = BitmapFactory.decodeFile(mFileName);
                    iv.setImageBitmap(bm);
                    //setContentView(iv);
                }
                filename = namelist.get(mPosition);
                file.delete();
                toastUtils.showSuccessWithStatus(getString(R.string.delete_sucess));
                mHandler.sendEmptyMessageDelayed(0, 1300);
                break;
        }
    }

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivity.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    //复制文件
    private void copyfile(String path, String filename) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(path);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(path); //读入原文件
                FileOutputStream fs = new FileOutputStream("/sdcard/DCIM/Camera/" + filename);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                inStream.close();
                toastUtils.showSuccessWithStatus(getString(R.string.save_success));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //4.4以上
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File("/sdcard/DCIM/Camera/" + filename));
                    intent.setData(contentUri);
                    this.sendBroadcast(intent);
                } else {
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory())));
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}
