package com.boer.delos.activity.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Chronometer;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.utils.ExternalStorageUtils;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StatusBarUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.camera.DatabaseManager;
import com.boer.delos.utils.camera.MyCamera;
import com.boer.delos.utils.camera.VerticalScrollView;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.tutk.IOTC.AVFrame;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.AVIOCTRLDEFs.SStreamDef;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IMonitor;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecListener;
import com.tutk.IOTC.MonitorClickListener;
import com.tutk.IOTC.St_SInfo;
import com.tutk.Logger.Glog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


@SuppressLint("all") //CameraListener
public class CameraMainActivity extends CommonBaseActivity implements
        ViewSwitcher.ViewFactory, IRegisterIOTCListener, MonitorClickListener,
        OnTouchListener, MediaCodecListener,CameraListener{

    private static final int REQUECT_CODE_ = 200;

    private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
    private static final int STS_SNAPSHOT_SCANED = 98;
    // private static final String FILE_TYPE = "image/*";


    private static final int OPT_MENU_ITEM_ALBUM = Menu.FIRST;
    private static final int OPT_MENU_ITEM_SNAPSHOT = Menu.FIRST + 1;
    private static final int OPT_MENU_ITEM_AUDIOCTRL = Menu.FIRST + 4;
    private static final int OPT_MENU_ITEM_AUDIO_IN = Menu.FIRST + 5;
    private static final int OPT_MENU_ITEM_AUDIO_OUT = Menu.FIRST + 6;

    private final int THUMBNAIL_LIMIT_HEIGHT = 720;
    private final int THUMBNAIL_LIMIT_WIDTH = 1280;

    private final String View_Account = "admin", View_Pwd = "admin";
    private IMonitor mHardMonitor = null;
    private MyCamera mCamera = null;
    private Device mDevice = new Device();
    private int EnvironmentMode = 0;
    // private int SOPTZOOM=-1;

    private int OriginallyChannelIndex = -1;

    private TextView txt_title;
    private String mDevUID;
    private String mDevUUID;
    //    private String mConnStatus = "";
    private String mFilePath = "";
    private int mVideoFPS;
    private long mVideoBPS;
    private int mOnlineNm;
    private int mFrameCount;
    private int mIncompleteFrameCount;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mMiniVideoWidth;
    private int mMiniVideoHeight;
    private int mSelectedChannel = 0;
    private byte quality = (byte) 3;

    private RelativeLayout toolbar_layout;
    private RelativeLayout linSpeaker;
    private LinearLayout linPnlCameraInfo;

    private LinearLayout nullLayout;
    private VerticalScrollView myScrollView;
    private HorizontalScrollView myHorScrollView;

    private boolean LevelFlip = false;
    private boolean VerticalFlip = false;
    private boolean isOpenLinQVGA = false;
    private boolean isOpenLinEmode = false;
    private boolean isShowToolBar = true;

    private boolean isShowImg = true;
    private boolean isShow = false;//是否显示清晰度选项

    // TOOLBAR
    private LinearLayout toolbarScrollView;
    private ImageButton button_toolbar_speaker;
    private ImageButton button_toolbar_recording;
    private ImageButton button_toolbar_snapshot;
    /*private ImageButton button_toolbar_mirror;
    private ImageButton button_toolbar_mirror_rl;
	private ImageButton button_toolbar_SET;
	private ImageButton button_toolbar_QVGA;
	private ImageButton button_toolbar_EMODE;*/

    private ImageButton btn_speaker;
    private TextView tv_speaktxt;

    private TextView txtConnectionSlash;
    private TextView txtResolutionSlash;
    private TextView txtShowFPS;
    private TextView txtFPSSlash;
    private TextView txtShowBPS;
    private TextView txtOnlineNumberSlash;
    private TextView txtShowFrameRatio;
    private TextView txtFrameCountSlash;
    private TextView txtQuality;
    private TextView txtRecvFrmPreSec;
    private TextView txtRecvFrmSlash;
    private TextView txtDispFrmPreSeco;
    private TextView recordingtv;

    private TextView txtConnectionStatus;
    private TextView txtConnectionMode;
    private TextView txtResolution;
    private TextView txtFrameRate;
    private TextView txtBitRate;
    private TextView txtOnlineNumber;
    private TextView txtFrameCount;
    private TextView txtIncompleteFrameCount;
    private TextView txtPerformance;
    private TextView mCHTextView;
    private TextView txtCodecMode;
    private TextView tvMode;
    private TextView tvResolution;
    private ImageView ivUpDown;
    private ImageView ivLeftRight;
    private LinearLayout layoutResolution;
    private LinearLayout layoutMode;

    private boolean mIsListening = false;
    private boolean mIsSpeaking = false;
    private boolean mIsRecording = false;

    private BitmapDrawable bg;
    private BitmapDrawable bgSplit;

    private ImageButton CH_button;
    private Button historybtn;  //  jiaweishi

    private Context mContext;
    private boolean unavailable = false;

    private static boolean wait_receive = true;

    private final boolean m_RunSoftDecode = true;
    private final boolean m_RunSoftDecode2 = true;

    //界面隐藏控件
    private RelativeLayout imglayout;
    private CheckedTextView fullimg;
    private CheckedTextView cameralowimg, cameramidimg, camerahighimg, camerastateimg;
    private ProgressBar progressBar;

    private Chronometer chronomter;
    private static final int BITMAP_QUALITY = 40;
    private LinearLayout ll_definition_add, ll_camera_tool_add;
    private List<Device> devices = new ArrayList<>(); //设置常用设备

//    private boolean mSoftCodecDefault; // add by sunzhibin 未知

    //TODO 去掉
    enum FrameMode {
        PORTRAIT, LANDSCAPE_ROW_MAJOR, LANDSCAPE_COL_MAJOR
    }

    private enum EMZoomAction {
        zoom_in, zoom_out
    }

    private FrameMode mFrameMode = FrameMode.PORTRAIT;
    private EMZoomAction ZoomAction = EMZoomAction.zoom_in;
    private final int mZoomOperDelayIn = 1000;// 发送命令间隔时间
    private final int mZoomOperDelayOut = 1000;// 发送命令间隔时间

    private boolean isSaveBitmap = false;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivBack;
    private ImageView ivRight;

    @Override
    protected int initLayout() {
        return R.layout.live_view_portrait_mix;
    }

    @Override
    protected void initView() {

        mContext = this;
        bg = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_striped);
        bgSplit = (BitmapDrawable) getResources().getDrawable(
                R.mipmap.bg_striped_split_img);

        Bundle bundle = getIntent().getExtras();
        DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDevice = deviceRelate.getDeviceProp();
        mDevUID = mDevice.getAddr();
//        mDevUUID = bundle.getString("dev_uuid");
        for (MyCamera camera : MainTabActivity.mCameraList) {
            if (camera.getUID().equals(mDevUID)) {
                mDevUUID = camera.getUUID();
                mCamera = camera;
                break;
            }
        }

        init();
        if (mCamera == null) {
            finish();
        }
        else{
            setupMode(curModeIndex);
            setResult(curResolutionIndex);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }


    @Override
    protected void onResume() {
        super.onResume();

//        if (mCamera != null)
//            mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);

        if (mHardMonitor != null) {
            mHardMonitor.enableDither(mCamera.mEnableDither);
            mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        }

        if (mCamera != null) {
            mCamera.registerIOTCListener(this);
            mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);

            if (mIsListening)
                mCamera.startListening(mSelectedChannel, mIsRecording);
            if (mIsSpeaking)
                mCamera.startSpeaking(mSelectedChannel);
        }

        Configuration cfg = getResources().getConfiguration();
        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (unavailable)
                setupViewInLandscapeLayout_wait();
            else
                setupViewInLandscapeLayout();

        } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (unavailable)
                setupViewInPortraitLayout_wait();
            else
                setupViewInPortraitLayout();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.unregisterIOTCListener(this);
            mCamera.stopSpeaking(mSelectedChannel);
            mCamera.stopListening(mSelectedChannel);
            mCamera.stopShow(mSelectedChannel);
        }

        if (mIsRecording) {
            // mIsSpeaking = false;
            // mIsListening = false;
            mCamera.stopRecording();
            button_toolbar_recording
                    .setBackgroundResource(R.drawable.btn_recording_switch_start);
            mIsRecording = false;
        }
        if (mHardMonitor != null)
            mHardMonitor.deattachCamera();
    }

    private void init() {
        if (mCamera != null) {
            mCamera.registerIOTCListener(this);
            mCamera.SetCameraListener(this);
            if (!mCamera.isSessionConnected()) {

                mCamera.connect(mDevUID);
                mCamera.start(Camera.DEFAULT_AV_CHANNEL, View_Account,
                        View_Pwd);
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                        SMsgAVIoctrlGetSupportStreamReq.parseContent());
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
                                .parseContent());
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration cfg = getResources().getConfiguration();

        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (unavailable)
                setupViewInLandscapeLayout_wait();
            else
                setupViewInLandscapeLayout();

        } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (unavailable)
                setupViewInPortraitLayout_wait();
            else
                setupViewInPortraitLayout();
        }
    }

    private RelativeLayout relativeLayoutLand;
    private RelativeLayout relativeLayoutPort;
    private void setupViewInLandscapeLayout() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(relativeLayoutPort!=null){
            relativeLayoutPort.removeAllViews();
        }
        if(relativeLayoutLand!=null){
            relativeLayoutLand.removeAllViews();
        }
        hideBottomUIMenu();
        setContentView(R.layout.live_view_landscape_mix);



        relativeLayoutLand = (RelativeLayout) findViewById(R.id.relativeLayout);
        if(chronomter==null){
            chronomter =(Chronometer) View.inflate(this,R.layout.layout_camera_chronometer,null);
        }
        relativeLayoutLand.removeAllViews();
        try{
            relativeLayoutLand.addView(chronomter);
        }
        catch (Exception e){
            e.printStackTrace();
        }



        //ImageButton imgback;
        toolbar_layout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        fullimg = (CheckedTextView)findViewById(R.id.img_full);
        /*toolbarScrollView = (LinearLayout) findViewById(R.id.ScrollView_toolbar);
        toolbarScrollView.setHorizontalFadingEdgeEnabled(false);*/
        nullLayout = (LinearLayout) findViewById(R.id.null_layout);

        linPnlCameraInfo = (LinearLayout) findViewById(R.id.pnlCameraInfo);

        recordingtv = (TextView) findViewById(R.id.tv_recording);
        linSpeaker = (RelativeLayout) findViewById(R.id.speaker_layout);
        linSpeaker.setVisibility(View.GONE);

