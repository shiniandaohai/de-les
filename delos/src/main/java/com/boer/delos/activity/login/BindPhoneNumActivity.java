package com.boer.delos.activity.login;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.activity.personal.BindNewPhoneListeningActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
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

import butterknife.Bind;

import static com.boer.delos.R.id.llRegister;
import static com.boer.delos.R.id.tv_verify_code;

/**
 * Created by Administrator on 2017/11/24.
 */

public class BindPhoneNumActivity extends CommonBaseActivity implements TextWatcher,View.OnClickListener {
    @Bind(R.id.et_username)
    EditText mEtUsername;

    @Bind(R.id.et_verify_code)
    EditText mEtVerifyCode;

    @Bind(tv_verify_code)
    TextView mTvVerifyCode;

    @Bind(R.id.et_password)
    EditText mEtPassword;

    @Bind(R.id.btn_commit)
    Button mBtnCommit;

    @Bind(llRegister)
    LinearLayout mLlRegister;

    VerificationCode verificationCode;
    private String code = "";
    private String smsToken;// 验证码令牌
    @Override
    protected int initLayout() {
        return R.layout.activity_bind_phone_num;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("绑定手机号");
        mTvVerifyCode.setText(getString(R.string.text_get_code));
        mTvVerifyCode.setTextColor(getResources().getColor(R.color.gray_et_hint));
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
    protected void initData() {

    }

    @Override
    protected void initAction() {
        mEtUsername.addTextChangedListener(this);
        mEtPassword.addTextChangedListener(this);

        mLlRegister.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        mTvVerifyCode.setOnClickListener(this);
        verificationCode = new VerificationCode(this, mTvVerifyCode);
//        mCbUseProtocol.setButtonDrawable(R.drawable.btn_checkbox_style);

        mEtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String psd = mEtPassword.getText().toString();
                String username = mEtUsername.getText().toString();
                if (!hasFocus) {
                    if (StringUtil.isEmpty(username)) {
                        hideInput();
                        toastUtils.showErrorWithStatus(getString(R.string.text_username_null));
                        return;
                    }
                    if (username.contains(" ")) {
                        hideInput();
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
            case R.id.btn_commit:
                if (this.getCurrentFocus().getWindowToken() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                bindMobile();
                break;

            case tv_verify_code:
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

                if(SharedPreferencesUtils.isCanSendVerifyCode(this,"bindMobile")){
                    toastUtils.showProgress(null);
                    hideInput();
                    verifyMobile(); //请求TOKEN
                }
                else{
                    ToastHelper.showShortMsg("请求超过上限！");
                }
                break;
            case llRegister:
                try{
                    if (this.getCurrentFocus()!=null&&this.getCurrentFocus().getWindowToken() != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        MemberController.getInstance().smsVerify(this, StringUtil.getTextViewString(mEtUsername), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                if (JsonUtil.parseStateCode(Json)) {
                    code = JsonUtil.parseString(Json, "sms");
                    smsToken = JsonUtil.parseString(Json, "smsToken");
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
            }
        });
    }

    private void verifyMobile() {
        MemberController.getInstance().mobileVerify(this, mEtUsername.getText().toString(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("BindNewPhoneListeningActivity verifyMobile===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    if ("1".equals(JsonUtil.parseString(Json, "isExist"))) {
                        toastUtils.showInfoWithStatus("该号码已注册");
                    } else {
                        verificationCode.startTimerTask();
                        getCode();
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

    private void bindMobile() {
        if (StringUtil.isEmpty(mEtVerifyCode.getText().toString())) {
            toastUtils.showInfoWithStatus("请输入验证码");
            return;
        }
        if (!mEtVerifyCode.getText().toString().equals(code)) {
            toastUtils.showInfoWithStatus("验证码不正确");
            return;
        }

        String pwd=mEtPassword.getText().toString();

        MemberController.getInstance().bindMobile(this,pwd, mEtVerifyCode.getText().toString(), smsToken, Constant.LOGIN_USER.getId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.showSuccessWithStatus("绑定成功");
                    Constant.LOGIN_USER.setMobile(mEtUsername.getText().toString());
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(Constant.LOGIN_USER));
                    Intent intent = new Intent(BindPhoneNumActivity.this, MainTabActivity.class);
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
