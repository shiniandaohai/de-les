package com.boer.delos.activity.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.camera.TranslucentStatus;
import com.boer.delos.view.TitleLayout;
import com.tutk.Logger.Glog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressLint({"all", "InflateParams"})
public class GridViewGalleryActivity extends CommonBaseActivity implements TitleLayout.titleLayoutClick, TitleLayout.titleLayoutContentClick,
        OnClickListener {

    private String TAG = "GridViewGalleryActivity";

    private String imagesPath;
    private String videosPath;
    private GridView gridview;
    private ListView videolv;
    private RelativeLayout bottombar;
    private RelativeLayout bottomlinear;

    private Button btnDel;
    private ImageButton btn_change_mode;
    private Button btnselect;
    private ImageButton savebtn, delbtn;

    private boolean mThumbnaillDone = false;

    //camera topBar
    private View camera_topbar_title;
    private ImageView camera_img_left;
    private CheckedTextView camera_tvContentLeft;
    private CheckedTextView camera_tvContentRight;
    private TextView camera_tv_right;
    private LoadVideoThumbnail mLoadVideoThumbnail;


    private enum MyMode {
        PHOTO, VIDEO
    }

    private MyMode mMode = MyMode.PHOTO;

    /**
     * The Constant DEFAULT_LIST_SIZE.
     */
    private static final int DEFAULT_LIST_SIZE = 1;
    /**
     * The Constant IMAGE_RESOURCE_IDS.
     */
    final List<String> IMAGE_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
    final List<String> VIDEO_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
    private List<String> videoPath = new ArrayList<String>(DEFAULT_LIST_SIZE);
    private List<Bitmap> videoImage = new ArrayList<Bitmap>(DEFAULT_LIST_SIZE);
    private List<Boolean> multiDel_photo = new ArrayList<Boolean>();
    private List<Boolean> multiDel_video = new ArrayList<Boolean>();
    private ImageAdapter imageAdapter;
    private VideoAdapter videoAdapter;
    private Object mLock = new Object();
    private boolean mIsEdit = false;
    private boolean mIslayoutshow = false;//是否显示bottomlayout
    private List<Long> timelist = new ArrayList<Long>();
    private String[] imageFiles;

    @Override
    protected int initLayout() {
        return R.layout.activity_camera_gallery;
    }

    protected void initView() {
        tlTitleLayout.setVisibility(View.GONE);
        bottomlinear = (RelativeLayout) findViewById(R.id.bottomlinear);
        savebtn = (ImageButton) findViewById(R.id.btn_save);
        delbtn = (ImageButton) findViewById(R.id.btn_delete);
        btnselect = (Button) findViewById(R.id.btn_select);
        bottombar = (RelativeLayout) findViewById(R.id.gridview_bottom);
        gridview = (GridView) findViewById(R.id.gridview);
        videolv = (ListView) findViewById(R.id.lv_video);

        videolv.setVisibility(View.GONE);
        gridview.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {
        //初始化camera topBar
        initCameraTopBar();
        Bundle extras = this.getIntent().getExtras();
        imagesPath = extras.getString("images_path"); // XXX: extras may be null and data stored in
        videosPath = extras.getString("videos_path");
        setImagesPath(imagesPath);
//          setImagesPath("/storage/emulated/0/Movies");
        //removeCorruptImage();

        videoAdapter = new VideoAdapter(this);
        imageAdapter = new ImageAdapter(this);

        String[] paths = new String[]{videosPath};
        mLoadVideoThumbnail = new LoadVideoThumbnail();//获得视频缩略图和时长
        mLoadVideoThumbnail.execute(paths);
        videolv.setAdapter(videoAdapter);
        gridview.setAdapter(imageAdapter);
    }

    @Override
    protected void initAction() {
        initListener();

    }

    private void initListener() {
        cameraTopBarOnClick();

        savebtn.setOnClickListener(this);
        delbtn.setOnClickListener(this);
        btnselect.setOnClickListener(this);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (!mIsEdit) {
                    //if (mMode == MyMode.PHOTO) {
                    Intent intent = new Intent(GridViewGalleryActivity.this, PhotoViewerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("files", (ArrayList<String>) IMAGE_FILES);
                    bundle.putStringArray("filenames", imageFiles);
                    bundle.putInt("pos", position);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    multiDel_photo.set(position, !multiDel_photo.get(position));
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });
        gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                // TODO Auto-generated method stub
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:


                                L.i("AAAAB" + IMAGE_FILES.size());

                                GridViewGalleryActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        L.i("AAAAB" + "哈哈哈哈");

                                        if (mMode == MyMode.PHOTO) {
                                            // 删除图片操作
                                            imageAdapter.deleteImageAtPosition(position);

                                        } else {
                                            File file = new File(VIDEO_FILES.get(position));
                                            videoImage.remove(position);
                                            videoPath.remove(position);
                                            VIDEO_FILES.remove(position);
                                            file.delete();
                                            if (file.exists()) {
                                                try {
                                                    mLock.wait(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            setVideoPath(videosPath);
                                        }
                                    }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                // No button clicked
                                break;
                        }
                    }
                };
                if (mMode == MyMode.PHOTO) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                    builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisSnapshot))
                            .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                            .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                    builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisRecord))
                            .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                            .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();
                }
                return true;
            }
        });

        videolv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsEdit) {
                    Intent intent = new Intent(GridViewGalleryActivity.this, LocalPlaybackActivity.class);
                    int mSize = VIDEO_FILES.size();

                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("videos", (ArrayList<String>) VIDEO_FILES);
                    bundle.putInt("position", position);
                    bundle.putInt("size", mSize);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    multiDel_video.set(position, !multiDel_video.get(position));
                    videoAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /*初始化 Camera Topbar*/
    private void initCameraTopBar() {
        // camera的topbar

        camera_topbar_title = findViewById(R.id.camera_topbar_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = camera_topbar_title.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            camera_topbar_title.setLayoutParams(params);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.blue);//通知栏所需颜色
        }
        //camera的topbar
        camera_img_left = (ImageView) findViewById(R.id.img_camera_left);

        camera_tvContentLeft = (CheckedTextView) findViewById(R.id.camera_textviewContentLeft);
        camera_tvContentRight = (CheckedTextView) findViewById(R.id.camera_textviewContentRight);
        camera_tv_right = (TextView) findViewById(R.id.camera_tv_right);

        camera_tvContentLeft.setClickable(false);
        camera_tvContentRight.setClickable(true);

    }

    private void cameraTopBarOnClick() {
        camera_img_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        camera_tvContentLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                toastUtils.showInfoWithStatus("哈哈");

                if (mIsEdit) {
                    changeView();
                }
                videolv.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);