//        chronomter = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        // toolbar
        // add by sunzhibin 动画用
        ll_camera_tool_add = (LinearLayout) findViewById(R.id.ll_camera_tool_add);
        ll_definition_add = (LinearLayout) findViewById(R.id.ll_definition_add);

        button_toolbar_speaker = (ImageButton) findViewById(R.id.button_speaker);
        button_toolbar_recording = (ImageButton) findViewById(R.id.button_recording);
        button_toolbar_snapshot = (ImageButton) findViewById(R.id.button_snapshot);
        //imgback = (ImageButton) findViewById(R.id.img_back);*/
        //清晰度
        camerahighimg = (CheckedTextView) findViewById(R.id.img_high);
        cameramidimg = (CheckedTextView) findViewById(R.id.img_mid);
        cameralowimg = (CheckedTextView) findViewById(R.id.img_low);
        camerastateimg = (CheckedTextView) findViewById(R.id.img_state);

        camerahighimg.setVisibility(View.GONE);
        cameralowimg.setVisibility(View.GONE);
        cameramidimg.setVisibility(View.GONE);

        camerastateimg.setOnClickListener(CameraClick);
        camerahighimg.setOnClickListener(CameraClick);
        cameramidimg.setOnClickListener(CameraClick);
        cameralowimg.setOnClickListener(CameraClick);

        btn_speaker = (ImageButton) findViewById(R.id.btn_speaker);
        btn_speaker.setOnTouchListener(this);
        tv_speaktxt = (TextView) findViewById(R.id.speaker_text);

        button_toolbar_speaker.setOnClickListener(ToolBarClick);

        button_toolbar_recording.setOnClickListener(ToolBarClick);
        button_toolbar_snapshot.setOnClickListener(ToolBarClick);

        if(fullimg!=null)
        fullimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换到竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setupViewInPortraitLayout();
            }
        });

        myScrollView = (VerticalScrollView) findViewById(R.id.scrollView);
        myHorScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

//        toolbar_layout.setVisibility(View.GONE); // modify 2017/3/2
        isShowToolBar = false;
        imglayout = null;
        txtConnectionStatus = null;
        txtConnectionMode = null;
        txtResolution = null;
        txtFrameRate = null;
        txtBitRate = null;
        txtOnlineNumber = null;
        txtFrameCount = null;
        txtIncompleteFrameCount = null;
        txtRecvFrmPreSec = null;
        txtDispFrmPreSeco = null;
        txtPerformance = null;
        txtCodecMode = null;

        mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
        mHardMonitor.setMaxZoom(3f);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);

        myScrollView.setVisibility(View.VISIBLE);
        mHardMonitor.SetOnMonitorClickListener(this);
        mHardMonitor.setMediaCodecListener(this);
        ((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
//        mHardMonitor.cleanFrameQueue();

        wait_receive = true;

        if (mIsRecording&&!mIsListening) {
            button_toolbar_recording.setSelected(true);
            if(chronomter!=null)
                chronomter.setVisibility(View.VISIBLE);

            button_toolbar_speaker.setSelected(false);
            button_toolbar_speaker.getBackground().setAlpha(100);
            linSpeaker.setVisibility(View.INVISIBLE);
        }
        else if(!mIsRecording&&mIsListening){
            button_toolbar_speaker.setSelected(true);
            linSpeaker.setVisibility(View.VISIBLE);

            button_toolbar_recording.setSelected(false);
            button_toolbar_recording.getBackground().setAlpha(100);
            if(chronomter!=null)
            chronomter.setVisibility(View.GONE);
        }
        setupLandscapeToolBar();
    }

    private void setupViewInLandscapeLayout_wait() {
        if (mHardMonitor != null)
            mHardMonitor.deattachCamera();
        mHardMonitor.setMaxZoom(3f);
        //((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        mHardMonitor.SetOnMonitorClickListener(this);
        //calculateSurfaceViewSize();
    }

    private void setupViewInLandscapeLayout_wait_Mediacodec() {
        if (mHardMonitor != null)
            mHardMonitor.deattachCamera();

        mHardMonitor.cleanFrameQueue();
        mHardMonitor.setMaxZoom(3f);
        // ((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        mHardMonitor.SetOnMonitorClickListener(this);
        mHardMonitor.setMediaCodecListener(this);
        calculateSurfaceViewSize();
    }

    private void setupViewInPortraitLayout() {
        if(relativeLayoutLand!=null){
            relativeLayoutLand.removeAllViews();
        }
        if(relativeLayoutPort!=null){
            relativeLayoutPort.removeAllViews();
        }

        setContentView(R.layout.live_view_portrait_mix);
        initTopBar(mDevice.getName(), null, true, true);

        toolbarScrollView = null;
        toolbar_layout = null;

        relativeLayoutPort = (RelativeLayout) findViewById(R.id.relativeLayout);
        if(chronomter==null){
            chronomter =(Chronometer) View.inflate(this,R.layout.layout_camera_chronometer,null);
        }
        relativeLayoutPort.removeAllViews();
        relativeLayoutPort.addView(chronomter);

        nullLayout = (LinearLayout) findViewById(R.id.null_layout);
        recordingtv = (TextView) findViewById(R.id.tv_recording);

        linPnlCameraInfo = (LinearLayout) findViewById(R.id.pnlCameraInfo);

        linSpeaker = (RelativeLayout) findViewById(R.id.speaker_layout);
        linSpeaker.setVisibility(View.INVISIBLE);
        historybtn = (Button) findViewById(R.id.btn_history);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder_img = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/" + mDevUID);
                File folder_video = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Record/" + mDevUID);
                Intent intent = new Intent();
                intent.putExtra("images_path", folder_img.getAbsolutePath());
                intent.putExtra("videos_path", folder_video.getAbsolutePath());
                intent.setClass(CameraMainActivity.this, GridViewGalleryActivity.class);
                startActivity(intent);
            }
        });
        // toolbar
        button_toolbar_speaker = (ImageButton) findViewById(R.id.button_speaker);
        button_toolbar_recording = (ImageButton) findViewById(R.id.button_recording);
        button_toolbar_snapshot = (ImageButton) findViewById(R.id.button_snapshot);

        //清晰度
        camerahighimg = (CheckedTextView) findViewById(R.id.img_high);
        cameramidimg = (CheckedTextView) findViewById(R.id.img_mid);
        cameralowimg = (CheckedTextView) findViewById(R.id.img_low);
        camerastateimg = (CheckedTextView) findViewById(R.id.img_state);

        camerahighimg.setVisibility(View.GONE);
        cameralowimg.setVisibility(View.GONE);
        cameramidimg.setVisibility(View.GONE);

        camerastateimg.setOnClickListener(CameraClick);
        camerahighimg.setOnClickListener(CameraClick);
        cameramidimg.setOnClickListener(CameraClick);
        cameralowimg.setOnClickListener(CameraClick);

        button_toolbar_speaker.setOnClickListener(ToolBarClick);
        button_toolbar_recording.setOnClickListener(ToolBarClick);
        button_toolbar_snapshot.setOnClickListener(ToolBarClick);

        txtConnectionSlash = (TextView) findViewById(R.id.txtConnectionSlash);
        txtResolutionSlash = (TextView) findViewById(R.id.txtResolutionSlash);
        txtShowFPS = (TextView) findViewById(R.id.txtShowFPS);
        txtFPSSlash = (TextView) findViewById(R.id.txtFPSSlash);
        txtShowBPS = (TextView) findViewById(R.id.txtShowBPS);
        txtOnlineNumberSlash = (TextView) findViewById(R.id.txtOnlineNumberSlash);
        txtShowFrameRatio = (TextView) findViewById(R.id.txtShowFrameRatio);
        txtFrameCountSlash = (TextView) findViewById(R.id.txtFrameCountSlash);
        txtQuality = (TextView) findViewById(R.id.txtQuality);
        txtDispFrmPreSeco = (TextView) findViewById(R.id.txtDispFrmPreSeco);
        txtRecvFrmSlash = (TextView) findViewById(R.id.txtRecvFrmSlash);
        txtRecvFrmPreSec = (TextView) findViewById(R.id.txtRecvFrmPreSec);
        txtPerformance = (TextView) findViewById(R.id.txtPerformance);

        txtConnectionStatus = (TextView) findViewById(R.id.txtConnectionStatus);
        txtConnectionMode = (TextView) findViewById(R.id.txtConnectionMode);
        txtResolution = (TextView) findViewById(R.id.txtResolution);
        txtFrameRate = (TextView) findViewById(R.id.txtFrameRate);
        txtBitRate = (TextView) findViewById(R.id.txtBitRate);
        txtOnlineNumber = (TextView) findViewById(R.id.txtOnlineNumber);
        txtFrameCount = (TextView) findViewById(R.id.txtFrameCount);
        txtIncompleteFrameCount = (TextView) findViewById(R.id.txtIncompleteFrameCount);
        txtCodecMode = (TextView) findViewById(R.id.txtCodecMode);
        mCHTextView = (TextView) findViewById(R.id.CH_textview);
        mCHTextView.setText("CH" + (mSelectedChannel + 1));
        CH_button = (ImageButton) findViewById(R.id.CH_button);

        myScrollView = (VerticalScrollView) findViewById(R.id.scrollView);
        myHorScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);


        btn_speaker = (ImageButton) findViewById(R.id.btn_speaker);
        btn_speaker.setOnTouchListener(this);
        tv_speaktxt = (TextView) findViewById(R.id.speaker_text);
        //视频界面控件布局
        imglayout = (RelativeLayout) findViewById(R.id.imglayout);
