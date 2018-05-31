package com.boer.delos.activity.smartdoorbell;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.AlarmMessageInfo;
import com.boer.delos.activity.smartdoorbell.imageloader.FileHelper;
import com.boer.delos.activity.smartdoorbell.imageloader.HttpsURLConnectionHelp;
import com.boer.delos.activity.smartdoorbell.imageloader.ImageLoaderThreadPool;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.request.smartdoorbell.ICVSSUserModule;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ZipUtils;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.alarm.AlarmType;
import com.eques.icvss.utils.Method;
import com.eques.icvss.utils.ResultCode;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import cn.jzvd.JZVideoPlayerStandard;

import static com.boer.delos.activity.smartdoorbell.imageloader.FileHelper.getRootFilePath;

/**
 * Created by Administrator on 2018/1/24.
 */

public class SmartDoorbellShowAlarmActivity extends CommonBaseActivity{
    @Bind(R.id.ivPic)
    ImageView ivPic;
    @Bind(R.id.vpPics)
    ViewPager vpPics;
    @Bind(R.id.flPics)
    FrameLayout flPics;
    @Bind(R.id.tvPicIndex)
    TextView tvPicIndex;
    @Bind(R.id.vvVideo)
    JZVideoPlayerStandard vvVideo;
    private ICVSSUserInstance icvss;
    private ImageLoaderThreadPool imageLoaderThreadPool;
    private String alarmFilePath;
    private String alarmFileName;
    private AlarmMessageInfo alarmMessageInfo;
    private ArrayList<String> fileNames=new ArrayList<>(5);
    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_show_alarm;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("报警消息");
        tlTitleLayout.setLinearRightImage(R.drawable.ic_download);

        int windowWidth = FileHelper.getScreenWidth(this);
        int width = windowWidth;
        int height = (width * 3)/ 4;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        ivPic.setLayoutParams(params);

