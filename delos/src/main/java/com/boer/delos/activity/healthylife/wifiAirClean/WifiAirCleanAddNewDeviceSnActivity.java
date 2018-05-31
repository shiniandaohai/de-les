package com.boer.delos.activity.healthylife.wifiAirClean;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.zxing.activity.CaptureActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.wifiAirClean.WifiAirCleanController;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WifiAirCleanAddNewDeviceSnActivity extends CommonBaseActivity {
    @Bind(R.id.et_sn)
    TextView etSn;
    private static final int REQUEST_QRCODE =0;
    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_air_clean_add_new_device_sn;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("添加新设备");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_QRCODE){
                String result=data.getStringExtra("result");
                if(StringUtil.checkDigitAndLetter(result)){
                    etSn.setText(result);
                }
                else{
                    ToastHelper.showShortMsg("无效的设备二维码");
                }

            }
        }
    }

    @OnClick({R.id.tv_scan, R.id.tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_scan:
                Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_QRCODE);
                break;
            case R.id.tv_commit:
                doCommit();
                break;
        }
    }

    private void doCommit() {
        String sn=etSn.getText().toString();
        if(StringUtil.isEmpty(sn)){
            ToastHelper.showShortMsg("序列号不能为空");
            return;
        }
        toastUtils.showProgress("");
        WifiAirCleanController.getInstance().bingDevice(mContext, sn, Constant.WIFI_AIR_CLEAN_UID, "空气净化器", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        startActivity(new Intent(mContext,WifiAirCleanManagerActivity.class));
                    } else {
                        ToastHelper.showShortMsg(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("添加新设备失败");
                }
                toastUtils.dismiss();
            }

            @Override
            public void onFailed(String json) {
                ToastHelper.showShortMsg("添加新设备失败");
                toastUtils.dismiss();
            }
        });
    }
}
