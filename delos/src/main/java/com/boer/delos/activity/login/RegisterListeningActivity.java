package com.boer.delos.activity.login;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.TokenResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.thread.VerificationCode;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

import static com.boer.delos.R.id.llRegister;
import static com.boer.delos.R.id.tv_verify_code;

/**
 * @author PengJiYang
 * @Description: "注册"界面
 * create at 2016/3/9 10:57
 */
public class RegisterListeningActivity extends CommonBaseActivity
        implements View.OnClickListener, TextWatcher {

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

    @Bind(R.id.tv_service)
    TextView tvUseProtocol;

    @Bind(R.id.cb_useProtocol)
    CheckBox mCbUseProtocol;

    @Bind(llRegister)
    LinearLayout mLlRegister;


    @Bind(R.id.ll_useProtocol)
    LinearLayout mLlUseProtocol;

    private boolean isHidden = false;
    VerificationCode verificationCode;
    private String code = "";

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(getString(R.string.login_register));
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

        tvUseProtocol.setOnClickListener(this);
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

                if(SharedPreferencesUtils.isCanSendVerifyCode(this,"register")){
                    toastUtils.showProgress(null);
                    hideInput();
                    negotiate(); //请求TOKEN
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

    private void checkMobile() {
        MemberController.getInstance().mobileVerify(this, StringUtil.getTextViewString(mEtUsername), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                if (JsonUtil.parseStateCode(Json)) {
                    L.e("checkMobile===" + Json);
                    toastUtils.dismiss();
                    int isExist = JsonUtil.parseInt(Json, "isExist");
                    if (isExist == 0) {
                        verificationCode.startTimerTask();
                        getCode();
                    } else {
                        toastUtils.showInfoWithStatus(getString(R.string.toast_mobile_exist));
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
                if (JsonUtil.parseStateCode(Json)) {
                    code = JsonUtil.parseString(Json, "sms");
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
        L.e("checkBox===" + mCbUseProtocol.isChecked());
        if (!mCbUseProtocol.isChecked()) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_check_hint));
            return;
        }
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
            toastUtils.showInfoWithStatus(getString(R.string.text_code_six_error));
            return;
        }
        if (!code.equals(mEtVerifyCode.getText().toString())) {
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
//        startActivity(new Intent(RegisterListeningActivity.this, LoginSelectListeningActivity.class));
        register();
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
                            toastUtils.showInfoWithStatus(result.getMsg());
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
     * 注册用户
     */
    /**
     * 注册用户
     */
    private void register() {
        MemberController.getInstance().register(this, mEtUsername.getText().toString(),
                mEtPassword.getText().toString(), "", "Android", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        if (JsonUtil.parseStateCode(Json)) {
                            toastUtils.showSuccessWithStatus(getString(R.string.register_success));
                            Constant.TOKEN = JsonUtil.parseString(Json, "token");
                            User user = JsonUtil.parseDataObject(Json, User.class, "user");
                            SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                            Intent intent = new Intent(RegisterListeningActivity.this, LoginActivity.class);
//                            intent.putExtra("from","register");
//                            intent.putExtra("uid",user.getId());
                            Constant.USERID = user.getId();
                            startActivity(intent);
                            ActivityStack.getInstance().addActivity(RegisterListeningActivity.this);
                        } else {
                            toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null)
                            toastUtils.showErrorWithStatus(getString(R.string.text_register_fail));
                    }
                });
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
