package com.boer.delos.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

/**
 * @author PengJiYang
 * @Description: "重置密码"界面
 * create at 2016/3/9 10:57
 *
 */
public class ResetPasswordListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    private android.widget.EditText etNewPass;
    private android.widget.ImageView ivResetPassVisible;
    private com.zhy.android.percent.support.PercentLinearLayout llRegisterPassVisible;
    private android.widget.TextView tvResetPassConfirmBtn;

    private boolean isHidden = false;
    private String sms = "";// 短信验证码
    private String smsToken = "";// 验证码令牌
    private String retrievePassPhone = "";

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Intent intent = new Intent(ResetPasswordListeningActivity.this, LoginActivity.class);
                intent.putExtra("ActivityName", "ResetPasswordListeningActivity");
                intent.putExtra("PhoneNumber", retrievePassPhone);
                startActivity(intent);
                ActivityStack.getInstance().finishActivities();// 关闭已经打开的界面
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        sms = getIntent().getStringExtra("sms");
        smsToken = getIntent().getStringExtra("smsToken");
        retrievePassPhone = getIntent().getStringExtra("RetrievePassPhone");
        initView();
    }

    private void initView() {
        initTopBar(R.string.reset_pass_title_text, null, true, false);
        this.tvResetPassConfirmBtn = (TextView) findViewById(R.id.tvResetPassConfirmBtn);
        this.llRegisterPassVisible = (PercentLinearLayout) findViewById(R.id.llRegisterPassVisible);
        this.ivResetPassVisible = (ImageView) findViewById(R.id.ivResetPassVisible);
        this.etNewPass = (EditText) findViewById(R.id.etNewPass);

        this.llRegisterPassVisible.setOnClickListener(this);
        this.tvResetPassConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llRegisterPassVisible:
                setPasswordVisible();
                break;
            case R.id.tvResetPassConfirmBtn:
                confirmReset();
                break;
        }
    }

    /**
     * 确认重置(提交请求)
     */
    private void confirmReset() {
        if (StringUtil.isEmpty(etNewPass.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.reset_input_pass));
            return;
        }
        if (etNewPass.getText().toString().length()<6) {
            toastUtils.showInfoWithStatus("请输入6-16位密码");
            return;
        }

        if(StringUtil.checkPassword(etNewPass.getText().toString())) {
            MemberController.getInstance().resetPassword(this, etNewPass.getText().toString(), sms, smsToken, new RequestResultListener() {
                @Override
                public void onSuccess(String Json) {
                    L.e("重置密码:"+Json);
                    if (JsonUtil.parseStateCode(Json)) {
                        toastUtils.showSuccessWithStatus("重置密码成功");
                        handler.sendEmptyMessageDelayed(1,1000);

                    } else {
                        toastUtils.showErrorWithStatus("重置密码失败");
                    }
                }

                @Override
                public void onFailed(String json) {
                    toastUtils.showErrorWithStatus("服务器异常");
                }
            });
        } else {
            return;
        }
    }

    /**
     * 设置密码的可见性
     */
    private void setPasswordVisible() {
        if(isHidden) {

            // 设置密码不可见
            etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivResetPassVisible.setImageResource(R.drawable.ic_reset_password_hide);
        } else {

            // 设置密码可见
            etNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivResetPassVisible.setImageResource(R.drawable.ic_reset_password_view);
        }

        isHidden = !isHidden;
        etNewPass.postInvalidate();

        // 切换后,将光标置于密码文本的末尾
        CharSequence password = etNewPass.getText();
        if(password instanceof Spannable) {
            Spannable spannable = (Spannable) password;
            Selection.setSelection(spannable, password.length());
        }
    }
}
