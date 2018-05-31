package com.boer.delos.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.thread.VerificationCode;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

/**
 * @author PengJiYang
 * @Description: 绑定新手机号界面
 * create at 2016/5/12 11:45
 */
public class BindNewPhoneListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private EditText etNewPhone;
    private EditText etVerificationCode;
    private TextView tvGetVerificationCode;
    private TextView tvCommit;

    private VerificationCode timerTask;
    private String sms;// 验证码
    private String smsToken;// 验证码令牌
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_new_phone);

//        user = new Gson().fromJson(SharedUtils.getInstance().getTagSp(SharedTag.user_login), User.class);
        user = Constant.LOGIN_USER;
        initView();
    }

    private void initView() {
        initTopBar(R.string.bind_new_phone, null, true, false);
        this.tvCommit = (TextView) findViewById(R.id.tvCommit);
        this.tvGetVerificationCode = (TextView) findViewById(R.id.tvGetVerificationCode);
        this.etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        this.etNewPhone = (EditText) findViewById(R.id.etNewPhone);

        this.tvGetVerificationCode.setOnClickListener(this);
        this.tvCommit.setOnClickListener(this);
        timerTask = new VerificationCode(this, tvGetVerificationCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGetVerificationCode:
                if (this.getCurrentFocus().getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                verifyMobile();
                break;
            case R.id.tvCommit:
                resetMobile();
                break;
        }
    }

    /**
     * 验证新手机号是否可用
     */
    private void verifyMobile() {
        L.e("BindNewPhoneListeningActivity mobile===" + this.etNewPhone.getText().toString());
        if (StringUtil.isEmpty(this.etNewPhone.getText().toString())) {
            toastUtils.showInfoWithStatus("请输入新手机号");
            return;
        }
        if (!StringUtil.isMobile(this.etNewPhone.getText().toString())) {
            toastUtils.showInfoWithStatus("手机号格式不正确");
            return;
        }

        if(SharedPreferencesUtils.isCanSendVerifyCode(this,"bindNewPhonePassword")){
            MemberController.getInstance().mobileVerify(this, etNewPhone.getText().toString(), new RequestResultListener() {
                @Override
                public void onSuccess(String Json) {
                    L.e("BindNewPhoneListeningActivity verifyMobile===" + Json);
                    String ret = JsonUtil.parseString(Json, "ret");
                    if ("0".equals(ret)) {
                        if ("1".equals(JsonUtil.parseString(Json, "isExist"))) {
                            toastUtils.showInfoWithStatus("该号码已注册");
                        } else {
                            timerTask.startTimerTask();
                            getVerificationCode();
                        }
                    } else {
                        toastUtils.showInfoWithStatus(JsonUtil.parseString(Json, "msg"));
                    }
                }

                @Override
                public void onFailed(String json) {
                    toastUtils.showErrorWithStatus("系统异常");
                }
            });
        }
        else{
            ToastHelper.showShortMsg("请求超过上限！");
        }
    }

    /**
     * 获取验证码
     */
    private void getVerificationCode() {

        MemberController.getInstance().smsVerify(this, etNewPhone.getText().toString(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("BindNewPhoneListeningActivity getVerificationCode===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    sms = JsonUtil.parseString(Json, "sms");
                    smsToken = JsonUtil.parseString(Json, "smsToken");
                } else {
                    toastUtils.showInfoWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showErrorWithStatus("系统异常");
            }
        });
    }

    /**
     * 重置手机号
     */
    private void resetMobile() {
        if (StringUtil.isEmpty(etVerificationCode.getText().toString())) {
            toastUtils.showInfoWithStatus("请输入验证码");
            return;
        }
        if (!etVerificationCode.getText().toString().equals(sms)) {
            toastUtils.showInfoWithStatus("验证码不正确");
            return;
        }
        MemberController.getInstance().resetMobile(this, etVerificationCode.getText().toString(), smsToken, user.getId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("BindNewPhoneListeningActivity resetMobile===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.showSuccessWithStatus("绑定成功");
                    user.setMobile(etNewPhone.getText().toString());
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    Intent intent = new Intent(BindNewPhoneListeningActivity.this, LoginActivity.class);
                    intent.putExtra("ActivityName", "BindNewPhoneListeningActivity");
                    intent.putExtra("PhoneNumber", etNewPhone.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    toastUtils.showInfoWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showErrorWithStatus("系统异常");
            }
        });
    }
}
