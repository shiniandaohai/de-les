package com.boer.delos.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.TokenResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.thread.VerificationCode;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author PengJiYang
 * @Description: "忘记密码"界面
 * create at 2016/3/9 10:56
 */
public class LoginErrorActivity extends CommonBaseActivity
        implements View.OnClickListener, TextWatcher {


    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.et_verify_code)
    EditText mEtVerifyCode;
    @Bind(R.id.tv_verify_code)
    TextView mTvVerifyCode;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    @Bind(R.id.llRegister)
    LinearLayout mLlRegister;

    private boolean isHidden = false;
    VerificationCode verificationCode;
    private String sms = "";
    private String smsToken;

    @Override
    protected int initLayout() {
        return R.layout.activity_login_error;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(getString(R.string.text_password_forget));
        mTvVerifyCode.setText(getString(R.string.text_get_code));
        mTvVerifyCode.setTextColor(getResources().getColor(R.color.gray_et_hint));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtil.isEmpty(mEtPassword.getText().toString())
                || StringUtil.isEmpty(mEtPassword.getText().toString())) {
            mBtnCommit.setEnabled(false);
            mBtnCommit.setBackgroundResource(R.drawable.btn_commit_unclick);

        } else {
            mBtnCommit.setEnabled(true);
            mBtnCommit.setBackgroundResource(R.drawable.btn_blue_save);
        }
    }

    @Override
    protected void initAction() {
        mEtUsername.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);

        mLlRegister.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        mTvVerifyCode.setOnClickListener(this);
        verificationCode = new VerificationCode(this, mTvVerifyCode);

        mEtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String psd = mEtPassword.getText().toString();
                String username = mEtUsername.getText().toString();
                if (!hasFocus) {
                    if (StringUtil.isEmpty(username)) {
                        toastUtils.showErrorWithStatus(getString(R.string.text_username_null));
                        return;
                    }
                    if (username.contains(" ")) {
                        toastUtils.showErrorWithStatus(getString(R.string.text_username_contains_null));
                    }

                }
                if (StringUtil.isEmpty(username)) {
                    mBtnCommit.setEnabled(false);
                    mBtnCommit.setBackgroundResource(R.drawable.btn_commit_unclick);

                } else {
                    mBtnCommit.setEnabled(true);
                    mBtnCommit.setBackgroundResource(R.drawable.btn_blue_save);

                }
            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_service:
                startActivity(new Intent(this, RegisterAgreementListeningActivity.class));
                break;
            case R.id.btn_commit:
                commitRegister();
                if (this.getCurrentFocus().getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;

            case R.id.tv_verify_code:
                if (StringUtil.isEmpty(mEtUsername.getText().toString())) {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_input_mobile));
                    hideInput();
                    return;
                }
                if (!StringUtil.isMobile(mEtUsername.getText().toString())) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_error_mobile));
                    hideInput();
                    return;
                }

                if(SharedPreferencesUtils.isCanSendVerifyCode(this,"forgetPassword")){
                    toastUtils.showProgress(null);
                    hideInput();
                    negotiate(); //请求TOKEN
                }
                else{
                    ToastHelper.showShortMsg("请求超过上限！");
                }
                break;
            case R.id.llRegister:
                if (this.getCurrentFocus().getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
        }
    }

    private void checkMobile() {
        MemberController.getInstance().mobileVerify(this, StringUtil.getTextViewString(mEtUsername), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                if (JsonUtil.parseStateCode(Json)) {
                    L.e("checkMobile===" + Json);
                    toastUtils.dismiss();
                    int isExist = JsonUtil.parseInt(Json, "isExist");
                    if (isExist != 0) {
                        verificationCode.startTimerTask();
                        getCode();
                    } else {
                        toastUtils.showInfoWithStatus(getString(R.string.toast_mobile_not_exist));
                    }

                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        MemberController.getInstance().smsVerify(this, StringUtil.getTextViewString(mEtUsername), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                Loger.d("checkMobile " + Json);
                if (JsonUtil.parseStateCode(Json)) {
                    sms = JsonUtil.parseString(Json, "sms");
                    smsToken = JsonUtil.parseString(Json, "smsToken");
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
     * 提交用户注册请求
     */
    private void commitRegister() {
        if (StringUtil.isEmpty(mEtUsername.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_mobile));
            return;
        }
        if (!StringUtil.isMobile(mEtUsername.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_mobile));
            return;
        }
        if (StringUtil.isEmpty(mEtVerifyCode.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_code));
            return;
        }
        if (mEtVerifyCode.getText().length() < 6) {
            toastUtils.showInfoWithStatus(getString(R.string.text_psd_short));
            return;
        }
        if (!sms.equals(mEtVerifyCode.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_code));
            return;
        }
        if (StringUtil.isEmpty(mEtPassword.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_pwd));
            return;
        }
        if (!StringUtil.checkPassword(mEtPassword.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_pwd));
            return;
        }
        if (mEtPassword.getText().toString().contains(" ")) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_pwd));
            return;
        }
//        startActivity(new Intent(RegisterListeningActivity.this, LoginSelectListeningActivity.class));
        forgetPassword();
    }

    /**
     * 发送握手请求，获取握手令牌
     */
    private void negotiate() {
        if (!NetUtil.checkNet(this)) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
            return;
        }
        MemberController.getInstance().negotiate(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

                try {
                    TokenResult result = new Gson().fromJson(Json, TokenResult.class);
                    if (result.getRet() != 0) {
                        if (result.getMsg() != null)
                            toastUtils.showInfoWithStatus(getString(R.string.link_time_out));
                    } else {

                        Constant.NEGOTIATE_TOKEN = result.getToken();
                        checkMobile();
                    }
                } catch (Exception e) {
                    L.e("loginNegotiate" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                try {
                    if (StringUtil.isEmpty(json) && toastUtils != null) {
                        toastUtils.showErrorWithStatus(getString(R.string.toast_query_token_fail));

                    } else if (toastUtils != null) {
                        toastUtils.showErrorWithStatus(json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     */
    private void forgetPassword() {
        MemberController.getInstance().resetPassword(this, mEtPassword.getText().toString(), sms, smsToken, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    toastUtils.showSuccessWithStatus(getString(R.string.text_reset_psd_success));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000 * 2);
                } else {
                    toastUtils.showSuccessWithStatus(getString(R.string.text_reset_psd_fail));

                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null) {
                    toastUtils.showSuccessWithStatus(getString(R.string.text_reset_psd_fail));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

//       /**
    //     * 设置密码的可见性
    //     */
//    private void setPasswordVisible() {
//        if (isHidden) {
//
//            // 设置密码不可见
//            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            ivRegisterPassVisible.setImageResource(R.drawable.ic_reset_password_hide);
//        } else {
//
//            // 设置密码可见
//            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            ivRegisterPassVisible.setImageResource(R.drawable.ic_reset_password_view);
//        }
//
//        isHidden = !isHidden;
//        mEtPassword.postInvalidate();
//
//        // 切换后,将光标置于密码文本的末尾
//        CharSequence password = mEtPassword.getText();
//        if (password instanceof Spannable) {
//            Spannable spannable = (Spannable) password;
//            Selection.setSelection(spannable, password.length());
//        }
//    }
}

