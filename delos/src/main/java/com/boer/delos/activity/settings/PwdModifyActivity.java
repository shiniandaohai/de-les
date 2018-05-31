package com.boer.delos.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.service.PollService;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/31.
 */
public class PwdModifyActivity extends CommonBaseActivity {

    @Bind(R.id.etOldPassword)
    EditText etOldPassword;
    @Bind(R.id.etNewPassword)
    EditText etNewPassword;
    @Bind(R.id.tvResetConfirm)
    TextView tvResetConfirm;
    private String oldPassword;
    private String newPassword;

    @Override
    protected int initLayout() {
        return R.layout.activity_pwd_modify;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        tlTitleLayout.setTitle(R.string.my_center_reset_password);

    }

    @Override
    protected void initAction() {

    }


    private void resetPassword() {


        String userOldPassword = SharedUtils.getInstance().getTagSp(SharedTag.user_old_password);
        if (userOldPassword == null) {
            userOldPassword = "";
        }
        String inputOldPswd = Md5Encrypt.stringMD5(oldPassword);
        if (StringUtil.isEmpty(oldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_old_pwd));
            return;
        }
        if (!StringUtil.checkPassword(oldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
            return;
        }
        if (StringUtil.isEmpty(newPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_new_pwd));
            return;
        }

        if (!StringUtil.checkPassword(newPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
            return;
        }

        if (!inputOldPswd.equals(userOldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_diff_old));
            return;
        }

        toastUtils.showProgress("");
        MemberController.getInstance().resetCloudPassword(this, Md5Encrypt.stringMD5(oldPassword), Md5Encrypt.stringMD5(newPassword), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.dismiss();
                    ToastHelper.showShortMsg(getString(R.string.edit_success));
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_old_password, Md5Encrypt.stringMD5(newPassword));
                    memberLogout();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
            }
        });
    }


    /**
     * 退出当前账号
     */
    private void memberLogout() {
        //用户退出
        MemberController.getInstance().logout(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                logout();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 用户退出
     */
    public void logout() {

        //清理静态数据
        clearStaticData();
        //清除保存的数据
        SharedPreferencesUtils.clearAll(this);
        //停止5秒轮询
        stopService(new Intent(this, PollService.class));
        //发送退出广播
        sendBroadcast(new Intent(Constant.ACTION_EXIT));

        //主机信息清空 add by sunzhibin
        Constant.gatewayInfos.clear();
    }


    /**
     * 清理靜态数据
     */

    protected void clearStaticData() {
        Constant.DEVICE_MD5_VALUE = "";
        Constant.GATEWAY_MD5_VALUE = "";
        Constant.GLOBALMODE_MD5_VALUE = "";
        Constant.DEVICE_RELATE = new ArrayList<>();
        Constant.GATEWAY = null;
        Constant.GLOBAL_MODE = new ArrayList<>();
    }


    @OnClick(R.id.tvResetConfirm)
    public void onClick() {

        newPassword = etNewPassword.getText().toString();
        oldPassword = etOldPassword.getText().toString();
        resetPassword();
    }
}
