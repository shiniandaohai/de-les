package org.linphone.squirrel;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.boer.delos.commen.BaseApplication;
import com.boer.delos.jpush.JPushInit;

import java.util.Timer;

import cn.semtec.www.nebular.CloudCallServiceManager;
import cn.semtec.www.nebular.SIPService;

/**
 * @author wangkai
 * @Description: squirrelCallImpl
 * create at 2016/1/17 23:32
 */
public class squirrelCallImpl {

    public static squirrelCallImpl instance = null;

    public static final int squirrelCallIdle = 0;
    /**
     * <Initial call state
     */
    public static final int squirrelCallIncomingReceived = 1;
    /**
     * <This is a new incoming call
     */
    public static final int squirrelCallOutgoingInit = 2;
    /**
     * <An outgoing call is started
     */
    public static final int squirrelCallOutgoingProgress = 3;
    /**
     * <An outgoing call is in progress
     */
    public static final int squirrelCallOutgoingRinging = 4;
    /**
     * <An outgoing call is ringing at remote end
     */
    public static final int squirrelCallOutgoingEarlyMedia = 5;
    /**
     * <An outgoing call is proposed early media
     */
    public static final int squirrelCallConnected = 6;
    /**
     * <Connected, the call is answered
     */
    public static final int squirrelCallStreamsRunning = 7;
    /**
     * <The media streams are established and running
     */
    public static final int squirrelCallPausing = 8;
    /**
     * <The call is pausing at the initiative of local end
     */
    public static final int squirrelCallPaused = 9;
    /**
     * < The call is paused, remote end has accepted the pause
     */
    public static final int squirrelCallResuming = 10;
    /**
     * <The call is being resumed by local end
     */
    public static final int squirrelCallRefered = 11;
    /**
     * <The call is being transfered to another party, resulting in a new outgoing call to follow immediately
     */
    public static final int squirrelCallError = 12;
    /**
     * <The call encountered an error
     */
    public static final int squirrelCallEnd = 13;
    /**
     * <The call ended normally
     */
    public static final int squirrelCallPausedByRemote = 14;
    /**
     * <The call is paused by remote end
     */
    public static final int squirrelCallUpdatedByRemote = 15;
    /**
     * <The call's parameters change is requested by remote end, used for example when video is added by remote
     */
    public static final int squirrelCallIncomingEarlyMedia = 16;
    /**
     * <We are proposing early media to an incoming call
     */
    public static final int squirrelCallUpdating = 17;
    /**
     * <A call update has been initiated by us
     */
    public static final int squirrelCallReleased = 18;
    /**
     * < The call object is no more retained by the core
     */

    public static final int squirrelRegistrationProgress = 19;
    /**
     * <Registration is in progress
     */
    public static final int squirrelRegistrationOk = 20;
    /**
     * < Registration is successful
     */
    public static final int squirrelRegistrationCleared = 21;
    /**
     * < Unregistration succeeded
     */
    public static final int squirrelRegistrationFailed = 22;
    /**
     * <Registration failed
     */
    public static final int squirrelHasUnlock = 23;     /*hava lock msg arrived*/
    public static final int squirrelMessageArrived = 24; /* new text message arrived */
    public static final int squirrelMessageDelivered = 25; /*send text message succeeded*/
    public static final int squirrelMessageNotDelivered = 26; /*send text message failed */


    //定义SIP服务器域名和端口
    public static final String servername = "semtec-ss1";//伟创达
    public static final String servername_BOER = "boer-ss1"; //boer
    public static final int serverport = 9647;
    //public static final String servername = "tedi-ss1";
    //public static final int serverport = 5070;

    public int currentCallId = -1;

    private int currentCallState = -1;
    private int currentRegState = -1;
    private Timer timer = null;
    private Handler mHandler = null;
    Handler callHandler = null;

    public static int login_state = 0;
    public static boolean help2terminate = false;

    public void setHandler(Handler _handle) {
        mHandler = _handle;
    }

    public void setCallHandler(Handler _h) {
        callHandler = _h;
    }

    ;

    public Handler getCallHandler() {
        return callHandler;
    }

    public int isReceivedCall() {
        if (currentCallState == squirrelCallIncomingReceived)
            return 1;
        return 0;
    }


    public int getCurrentCallId() {
        return currentCallId;
    }

    public void regState(String domain, String reason, int state) //由用户实现，被底层调用。
    {

        if (mHandler == null)
            return;
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 1;
        bundle.putInt("state", state);
        bundle.putString("domain", domain);
        bundle.putString("reason", reason);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        if (state == squirrelRegistrationProgress) {
//            Log.d("SQUIRREL", "login " + domain + " progressing..." + "reason:" + reason);

        } else if (state == squirrelRegistrationOk) {
//            Log.d("SQUIRREL", "login " + domain + " Succeeded" + "reason:" + reason);

        } else if (state == squirrelRegistrationCleared) {
//            Log.d("SQUIRREL", "logout " + domain + " Succeeded" + "reason:" + reason);

        } else if (state == squirrelRegistrationFailed) {
//            Log.d("SQUIRREL", "login " + domain + " Failed" + "reason:" + reason);

        }
//        Log.d("SQUIRREL", reason);


    }

