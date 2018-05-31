package com.boer.delos.activity.healthylife.wifiAirClean;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity2;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
import com.hiflying.smartlink.v3.SnifferSmartLinker;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WifiAirCleanAddNewDeviceActivity extends CommonBaseActivity2 {
    @Bind(R.id.iv_indicate)
    ImageView ivIndicate;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.tv_ssid)
    TextView tvSsid;
    @Bind(R.id.ll_ssid)
    LinearLayout llSsid;
    @Bind(R.id.tv_ok)
    TextView tvOk;
    private String type;
    private String sn;
    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_air_clean_add_new_device;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("添加新设备");
    }

    @Override
    protected void initData() {
        type = getIntent().getStringExtra("type");
        sn=getIntent().getStringExtra("sn");
        if(StringUtil.isEmpty(sn)){
            tlTitleLayout.setTitle("添加新设备");
        }
        else{
            tlTitleLayout.setTitle("重置网络");
        }
        if (type.equals("T66")) {
            initT66();
        } else if (type.equals("T30")) {
            if(StringUtil.isEmpty(sn)){
                ivIndicate.setImageResource(R.mipmap.ic_indicate_t30);
                llSsid.setVisibility(View.GONE);
                tvOk.setText("设备绑定");
            }
            else{
                ivIndicate.setImageResource(R.mipmap.ic_indicate_t30);
                llSsid.setVisibility(View.GONE);
                tvOk.setVisibility(View.GONE);
//                tvOk.setText("设备绑定");
            }
        }
    }

    @Override
    protected void initAction() {

    }

    @OnClick(R.id.tv_ok)
    public void onViewClicked() {
        if (type.equals("T66")) {
            doStartConfig();
        } else if (type.equals("T30")) {
            startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceSnActivity.class).putExtra("type",type));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (type.equals("T66")) {
            destroyT66();
        } else if (type.equals("T30")) {

        }
    }

    protected ProgressDialog mWaitingDialog;
    protected ISmartLinker mSnifferSmartLinker;
    private boolean mIsConncting = false;
    private BroadcastReceiver mWifiChangedReceiver;
    protected Handler mViewHandler = new Handler();
    private static final String TAG = "NewDeviceT66";
    private void initT66(){
        mSnifferSmartLinker = SnifferSmartLinker.getInstance();
        mWaitingDialog = new ProgressDialog(this);
        mWaitingDialog.setMessage("等待中...");
        mWaitingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mWaitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                mSnifferSmartLinker.setOnSmartLinkListener(null);
                mSnifferSmartLinker.stop();
                mIsConncting = false;
            }
        });
        tvSsid.setText(getSSid());

        mWifiChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null && networkInfo.isConnected()) {
                    tvSsid.setText(getSSid());
                    etPassword.requestFocus();
                    tvOk.setEnabled(true);
                }else {
                    tvSsid.setText("没有wifi连接");
                    etPassword.requestFocus();
                    tvOk.setEnabled(false);
                    if (mWaitingDialog.isShowing()) {
                        mWaitingDialog.dismiss();
                    }
                }
            }
        };
        registerReceiver(mWifiChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    private void destroyT66(){
        mSnifferSmartLinker.setOnSmartLinkListener(null);
        try {
            unregisterReceiver(mWifiChangedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   private void doStartConfig() {
        String ssid=tvSsid.getText().toString();
        if(StringUtil.isEmpty(ssid)){
            ToastHelper.showShortMsg("SSID不能为空");
            return;
        }
        String pwd=etPassword.getText().toString();
        if(StringUtil.isEmpty(pwd)){
            ToastHelper.showShortMsg("密码不能为空");
            return;
        }

       if(!mIsConncting){
           try {
               mSnifferSmartLinker.setOnSmartLinkListener(mOnSmartLinkListener);
               mSnifferSmartLinker.start(getApplicationContext(), pwd,
                       ssid);
               mIsConncting = true;
               mWaitingDialog.show();
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
       }
    }

    private OnSmartLinkListener mOnSmartLinkListener=new OnSmartLinkListener(){
        @Override
        public void onLinked(final SmartLinkedModule module) {
            Log.w(TAG, "onLinked");
            mViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "配置成功",
                            Toast.LENGTH_SHORT).show();
                    mWaitingDialog.dismiss();
                    if(StringUtil.isEmpty(sn)){
                        startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceSnActivity.class).putExtra("type",type));
                    }
                    else{
                        startActivity(new Intent(mContext,WifiAirCleanManagerActivity.class));
                    }
                }
            });
        }

        @Override
        public void onCompleted() {
            Log.w(TAG, "onCompleted");
            mViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "设置完成",
                            Toast.LENGTH_SHORT).show();
                    mWaitingDialog.dismiss();
                    mIsConncting = false;
                }
            });
        }

        @Override
        public void onTimeOut() {
            Log.w(TAG, "onTimeOut");
            mViewHandler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "设置超时",
                            Toast.LENGTH_SHORT).show();
                    mWaitingDialog.dismiss();
                    mIsConncting = false;
                }
            });
        }
    };

    private String getSSid(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        if(wm != null){
            WifiInfo wi = wm.getConnectionInfo();
            if(wi != null){
                String ssid = wi.getSSID();
                if(ssid.length()>2 && ssid.startsWith("\"") && ssid.endsWith("\"")){
                    return ssid.substring(1,ssid.length()-1);
                }else{
                    return ssid;
                }
            }
        }
        return "";
    }
}
