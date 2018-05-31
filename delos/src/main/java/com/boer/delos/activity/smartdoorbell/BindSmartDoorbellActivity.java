package com.boer.delos.activity.smartdoorbell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.request.smartdoorbell.ICVSSUserModule;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.camera.WifiAdmin;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.Method;
import com.eques.icvss.utils.ResultCode;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/15.
 */

public class BindSmartDoorbellActivity extends CommonBaseActivity implements ICVSSListener{

    private static ICVSSUserInstance icvss;
    private static final String DISTRIBUTE_URL = "thirdparty.ecamzone.cc:8443";
    private String preferfsUserName="001207c40173";
    private String preferfsAppkey="sdk_demo";
    private String preferfsKeyId="5d91e3b2b7fbb31c";//固定值
    private static final int HANDLER_WAHT_MSGRESP = 0;
    private static final int HANDLER_WAHT_ONDISCONNECT = 1;
    private static final int HANDLER_WAHT_QRCODE = 3;
    private static final int HANDLER_WAHT_OPENLOCK = 4;
    private int stepIndex = 0;
    @Bind(R.id.tv_connect1)
    TextView tvConnect1;
    @Bind(R.id.tv_connect2)
    TextView tvConnect2;
    @Bind(R.id.tv_connect3)
    TextView tvConnect3;
    @Bind(R.id.tv_ssid)
    TextView tvSsid;
    @Bind(R.id.etPwd)
    EditText etPwd;
    @Bind(R.id.ll_step1)
    LinearLayout llStep1;
    @Bind(R.id.iv_host)
    ImageView ivHost;
    @Bind(R.id.ll_step2)
    RelativeLayout llStep2;
    @Bind(R.id.iv_qrcode)
    ImageView ivQrcode;
    @Bind(R.id.step3)
    LinearLayout step3;
    @Bind(R.id.ll_fail)
    LinearLayout llFail;
    @Bind(R.id.ll_success)
    LinearLayout llSuccess;
    @Bind(R.id.tvCommit)
    TextView tvCommit;

    @Override
    protected int initLayout() {
        return R.layout.activity_bind_smart_door_bell;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("绑定智能门铃");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        toStep1();
    }