//                gridview.setAdapter(imageAdapter);
                setContentLeft();
                mMode = MyMode.PHOTO;
            }
        });
        camera_tvContentRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                toastUtils.showInfoWithStatus("哈哈");

                gridview.setVisibility(View.GONE);
                videolv.setVisibility(View.VISIBLE);
//                videolv.setAdapter(videoAdapter);
                setContentRight();
                if (mIsEdit) {
                    changeView();
                }
                mMode = MyMode.VIDEO;
            }
        });
        camera_tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEdit) {
                    bottombar.startAnimation(AnimationUtils.loadAnimation(GridViewGalleryActivity.this, R.anim.bottombar_slide_show));
                    bottombar.setVisibility(View.VISIBLE);

                    camera_tv_right.setText(R.string.cancel);
                    mIsEdit = true;
//                    if (mMode == MyMode.VIDEO) {
                    btnselect.setVisibility(View.VISIBLE);
//                    }
                    bottomlinear.setVisibility(View.GONE);
                } else {
                    changeView();
                }
            }
        });

    }

    public void setContentLeft() {
        camera_tvContentLeft.toggle();//.setBackground(getDrawable(R.mipmap.left_white_sel));
        camera_tvContentLeft.setTextColor(getResources().getColor(R.color.white));

        camera_tvContentRight.toggle();
        camera_tvContentRight.setTextColor(getResources().getColor(R.color.blue_btn_bg));

        camera_tvContentLeft.setClickable(false);
        camera_tvContentRight.setClickable(true);
        imageAdapter.notifyDataSetChanged();
    }

    public void setContentRight() {
//        camera_tvContentRight.setBackgroundResource(R.drawable.shape_camera_full);
        camera_tvContentRight.toggle();
        camera_tvContentLeft.toggle();
        camera_tvContentRight.setTextColor(getResources().getColor(R.color.white));

//        camera_tvContentLeft.setBackgroundResource(R.drawable.shape_camera_stock);
        camera_tvContentLeft.setTextColor(getResources().getColor(R.color.blue_btn_bg));
        camera_tvContentLeft.setClickable(true);
        camera_tvContentRight.setClickable(false);
        videoAdapter.notifyDataSetChanged();
    }

    /*初始化 Camera Topbar 结束*/
    @Override
    protected void onResume() {
        super.onResume();
        setImagesPath(imagesPath);
//		L.i("AAAA" + imagesPath + " == " + IMAGE_FILES.size() +" == "+ IMAGE_FILES.toString());
    }

    private void setVideoPath(String path) {
        VIDEO_FILES.clear();
        multiDel_video.clear();
        videoPath.clear();
        timelist.clear();

        File folder = new File(path);
        String[] videoFiles = folder.list();

        if (videoFiles != null && videoFiles.length > 0) {
            Arrays.sort(videoFiles);
            for (final String videofile : videoFiles) {
                videoPath.add(videofile);
                Bitmap res_to_bmp = getVideoThumbnail(path + "/" + videofile, 80, 80,
                        MediaStore.Images.Thumbnails.MICRO_KIND);// BitmapFactory.decodeResource(getResources(), R.mipmap.ceo_record_clicked);
                videoImage.add(res_to_bmp);
                Log.i("gwq", "file=" + videofile);
                //videoImage.add(getVideoThumbnail(path+"/"+videofile));
                timelist.add(getTimeLong(path + "/" + videofile));
                multiDel_video.add(false);
                VIDEO_FILES.add(path + "/" + videofile);
            }
        }
        if (videoAdapter != null)
            videoAdapter.notifyDataSetChanged();
    }

    //获得视频文件时长
    private long getTimeLong(String file) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long duration = 0;
        duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration;
    }

    private void setVideoImage(final String path) {
        Glog.I(TAG, "Start to build the thumnail");
        mThumbnaillDone = true;
        File folder = new File(path);
        String[] videoFiles = folder.list();
        int i = 0;

        if (videoFiles != null && videoFiles.length > 0) {
            Arrays.sort(videoFiles);
            for (final String videofile : videoFiles) {
                Bitmap bmp = ThumbnailUtils.createVideoThumbnail(path + "/" + videofile, MediaStore.Video.Thumbnails.MINI_KIND);
                if (bmp == null) {
                    Bitmap res_to_bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ceo_record);
                    videoImage.set(i, res_to_bmp);
                } else {
                    videoImage.set(i, bmp);
                }
                i += 1;
            }
            Glog.I(TAG, "Thumbnaill done");

            if (mMode == MyMode.VIDEO) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //videoAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public final void removeCorruptImage() {
        Iterator<String> it = IMAGE_FILES.iterator();
        while (it.hasNext()) {
            String path = it.next();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            // XXX: CA's hack, snapshot may fail and create corrupted bitmap
            if (bitmap == null) {
                it.remove();
            }
        }
    }

    public final synchronized void setImagesPath(String path) {
        IMAGE_FILES.clear();
        multiDel_photo.clear();
        File folder = new File(path);
        imageFiles = folder.list();

        if (imageFiles != null && imageFiles.length > 0) {
            Arrays.sort(imageFiles);
            for (String imageFile : imageFiles) {
                IMAGE_FILES.add(path + "/" + imageFile);
                multiDel_photo.add(false);
            }
            Collections.reverse(IMAGE_FILES);
        }
        if (imageAdapter != null)
            imageAdapter.notifyDataSetChanged();
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public ImageAdapter(Context c) {
            this.mInflater = LayoutInflater.from(c);
            mContext = c;
        }

        public int getCount() {
            if (IMAGE_FILES.size() == 0) {
                tlTitleLayout.hideRight();
                camera_tv_right.setVisibility(View.INVISIBLE);
            } else {
                camera_tv_right.setVisibility(View.VISIBLE);
                if (mIsEdit) {
                    tlTitleLayout.setRightText(R.string.cancel);
                    camera_tv_right.setText(R.string.cancel);
                } else {
                    tlTitleLayout.setRightText(R.string.txt_sele);
                    camera_tv_right.setText(R.string.txt_sele);
                }


            }
            return IMAGE_FILES.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            convertView = new RelativeLayout(mContext);
            holder = new ViewHolder();

            View view = mInflater.inflate(R.layout.gridview_photo_item, null);
//			holder.photo_thumb_img = new ImageView(mContext);
//			((ViewGroup) view).addView(holder.photo_thumb_img);
            //view.setPadding(8, 0, 8, 8);
            ((ViewGroup) convertView).addView(view);
            holder.del_check_img = (ImageView) convertView.findViewById(R.id.video_image_check);
            holder.photo_thumb_img = (ImageView) convertView.findViewById(R.id.video_image);

            if (holder != null) {
                BitmapFactory.Options bfo = new BitmapFactory.Options();
                bfo.inSampleSize = 4;

                Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_FILES.get(position), bfo);

                // XXX: CA's hack, snapshot may fail and create corrupted bitmap
                if (bitmap == null) {
                    for (int i = this.getCount() - 1; i >= 0; i--) {
                        bitmap = BitmapFactory.decodeFile(IMAGE_FILES.get(i), bfo);
                        if (bitmap != null)
                            break;
                    }
                }

                holder.photo_thumb_img.setBackground(new BitmapDrawable(bitmap));//.setImageBitmap(bitmap);
                if (mIsEdit) {
                    holder.del_check_img.setVisibility(View.VISIBLE);
                    if (multiDel_photo.get(position)) {
                        holder.del_check_img.setImageResource(R.mipmap.ic_device_sel);
                    /*	bottomlinear.setVisibility(View.VISIBLE);
                        btnselect.setVisibility(View.GONE);
					}else {
						btnselect.setVisibility(View.VISIBLE);
						bottomlinear.setVisibility(View.GONE);*/
                    }
                    bottomlinear.setVisibility(View.GONE);
                    btnselect.setVisibility(View.VISIBLE);
                    for (int i = 0; i < multiDel_photo.size(); i++) {
                        if (multiDel_photo.get(i)) {
                            bottomlinear.setVisibility(View.VISIBLE);
                            btnselect.setVisibility(View.GONE);
                            break;
                        }
                    }
                } else {
                    holder.del_check_img.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

        public final boolean deleteImageAtPosition(int position) {
            File file = new File(IMAGE_FILES.get(position));
            boolean deleted = file.delete();
            IMAGE_FILES.remove(position);
            this.notifyDataSetChanged();
            return deleted;
        }

        public final class ViewHolder {
            public ImageView photo_thumb_img;
            public ImageView del_check_img;
        }
    }

    public class VideoAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        public VideoAdapter(Context c) {
            this.mInflater = LayoutInflater.from(c);
            mContext = c;
        }

        public int getCount() {
            if (VIDEO_FILES.size() == 0) {
//                tlTitleLayout.hideRight();
                camera_tv_right.setVisibility(View.INVISIBLE);
            } else {
                camera_tv_right.setVisibility(View.VISIBLE);
                if (mIsEdit) {

                    tlTitleLayout.setRightText(R.string.cancel);
                    camera_tv_right.setText(R.string.cancel);
                } else {

                    tlTitleLayout.setRightText(R.string.txt_sele);
                    camera_tv_right.setText(R.string.txt_sele);
                }
            }
            return VIDEO_FILES.size();
        }

        public String getItem(int position) {
            return VIDEO_FILES.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.gridview_video_item, null);
                convertView.setPadding(8, 8, 8, 8);
                holder.del_check_img = (ImageView) convertView.findViewById(R.id.video_image_check);
                holder.video_thumb_img = (ImageView) convertView.findViewById(R.id.video_image);
                holder.video_text = (TextView) convertView.findViewById(R.id.video_text);
                holder.video_length = (TextView) convertView.findViewById(R.id.tv_length);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (videoImage.size() > 0)
                holder.video_thumb_img.setImageBitmap(videoImage.get(position));
//				holder.video_thumb_img.setBackgroundResource(R.drawable.ceo_record_clicked);
            holder.video_text.setText(videoPath.get(position));
            holder.video_length.setText(FormatTime(timelist.get(position)));
            if (mIsEdit) {
                holder.del_check_img.setVisibility(View.VISIBLE);
                if (multiDel_video.get(position)) {
                    holder.del_check_img.setImageResource(R.mipmap.ic_device_sel);
                        /*bottomlinear.setVisibility(View.VISIBLE);
                        btnselect.setVisibility(View.GONE);
					} else {

						bottomlinear.setVisibility(View.GONE);
						btnselect.setVisibility(View.VISIBLE);*/
                }
                bottomlinear.setVisibility(View.GONE);
                btnselect.setVisibility(View.VISIBLE);
                for (int i = 0; i < multiDel_video.size(); i++) {
                    if (multiDel_video.get(i)) {
                        bottomlinear.setVisibility(View.VISIBLE);
                        btnselect.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.del_check_img.setVisibility(View.GONE);
            }

            return convertView;
        }

        public final boolean deleteImageAtPosition(int position) {
            File file = new File(IMAGE_FILES.get(position));
            boolean deleted = file.delete();
            IMAGE_FILES.remove(position);
            this.notifyDataSetChanged();
            return deleted;
        }

        public final class ViewHolder {
            public ImageView video_thumb_img;
            public ImageView del_check_img;
            public TextView video_text;
            public TextView video_length;
            public TextView video_time;
        }

        private String FormatTime(long time) {
            time /= 1000;
            long minute = time / 60;
            long second = time % 60;
            minute %= 60;

            return String.format("%02d:%02d", minute, second);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                //图片/录像保存到手机相册
                if (mMode == MyMode.PHOTO) {
                    for (int i = 0; i < multiDel_photo.size(); i++) {
                        if (multiDel_photo.get(i)) {
                            copyVideo(IMAGE_FILES.get(i), imageFiles[i]);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(new File("/sdcard/DCIM/Camera/" + imageFiles[i]));
                                intent.setData(contentUri);
                                this.sendBroadcast(intent);
                            } else {
                                sendBroadcast(new Intent(
                                        Intent.ACTION_MEDIA_MOUNTED,
                                        Uri.parse("file://"
                                                + Environment.getExternalStorageDirectory())));
                            }
                            // 最后通知图库更新
                            //Uri uri = Uri.parse("/sdcard/DCIM/Camera/"+imageFiles[i]);
                            //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));//Uri.fromFile(new File(IMAGE_FILES.get(i)))));//.getPath());// Uri.parse("file://" + IMAGE_FILES.get(i))));
                            /*MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_DCIM).getPath() + "/" + imageFiles[i]},null,null);*/
                        }
                    }
                } else {
                    for (int i = 0; i < multiDel_video.size(); i++) {
                        if (multiDel_video.get(i)) {
                            copyVideo(VIDEO_FILES.get(i), videoPath.get(i));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
                                Intent mediaScanIntent = new Intent(
                                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(new File("/sdcard/DCIM/Camera/" + videoPath.get(i)));//VIDEO_FILES.get(i))); //out is your output file
                                mediaScanIntent.setData(contentUri);
                                GridViewGalleryActivity.this.sendBroadcast(mediaScanIntent);
                            } else {
                                sendBroadcast(new Intent(
                                        Intent.ACTION_MEDIA_MOUNTED,
                                        Uri.parse("file://"
                                                + Environment.getExternalStorageDirectory())));
                            }
                        }
                    }
                    // 最后通知图库更新
                    //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"
                    //+ Environment.getExternalStorageDirectory())));
                }
                bottombar.startAnimation(AnimationUtils.loadAnimation(GridViewGalleryActivity.this, R.anim.bottombar_slide_hide));
                bottombar.setVisibility(View.GONE);
                tlTitleLayout.setRightText(R.string.txt_sele);

                camera_tv_right.setText(R.string.txt_sele);

                toastUtils.showSuccessWithStatus(getResources().getString(R.string.save_success));
                mIsEdit = false;
                break;
            case R.id.btn_select:
                //全选按钮
                if (mMode == MyMode.PHOTO) {
                    int deleteList = multiDel_photo.size();
                    for (int i = 0; i < deleteList; i++) {
                        multiDel_photo.set(i, true);
                        bottomlinear.setVisibility(View.VISIBLE);
                        btnselect.setVisibility(View.GONE);
                    }
                    imageAdapter.notifyDataSetChanged();
                } else {
                    int deleteList = multiDel_video.size();
                    for (int i = 0; i < deleteList; i++) {
                        multiDel_video.set(i, true);
                    }
                    bottomlinear.setVisibility(View.VISIBLE);
                    btnselect.setVisibility(View.GONE);
                    videoAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_delete:
                if (mMode == MyMode.PHOTO) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                    builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisSnapshot))
                            .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                            .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                    builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisRecord))
                            .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                            .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();
                }
                break;
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (mMode == MyMode.PHOTO) {
                        int deleteList = multiDel_photo.size();
                        for (int i = 0; i < deleteList; i++) {
                            if (multiDel_photo.get(i)) {
                                File file = new File(IMAGE_FILES.get(i));
                                file.delete();
                            }
                        }
                        setImagesPath(imagesPath);
                        changeView();
                    } else {
                        dialog.dismiss();
                        toastUtils.showProgress("正在删除");
                        int deleteList = multiDel_video.size();
                        for (int i = 0; i < deleteList; i++) {
                            if (multiDel_video.get(i)) {
                                File file = new File(VIDEO_FILES.get(i));
                                file.delete();
                                /*if (file.exists()) {
                                    try {
										mLock.wait(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}*/
                            }
                        }

                        setVideoPath(videosPath);
                        changeView();
                        toastUtils.dismiss();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // No button clicked
                    break;
            }
        }
    };

    //得到也保存的视频信息
    public static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    //复制文件
    private void copyVideo(String path, String filename) {
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
                    //bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    private OnClickListener btnDelClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            if (mMode == MyMode.PHOTO) {
                                int deleteList = multiDel_photo.size();
                                for (int i = 0; i < deleteList; i++) {
                                    if (multiDel_photo.get(i)) {
                                        File file = new File(IMAGE_FILES.get(i));
                                        file.delete();
                                    }
                                }
                                setImagesPath(imagesPath);
                            } else {
                                int deleteList = multiDel_video.size();
                                for (int i = 0; i < deleteList; i++) {
                                    if (multiDel_video.get(i)) {
                                        File file = new File(VIDEO_FILES.get(i));
                                        file.delete();

                                        if (file.exists()) {
                                            try {
                                                mLock.wait(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                                setVideoPath(videosPath);
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                    }
                }
            };
            if (mMode == MyMode.PHOTO) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisSnapshot))
                        .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

                builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisRecord))
                        .setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();
            }
        }
    };

    private void changeView() {
        bottombar.startAnimation(AnimationUtils.loadAnimation(GridViewGalleryActivity.this, R.anim.bottombar_slide_hide));
        bottombar.setVisibility(View.GONE);
        tlTitleLayout.setRightText(R.string.txt_sele);

        camera_tv_right.setText(R.string.txt_sele);

        if (mMode == MyMode.PHOTO) {
            for (int i = 0; i < multiDel_photo.size(); i++) {
                multiDel_photo.set(i, false);
            }
            imageAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < multiDel_video.size(); i++) {
                multiDel_video.set(i, false);
            }
            videoAdapter.notifyDataSetChanged();
        }
        mIsEdit = false;
    }

    @Override
    public void leftViewContentClick() {
        if (mIsEdit) {
            changeView();
        }
        videolv.setVisibility(View.GONE);
        gridview.setVisibility(View.VISIBLE);
//        gridview.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        mMode = MyMode.PHOTO;
    }

    @Override
    public void rightViewContentClick() {
        gridview.setVisibility(View.GONE);
        videolv.setVisibility(View.VISIBLE);
        if (mIsEdit) {
            changeView();
        }
//        videolv.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
    /*	Thread mThread = new Thread(new Runnable() {
            @Override
			public void run() {
				setVideoPath(videosPath);
			}
		});
		mThread.start();*/
        mMode = MyMode.VIDEO;
    }


    //获得视频缩略图
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap == null) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ceo_record_clicked);
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    class LoadVideoThumbnail extends AsyncTask<String, String, List<List>> {
        @Override
        protected void onPreExecute() {
            VIDEO_FILES.clear();
            multiDel_video.clear();
            videoPath.clear();
            timelist.clear();
            findViewById(R.id.tv_loading).setVisibility(View.VISIBLE);
        }

        @Override
        protected List<List> doInBackground(String... paths) {
            List<List> listResult = new ArrayList<>();

            List<String> listDataFiles = new ArrayList<>();
            List<String> videoPath = new ArrayList<>();
            List<Bitmap> videoImage = new ArrayList<>();
            List<Boolean> multiDel_video = new ArrayList<>();
            List<Long> timeLists = new ArrayList<>();

            File folder = new File(paths[0]);
            String[] videoFiles = folder.list();

            if (videoFiles != null && videoFiles.length > 0) {
                Arrays.sort(videoFiles);
                for (final String file : videoFiles) {
                    videoPath.add(file);
                    timeLists.add(getTimeLong(videosPath + "/" + file));
                    //Bitmap res_to_bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ceo_record_clicked);
                    //videoImage.add(res_to_bmp);
                    //Log.i("gwq", "file1=" + getVideoThumbnail(videosPath + "/" + videofile));
                    videoImage.add(getVideoThumbnail(videosPath + "/" + file, 80, 80,
                            MediaStore.Images.Thumbnails.MICRO_KIND));
                    multiDel_video.add(false);
                    listDataFiles.add(videosPath + "/" + file);
                }
            }
            listResult.add(listDataFiles); // 0
            listResult.add(videoPath);     // 1
            listResult.add(videoImage);    // 2
            listResult.add(multiDel_video);// 3
            listResult.add(timeLists);     // 4

            return listResult;
        }

        @Override
        protected void onPostExecute(List<List> lists) {
            super.onPostExecute(lists);
            VIDEO_FILES.addAll(lists.get(0));
            multiDel_video.addAll(lists.get(3));
            videoPath.addAll(lists.get(1));
            timelist.addAll(lists.get(4));
            videoImage.addAll(lists.get(2));
            findViewById(R.id.tv_loading).setVisibility(View.GONE);
            videoAdapter.notifyDataSetChanged();
            //videolv.setAdapter(videoAdapter);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mLoadVideoThumbnail.isCancelled()) {
            mLoadVideoThumbnail.cancel(true);
            mLoadVideoThumbnail = null;
        }
    }
}