    public static squirrelCallImpl getInstance() {
        if (instance == null) {
            synchronized (squirrelCallImpl.class) {
                if (instance == null) {
                    instance = new squirrelCallImpl();
                }
            }
        }
        return instance;
    }

    static void load() {

    }

    public void initSquirrel() {
        System.loadLibrary("ffmpeg-linphone-arm");
        System.loadLibrary("semtec-squirrel-armeabi-v7a");
        squirrelSetPowerManager(BaseApplication.getAppContext().getSystemService(Context.POWER_SERVICE));
        squirrelInit(this);

        //Settings for ICE
        squirrelSetStunServer("stun.3cx.com");
        squirrelSetFirewall(2);
        //squirrelSetFirewall(1);
//        CrashHandler.getInstance().init(this);//初始化全局异常管理
        try {
            new JPushInit(BaseApplication.getAppContext(), "", "");
            //JPushInterface.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }



		/*
        timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {

				squirrelIterate();
			}
		}, 20, 20);
		*/

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        CloudCallServiceManager receiver = new CloudCallServiceManager();
        BaseApplication.getAppContext().registerReceiver(receiver, filter);
        BaseApplication.getAppContext().startService(
                new Intent(BaseApplication.getAppContext(), SIPService.class));
        instance = this;
    }

    public void callState(String username, String nickname, int state, int callid)//由用户实现，被底层调用。
    {

        if (mHandler == null)
            return;
        String _nickname = username;
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 2;
        bundle.putInt("state", state);
        bundle.putInt("callid", callid);
        bundle.putString("username", username);
        if (nickname != null)
            _nickname = nickname;

        bundle.putString("nickname", _nickname);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        if (state == squirrelCallOutgoingInit) {
            //start other activity;
            currentCallId = callid;
        } else if (state == squirrelCallEnd) {
            if (currentCallId == callid)
                currentCallId = -1;
        }
        currentCallState = state;

    }

    public void messageState(String from, String text, int state, int usrdata)//由用户实现，被底层调用
    {
        if (state == squirrelMessageDelivered) {
            //            Log.d("SQUIRREL", "send text " + usrdata + " ok \n");
        } else if (state == squirrelMessageNotDelivered) {
            //            Log.d("SQUIRREL", "send text " + usrdata + " failed \n");
        } else if (state == squirrelMessageArrived) {
            //            Log.d("SQUIRREL", "from " + from + "recevied text: " + text + "\n");
        }

    }

//    @Override
//    public void onTerminate() {
//        super.onTerminate();
///*        //退出呼叫库
//       squirrelUninit();*/
//    }

    public native void squirrelInit(Object obj);

    public native void squirrelUninit();

    public native void squirrelIterate(); //每隔20MS调用一次。

    public native void squirrelCall(String username, int av);

    public native void squirrelAnswer(int callid, int av);

    public native void squirrelTerminate(int callid);

    public native void squirrelUnlock(int callid);

    public native void squirrelUpdateCall(int callid);

    public native void squirrelSwitchVideo(int callid, int oc);

    public native void squirrelAccountLogin(String proxy, int port, int protocol, String outbound, String username, String passwd, String nickname, int isdefault);

    public native void squirrelAccountExit(String proxy, int port, String username);

    public native void squirrelSetCleanProxyAndAccount();

    public native void squirrelSetStunServer(String stunserver);

    public native void squirrelSetFirewall(int firewall);

    public native void squirrelSetGlobalVideo(int local, int remote);

    public native void squirrelSetCaFilePath(String path);

    public native void squirrelSetRemoteVideoWindow(Object window);

    public native void squirrelSetLocalVideoWindow(Object window);

    public native void squirrelSetCameraRotation(int rotation);

    public native String squirrelGetInstanceUUID();

    public native void squirrelSetInstanceUUID(String uuid);

    public native void squirrelSetCamera(int id);

    public native int squirrelGetCamera();

    public native void squirrelSetLogEnabled(int enabled);

    public native void squirrelSetPowerManager(Object obj);

    //1008
    public native void squirrelSendMessage(String toUserName, String proxy, int port, String content, int userdata);

    public native void squirrelDestroyChat(String strTo);

    public native int squirrelGetAudioCodecSize();

    public native long squirrelGetAudioCodec(int idx);

    public native String squirrelGetAuidoCodecName(long pt);

    public native void squirrelResetAudioCodecList(long pt, int isend);

    public native void squirrelSetNetworkReachable(int isReachable);

    public native void squirrelRefreshRegisters();

    //1009
    public native int squirrelGetICEState(int isVideo);

    public native float squirrelGetDownloadBandwidth(int isVideo);

    public native float squirrelGetUploadBandwidth(int isVideo);

    public native float squirrelGetRoundTripDelay(int isVideo);

    public native float squirrelGetLocalLossRate(int isVideo);

    public native float squirrelGetLocalLateRate(int isVideo);

    public native float squirrelGetCurrentQuality();

    public native void squirrelSetMicMuted(int isEnabled);

}
