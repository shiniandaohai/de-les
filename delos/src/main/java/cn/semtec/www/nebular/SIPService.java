package cn.semtec.www.nebular;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import org.linphone.squirrel.squirrelCallImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 15/9/22.
 */
public class SIPService extends Service {

    //------
    private squirrelCallImpl squirrelCall=null;
    private   Timer  squirrel_timer =null;
    SharedPreferences preferences;
    SharedPreferences.Editor pref_editor;
    private String sip_username;
    private String sip_pwd;
    private int hbcnt;

    int currentCallID = -1;

    Timer timer;
    Timer squirreltimer;
    TimerTask timerTask;
    TimerTask squirreltask;
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    final Handler squirrelhandler = new Handler();
    Notification noti;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    //接到呼叫消息，弹出对话框，如果用户选择接通，跳转到CallActivity界面，在界面中，根据值，调用接通方法
    public static boolean isCallOn = false;


    AlertDialog dialog = null;
    public SIPService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        preferences = getApplication().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        pref_editor = preferences.edit();
        //20ms sip heartbeat
//        squirrelCall = (SquirrelCallImpl)getApplication();
        squirrelCall = squirrelCallImpl.getInstance();

        squirrelCall.setHandler(mMsgHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        //Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();


        squirreltask = new TimerTask(){
            public void run() {


                squirrelhandler.post(new Runnable() {
                    public void run() {
                        /*
                        hbcnt++;
                        if(hbcnt>=300){
                            System.out.println("squirrelIterate");
                            hbcnt = 0;
                        }
                        */
                        squirrelCall.squirrelIterate();
                    }
                });
            }
        };

        squirreltimer = new Timer(true);
        squirreltimer.schedule(squirreltask, 10, 20);



       // LoginToServer();
        //Toast.makeText(this, "The squirrel Service was Created", Toast.LENGTH_LONG).show();
      //  createNotification();

        startTimer();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
      //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 500, 1000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                //squirreltimer.schedule(squirreltask, 10, 20);
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        String strDate = simpleDateFormat.format(calendar.getTime());
                        String title = getApplication().getString(R.string.app_name);

                        if(squirrelCallImpl.login_state == 0) title +=" 离线";
                        else title += " 在线";

                        //show the toast
                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
                        //toast.show();
                        //实例化Intent
                        //Intent intent = new Intent(MyService.this, MyService.class);
                        //获取PendingIntent
                        //PendingIntent pi = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                        //设置事件信息
                      /*  Intent intent = new Intent(SIPService.this, MainListeningActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(SIPService.this, 0, intent, 0);
                        noti = builder
                                .setContentTitle(title)
                                .setContentText(strDate.toString()).setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pIntent)
                                .build();
                        noti.flags |= Notification.FLAG_NO_CLEAR;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        //发出通知
                        //获取NotificationManager实例
                        notificationManager.notify(0,noti);*/
                    }
                });
            }
        };
    }

    //-----------
    Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://注册状态
                    //在这可进行想要的操作Scase 1:
                    HandlerRegState(msg.getData());
                case 2: //呼叫状态
                    HandlerCallState(msg.getData());
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void HandlerRegState(Bundle _bundle)
    {
        String sState=null;
        int state  = _bundle.getInt("state");
        String domain = _bundle.getString("domain");
        if(state == squirrelCallImpl.squirrelRegistrationProgress) {
            sState = "Login " +domain;
           //Toast.makeText(this, "云对讲正在登录...", Toast.LENGTH_SHORT).show();
        }else if(state == squirrelCallImpl.squirrelRegistrationOk ){

          //Toast.makeText(this, "云对讲登录" + preferences.readString("login_username", null)+"成功", Toast.LENGTH_SHORT).show();
            squirrelCallImpl.login_state = 1;
        }
        else if(state == squirrelCallImpl.squirrelRegistrationFailed ){
            sState = "Login " + domain +"Failed!";
           //Toast.makeText(this, "云对讲登录失败", Toast.LENGTH_LONG).show();
            squirrelCallImpl.login_state = 0;
            for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
                Device device = deviceRelate.getDeviceProp();
                //重新登录
                if ("Guard".equals(device.getType())) {
                    loginToSipServer(device.getGuardInfo().getAccount(), device.getGuardInfo().getPwd(),
                            device.getGuardInfo().getSIP());
                    break;
                }
            }
        }
        else if(state == squirrelCallImpl.squirrelRegistrationCleared ){
            sState = "Logout " + domain +"Succeeded!";
            squirrelCallImpl.login_state = 0;
           // LoginToServer();
          //Toast.makeText(this, "云对讲退出登录", Toast.LENGTH_SHORT).show();
        }
        else{

        }
        //mStatus.setText(sState);
    }

    /**
     * 登录Sip服务
     * @param account
     * @param passwd
     * @param SIP
     */
    protected void loginToSipServer(String account, String passwd, String SIP) {
        /**测试数据
         * 服务器获取的数据
         * "account": "051008010300010105011",
         "SIP": "075500000100010100000",
         "pwd": "080103",
         "hostID": "001207C40173"
         需要生成的数据
         * roomID 0001010501
         passwd 080103
         zoneID 05100801
         userName 051008010300010105011
         */
        if(account.length() != 21){
            return;
        }else{
            String zoneID = account.substring(0, 8);
            String roomID = account.substring(account.length() - 1 - 10, account.length() - 1);
            //   String username = zoneID + "03" + roomID + "1";
            //保存在本地
            pref_editor.putString("roomID", roomID);
            pref_editor.putString("password", passwd);
            pref_editor.putString("zoneID", zoneID);
            pref_editor.putString("login_username", account);
            pref_editor.putString("SIP", SIP);
            pref_editor.commit();
            int protocol = 1;

            squirrelCallImpl.login_state = 0;
            if ( passwd != null)
                squirrelCall.squirrelAccountLogin(squirrelCallImpl.servername, squirrelCallImpl.serverport, protocol, null, account, passwd, null, 1);
        }
    }

    void HandlerCallState(Bundle _bundle)
    {
        final Bundle tmp = _bundle;
        int state =_bundle.getInt("state");
        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String username = preferences.getString("login_username", "");
//        Log.d("SQUIRREL", "----------------current state " + state);
        if(state == squirrelCallImpl.squirrelCallIncomingReceived) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(SIPService.this);
            builder.setTitle("提示");
            builder.setMessage(username+"呼叫门禁");
            builder.setCancelable(false);
            builder.setPositiveButton("接听", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    isCallOn = true;
                    Intent intent = new Intent(SIPService.this, CallListeningActivity.class);
                    intent.putExtras(tmp);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("挂断", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetICEState();
                    if (currentCallID == -1)
                        currentCallID = squirrelCall.getCurrentCallId();
                    if (currentCallID != -1) {
                        squirrelCall.squirrelUnlock(currentCallID);
                    }
                    isCallOn = false;
                }
            });
            if(dialog!=null){
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
            dialog = builder.create();

            dialog.getWindow()
                    .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            dialog.show();


        }else {
            Handler mh =  squirrelCall.getCallHandler();
            if(dialog!=null){
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
            if(mh != null){
                Message msg = new Message();
                msg.setData(_bundle);
                msg.what = 2;
                mh.sendMessage(msg);
            }
        }

    }

    private void GetICEState(){
        int a =0,v =0;
        a = squirrelCall.squirrelGetICEState(0);
        v = squirrelCall.squirrelGetICEState(1);
        System.out.println("ICEAV =" + Integer.toString(a)+ Integer.toString(v));
    }
}
