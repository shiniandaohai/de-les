package cn.semtec.www.nebular;

/**
 * Created by mac on 15/9/22.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;

import org.linphone.mediastream.Log;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.squirrel.squirrelCallImpl;

import java.util.Timer;
import java.util.TimerTask;

//AppCompat
public class CallListeningActivity extends BaseListeningActivity {

    private ImageView mTerminate;
    private ImageView mUnlock;
    static int SwitchVideoStatus = 0;
    AudioManager audioManager = null;
    private SurfaceView remoteview;
    private SurfaceView localview;
    private squirrelCallImpl myCallImpl = null;
    private AndroidVideoWindowImpl mVideoWindow = null;
    int currentCallID = -1;
    String username = null;

    //TextView status = null;
    MediaPlayer mMediaPlayer;
    private int micMute=0;
    SharedPreferences preferences;
    private ImageView mSwitchAudio;
    private TextView mTextViewAudio;
    //ICE status
    Timer ICEtimer;
    TimerTask ICEtimertask;
    final Handler ICEHandler = new Handler();

    private boolean isOpenDoor = false;//根据值切换图片
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2: //呼叫状态
                    HandlerCallState(msg.getData());
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };



    void HandlerCallState(Bundle _bundle)
    {
        int state = _bundle.getInt("state");
        currentCallID = _bundle.getInt("callid");
        username = _bundle.getString("login_username");

        if(state == squirrelCallImpl.squirrelCallConnected){
            toastUtils.dismiss();
           // OnStopRing();
          //  status.setText(nickname != null ? nickname + "应答" : username + " Answered!");
            //Toast.makeText(this, "应答" +username + " Answered...", Toast.LENGTH_LONG).show();
            findViewById(R.id.remotevideo).setVisibility(View.VISIBLE);

        }else if(state == squirrelCallImpl.squirrelCallStreamsRunning) {
            //status.setText(nickname != null ? nickname + "接通" : username + " talking...");
            toastUtils.dismiss();
          // Toast.makeText(this, "接通" +username + " talking...", Toast.LENGTH_LONG).show();

        }
        else if(state == squirrelCallImpl.squirrelCallEnd) {
            toastUtils.dismiss();
            if(!isFinishing()) {
              //  status.setText(nickname != null ? nickname : username + " talk end.");
                finish();
            }
        }
        else if(state == squirrelCallImpl.squirrelCallError){
            toastUtils.dismiss();
            Toast.makeText(this, "云对讲-无法接通！", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initTopBar("智能门禁", null, true, false);
        toastUtils.showProgress("请稍后...");
       /* this.ivRight.setImageResource(R.drawable.ic_homepage_message);
        this.ivRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(CallListeningActivity.this, InformationCenterListeningActivity.class));
                return false;
            }
        });*/


        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        myCallImpl =  (SquirrelCallImpl)getApplication();
        myCallImpl = squirrelCallImpl.getInstance();
        myCallImpl.setCallHandler(mHandler);
        mTerminate = (ImageView) findViewById(R.id.terminate);
        mUnlock = (ImageView) findViewById(R.id.unlock);
        mTextViewAudio = (TextView) findViewById(R.id.id_textViewAudio);
        remoteview = (org.linphone.mediastream.video.display.GL2JNIView)findViewById(R.id.remotevideo);
        localview = (SurfaceView)findViewById(R.id.localvideo);
        mSwitchAudio = (ImageView) findViewById(R.id.SwitchAudio);
        //status = (TextView)findViewById(R.id.callstatus);
        currentCallID = -1;

        preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        Bundle _bundle = this.getIntent().getExtras();
        if(_bundle != null){
            int state = _bundle.getInt("state");
            currentCallID = _bundle.getInt("callid");
            username = _bundle.getString("login_username");
           // Toast.makeText(this, "username"+username , Toast.LENGTH_LONG).show();
            if(state == squirrelCallImpl.squirrelCallIncomingReceived){
//                Log.d("SQUIRREL", "callstate == CallIncomingReceived");
               //OnPlayRing();
              /*  new Handler().postDelayed(new Runnable(){
                    public void run() {

                    }
                }, 5000);*/
                //根据传入的值，来判断进入界面是否需要进行接听操作
                if(SIPService.isCallOn){
                    // Toast.makeText(CallListeningActivity.this, "isCallOn" , Toast.LENGTH_LONG).show();
                    if(myCallImpl != null) {
                        //Toast.makeText(CallListeningActivity.this, "myCallImpl != null" , Toast.LENGTH_LONG).show();
                        if (currentCallID != -1 && myCallImpl.isReceivedCall() == 1) {
                            // Toast.makeText(CallListeningActivity.this, "squirrelAnswer" , Toast.LENGTH_LONG).show();
                            //myCallImpl.squirrelAnswer(currentCallID, 1);
                            myCallImpl.squirrelAnswer(currentCallID, 1);
                            //myCallImpl.squirrelSwitchVideo(currentCallID,0);
                        }
                    }
                }

            }

        }

        //Switch to front camera
        int videoDeviceId = myCallImpl.squirrelGetCamera();
        videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
        myCallImpl.squirrelSetCamera(videoDeviceId);


        remoteview.setZOrderOnTop(true);
        remoteview.setZOrderOnTop(false);
        localview.setZOrderOnTop(false);
        localview.setZOrderMediaOverlay(true);

        mVideoWindow = new AndroidVideoWindowImpl(remoteview, localview, new AndroidVideoWindowImpl.VideoWindowListener() {
            @Override
            public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                if(myCallImpl != null) {

                    myCallImpl.squirrelSetRemoteVideoWindow(vw);

                    myCallImpl.squirrelSetCameraRotation(rotationToAngle(getWindowManager().getDefaultDisplay()
                            .getRotation()));
                    remoteview = surface;
                }
            }

            @Override
            public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {

            }

            @Override
            public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
                if(myCallImpl != null) {
                    localview = surface;
                    myCallImpl.squirrelSetLocalVideoWindow(localview);
                }
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
                if(myCallImpl != null) {

                    myCallImpl.squirrelSetLocalVideoWindow(null);
                }
            }
        });



        mTerminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetICEState();
                if (currentCallID == -1)
                    currentCallID = myCallImpl.getCurrentCallId();
                if (currentCallID != -1) {
                    myCallImpl.squirrelUnlock(currentCallID);
                }

            }
        });

        /*
        mSwitchVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                myCallImpl.squirrelSwitchVideo(currentCallID, SwitchVideoStatus);
                if(SwitchVideoStatus == 0) SwitchVideoStatus =1;
                else SwitchVideoStatus = 0;
            }
        });
        */
        mSwitchAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    mSwitchAudio.setBackgroundResource(R.mipmap.receiver);
                    mTextViewAudio.setText("听筒模式");
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    mSwitchAudio.setBackgroundResource(R.mipmap.speaker);
                    mTextViewAudio.setText("扬声器模式");
                }
            }
        });


        audioManager.setSpeakerphoneOn(true);


        //get call status
        ICEtimertask = new TimerTask(){
            public void run() {
                ICEHandler.post(new Runnable() {
                    public void run() {
                    }
                });
            }
        };

        ICEtimer = new Timer(true);



    }
    @Override
    public void onResume() {
        super.onResume();
        if(remoteview != null){
            ((GLSurfaceView)remoteview).onResume();
        }

        if(mVideoWindow != null)
        {
            synchronized (mVideoWindow){
                if(myCallImpl != null)
                    myCallImpl.squirrelSetRemoteVideoWindow(mVideoWindow);
            }

        }

    }

    @Override
    public void onPause() {
        if (mVideoWindow != null) {
            synchronized (mVideoWindow) {

                if(myCallImpl != null)
                    myCallImpl.squirrelSetRemoteVideoWindow(null);
            }
        }

        if (remoteview != null) {
            ((GLSurfaceView) remoteview).onPause();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onTerminate();
        audioManager.setSpeakerphoneOn(false);
       // OnStopRing();
        if(mVideoWindow != null)
            mVideoWindow.release();
        myCallImpl.setCallHandler(null);
        ICEtimer.cancel();
        SIPService.isCallOn = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    static int rotationToAngle(int r)
    {
        switch (r) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public void switchCamera() {
        try {
            int videoDeviceId = myCallImpl.squirrelGetCamera();
            videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
            myCallImpl.squirrelSetCamera(videoDeviceId);
            myCallImpl.squirrelUpdateCall(myCallImpl.getCurrentCallId());

            // previous call will cause graph reconstruction -> regive preview
            // window
            if (localview != null) {
                myCallImpl.squirrelSetLocalVideoWindow(localview);
            }
        } catch (ArithmeticException ae) {
            Log.e("Cannot swtich camera : no camera");
        }
    }

  /*  @Override
    public void onBackPressed() {
        Log.d("onBackPressed");
        onTerminate();
        return;
    }*/

    public void onTerminate(){
        System.out.println("onTerminate sending callend");
        if(myCallImpl  != null){
            if(currentCallID == -1)
                currentCallID = myCallImpl.getCurrentCallId();
//            Log.d("SQUIRREL","current callid = ",currentCallID);
            if(currentCallID != -1) {
                //SquirrelCallImpl.help2terminate = true;
                /**
                 * 原有代码
                 *  myCallImpl.squirrelTerminate(currentCallID);
                 */
                myCallImpl.squirrelTerminate(currentCallID);
                SIPService.isCallOn = false;
            }
        }


        //finish();
        //LoginToServer();
        //Intent intent = new Intent(CallListeningActivity.this, MainListeningActivity.class);
        //startActivity(intent);
        //CallListeningActivity.this.finish();
    }

   /* public void OnAnswerCall(View view){
        if(myCallImpl != null) {

            if (currentCallID != -1 && myCallImpl.isReceivedCall() == 1) {
                //myCallImpl.squirrelAnswer(currentCallID, 1);
                myCallImpl.squirrelAnswer(currentCallID, 1);
                //myCallImpl.squirrelSwitchVideo(currentCallID,0);
            }
        }
    }*/

    private void OnPlayRing(){
        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch(Exception e) {
        }
    }

    private void OnStopRing(){
        if(mMediaPlayer!=null) {
            mMediaPlayer.setLooping(false);
            mMediaPlayer.stop();
        }
    }
/*
    private void LoginToServer() {
        SharedPreferences preferences;
        preferences = getApplication().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String proxy = "sip:112.74.28.193:9647";
        int protocol = 1;
        String roomID = preferences.readString("roomID", null);
        String passwd = preferences.readString("password", null);
        String zoneID = preferences.readString("zoneID", null);
        String username = zoneID + "03" + roomID + "1";
        myCallImpl.squirrelAccountLogin(SquirrelCallImpl.servername, SquirrelCallImpl.serverport, protocol, null, username, passwd, null, 1);
    }*/

    private void GetICEState(){
        int a =0,v =0;
        a = myCallImpl.squirrelGetICEState(0);
        v = myCallImpl.squirrelGetICEState(1);
        System.out.println("ICEAV =" + Integer.toString(a)+ Integer.toString(v));
    }
}

