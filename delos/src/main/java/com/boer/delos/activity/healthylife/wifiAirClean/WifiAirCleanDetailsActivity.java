package com.boer.delos.activity.healthylife.wifiAirClean;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.zxing.encoding.EncodingHandler;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.WifiAirCleanDeviceInfo;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.wifiAirClean.WifiAirCleanController;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.ToastHelper;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;

/**
 * Created by gaolong on 2017/4/14.
 */
public class WifiAirCleanDetailsActivity extends CommonBaseActivity{
    @Bind(R.id.vp_air_operation)
    ViewPager vpAirOperation;
    @Bind(R.id.tv_temperature)
    TextView tvTemperature;
    @Bind(R.id.tv_humidity)
    TextView tvHumidity;
    @Bind(R.id.tv_pm25)
    TextView tvPm25;
    @Bind(R.id.tv_tvoc)
    TextView tvTvoc;
    @Bind(R.id.tv_air_health)
    TextView tvAirHealth;
    @Bind(R.id.tv_wind_speed)
    public TextView tvWindSpeed;
    @Bind(R.id.llayout_data)
    LinearLayout llayoutData;
    @Bind(R.id.tv_status)
    TextView tvStatus;

    static int skinColor;
    private WifiAirCleanDeviceInfo mWifiAirCleanDeviceInfo;
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragmentList;
    WifiAirControlFragment fragmentControl;
    WifiAirDataFragment fragmentData;

    @Override
    protected int initLayout() {
        return R.layout.activity_air_clean;
    }

    @Override
    protected void initView() {
        mWifiAirCleanDeviceInfo=(WifiAirCleanDeviceInfo)getIntent().getSerializableExtra("info");
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back_white);
        tlTitleLayout.setTitle(mWifiAirCleanDeviceInfo.getNickName(), R.color.white);
        tlTitleLayout.setLinearRightImage(R.mipmap.ic_share_white);
        initMqtt(this);

        fragmentList = new ArrayList<>();
        fragmentControl = new WifiAirControlFragment();
        fragmentData = new WifiAirDataFragment();
        fragmentControl.setControlFragmentListener(new WifiAirControlFragment.OnControlFragmentClickItem() {
            @Override
            public void posCmd(String cmdType, int cmdValue) {
                isSendCmd=true;
                if(cmdType.equals("AirPower")&&cmdValue==0){
                    toastUtils.showProgress("正在开机...");
                }
                else{
                    toastUtils.showProgress("请稍后...");
                }
                publish(getCmdJson(cmdType,cmdValue));
            }
        });
        fragmentData.setDataFragmentListener(new WifiAirDataFragment.OnDataFragmentClickItem() {
            @Override
            public void posCmd(String cmdType, int cmdValue) {
                publish(getCmdJson(cmdType,cmdValue));
            }
        });
        fragmentList.add(fragmentControl);
        fragmentList.add(fragmentData);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpAirOperation.setAdapter(viewPagerAdapter);

        int color=getColorByQuality("良");
        skinColor=color;
        fragmentControl.setSkin();
        fragmentData.setSkin();
        statusBarTheme(true, color);
        tlTitleLayout.setTitleBackgroundColor(color);
        llayoutData.setBackgroundColor(color);
        getDeviceInfo();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
    @Override
    protected void initData() {
    }