        vvVideo.setLayoutParams(params);

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width, height);
        vpPics.setLayoutParams(params1);
        vpPics.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPicIndex.setText((position+1)+"/"+jpgList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        alarmMessageInfo=(AlarmMessageInfo)getIntent().getSerializableExtra("alarmInfo");
        imageLoaderThreadPool = ImageLoaderThreadPool.getInstance(1, ImageLoaderThreadPool.Type.LIFO);

        String prePicUrl=icvss.equesGetThumbUrl(alarmMessageInfo.getPvids().get(0),alarmMessageInfo.getBid()).toString();
        String imagePathTemp = getAlarmPath("thumbImage") + alarmMessageInfo.getPvids().get(0)+".jpg";
        ivPic.setTag(imagePathTemp);
        if(StringUtils.isNotBlank(prePicUrl.toString()) && StringUtils.isNotBlank(imagePathTemp)){
            imageLoaderThreadPool.loadImage(prePicUrl.toString(), ivPic, imagePathTemp, imagePathTemp);
        }else{
            ivPic.setImageResource(R.drawable.bg_door_bell_camera);
        }

        alarmFilePath=getAlarmPath("fileImage");
        String extension="";
        if(alarmMessageInfo.getType()== AlarmType.PIR_VIDEO.code){
            extension=".mp4";
        }
        else if(alarmMessageInfo.getType() == AlarmType.PIR_ZIP.code){
            extension=".zip";
        }
        else{
            extension=".jpg";
        }
        alarmFileName=alarmFilePath+alarmMessageInfo.getAlarmTime()+extension;

        String alarmFileUrl = icvss.equesGetAlarmfileUrl(alarmMessageInfo.getFids().get(0), alarmMessageInfo.getBid()).toString();
        toastUtils.showProgress("");
        new DownLoadImage(alarmFileUrl, alarmFileName).start();
    }

    @Override
    public void rightViewClick() {
        String[] array = (String[])fileNames.toArray(new String[fileNames.size()]);
        MediaScannerConnection.scanFile(this, array, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.showShortMsg("保存到相册");
                    }
                });
            }
        });
    }

    public String getAlarmPath(String path) {
        String camPicPath = getRootFilePath() + getPackageName() + File.separator + "AlarmFile" + File.separator+path+File.separator;
        return camPicPath;
    }


    private ArrayList<String> jpgList=new ArrayList<>(5);
    public static final int MESSAGE_WHAT_SUCCESS = 1000; //下载成功
    public static final int MESSAGE_WHAT_FAILED = 1001; //下载失败
    public static final int MESSAGE_WHAT_VIDEO_PLAY = 1002; //播放报警视频
    private boolean loadFlag;
    class DownLoadImage extends Thread{

        private String downLoadUrl;
        private String downLoadpath;

        public DownLoadImage(String url, String path){
            this.downLoadUrl = url;
            this.downLoadpath = path;
        }

        public void run() {
            // 最后从指定的url中下载图片
            try {
                HttpsURLConnection conn = HttpsURLConnectionHelp.getHttpsConnection(downLoadUrl);
                //设置连接超时时长
                conn.setInstanceFollowRedirects(true);

                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    InputStream is = conn.getInputStream();

                    boolean bo = FileHelper.writeFile(downLoadpath, is);
                    if(bo){
                        if(alarmMessageInfo.getType() == AlarmType.PIR_VIDEO.code){

                            Message msg = new Message();
                            msg.what = MESSAGE_WHAT_VIDEO_PLAY;
                            msg.obj = downLoadpath;
                            mHandler.sendMessage(msg);
                            fileNames.add(alarmFileName);
                        }else{
                            if(alarmMessageInfo.getType() == AlarmType.PIR_ZIP.code){
                                String unZipPath = downLoadpath.substring(0, downLoadpath.indexOf(alarmMessageInfo.getAlarmTime() + ".zip"));
                                unZipPath=unZipPath.substring(0,unZipPath.length()-1);

                                jpgList=ZipUtils.upZipFileReturnList(new File(downLoadpath), unZipPath);
                                fileNames=jpgList;
                                File myFilePath = new File(downLoadpath);
                                myFilePath.delete();
                            }
                            else{
                                jpgList.add(alarmFileName);
                                fileNames.add(alarmFileName);
                            }
                            loadFlag = true;
                            showImage();
                        }
                    }else{
                        mHandler.sendEmptyMessage(MESSAGE_WHAT_FAILED);
                    }
                }else{
                    mHandler.removeMessages(MESSAGE_WHAT_FAILED);

                    InputStream stream = conn.getErrorStream();
                    String json = Inputstr2Str_byteArr(stream, "utf-8");
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.optInt(Method.ATTR_ERROR_CODE);

                    Message msg = new Message();
                    msg.what = MESSAGE_WHAT_FAILED;
                    String obj;
                    if(code == ResultCode.NOTFOUND_PIC){
                        obj = "读取失败,图片已在其它客户端删除";

                    }else if(code == ResultCode.EXPIRED_TOKEN){
                        obj = "加载失败,Token值过期";

                    }else{
                        obj = "加载失败,请检查网络";
                    }
                    msg.obj = obj;
                    mHandler.sendMessage(msg);
                }

            } catch (Exception ex) {
                mHandler.sendEmptyMessage(MESSAGE_WHAT_FAILED);
            }
        }
    }

    private void showImage(){
        if(jpgList.size() <= 0){
            if(loadFlag){
                mHandler.sendEmptyMessage(MESSAGE_WHAT_FAILED);
            }else{
                //downLoadImage();
            }
            return;
        }

        Collections.sort(jpgList);

        Message msg = new Message();
        msg.what = MESSAGE_WHAT_SUCCESS;
        msg.obj = jpgList;
        mHandler.sendMessage(msg);

    }

    public String Inputstr2Str_byteArr(InputStream in, String encode){
        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;
        try
        {
            if (encode == null || encode.equals(""))
            {
                // 默认以utf-8形式
                encode = "utf-8";
            }
            while ((len = in.read(b)) != -1)
            {
                sb.append(new String(b, 0, len, encode));
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            toastUtils.dismiss();
            switch (msg.what) {
                case MESSAGE_WHAT_SUCCESS:
                    ArrayList<String> imageNames = (ArrayList<String>) msg.obj;
                    ArrayList<View> imageViews = new ArrayList<View>();

                    for (int i = 0; i < imageNames.size(); i++) {
                        ImageView image = new ImageView(mContext);
                        imageViews.add(image);
                    }

                    ivPic.setVisibility(View.GONE);
                    flPics.setVisibility(View.VISIBLE);

                    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(imageViews, imageNames);
                    vpPics.setAdapter(myPagerAdapter);
                    tvPicIndex.setText((1)+"/"+jpgList.size());
                    break;

                case MESSAGE_WHAT_FAILED:
                    String obj = (String)msg.obj;
                    if(obj != null){
                        ToastHelper.showShortMsg(obj);
                    }

                    break;

                case MESSAGE_WHAT_VIDEO_PLAY:
                    String mp4PlayPath = (String) msg.obj;
                    Uri uri = Uri.parse("file://"+mp4PlayPath);
//                    // 调用系统自带的播放器
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(uri, "video/*");
//                    startActivity(intent);

//                    ivPic.setVisibility(View.GONE);
//                    vvVideo.setVisibility(View.VISIBLE);
//                    vvVideo.setMediaController(new MediaController(mContext));
//                    vvVideo.setVideoURI(uri);
//                    vvVideo.start();
//                    vvVideo.requestFocus();
                    ivPic.setVisibility(View.GONE);
                    vvVideo.setVisibility(View.VISIBLE);
                    vvVideo.setUp("file://"+mp4PlayPath, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");
                    vvVideo.thumbImageView.setImageResource(R.drawable.bg_door_bell_camera);
                    break;

                default:
                    break;
            }
        };
    };

    class MyPagerAdapter extends PagerAdapter {

        private ArrayList<View> imageViews;
        private ArrayList<String> fileNames;

        public MyPagerAdapter(ArrayList<View> views, ArrayList<String> names){
            this.imageViews = views;
            this.fileNames = names;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = (ImageView)imageViews.get(position);
            String fileName = fileNames.get(position);

            Bitmap bitmap = null;
            try
            {
                String imagePath =  fileName;

                if(FileHelper.fileIsExist(imagePath)){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig=null;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            view.setImageBitmap(bitmap);
            container.removeView(imageViews.get(position));
            container.addView(imageViews.get(position));

            return imageViews.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View)object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeMessages(MESSAGE_WHAT_SUCCESS);
        mHandler.removeMessages(MESSAGE_WHAT_FAILED);
        mHandler.removeMessages(MESSAGE_WHAT_VIDEO_PLAY);
    }
}
