package com.boer.delos.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.TokenResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.thread.VerificationCode;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentLinearLayout;

/**
 * @author PengJiYang
 * @Description: "手机密码找回"界面
 * create at 2016/3/9 10:58
 */
public class RetrievePasswordListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private android.widget.EditText etRetrievePassPhone;
    private android.widget.EditText etRetrievePassVerificationCode;
    private android.widget.TextView tvRetrievePassVerificationBtn;
    private android.widget.TextView tvRetrievePassCommitBtn;
    private PercentLinearLayout llRootView;

    VerificationCode verificationCode;
    private String code = "";// 短信验证码
    private String codeToken = "";// 验证码令牌

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        initView();
    }

    private void initView() {
        initTopBar(R.string.retrieve_pass_title_text, null, true, false);
        this.tvRetrievePassCommitBtn = (TextView) findViewById(R.id.tvRetrievePassCommitBtn);
        this.tvRetrievePassVerificationBtn = (TextView) findViewById(R.id.tvRetrievePassVerificationBtn);
        this.etRetrievePassVerificationCode = (EditText) findViewById(R.id.etRetrievePassVerificationCode);
        this.etRetrievePassPhone = (EditText) findViewById(R.id.etRetrievePassPhone);
        this.llRootView = (PercentLinearLayout) findViewById(R.id.llRootView);


        this.tvRetrievePassVerificationBtn.setOnClickListener(this);
        this.tvRetrievePassCommitBtn.setOnClickListener(this);
        verificationCode = new VerificationCode(this, tvRetrievePassVerificationBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRetrievePassCommitBtn:
                hideInput();
                commit();
                break;
            case R.id.tvRetrievePassVerificationBtn:
                tvRetrievePassVerificationBtn.setEnabled(true);
                hideInput();
                negotiate();
                break;
        }
    }

    /**
     * 发送握手请求，获取握手令牌
     */
    private void negotiate() {
        MemberController.getInstance().negotiate(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

                try {
                    TokenResult result = new Gson().fromJson(Json, TokenResult.class);
                    if (result.getRet() != 0) {
                        if (result.getMsg() != null)
                            toastUtils.showInfoWithStatus(result.getMsg());
                    } else {

                        Constant.NEGOTIATE_TOKEN = result.getToken();
                        getVerificationCode();
                    }
                } catch (Exception e) {
                    L.e("loginNegotiate" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                try {
                    if (StringUtil.isEmpty(json)) {
                        toastUtils.showErrorWithStatus(getString(R.string.toast_query_token_fail));

                    } else {
                        toastUtils.showErrorWithStatus(json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getVerificationCode() {
        if (StringUtil.isEmpty(etRetrievePassPhone.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.input_phone_hint));
            hideInput();
        } else if (!StringUtil.isMobile(etRetrievePassPhone.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.wrong_phone_hint));
            hideInput();
            return;
        } else {
            if(SharedPreferencesUtils.isCanSendVerifyCode(this,"retrievePassword")){
                checkMobile();
            }
            else{
                ToastHelper.showShortMsg("请求超过上限！");
            }
        }
    }

    private void checkMobile() {
        MemberController.getInstance().mobileVerify(this, StringUtil.getTextViewString(etRetrievePassPhone), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                if (JsonUtil.parseStateCode(Json)) {
                    int isExist = JsonUtil.parseInt(Json, "isExist");
                    if (isExist == 0) {
                        toastUtils.showInfoWithStatus(getString(R.string.toast_mobile_not_exist));
                    } else {
                        verificationCode.startTimerTask();
                        getCode();
                    }

                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
//                toastUtils.showErrorWithStatus(json); //后台每天发送限制十次
                L.i(json);
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        MemberController.getInstance().smsVerify(this, StringUtil.getTextViewString(etRetrievePassPhone), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                if (JsonUtil.parseStateCode(Json)) {
                    code = JsonUtil.parseString(Json, "sms");
                    codeToken = JsonUtil.parseString(Json, "smsToken");
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });

    }

    /**
     * 提交请求
     */
    private void commit() {
        if (StringUtil.isEmpty(etRetrievePassPhone.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.input_phone_hint));
            hideInput();
            return;
        } else if (!StringUtil.isMobile(etRetrievePassPhone.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.wrong_phone_hint));
            hideInput();
            return;
        }
        if (StringUtil.isEmpty(etRetrievePassVerificationCode.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_code));
            hideInput();
            return;
        }
        if (etRetrievePassVerificationCode.getText().toString().length() != 6) {
            toastUtils.showInfoWithStatus(getString(R.string.verification_code_desc));
            hideInput();
            return;
        }

        if (!code.equals(etRetrievePassVerificationCode.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_code));
            hideInput();
            return;
        }

        Intent intent = new Intent(RetrievePasswordListeningActivity.this, ResetPasswordListeningActivity.class);
        intent.putExtra("RetrievePassPhone", etRetrievePassPhone.getText().toString());
        intent.putExtra("sms", code);
        intent.putExtra("smsToken", codeToken);
        startActivity(intent);
        finish();
    }
}