    @Override
    public void rightViewClick() {
        try {
            //第一个参数是你输入的编码，第二个参数是宽和高，因为生成的二维码图片是正方形，所以设置一个即可
            HashMap<String,String> msg=new HashMap<String,String>();
            msg.put("sn",mWifiAirCleanDeviceInfo.getSn());
            msg.put("nickName", mWifiAirCleanDeviceInfo.getNickName());
            String msgJson=new Gson().toJson(msg);
            Bitmap grcode= EncodingHandler.createQRCode(msgJson,DensityUitl.dip2px(mContext,300));
            //将生成的二维码显示到指定的ImageView中
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            final AlertDialog alertDialog = builder.create();
            ImageView imageVinew= new ImageView(this);
            ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            imageVinew.setLayoutParams(params);
            imageVinew.setImageBitmap(grcode);
            alertDialog.setView(imageVinew);
            alertDialog.show();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private boolean isSendCmd;

    private void updateUI(String json){
        try {
            JSONObject msg=new JSONObject(json);
            JSONObject content=msg.getJSONObject("Content");
            String actionType=msg.getString("ActionType");

            if(actionType.equals("St")){
                if(isSendCmd){
                    isSendCmd=false;
                    toastUtils.showProgress("设置成功");
                    toastUtils.dismiss();
                }

                tvWindSpeed.setText(content.getString("AirSpeed"));
                fragmentControl.speed=Integer.valueOf(content.getString("AirSpeed"));
                fragmentControl.getMod(content.getString("Power"),content.getString("AirPower"));
                fragmentData.getMod(content.getString("Power"),content.getString("AirPower"));
                String power=content.getString("Power");
                if(power.equals("0")){
                    tvStatus.setText("狂风模式");
                }
                else if(power.equals("1")){
                    tvStatus.setText("自动模式");
                }
                else if(power.equals("2")){
                    tvStatus.setText("睡眠模式");
                }
                else if(power.equals("4")){
                    tvStatus.setText("手动模式");
                }
            }
            else if(actionType.equals("IAQ")){
                tvHumidity.setText(content.getString("humidity")+"%");
                tvTemperature.setText(content.getString("temp")+"℃");
                tvPm25.setText(content.getString("pm25")+"μg/m3");
                tvTvoc.setText(content.getString("tvoc")+"ppm");
                String quality=content.getString("quality");
                tvAirHealth.setText(quality);
                int color=getColorByQuality(quality);
                skinColor=color;
                fragmentControl.setSkin();
                fragmentData.setSkin();
                statusBarTheme(true, color);
                tlTitleLayout.setTitleBackgroundColor(color);
                llayoutData.setBackgroundColor(color);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimerTask();
        destroyMqtt();
    }

    private int getColorByQuality(String quality){
        if(quality.equals("极差")){
            return Color.parseColor("#f24949");
        }
        else if(quality.equals("差")){
            return Color.parseColor("#f26549");
        }
        else if(quality.equals("中")){
            return Color.parseColor("#f29d49");
        }
        else if(quality.equals("良")){
            return Color.parseColor("#48b38a");
        }
        else if(quality.equals("优")){
            return Color.parseColor("#49b0f2");
        }
        else if(quality.equals("超优")){
            return Color.parseColor("#1796e5");
        }
        else{
            return Color.parseColor("#48b38a");
        }
    }

    private static final String TAG="MqttHelper";
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private String host = "tcp://iot.ucheer.com:8088";
    private String userName = "admin";
    private String passWord = "password";
    private String myTopic = "topic";
    private String clientId = Constant.LOGIN_USER.getId();

    private void initMqtt(Context context) {
        myTopic=mWifiAirCleanDeviceInfo.getSn();
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(context, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
//        conOpt.setUserName(userName);
//        // 密码
//        conOpt.setPassword(passWord.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection(context);
        }
    }

    private void doClientConnection(Context context) {
        if (!client.isConnected() && isConnectIsNomarl(context)) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    private void publish(String msg){
        String topic = myTopic;
        Integer qos = 0;
        Boolean retained = false;
        try {
            client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void destroyMqtt(){
        try {
            if(client!=null){
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.i(TAG, "messageArrived:" + str1);
            Log.i(TAG, str2);

            updateUI(str1);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
        }
    };

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                // 订阅myTopic话题
                client.subscribe(new StringBuilder(myTopic).reverse().toString(),1);
                startTimerTask();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
        }
    };

    /** 判断网络是否连接 */
    private boolean isConnectIsNomarl(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    private ScheduledExecutorService singleThreadScheduledPool;
    private String iaqMsgJson;
    private String airSpeedMsgJson;
    private void startTimerTask(){
        if(singleThreadScheduledPool==null){
            singleThreadScheduledPool = Executors.newSingleThreadScheduledExecutor();
        }
        if(iaqMsgJson ==null){
            HashMap<String,Object> msg=new HashMap<String, Object>();
            msg.put("CMDFrom","ClientIOS");
            HashMap<String,Object> cmd=new HashMap<String, Object>();
            cmd.put("MessageType","0");
            HashMap<String,Object> content=new HashMap<String, Object>();
            content.put("IAQ","0");
            cmd.put("Content",content);
            cmd.put("ActionType","IAQ");
            cmd.put("SN",myTopic);
            msg.put("CMD",cmd);
            msg.put("SendUser",clientId);
            msg.put("AcceptUser",myTopic);
            iaqMsgJson =new Gson().toJson(msg);
        }
        if(airSpeedMsgJson ==null){
            HashMap<String,Object> msg=new HashMap<String, Object>();
            msg.put("CMDFrom","ClientIOS");
            HashMap<String,Object> cmd=new HashMap<String, Object>();
            cmd.put("MessageType","0");
            HashMap<String,Object> content=new HashMap<String, Object>();
            content.put("AirSpeed","0");
            cmd.put("Content",content);
            cmd.put("ActionType","AirSpeed");
            cmd.put("SN",myTopic);
            msg.put("CMD",cmd);
            msg.put("SendUser",clientId);
            msg.put("AcceptUser",myTopic);
            airSpeedMsgJson =new Gson().toJson(msg);
        }
        singleThreadScheduledPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                publish(iaqMsgJson);
//                publish(airSpeedMsgJson);
            }
        },0,15, TimeUnit.SECONDS);
    }

    private void stopTimerTask(){
        if(singleThreadScheduledPool!=null){
            singleThreadScheduledPool.shutdown();
        }
    }

    private String getCmdJson(String cmdType,int cmdValue){
        HashMap<String,Object> msg=new HashMap<String, Object>();
        msg.put("CMDFrom","ClientIOS");
        HashMap<String,Object> cmd=new HashMap<String, Object>();
        cmd.put("MessageType","0");
        cmd.put("ActionType",cmdType);
        cmd.put("SN",mWifiAirCleanDeviceInfo.getSn());
        cmd.put("Pn", getMacAddress());
        HashMap<String,Object> content=new HashMap<String, Object>();
        content.put(cmdType,cmdValue+"");
        cmd.put("Content",content);
        msg.put("CMD",cmd);
        msg.put("SendUser",Constant.LOGIN_USER.getId());
        msg.put("AcceptUser",mWifiAirCleanDeviceInfo.getSn());
        return new Gson().toJson(msg);
    }

    private String getMacAddress() {
        String macAddress =null;
        WifiManager wifiManager =
                (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null== wifiManager ?null: wifiManager.getConnectionInfo());

        if(!wifiManager.isWifiEnabled())
        {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if(null!= info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    private void getDeviceInfo(){
        toastUtils.showProgress("");
        WifiAirCleanController.getInstance().deviceState(mContext, mWifiAirCleanDeviceInfo.getSn(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject objJson = jsonObject.getJSONObject("obj");
                        tvWindSpeed.setText(objJson.getString("airgear"));
                        fragmentControl.speed=Integer.valueOf(objJson.getString("airgear"));
                        fragmentControl.getDeviceState(objJson.getString("state"));
                        fragmentControl.getDeviceAirPower(objJson.getString("AirPower"));
                        fragmentData.getDeviceAirPower(objJson.getString("AirPower"));
                        String power=objJson.getString("state");
                        if(power.equals("0")){
                            tvStatus.setText("狂风模式");
                        }
                        else if(power.equals("1")){
                            tvStatus.setText("自动模式");
                        }
                        else if(power.equals("2")){
                            tvStatus.setText("睡眠模式");
                        }
                        else if(power.equals("4")){
                            tvStatus.setText("手动模式");
                        }
                    } else {
                        ToastHelper.showShortMsg(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toastUtils.dismiss();
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
            }
        });
    }
}