    private void toStep1() {
        llStep1.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE);
        llFail.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        stepIndex = 1;
        tvSsid.setText(getWifiInfoSSid());
    }

    private void toStep2() {
        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.VISIBLE);
        step3.setVisibility(View.GONE);
        llFail.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        stepIndex = 2;
        tvCommit.setText("准备扫描");
    }

    private void toStep3() {
        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        step3.setVisibility(View.VISIBLE);
        llFail.setVisibility(View.GONE);
        llSuccess.setVisibility(View.GONE);

        stepIndex = 3;
        tvCommit.setVisibility(View.GONE);
        tvConnect1.setTextColor(Color.parseColor("#34ad66"));
        tvConnect2.setTextColor(Color.parseColor("#34ad66"));
        tvConnect3.setTextColor(Color.parseColor("#86d5f2"));

        toastUtils.showProgress("登录中...");
        icvss.equesLogin(this, DISTRIBUTE_URL, preferfsUserName, preferfsAppkey);
        llFail.setVisibility(View.GONE);
//        icvss.equesLogin(this, DISTRIBUTE_URL, Constant.CURRENTHOSTID, preferfsAppkey);
    }

    private void toSuccess() {
        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE);
        llSuccess.setVisibility(View.VISIBLE);

        stepIndex = 0;
        tvCommit.setVisibility(View.VISIBLE);
        tvCommit.setText("确定");
    }

    private void toFail() {
        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE);
        llFail.setVisibility(View.VISIBLE);
        llSuccess.setVisibility(View.GONE);

        stepIndex = -1;
        tvCommit.setVisibility(View.VISIBLE);
        tvCommit.setText("重新绑定");
    }


    public String getWifiInfoSSid() {
        WifiAdmin wifiAdmin=new WifiAdmin(this);
        return wifiAdmin.getRealWifiSSID();
    }

    @OnClick(R.id.tvCommit)
    public void onViewClicked() {
        switch (stepIndex){
            case 1:
                String pwd=etPwd.getText().toString();
                if(!StringUtil.isEmpty(pwd)){
                    toStep2();
                }
                else{
                    ToastHelper.showShortMsg("密码不能为空");
                }
                break;
            case 2:
                toStep3();
                break;
            case 3:
                break;
            case 0:
                setResult(RESULT_OK,new Intent().putExtra("addr",mAddr));
                finish();
                break;
            case -1:
                toStep3();
                break;
        }
    }

    @Override
    public void onDisconnect(int i) {
        Log.d("onDisconnect","onPingPong:"+i);
        Message msg = new Message();
        msg.what = HANDLER_WAHT_ONDISCONNECT;
        msg.obj = "与服务器断开连接";
        mHandler.sendMessage(msg);
    }

    @Override
    public void onPingPong(int i) {
        Log.d("BindDoorbell","onPingPong:"+i);
    }

    @Override
    public void onMeaasgeResponse(JSONObject json) {
        Log.d("BindDoorbell","onMeaasgeResponse:"+json);
        Message msg = new Message();
        msg.obj = json;
        msg.what = HANDLER_WAHT_MSGRESP;
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_WAHT_ONDISCONNECT:
                    String msgText = (String) msg.obj;
                    ToastHelper.showShortMsg(msgText);
                    break;
                case HANDLER_WAHT_MSGRESP:
                    JSONObject json = (JSONObject) msg.obj;
                    String method = json.optString(Method.METHOD);
                    if (Method.METHOD_EQUES_SDK_LOGIN.equals(method)) {
                        int code = json.optInt(Method.ATTR_ERROR_CODE);
                        if (code == 4000) {
                            toastUtils.dismiss();
                            Bitmap bitmap = icvss.equesCreateQrcode(tvSsid.getText().toString(), etPwd.getText().toString(),
                                    preferfsKeyId, preferfsUserName,
                                    2, DensityUitl.dip2px(mContext,150));
                            ivQrcode.setImageBitmap(bitmap);
                        }
                    }
                    else if (method.equals(Method.METHOD_ONADDBDY_REQ)) {
                        ivQrcode.setImageBitmap(null);
                        String reqId = json.optString(Method.ATTR_REQID, null);
                        icvss.equesAckAddResponse(reqId, 1);
                    }
                    else if (method.equals(Method.METHOD_ONADDBDY_RESULT)) {
                        int addBdyCode = json.optInt(Method.ATTR_ERROR_CODE);
                        if(addBdyCode == ResultCode.SUCCESS){
                            toSuccess();
                        }else if (addBdyCode == ResultCode.TIMEOUT) {
                            toFail();
                        }
                        else if (addBdyCode == ResultCode.DUPLICATE_OPERA) {
                            ToastHelper.showShortMsg("设备已经添加");
                            toSuccess();
                        }
                        else if (addBdyCode == 4412) {
                            ToastHelper.showShortMsg("name所指定的设备或用户不存在");
                            toFail();
                        }
                        JSONArray devs = json.optJSONArray(Method.ATTR_ONLINES);
                        if(devs != null){
                            JSONObject jobj = devs.optJSONObject(0);
                            if (jobj != null) {
//                                bid = jobj.optString(Method.ATTR_BUDDY_BID, null);
//                                uid = jobj.optString(Method.ATTR_BUDDY_UID, null);
                            }
                        }

                        mAddr=json.optString("devname");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        icvss.equesUserLogOut();
        ICVSSUserModule.getInstance(this).closeIcvss();
        icvss = null;
    }


    private String mAddr;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK,new Intent().putExtra("addr",mAddr));
        finish();
    }

    @Override
    public void leftViewClick() {
        setResult(RESULT_OK,new Intent().putExtra("addr",mAddr));
        finish();
    }
}
