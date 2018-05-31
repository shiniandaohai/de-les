package com.boer.delos.activity.healthylife.skintest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.ToastHelper;

import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/17.
 */
public class SkinTestActivity extends CommonBaseActivity {
    @Bind(R.id.progressBar)
    com.boer.delos.widget.CircleProgressBarView circleProgressBarView;
    private int checkedPos = -1;

    @Override
    protected int initLayout() {
        return R.layout.activity_skin_test;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.skin_test);
    }

    @Override
    protected void initData() {
        BLEClient.getInstance().setmHandler(mHandler);
        boolean isScanning=BLEClient.getInstance().isScanning();
        boolean isConnect=BLEClient.getInstance().getIsConnect();
        if((!isScanning&&!isConnect)){
            circleProgressBarView.setHintText(getString(R.string.txt_bluetooth_scaning));
            circleProgressBarView.startAnimation();
            BLEClient.getInstance().initBLE(this);
        }
        else if(BLEClient.getInstance().getIsConnect()){
            startActivity(new Intent(SkinTestActivity.this,SkinWaterAndOilActivity.class));
            finish();
        }
        else if(isScanning){
            circleProgressBarView.setHintText(getString(R.string.txt_bluetooth_scaning));
            circleProgressBarView.startAnimation();
        }
    }

    @Override
    protected void initAction() {

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 1) {   //连接成功
                circleProgressBarView.setHintText(getApplication().getResources().getString(R.string.bt_state1));
                circleProgressBarView.stopAnimation();
                startActivity(new Intent(SkinTestActivity.this,SkinWaterAndOilActivity.class));
                finish();
            }
            else if (message.what == 2) {  //断开连接

            }
            return false;
        }
    });

    //蓝牙打开回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, getResources().getString(R.string.txt_bluetooth_on), Toast.LENGTH_SHORT).show();
                BLEClient.getInstance().scanBLE(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(circleProgressBarView!=null){
            circleProgressBarView.stopAnimation();
        }
    }

    @OnClick(R.id.btn_reset)
    void resetClick(View view){
        if((!BLEClient.getInstance().isScanning()&&!BLEClient.getInstance().getIsConnect())){
            BLEClient.getInstance().scanBLE(true);
        }
        else{
            ToastHelper.showShortMsg("正在扫描中...");
        }
    }
}