//        chronomter = (Chronometer) findViewById(R.id.chronometer);
        fullimg = (CheckedTextView) findViewById(R.id.img_full);
        fullimg.setOnClickListener(new View.OnClickListener() { //切换到横屏
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //切换到横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setupViewInLandscapeLayout();
            }
        });


        if (mIsRecording&&!mIsListening) {
            button_toolbar_recording.setSelected(true);
            if(chronomter!=null)
                chronomter.setVisibility(View.VISIBLE);

            button_toolbar_speaker.setSelected(false);
            button_toolbar_speaker.getBackground().setAlpha(100);
            linSpeaker.setVisibility(View.INVISIBLE);
        }
        else if(!mIsRecording&&mIsListening){
            button_toolbar_speaker.setSelected(true);
            linSpeaker.setVisibility(View.VISIBLE);

            button_toolbar_recording.setSelected(false);
            button_toolbar_recording.getBackground().setAlpha(100);
            if(chronomter!=null)
                chronomter.setVisibility(View.GONE);
        }

        if (mCamera != null && mCamera.isSessionConnected()
                && mCamera.getMultiStreamSupported(0)
                && mCamera.getSupportedStream().length > 1) {
            CH_button.setVisibility(View.VISIBLE);
        } else {
            CH_button.setVisibility(View.GONE);
        }
        txtConnectionSlash.setText("");
        txtResolutionSlash.setText("");
        txtShowFPS.setText("");
        txtFPSSlash.setText("");
        txtShowBPS.setText("");
        txtOnlineNumberSlash.setText("");
        txtShowFrameRatio.setText("");
        txtFrameCountSlash.setText("");
        txtRecvFrmSlash.setText("");
        try {
            txtPerformance
                    .setText(getPerformance((int) (((float) mCamera
                            .getDispFrmPreSec() / (float) mCamera
                            .getRecvFrmPreSec()) * 100)));
            L.d("LiewViewActivity Por " + getPerformance((int) (((float) mCamera.getDispFrmPreSec() / (float) mCamera.getRecvFrmPreSec()) * 100)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtConnectionMode.setVisibility(View.GONE);
        txtFrameRate.setVisibility(View.GONE);
        txtBitRate.setVisibility(View.GONE);
        txtFrameCount.setVisibility(View.GONE);
        txtIncompleteFrameCount.setVisibility(View.GONE);
        txtRecvFrmPreSec.setVisibility(View.GONE);
        txtDispFrmPreSeco.setVisibility(View.GONE);
        txtCodecMode.setVisibility(View.GONE);
        /*FPS 在线人数等 控件*/
        linPnlCameraInfo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                CameraListActivity.nShowMessageCount++;
                showMessage();

            }
        });

        CH_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final AlertDialog dlg = new AlertDialog.Builder(
                        CameraMainActivity.this).create();
                ListView view = new ListView(CameraMainActivity.this);
                dlg.setView(view);
                dlg.setCanceledOnTouchOutside(true);
                Window window = dlg.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                // lp.y = -64;
                lp.dimAmount = 0f;

                ArrayAdapter<SStreamDef> adapter = new ArrayAdapter<SStreamDef>(
                        CameraMainActivity.this,
                        android.R.layout.simple_list_item_1);
                for (SStreamDef streamDef : mCamera.getSupportedStream())
                    adapter.add(streamDef);

                view.setAdapter(adapter);
                view.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        if (mHardMonitor != null)
                            mHardMonitor.deattachCamera();

                        mCamera.stopShow(mSelectedChannel);
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopSpeaking(mSelectedChannel);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        System.out.println("OnSpinStreamItemSelected: " + arg2);
                        mSelectedChannel = arg2;
                        mCHTextView.setText("CH" + (mSelectedChannel + 1));


                        mHardMonitor.enableDither(mCamera.mEnableDither);
                        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
                        if (mIsListening)
                            mCamera.startListening(mSelectedChannel,
                                    mIsRecording);
                        if (mIsSpeaking)
                            mCamera.startSpeaking(mSelectedChannel);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);
                                mCHTextView.setText("CH" + (mSelectedChannel + 1));
                            }
                        }, 2500);

                        dlg.dismiss();
                    }
                });

                dlg.show();

            }
        });

        mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
        ((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
        mHardMonitor.setMaxZoom(3f);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        mHardMonitor.SetOnMonitorClickListener(this);
        L.d("LiveViewActivity Por " + myScrollView.getMeasuredHeight());

        wait_receive = true;


        setupPortraitToolBar();
        showBottomUIMenu();
    }

    private void setupViewInPortraitLayout_wait() {

        if (mHardMonitor != null)
            mHardMonitor.deattachCamera();
        // ((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
        mHardMonitor.setMaxZoom(3f);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        mHardMonitor.SetOnMonitorClickListener(this);

//        calculateSurfaceViewSize();
    }

    private void setupViewInPortraitLayout_wait_Mediacodec() {
        if (mHardMonitor != null)
            mHardMonitor.deattachCamera();

//        mHardMonitor = null;
//        mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
        //  ((SurfaceView) mHardMonitor).setZOrderMediaOverlay(false);
        mHardMonitor.cleanFrameQueue();
        mHardMonitor.setMaxZoom(3f);
        mHardMonitor.enableDither(mCamera.mEnableDither);
        mHardMonitor.attachCamera(mCamera, mSelectedChannel);
        mHardMonitor.setMediaCodecListener(this);
        mHardMonitor.setMediaCodecListener(this);
        calculateSurfaceViewSize();
    }

    private void showMessage() {

        St_SInfo stSInfo = new St_SInfo();
        IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);

        if (CameraListActivity.nShowMessageCount >= 10) {

            //txtConnectionStatus.setText(mConnStatus);
            txtConnectionMode.setText(getSessionMode(mCamera != null ? mCamera
                    .getSessionMode() : -1)
                    + " C: "
                    + IOTCAPIs.IOTC_Get_Nat_Type()
                    + ", D: "
                    + stSInfo.NatType
                    + ",R" + mCamera.getbResend());

            txtConnectionSlash.setText(" / ");
            txtResolutionSlash.setText(" / ");
            txtShowFPS.setText(getText(R.string.txtFPS));
            txtFPSSlash.setText(" / ");
            txtShowBPS.setText(getText(R.string.txtBPS));
            // txtShowOnlineNumber.setText(getText(R.string.txtOnlineNumber));
            txtOnlineNumberSlash.setText(" / ");
            txtShowFrameRatio.setText(getText(R.string.txtFrameRatio));
            txtFrameCountSlash.setText(" / ");
            txtQuality.setText(getText(R.string.txtQuality));
            txtRecvFrmSlash.setText(" / ");
            // mCamera.getDispFrmPreSec()
            txtConnectionMode.setVisibility(View.VISIBLE);
            // txtResolution.setVisibility(View.VISIBLE);
            txtFrameRate.setVisibility(View.VISIBLE);
            txtBitRate.setVisibility(View.VISIBLE);
            txtOnlineNumber.setVisibility(View.VISIBLE);
            txtFrameCount.setVisibility(View.VISIBLE);
            txtIncompleteFrameCount.setVisibility(View.VISIBLE);
            txtRecvFrmPreSec.setVisibility(View.VISIBLE);
            txtDispFrmPreSeco.setVisibility(View.VISIBLE);
            txtCodecMode.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 计算SurfaceView 的大小add by sunzhibin  2016/12/2
     */
    private void calculateSurfaceViewSize() {
        // calculateData surface view size

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        final int height = size.y;
        final SurfaceView surfaceView = (SurfaceView) mHardMonitor;

        if (surfaceView == null || myScrollView == null || myHorScrollView == null)
            return;
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {  // 横屏模式
            surfaceView.getLayoutParams().width = width;
            surfaceView.getLayoutParams().height = height;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams layoutParams =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    surfaceView.setLayoutParams(layoutParams);
//                    mMiniVideoHeight = surfaceView.getLayoutParams().height;
//                    mMiniVideoWidth = surfaceView.getLayoutParams().width;
//                    surfaceView.setLayoutParams(surfaceView.getLayoutParams());
                }
            });
            if (myHorScrollView != null) {
                myHorScrollView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        mHardMonitor.HorizontalScrollTouch(view, event);
                        return false;
                    }
                });
            }

        } else {   // 竖屏模式
            surfaceView.getLayoutParams().width = width;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    L.d("LivewViewActivity calculateSurfaceViewSize()" + myScrollView.getHeight() + "-----" + myScrollView.getMeasuredHeight());

                    if (myScrollView.getMeasuredHeight() == 0) {
                        handler.postDelayed(this, 200);
                    } else {
                        surfaceView.getLayoutParams().height = myScrollView.getMeasuredHeight();
//                        surfaceView.getLayoutParams().height = DensityUitl.dip2px(CameraMainActivity.this, 300); //没用

                        mMiniVideoHeight = surfaceView.getLayoutParams().height;
                        mMiniVideoWidth = surfaceView.getLayoutParams().width;
                        surfaceView.setLayoutParams(surfaceView.getLayoutParams());

                        surfaceView.setZOrderMediaOverlay(true);
                        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                    }
                }

            });
            if (myHorScrollView != null) {
                myHorScrollView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        mHardMonitor.HorizontalScrollTouch(view, event);
                        return false;
                    }
                });
            }
        }
    }

    private ImageButton.OnClickListener CameraClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.img_state) {

                if (isShow) {
                    cameralowimg.setVisibility(View.GONE);
                    cameramidimg.setVisibility(View.GONE);
                    camerahighimg.setVisibility(View.GONE);
                    isShow = false;
                } else {
                    cameralowimg.setVisibility(View.VISIBLE);
                    cameramidimg.setVisibility(View.VISIBLE);
                    camerahighimg.setVisibility(View.VISIBLE);
                    isShow = true;
                }
                return;
            }
            if (id == R.id.img_low) {
//                cameralowimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_low_orange));
//                camerastateimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_low_orange));
                cameralowimg.setChecked(true);
                cameramidimg.setChecked(false);
                camerahighimg.setChecked(false);
                camerastateimg.setText(getString(R.string.camera_mode_low));

                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
                                0, (byte) 5));
                Log.i("gwq", "bool=" + mCamera.isSessionConnected());
                mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);
                quality = (byte) 5;
            }
            if (id == R.id.img_mid) {
//                cameramidimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_mid_orange));
//                camerastateimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_mid_orange));
                cameralowimg.setChecked(false);
                cameramidimg.setChecked(true);
                camerahighimg.setChecked(false);
                camerastateimg.setText(getString(R.string.camera_mode_well));

                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
                                0, (byte) 3));
                quality = (byte) 3;
                mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);
            }
            if (id == R.id.img_high) {
//                camerahighimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_high_orange));
//                camerastateimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_high_orange));
                cameralowimg.setChecked(false);
                cameramidimg.setChecked(false);
                camerahighimg.setChecked(true);
                camerastateimg.setText(getString(R.string.camera_mode_high));

                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
                                0, (byte) 1));
                quality = (byte) 1;
                mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);
            } else {
                camerahighimg.setBackground(getResources().getDrawable(R.drawable.ic_camera_high));
            }
            cameralowimg.setVisibility(View.GONE);
            cameramidimg.setVisibility(View.GONE);
            camerahighimg.setVisibility(View.GONE);
            isShow = false;
        }
    };

    private Button.OnClickListener ToolBarClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //缺少权限时，进入权限设置页面
            switch (v.getId()) {
                case R.id.button_speaker:

                    if (mIsListening) {
                        button_toolbar_recording.setClickable(true);
                        button_toolbar_snapshot.setClickable(true);
//                        button_toolbar_snapshot.getBackground().setAlpha(255);
                        button_toolbar_recording.getBackground().setAlpha(255);

//                        button_toolbar_speaker
//                                .setBackgroundResource(R.drawable.btn_speaker_off_switch);
                        button_toolbar_speaker.setSelected(false);
                        linSpeaker.setVisibility(View.INVISIBLE);
                        mCamera.stopSpeaking(mSelectedChannel);
                        mCamera.stopListening(mSelectedChannel);
                        mIsListening = false;
                        mIsSpeaking = false;

                    } else {
//                        if (!MPermissions.shouldShowRequestPermissionRationale(CameraMainActivity.this, Manifest.permission.RECORD_AUDIO, REQUECT_CODE_)) {
//                            MPermissions.requestPermissions(CameraMainActivity.this, REQUECT_CODE_, Manifest.permission.RECORD_AUDIO);
//
//                            UserPermissionCheck.getApplicationAllPermissionList(CameraMainActivity.this);
//                            return;
//                        }
                        button_toolbar_recording.setClickable(false);
                        button_toolbar_recording.getBackground().setAlpha(100);


                        mIsListening = true;
                        mIsSpeaking = true;

//                        button_toolbar_speaker
//                                .setBackgroundResource(R.drawable.btn_speaker_on_switch);
                        button_toolbar_speaker.setSelected(true);
//                        setLayoutShow(true, 0);
                        linSpeaker.setVisibility(View.VISIBLE);
                        try {
                            mCamera.startListening(mSelectedChannel, mIsRecording);
                            mCamera.startSpeaking(mSelectedChannel);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastHelper.showShortMsg("请检查录音权限是否开启!");
                        }
                    }
                    break;
                case R.id.button_recording:
                    //创建保存视频文件路径
//                    button_toolbar_snapshot.setClickable(!mIsRecording);
                    button_toolbar_speaker.setClickable(!mIsRecording);
                    //横屏情况处理
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (mIsRecording) {
                            button_toolbar_speaker.setClickable(true);
                            button_toolbar_snapshot.setClickable(true);
                            button_toolbar_speaker.setEnabled(true);
                            button_toolbar_snapshot.setEnabled(true);
                            button_toolbar_speaker.getBackground().setAlpha(255);
//                            button_toolbar_recording
//                                    .setBackgroundResource(R.drawable.ic_camera_full_screen_video);
                            button_toolbar_recording.setSelected(false);
                            mCamera.stopListening(mSelectedChannel);
                            mCamera.stopSpeaking(mSelectedChannel);
                            // mIsSpeaking = false;
                            // mIsListening = false;
                            mCamera.stopRecording();
                            chronomter.stop();
                            // mCamera.setThumbnailPath(mContext);
                            mIsRecording = false;
                            recordTime=SystemClock.elapsedRealtime();
                            chronomter.removeCallbacks(threeMinuteSaveRunnable);
                            chronomter.setBase(recordTime);
                            chronomter.setVisibility(View.GONE);
                            //是否删除视频
//                            showMP4IsNeedSava(videoPath);
                            syncFileToGallery(videoPath);
                        } else {
                            button_toolbar_speaker.setClickable(false);
                            button_toolbar_speaker.getBackground().setAlpha(100);
                            if (getAvailaleSize() <= 300) {
                                Toast.makeText(mContext, R.string.recording_tips_size,
                                        Toast.LENGTH_SHORT).show();
                            }

                            if (mCamera.codec_ID_for_recording == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                                button_toolbar_speaker.setClickable(false);
                                button_toolbar_speaker.getBackground().setAlpha(100);
//                                button_toolbar_snapshot.setClickable(false);
//                                button_toolbar_recording
//                                        .setBackgroundResource(R.drawable.ic_camera_full_screen_stop);
                                button_toolbar_recording.setSelected(true);
                                mIsRecording = true;
                                mCamera.startListening(mSelectedChannel, mIsRecording);
                                mCamera.stopSpeaking(mSelectedChannel);

                                chronomter.setVisibility(View.VISIBLE);

                                recordTime=SystemClock.elapsedRealtime();
                                chronomter.setBase(recordTime);
                                chronomter.postDelayed(threeMinuteSaveRunnable,SAVE_TIME);
//                                String path = "/sdcard/Record/" + mDevUID + "/"
//                                        + getFileNameWithTimeMP4();
                                chronomter.start();
                                videoPath = saveVideoPath();
                                mCamera.startRecording(videoPath);
                                // mCamera.setThumbnailPath(path,mContext);
                            } else {
                                Toast.makeText(mContext,
                                        R.string.recording_tips_format,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                    //竖屏处理

                    if (mIsRecording) {
                        setLayoutShow(true, 2);
//                        recordingtv.setVisibility(View.GONE);
//                        button_toolbar_speaker.getBackground().setAlpha(255);
                        historybtn.setClickable(true);
                        historybtn.getBackground().setAlpha(255);
                        button_toolbar_speaker.setClickable(true);
                        button_toolbar_snapshot.setClickable(true);
                        button_toolbar_speaker.setEnabled(true);
                        button_toolbar_snapshot.setEnabled(true);
                        button_toolbar_speaker.getBackground().setAlpha(255);
                        CH_button.setEnabled(true);

                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopSpeaking(mSelectedChannel);
//                        button_toolbar_recording
//                                .setBackgroundResource(R.drawable.btn_recording_switch_start);
                        button_toolbar_recording.setSelected(false);
                        // mIsSpeaking = false;
                        // mIsListening = false;
                        mCamera.stopRecording();
                        chronomter.stop();
//                         mCamera.setThumbnailPath(mContext);
                        mIsRecording = false;
                        recordTime=SystemClock.elapsedRealtime();
                        chronomter.removeCallbacks(threeMinuteSaveRunnable);
                        chronomter.setBase(recordTime);
                        chronomter.setVisibility(View.GONE);

                        setLayoutShow(false, 0);
                        //是否删除视频
//                        showMP4IsNeedSava(videoPath);
                        syncFileToGallery(videoPath);
                    } else {
                        button_toolbar_speaker.setClickable(false);
                        button_toolbar_speaker.getBackground().setAlpha(100);
                        if (getAvailaleSize() <= 300) {
                            Toast.makeText(mContext, R.string.recording_tips_size,
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (mCamera.codec_ID_for_recording == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            button_toolbar_speaker.setClickable(false);
                            button_toolbar_speaker.getBackground().setAlpha(100);
//                            button_toolbar_speaker.getBackground().setAlpha(100);
                            historybtn.setClickable(false);
                            historybtn.getBackground().setAlpha(100);
//                            button_toolbar_snapshot.setClickable(false);
                            setLayoutShow(true, 1);

                            CH_button.setEnabled(false);

                            mIsRecording = true;
                            mCamera.startListening(mSelectedChannel, mIsRecording);
                            mCamera.stopSpeaking(mSelectedChannel);
//                            button_toolbar_recording
//                                    .setBackgroundResource(R.drawable.btn_recording_switch_stop);
                            button_toolbar_recording.setSelected(true);
                            chronomter.setVisibility(View.VISIBLE);
                            recordTime=SystemClock.elapsedRealtime();
                            chronomter.setBase(recordTime);
                            chronomter.start();
                            chronomter.postDelayed(threeMinuteSaveRunnable,SAVE_TIME);
                            videoPath = saveVideoPath();
                            mCamera.startRecording(videoPath);
                            // mCamera.setThumbnailPath(path,mContext);
                        } else {
                            Toast.makeText(mContext,
                                    R.string.recording_tips_format,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.button_snapshot:
//                    if (mCamera != null
//                            && mCamera.isChannelConnected(mSelectedChannel)) {
//
//                        if (isSDCardValid()) {
//
//                            File rootFolder = new File(Environment
//                                    .getExternalStorageDirectory()
//                                    .getAbsolutePath()
//                                    + "/Snapshot/");
//                            File targetFolder = new File(
//                                    rootFolder.getAbsolutePath() + "/" + mDevUID);
//                            // File rootFolder = new
//                            // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
//                            // + "/Snapshot");
//                            // File targetFolder = new
//                            // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
//                            // + "/Snapshot");
//
//                            if (!rootFolder.exists()) {
//                                try {
//                                    rootFolder.mkdir();
//                                } catch (SecurityException se) {
//                                }
//                            }
//
//                            if (!targetFolder.exists()) {
//
//                                try {
//                                    targetFolder.mkdir();
//                                } catch (SecurityException se) {
//                                }
//                            }
//
//                            final String file = targetFolder.getAbsoluteFile()
//                                    + "/" + getFileNameWithTime();
//                            mFilePath = file;
//                            if (mCamera != null) {
//                                try{
//                                    mCamera.setSnapshot(mContext, file);
//                                }
//                                catch(Exception e){
//                                    e.printStackTrace();
//                                    Log.d("Exception","Exception:"+e.getMessage());
//                                }
//                            }
//
//                        } else {
//                            Toast.makeText(
//                                    CameraMainActivity.this,
//                                    CameraMainActivity.this.getText(
//                                            R.string.tips_no_sdcard).toString(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    doSnapshot();
                    if (mIsRecording) {
                        setLayoutShow(true, 1);
                    }
                    break;

            }

        }
    };

    private void showMP4IsNeedSava(final String savaVideoPath) {
        final CustomFragmentDialog fragmentDialog =
                CustomFragmentDialog.newInstanse("提示", "您是否保存录制的视频", false);
        fragmentDialog.show(getSupportFragmentManager(), "dialog");
        fragmentDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                //删除保存的视频
                File file = new File(savaVideoPath);
                if (file.exists()) {
                    file.delete();
                }
                fragmentDialog.dismiss();
            }
        });
    }

    // filename: such as,M20101023_181010.jpg
    private static String getFileNameWithTime() {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);

        StringBuffer sb = new StringBuffer();
        sb.append("IMG_");
        sb.append(mYear);
        if (mMonth < 10)
            sb.append('0');
        sb.append(mMonth);
        if (mDay < 10)
            sb.append('0');
        sb.append(mDay);
        sb.append('_');
        if (mHour < 10)
            sb.append('0');
        sb.append(mHour);
        if (mMinute < 10)
            sb.append('0');
        sb.append(mMinute);
        if (mSec < 10)
            sb.append('0');
        sb.append(mSec);
        sb.append(".jpg");

        return sb.toString();
    }

    private static String getFileNameWithTimeMP4() {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSec = c.get(Calendar.SECOND);

        StringBuffer sb = new StringBuffer();
        // sb.append("MP4_");
        sb.append(mYear);
        if (mMonth < 10)
            sb.append('0');
        sb.append(mMonth);
        if (mDay < 10)
            sb.append('0');
        sb.append(mDay);
        sb.append('_');
        if (mHour < 10)
            sb.append('0');
        sb.append(mHour);
        if (mMinute < 10)
            sb.append('0');
        sb.append(mMinute);
        if (mSec < 10)
            sb.append('0');
        sb.append(mSec);
        sb.append(".mp4");

        sb.toString();

        return sb.toString();
    }

    private String getSessionMode(int mode) {

        String result = "";
        if (mode == 0)
            result = getText(R.string.connmode_p2p).toString();
        else if (mode == 1)
            result = getText(R.string.connmode_relay).toString();
        else if (mode == 2)
            result = getText(R.string.connmode_lan).toString();
        else
            result = getText(R.string.connmode_none).toString();

        return result;
    }

    private String getPerformance(int mode) {

        String result = "";
        if (mode < 30)
            result = getText(R.string.txtBad).toString();
        else if (mode < 60)
            result = getText(R.string.txtNormal).toString();
        else
            result = getText(R.string.txtGood).toString();

        return result;
    }

    private static boolean isSDCardValid() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private void quit() {
        isSaveBitmap = true;
        byte[] snapshot = null;
//        System.gc();
        Bitmap bmp = mCamera.Snapshot(mSelectedChannel);
        if (bmp != null) {
            try {
                if (ExternalStorageUtils.isMounted()) {
                    Bitmap tempBitmap = compressImage(bmp);
                    String pathStr = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + "bitmap";
                    L.d("LiveViewActivity " + pathStr);
                    if (tempBitmap != null) {
                        ExternalStorageUtils.saveBitmap(tempBitmap, pathStr, mDevUID);
                    }
                }
                if (bmp.getWidth() * bmp.getHeight() > THUMBNAIL_LIMIT_WIDTH
                        * THUMBNAIL_LIMIT_HEIGHT) {
                    if (!bmp.isRecycled())
                        bmp = Bitmap.createScaledBitmap(bmp, THUMBNAIL_LIMIT_WIDTH,
                                THUMBNAIL_LIMIT_HEIGHT, false);
                }
                snapshot = DatabaseManager.getByteArrayFromBitmap(bmp);
//                bmp.recycle();
                if (snapshot != null)
                    L.d("LiveViewActivity quit snapshot.size = " + snapshot.length);
            } catch (OutOfMemoryError E) {
                Glog.D("LiveView", "compressImage OutOfMemoryError" + E);
                System.gc();
                // continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        DatabaseManager manager = new DatabaseManager(this);
        manager.updateDeviceChannelByUID(mDevUID, mSelectedChannel);

        if (snapshot != null) {
            manager.updateDeviceSnapshotByUID(mDevUID, snapshot);
        }

        if (mCamera != null) {

            if (mIsListening)
                mCamera.LastAudioMode = 1;
            else if (mIsSpeaking)
                mCamera.LastAudioMode = 2;
            else
                mCamera.LastAudioMode = 0;

        }

        finish();
    }

    private Bitmap compressImage(Bitmap image) {

        Bitmap tempBitmap = image;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, BITMAP_QUALITY, baos); //
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());
            if (image.getWidth() * image.getHeight() > THUMBNAIL_LIMIT_WIDTH * THUMBNAIL_LIMIT_HEIGHT) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, opts);
                return bitmap;
            } else {
                Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == OPT_MENU_ITEM_ALBUM) {

            File folder = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Snapshot/" + mDevUID);
            // File folder = new
            // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
            // + "/Snapshot");
            String[] allFiles = folder.list();

            if (allFiles != null && allFiles.length > 0) {

                if (mHardMonitor != null)
                    mHardMonitor.deattachCamera();

			/*gwq	Intent intent = new Intent(CameraMainActivity.this,
                        GridViewGalleryActivity.class);
				intent.putExtra("snap", mDevUID);
				intent.putExtra("images_path", folder.getAbsolutePath());
				startActivity(intent);*/

            } else {
                String msg = CameraMainActivity.this.getText(
                        R.string.tips_no_snapshot_found).toString();
                ToastHelper.showShortMsg(msg);
            }

        } else if (id == OPT_MENU_ITEM_SNAPSHOT) {

            if (mCamera != null && mCamera.isChannelConnected(mSelectedChannel)) {

                if (isSDCardValid()) {

                    File rootFolder = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/Snapshot/");
                    File targetFolder = new File(rootFolder.getAbsolutePath()
                            + "/" + mDevUID);

                    if (!rootFolder.exists()) {
                        try {
                            rootFolder.mkdir();
                        } catch (SecurityException se) {
                            super.onOptionsItemSelected(item);
                        }
                    }

                    if (!targetFolder.exists()) {

                        try {
                            targetFolder.mkdir();
                        } catch (SecurityException se) {
                            super.onOptionsItemSelected(item);
                        }
                    }

                    final String file = targetFolder.getAbsoluteFile() + "/"
                            + getFileNameWithTime();
                    mFilePath = file;
                    if (mCamera != null) {
                        mCamera.setSnapshot(mContext, file);
                    }

                } else {

                    Toast.makeText(
                            CameraMainActivity.this,
                            CameraMainActivity.this.getText(
                                    R.string.tips_no_sdcard).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == OPT_MENU_ITEM_AUDIOCTRL) {

            ArrayList<String> s = new ArrayList<String>();
            s.add(getText(R.string.txtMute).toString());
            if (mCamera.getAudioInSupported(0))
                s.add(getText(R.string.txtListen).toString());
            if (mCamera.getAudioOutSupported(0))
                s.add(getText(R.string.txtSpeak).toString());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, s);

            final AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle(null);
            dlg.setIcon(null);

            ListView view = new ListView(this);
            view.setAdapter(adapter);
            view.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long id) {

                    if (mCamera == null)
                        return;

                    if (position == 1) { // Listening
                        mCamera.stopSpeaking(mSelectedChannel);
                        mCamera.startListening(mSelectedChannel, mIsRecording);
                        mIsListening = true;
                        mIsSpeaking = false;
                    } else if (position == 2) { // Speaking
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.startSpeaking(mSelectedChannel);
                        mIsListening = false;
                        mIsSpeaking = true;
                    } else if (position == 0) { // Mute
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopSpeaking(mSelectedChannel);
                        mIsListening = mIsSpeaking = false;
                    }

                    dlg.dismiss();
                    CameraMainActivity.this.invalidateOptionsMenu();
                }
            });

            dlg.setView(view);
            dlg.setCanceledOnTouchOutside(true);
            dlg.show();

        } else if (id == OPT_MENU_ITEM_AUDIO_IN) {

            if (!mIsListening) {
                mCamera.startListening(mSelectedChannel, mIsRecording);
            } else {
                mCamera.stopListening(mSelectedChannel);
            }

            mIsListening = !mIsListening;

            this.invalidateOptionsMenu();

        } else if (id == OPT_MENU_ITEM_AUDIO_OUT) {

            if (!mIsSpeaking) {
                mCamera.startSpeaking(mSelectedChannel);
            } else {
                mCamera.stopSpeaking(mSelectedChannel);
            }

            mIsSpeaking = !mIsSpeaking;

            this.invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:

                quit();

                break;
        }

