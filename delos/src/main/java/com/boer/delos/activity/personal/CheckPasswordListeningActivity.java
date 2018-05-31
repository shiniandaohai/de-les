package com.boer.delos.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.TokenResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

/**
 * @author PengJiYang
 * @Description: 绑定新手机号的验证密码界面
 * create at 2016/5/12 11:23
 */
public class CheckPasswordListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private TextView tvMobile;
    private EditText etPassword;
    private TextView tvCheck;

    private User user;

    //shape_solid_gray_bg2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
//        user = new Gson().fromJson(SharedUtils.getInstance().getTagSp(SharedTag.user_login), User.class);
        user = Constant.LOGIN_USER;
        initView();
//        negotiate();
    }

    private void initView() {
        initTopBar(R.string.bind_new_phone, null, true, false);
        this.tvCheck = (TextView) findViewById(R.id.tvCheck);
        this.etPassword = (EditText) findViewById(R.id.etPassword);
        this.tvMobile = (TextView) findViewById(R.id.tvMobile);

        this.tvMobile.setText(user.getMobile().substring(0, 3) + "****" + user.getMobile().substring(8));

        this.tvCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheck:
                checkPassword();
                break;
        }
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

                    }
                } catch (Exception e) {
                    L.e("loginNegotiate" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                if (StringUtil.isEmpty(json) && toastUtils != null) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_query_token_fail));

                } else if (toastUtils != null) {
                    toastUtils.showErrorWithStatus(json);
                }
            }
        });
    }

    /**
     * 验证密码
     */
    private void checkPassword() {
        if (StringUtil.isEmpty(etPassword.getText().toString())) {
            toastUtils.showInfoWithStatus("请输入账号密码");
            return;
        }
        MemberController.getInstance().login(this, user.getMobile(), etPassword.getText().toString(), "Android", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("CheckPasswordListeningActivity checkPassword===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    startActivity(new Intent(CheckPasswordListeningActivity.this, BindNewPhoneListeningActivity.class));
                    finish();
                } else {
                    toastUtils.showInfoWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showErrorWithStatus("系统异常" + json);
            }
        });
    }

}