//        return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {
        if (mCamera == camera && avChannel == mSelectedChannel) {
            if (bmp.getWidth() != mVideoWidth
                    || bmp.getHeight() != mVideoHeight) {
                mVideoWidth = bmp.getWidth();
                mVideoHeight = bmp.getHeight();
                bmp.recycle();
//                String pathStr = BaseApplication.SD_SAVEDIR + "cache/bitmap";
                L.d("LiveViewActivity receiveFrameData" + bmp.isRecycled());
//                if (bmp != null && isSaveBitmap) {
//                    if (ExternalStorageUtils.saveBitmap(bmp, pathStr, mDevUID)) {
//                        isSaveBitmap = false;
//                    }
//                }

            }
        }
        if (wait_receive) {
            if (mHardMonitor != null) {
                mHardMonitor.deattachCamera();
            }
            Configuration cfg = getResources().getConfiguration();

            if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setupViewInPortraitLayout_wait();  // add by sunzhibin
                        mCHTextView.setText("CH" + (mSelectedChannel + 1));
                    }
                });
            } else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setupViewInLandscapeLayout_wait();  //add by sunzhibin
                    }
                });
            }

            wait_receive = false;
        }

    }

    @Override
    public void receiveFrameInfo(final Camera camera, int avChannel,
                                 long bitRate, int frameRate, int onlineNm, int frameCount,
                                 int incompleteFrameCount) {
        L.d("LiveViewActivity receiveFrameInfo () " + onlineNm);
        if (onlineNm != 0)
            IsShowProgressBar(false); // add by sunzhibin

        if (mCamera == camera && avChannel == mSelectedChannel) {
            mVideoFPS = frameRate;
            mVideoBPS = bitRate;
            mOnlineNm = onlineNm;
            mFrameCount = frameCount;
            mIncompleteFrameCount = incompleteFrameCount;

            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);

            Message msg = handler.obtainMessage();
            msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveChannelInfo(final Camera camera, int avChannel,
                                   int resultCode) {
        L.d("LiveViewActivity receiveChannelInfo () " + resultCode);
        if (mCamera == camera && avChannel == mSelectedChannel) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);

            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {
        L.d("LiveViewActivity receiveChannelInfo () " + resultCode);
        if (mCamera == camera) {
            Bundle bundle = new Bundle();
            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int avChannel,
                                  int avIOCtrlMsgType, byte[] data) {
        L.d("LiveViewActivity receiveIOCtrlData () " + data.length);
        if (mCamera == camera) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            bundle.putByteArray("data", data);

            Message msg = handler.obtainMessage();
            msg.what = avIOCtrlMsgType;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }


    @Override
    public View makeView() {
        TextView t = new TextView(this);
        return t;
    }

    @Override
    public void OnClick() {
        // TODO Auto-generated method stub
        if (mIsListening || isOpenLinQVGA || isOpenLinEmode) {
            return;
        }

        if (doubleClickScreenFullOrSmall()) { //双击全屏或者竖屏
            return;
        }
        if (isShowToolBar) {
            isShowToolBar = false;
            if (toolbar_layout != null && toolbar_layout.getVisibility() == View.VISIBLE) {
                ll_camera_tool_add.startAnimation(cameraToolAnimation(1f, 0, 0, 0));
                ll_definition_add.startAnimation(cameraToolAnimation(0f, 0, 1f, 0));
//                toolbar_layout.setVisibility(View.GONE);
            }
        } else {
            isShowToolBar = true;
            if (toolbar_layout != null) {
                toolbar_layout.setVisibility(View.VISIBLE);
                ll_camera_tool_add.startAnimation(cameraToolAnimation(0f, 1f, 0, 0));
                ll_definition_add.startAnimation(cameraToolAnimation(0f, 0f, 0, 1f));
            }
        }

        Configuration cfg = getResources().getConfiguration();
        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isShowImg) {
                isShowImg = false;
                if (imglayout != null) {
                    imglayout.startAnimation(cameraToolAnimation(0, 0, 1f, 0));
                    imglayout.setVisibility(View.VISIBLE);
                }

            } else {
                isShowImg = true;
                if (imglayout != null && imglayout.getVisibility() == View.VISIBLE) {
                    imglayout.startAnimation(cameraToolAnimation(0, 0, 0f, 2f));
//                imglayout.setVisibility(View.GONE);

                }
            }
        }


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isShowToolBarRight=!isShowToolBarRight;
            if(isShowToolBarRight){
                findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) { // TODO 判断是否有录音的权限
//        UserPermissionCheck.getApplicationAllPermissionList(this); // 获取所有的权限是否获取状态


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                btn_speaker.setBackgroundResource(R.drawable.camera_call_on_press);
                if (mCamera != null) {
                    mCamera.startSpeaking(mSelectedChannel);
                    mCamera.stopListening(mSelectedChannel);
                }
                break;
            case MotionEvent.ACTION_UP:
                btn_speaker.setBackgroundResource(R.drawable.camera_call_on_nor);
                if (mCamera != null) {
                    mCamera.stopSpeaking(mSelectedChannel);
                    mCamera.startListening(mSelectedChannel, mIsRecording);
                }
                break;
        }
        return false;
    }

    //    @Override
//    public void OnSnapshotComplete() {
//        // TODO Auto-generated method stub
//        MediaScannerConnection.scanFile(CameraMainActivity.this,
//                new String[]{mFilePath.toString()},
//                new String[]{"image/*"},
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        L.i("LiveViewActivity OnSnapshotComplete() " + path + ":" + " -> uri=" + uri);
//                        Message msg = handler.obtainMessage();
//                        msg.what = STS_SNAPSHOT_SCANED;
//                        handler.sendMessage(msg);
//                    }
//                });
//    }

    @Override
    public void OnSnapshotComplete() {
        // TODO Auto-generated method stub
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    mFilePath, mFilePath.substring(mFilePath.lastIndexOf("/")+1), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mFilePath)));

        MediaScannerConnection.scanFile(CameraMainActivity.this,
                new String[] { mFilePath.toString() },
                new String[] { "image/*" },
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Message msg = handler.obtainMessage();
                        msg.what = STS_SNAPSHOT_SCANED;
                        handler.sendMessage(msg);
                    }
                });
    }

    private long getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return (availableBlocks * blockSize) / 1024 / 1024;
    }

    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int avChannel,
                                              byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf,
                                              boolean isIframe, int codecId) {
        L.d("LiveViewActivity receiveFrameDataForMediaCodec () " + Arrays.toString(pFrmInfoBuf));


        if (wait_receive) {

            if (mHardMonitor != null) {
                mHardMonitor.deattachCamera();
            }

            Configuration cfg = getResources().getConfiguration();

            if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setupViewInPortraitLayout_wait_Mediacodec();  // add by sunzhibin

//                        mCHTextView.setText("CH" + (mSelectedChannel + 1));
                    }
                });
            } else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        setupViewInLandscapeLayout_wait_Mediacodec();  // add by sunzhibin

                    }
                });
            }

            wait_receive = false;
        }
    }

    @Override
    public void Unavailable() {
        unavailable = true;
        Configuration cfg = getResources().getConfiguration();

        if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    setupViewInPortraitLayout_wait();
                    mCHTextView.setText("CH" + (mSelectedChannel + 1));
                }
            });
        } else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    setupViewInLandscapeLayout_wait();
                }
            });
        }
    }

    @Override
    public void zoomSurface(final float scale) {
        SurfaceView surfaceView = (SurfaceView) mHardMonitor;
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        lp.width = (int) (mMiniVideoWidth * scale);
        lp.height = (int) (mMiniVideoHeight * scale);

        surfaceView.setLayoutParams(lp);
    }

    /**
     * 控制进度条的显示
     */
    private void IsShowProgressBar(boolean isShow) {
//        if (mVideoHeight == 0 || mVideoWidth == 0) {
        if (isShow) {
            //toastUtils.showErrorWithStatus("获取监控失败，请检查网络连接");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    camerastateimg.setVisibility(View.INVISIBLE);
                    fullimg.setVisibility(View.INVISIBLE);
                }
            });
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
//                camerastateimg.setVisibility(View.VISIBLE);
                fullimg.setVisibility(View.VISIBLE);
                findViewById(R.id.hardMonitor).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setLayoutShow(boolean isShow, int position) {
        if (isShow) {
            if (linSpeaker != null) {
                linSpeaker.setVisibility(View.VISIBLE);
//                linSpeaker.setBackgroundColor(getResources().getColor(R.color.gray_back));
            }
            if (position == 0) {

                btn_speaker.setVisibility(View.VISIBLE);
                recordingtv.setVisibility(View.GONE);

            } else if (position == 1) {

                btn_speaker.setVisibility(View.GONE);
                tv_speaktxt.setVisibility(View.GONE);
//                recordingtv.setVisibility(View.VISIBLE);

                recordingtv.setText(getString(R.string.txt_recording));
                historybtn.getBackground().setAlpha(150);
                historybtn.setTextColor(getResources().getColor(R.color.gray_text));
            } else if (position == 2) {
                btn_speaker.setVisibility(View.GONE);
                tv_speaktxt.setVisibility(View.GONE);
//                recordingtv.setVisibility(View.VISIBLE);

                recordingtv.setText(getString(R.string.recording_save));
                historybtn.getBackground().setAlpha(0);
                historybtn.setTextColor(getResources().getColor(R.color.item_black_text));
            }
        } else {
            linSpeaker.setVisibility(View.INVISIBLE);
//            linSpeaker.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private long mChangeTime = 0; // 横竖屏的切换

    /**
     * 快速双击全屏
     *
     * @return
     */
    private boolean doubleClickScreenFullOrSmall() {
        if (System.currentTimeMillis() - mChangeTime > 1000 * 0.3) {
            mChangeTime = System.currentTimeMillis();
            return false;
        }

        Configuration cfg = getResources().getConfiguration();
        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (toolbar_layout != null && toolbar_layout.getVisibility() == View.VISIBLE) {
                ll_camera_tool_add.startAnimation(cameraToolAnimation(1f, 0, 0, 0));
                ll_definition_add.startAnimation(cameraToolAnimation(0f, 0, 1f, 0));
//                toolbar_layout.setVisibility(View.GONE);
            }

            if (unavailable)
                setupViewInPortraitLayout_wait();
            else
                setupViewInPortraitLayout();
            return true;
        }
        if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (unavailable)
                setupViewInLandscapeLayout_wait();
            else
                setupViewInLandscapeLayout();
            return true;
        }
        return false;
    }

    /**
     * 保存视频的路径
     */
    private String saveVideoPath() {
        File rootFolder = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/Record/");
        File targetFolder = new File(
                rootFolder.getAbsolutePath() + "/" + mDevUID);
        if (!rootFolder.exists()) {
            try {
                rootFolder.mkdir();
            } catch (SecurityException se) {
            }
        }

        if (!targetFolder.exists()) {
            try {
                targetFolder.mkdir();
            } catch (SecurityException se) {
            }
        }

        return targetFolder.getAbsolutePath() + "/" + getFileNameWithTimeMP4();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            int avChannel = bundle.getInt("avChannel");
            byte[] data = bundle.getByteArray("data");

            St_SInfo stSInfo = new St_SInfo();
            IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);
            switch (msg.what) {

                case STS_CHANGE_CHANNEL_STREAMINFO:
                    L.d("LiveViewActivity handleMessage () " + mVideoHeight);
                    if (txtResolution != null)
                        txtResolution.setText(String.valueOf(mVideoWidth) + "x"
                                + String.valueOf(mVideoHeight));

                    if (txtFrameRate != null)
                        txtFrameRate.setText(String.valueOf(mVideoFPS));

                    if (txtBitRate != null)
                        txtBitRate.setText(String.valueOf(mVideoBPS) + "Kbps");

                    if (txtOnlineNumber != null)
                        txtOnlineNumber.setText(String.valueOf(mOnlineNm));

                    if (txtFrameCount != null)
                        txtFrameCount.setText(String.valueOf(mFrameCount));

                    if (txtIncompleteFrameCount != null)
                        txtIncompleteFrameCount.setText(String
                                .valueOf(mIncompleteFrameCount));

                    if (txtConnectionMode != null)
                        txtConnectionMode
                                .setText(getSessionMode(mCamera != null ? mCamera
                                        .getSessionMode() : -1)
                                        + " C: "
                                        + IOTCAPIs.IOTC_Get_Nat_Type()
                                        + ", D: "
                                        + stSInfo.NatType
                                        + ",R"
                                        + mCamera.getbResend());

                    if (txtRecvFrmPreSec != null)
                        txtRecvFrmPreSec.setText(String.valueOf(mCamera
                                .getRecvFrmPreSec()));

                    if (txtDispFrmPreSeco != null)
                        txtDispFrmPreSeco.setText(String.valueOf(mCamera
                                .getDispFrmPreSec()));

                    if (txtPerformance != null)
                        txtPerformance
                                .setText(getPerformance((int) (((float) mCamera
                                        .getDispFrmPreSec() / (float) mCamera
                                        .getRecvFrmPreSec()) * 100)));

                    if (txtCodecMode != null) {
                        int codec_string = R.string.hardware_decode;
//                        if (mSoftCodecDefault || mSoftCodecCurrent)  //  modify by sunzhibin 2016/12/2
                        codec_string = R.string.software_decode;

                        txtCodecMode.setText(codec_string);
                    }


                    break;

                case STS_SNAPSHOT_SCANED:

                    Toast.makeText(CameraMainActivity.this,
                            getText(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT)
                            .show();

                    break;

                case Camera.CONNECTION_STATE_CONNECTING:

                    if (!mCamera.isSessionConnected()
                            || !mCamera.isChannelConnected(mSelectedChannel)) {

					/*mConnStatus = getText(R.string.connstus_connecting)
                            .toString();

					if (txtConnectionStatus != null) {
						txtConnectionStatus.setText(mConnStatus);
						txtConnectionStatus
								.setBackgroundResource(R.mipmap.live_unknow);

					}*/
                    }

                    break;

                case Camera.CONNECTION_STATE_CONNECTED:

                    if (mCamera.isSessionConnected()
                            && avChannel == mSelectedChannel
                            && mCamera.isChannelConnected(mSelectedChannel)) {

				/*	mConnStatus = getText(R.string.connstus_connected)
                            .toString();

					if (txtConnectionStatus != null) {
						txtConnectionStatus.setText(mConnStatus);
						txtConnectionStatus
								.setBackgroundResource(R.mipmap.live_online);
					}
*/
                        CameraMainActivity.this.invalidateOptionsMenu();
                    }

                    break;

                case Camera.CONNECTION_STATE_DISCONNECTED:

			/*	mConnStatus = getText(R.string.connstus_disconnect).toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.mipmap.live_offline);
				}
*/
                    CameraMainActivity.this.invalidateOptionsMenu();

                    break;

                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:

			/*	mConnStatus = getText(R.string.connstus_unknown_device)
                        .toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.mipmap.live_offline);
				}*/

                    CameraMainActivity.this.invalidateOptionsMenu();

                    break;

                case Camera.CONNECTION_STATE_TIMEOUT:

                    if (mCamera != null) {

                        mCamera.stopSpeaking(mSelectedChannel);
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopShow(mSelectedChannel);
                        mCamera.stop(mSelectedChannel);
                        mCamera.disconnect();
                        mCamera.connect(mDevUID);
                        mCamera.start(Camera.DEFAULT_AV_CHANNEL,
                                View_Account, View_Pwd);
                        mCamera.startShow(mSelectedChannel, true, m_RunSoftDecode, m_RunSoftDecode2);

                        mCamera.connect(mDevUID);
                        mCamera.start(Camera.DEFAULT_AV_CHANNEL,
                                View_Account, View_Pwd);
                        mCamera.sendIOCtrl(
                                Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                                SMsgAVIoctrlGetSupportStreamReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
                                        .parseContent());
                        mCamera.sendIOCtrl(
                                Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
                                        .parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
                        if (mIsListening)
                            mCamera.startListening(mSelectedChannel, mIsRecording);
                    }

                    break;

                case Camera.CONNECTION_STATE_CONNECT_FAILED:

			/*	mConnStatus = getText(R.string.connstus_connection_failed)
                        .toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.mipmap.live_offline);
				}*/

                    CameraMainActivity.this.invalidateOptionsMenu();

                    break;

                case Camera.CONNECTION_STATE_WRONG_PASSWORD:

			/*	mConnStatus = getText(R.string.connstus_wrong_password)
                        .toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.mipmap.live_offline);
				}*/

                    break;

                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:

                    CameraMainActivity.this.invalidateOptionsMenu();

                    break;
            }
        }
    };

    /**
     * 动画效果
     */
    //定义从右侧进入的动画效果
    protected Animation cameraToolAnimation(float fromX, float toX,
                                            float fromY, float toY) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, fromX,
                Animation.RELATIVE_TO_PARENT, toX,
                Animation.RELATIVE_TO_PARENT, fromY,
                Animation.RELATIVE_TO_PARENT, toY);
        inFromRight.setDuration(300);
        inFromRight.setInterpolator(new LinearOutSlowInInterpolator());
        inFromRight.setFillAfter(true);
        return inFromRight;
    }


    /**
     * 初始化头部控件
     *
     * @param centerTitle 中间标题文字内容
     * @param rightTitle  右边TextView文字内容
     * @param leftId      返回按钮显示Tag
     * @param rightId     右边按钮显示tag
     */
    public void initTopBar(String centerTitle, Integer rightTitle, boolean leftId, boolean rightId) {
        View vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
            vTitle.setBackgroundColor(getResources().getColor(R.color.layout_title_bg));
        } else {
            vTitle.setVisibility(View.GONE);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvRight = (TextView) findViewById(R.id.tvRight);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRight = (ImageView) findViewById(R.id.ivRight);

        if (centerTitle != null) tvTitle.setText(centerTitle);

        if (rightTitle != null) tvRight.setText(getString(rightTitle));

        if (leftId) ivBack.setVisibility(View.VISIBLE);
        else ivBack.setVisibility(View.GONE);

        if (rightId) ivRight.setVisibility(View.VISIBLE);
        else ivRight.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        boolean favorite = mDevice.getFavorite().equals("1");
        ivRight.setImageResource(!favorite ? R.mipmap.nav_collect_nor
                : R.mipmap.nav_red_collect);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = mDevice.getFavorite();
                if (tag.equals("1")) {
                    tag = "0";
                    ivRight.setImageResource(R.mipmap.nav_collect_nor);
                } else if (tag.equals("0")) {
                    tag = "1";
                    ivRight.setImageResource(R.mipmap.nav_red_collect);
                }
                //保存状态，并发送后台
                if (mDevice != null) {
                    devices.clear();
                    mDevice.setFavorite(tag);
                    devices.add(mDevice);
                    DeviceUpdateStatus.setCommonDevice(CameraMainActivity.this, devices, toastUtils);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null)
            mCamera.disconnect();
        mHardMonitor = null;
        chronomter.removeCallbacks(threeMinuteSaveRunnable);
    }


    private boolean isShowToolBarRight=false;
    private int curModeIndex=3;
    private int curResolutionIndex=2;
    private void setupPortraitToolBar(){
        tvMode=(TextView)findViewById(R.id.tv_mode);
        tvResolution=(TextView)findViewById(R.id.tv_resolution);
        ivUpDown=(ImageView)findViewById(R.id.iv_up_down);
        ivLeftRight=(ImageView)findViewById(R.id.iv_left_right);
        layoutMode=(LinearLayout)findViewById(R.id.layout_mode);
        layoutResolution=(LinearLayout)findViewById(R.id.layout_resolution);
        tvMode.setText(((TextView)layoutMode.getChildAt(curModeIndex)).getText().toString());
        tvResolution.setText(((TextView)layoutResolution.getChildAt(curResolutionIndex)).getText().toString());

//        setupMode(curModeIndex);
//        setResult(curResolutionIndex);

        tvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutMode.getVisibility()==View.VISIBLE){
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.VISIBLE);
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                }
                else{
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.GONE);
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.VISIBLE);
                }
            }
        });

        tvResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutResolution.getVisibility()==View.VISIBLE){
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.VISIBLE);
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                }
                else{
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.GONE);
                    layoutResolution.setVisibility(View.VISIBLE);
                    layoutMode.setVisibility(View.GONE);
                }
            }
        });
        ivUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updownMirror();
            }
        });
        ivLeftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightMirror();
            }
        });

        for(int i=0;i<layoutMode.getChildCount();i++){
            layoutMode.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.VISIBLE);
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                    TextView tv=(TextView)v;
                    tvMode.setText(tv.getText().toString());
                    curModeIndex=layoutMode.indexOfChild(v);
                    setupMode(curModeIndex);
                }
            });
        }

        for(int i=0;i<layoutResolution.getChildCount();i++){
            layoutResolution.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraMainActivity.this.findViewById(R.id.layout_bottom_default).setVisibility(View.VISIBLE);
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                    TextView tv=(TextView)v;
                    tvResolution.setText(tv.getText().toString());
                    curResolutionIndex=layoutResolution.indexOfChild(v);
                    setupQvga(curResolutionIndex+1);
                }
            });
        }
    }


    private void setupLandscapeToolBar(){
        tvMode=(TextView)findViewById(R.id.tv_mode);
        tvResolution=(TextView)findViewById(R.id.tv_resolution);
        ivUpDown=(ImageView)findViewById(R.id.iv_up_down);
        ivLeftRight=(ImageView)findViewById(R.id.iv_left_right);
        layoutMode=(LinearLayout)findViewById(R.id.layout_mode);
        layoutResolution=(LinearLayout)findViewById(R.id.layout_resolution);
        tvMode.setText(((TextView)layoutMode.getChildAt(curModeIndex)).getText().toString());
        tvResolution.setText(((TextView)layoutResolution.getChildAt(curResolutionIndex)).getText().toString());

//        setupMode(curModeIndex);
//        setResult(curResolutionIndex);

        tvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutMode.getVisibility()==View.VISIBLE){
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                }
                else{
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.VISIBLE);
                }
            }
        });

        tvResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutResolution.getVisibility()==View.VISIBLE){
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                }
                else{
                    layoutResolution.setVisibility(View.VISIBLE);
                    layoutMode.setVisibility(View.GONE);
                }
            }
        });
        ivUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updownMirror();
            }
        });
        ivLeftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightMirror();
            }
        });

        for(int i=0;i<layoutMode.getChildCount();i++){
            layoutMode.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                    TextView tv=(TextView)v;
                    tvMode.setText(tv.getText().toString());
                    curModeIndex=layoutMode.indexOfChild(v);
                    setupMode(curModeIndex);
                }
            });
        }

        for(int i=0;i<layoutResolution.getChildCount();i++){
            layoutResolution.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutResolution.setVisibility(View.GONE);
                    layoutMode.setVisibility(View.GONE);
                    TextView tv=(TextView)v;
                    tvResolution.setText(tv.getText().toString());
                    curResolutionIndex=layoutResolution.indexOfChild(v);
                    setupQvga(curResolutionIndex+1);
                }
            });
        }
    }

    private void inittoolarboolean() {
        if (mIsListening) {
            mCamera.stopSpeaking(mSelectedChannel);
            mCamera.stopListening(mSelectedChannel);
        }
        mIsListening = false;
        isOpenLinEmode = false;
        isOpenLinQVGA = false;
    }

    private void updownMirror(){
        inittoolarboolean();
        if (LevelFlip) {
            LevelFlip = false;
            if (!VerticalFlip)
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 0));
            else
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 2));

        } else {
            LevelFlip = true;
            if (!VerticalFlip)
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 1));
            else
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 3));
        }
    }

    private void leftRightMirror(){
        inittoolarboolean();
        if (VerticalFlip) {
            VerticalFlip = false;
            if (!LevelFlip)
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 0));
            else
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 1));
        } else {
            VerticalFlip = true;
            if (!LevelFlip)
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 2));
            else
                mCamera.sendIOCtrl(
                        Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
                        AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
                                .parseContent(0,
                                        (byte) 3));
        }
    }

    private void setupQvga(int qvgaValue){
        isOpenLinQVGA = false;
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
                        0, (byte) qvgaValue));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHardMonitor.deattachCamera();
                mHardMonitor.cleanFrameQueue();
                mCamera.stopShow(0);
                mCamera.startShow(0, true,
                        m_RunSoftDecode, m_RunSoftDecode2);
                mHardMonitor.attachCamera(mCamera, mSelectedChannel);
            }
        }, 2000);
    }

    private void setupMode(int modeValue){
        isOpenLinEmode = false;
//        nullLayout.setVisibility(View.VISIBLE);
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
                        .parseContent(0, (byte) modeValue));
    }

    private void doSnapshot(){
        inittoolarboolean();
        if (mCamera != null
                && mCamera.isChannelConnected(mSelectedChannel)) {

            if (isSDCardValid()) {

                File rootFolder = new File(Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath()
                        + "/Snapshot/");
                File targetFolder = new File(
                        rootFolder.getAbsolutePath() + "/" + mDevUID);
                if (!rootFolder.exists()) {
                    try {
                        rootFolder.mkdir();
                    } catch (SecurityException se) {
                    }
                }

                if (!targetFolder.exists()) {

                    try {
                        targetFolder.mkdir();
                    } catch (SecurityException se) {
                    }
                }

                final String file = targetFolder.getAbsoluteFile()
                        + "/" + getFileNameWithTime();
                mFilePath = file;
                if (mCamera != null) {
                    mCamera.setSnapshot(mContext, file);
                }

            } else {

                Toast.makeText(
                        CameraMainActivity.this,
                        CameraMainActivity.this.getText(
                                R.string.tips_no_sdcard).toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long recordTime;

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        adaptTheme(true);
        statusBarTheme(false, getResources().getColor(R.color.layout_title_bg));
    }

    protected void showBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        adaptTheme(true);
        statusBarTheme(true, getResources().getColor(R.color.red));
        StatusBarUtil.StatusBarLightMode(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void adaptTheme(final boolean isTranslucentStatusFitSystemWindowTrue) {
        if (isTranslucentStatusFitSystemWindowTrue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }


    public static void saveImageToGallery(Context context, Bitmap bmp,String path) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    private String videoPath;
    private void syncFileToGallery(String filePath){
        File paramFile=new File(filePath);
        if(!paramFile.exists()){
            return;
        }
        //获取ContentResolve对象，来操作插入视频
        ContentResolver localContentResolver = mContext.getContentResolver();
        //ContentValues：用于储存一些基本类型的键值对
        ContentValues localContentValues = getVideoContentValues(filePath, System.currentTimeMillis());
        //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,localContentValues);
        if(isAutoSave){
            isAutoSave=false;
            ToastHelper.showShortMsg("录制时限3分钟\n已自动存储成功");
        }
        else{
            ToastHelper.showShortMsg(CameraMainActivity.this.getString(R.string.tips_snapshot_ok));
        }
    }

    //再往数据库中插入数据的时候将，将要插入的值都放到一个ContentValues的实例当中
    private ContentValues getVideoContentValues(String filePath, long paramLong){
        File paramFile=new File(filePath);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/"+filePath.substring(filePath.lastIndexOf(".")+1));
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    private static long SAVE_TIME=1000*60*3;
    private boolean isAutoSave;
    private Runnable threeMinuteSaveRunnable=new Runnable() {
        @Override
        public void run() {
            isAutoSave=true;
            button_toolbar_recording.performClick();
        }
    };
}
