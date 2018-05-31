package com.boer.delos.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.activity.settings.NetGatePermisionSettingActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Host;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.DigitalTrans;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 带头像的用户信息的家人添加界面
 */
public class AddFamilyDetailListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private TextView tvCommit;
    private TextView tvUserName;
    private TextView tvSignature;
    private TextView tvMobile;
    private ImageView ivAvatar;
    private User user;
    private Host host;

    private String limitStatus;
    private String limitTime;
    private String pession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_detail);
        initView();
        initData();
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra(Constant.KEY_USER);
        host = (Host) getIntent().getSerializableExtra(Constant.KEY_HOST);
        setUserData();

    }

    private void setUserData() {
        if (user != null) {
            tvUserName.setText(Constant.userNameMask(user.getName()));
            tvUserName.setText(user.getName());
            String signature = "";
            if (!StringUtil.isEmpty(user.getSignature())) {
                signature = user.getSignature();
            }
            tvSignature.setText(DigitalTrans.FromEncoding(signature));
            tvMobile.setText(user.getMobile());
            if (!TextUtils.isEmpty(user.getAvatarUrl())) {

                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            }
        }
    }

    private void initView() {
        initTopBar(R.string.family_family_add, null, true, false);
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvSignature = (TextView) findViewById(R.id.tvSignature);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvCommit = (TextView) findViewById(R.id.tvCommit);
        tvCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCommit:
                gotoSelectPession();
                break;
        }
    }

    private void gotoSelectPession() {

        Intent intent = new Intent(this,
                NetGatePermisionSettingActivity.class);
        User us = new User();
        us.setName(user.getName());
        us.setId(user.getId());
        us.setMobile(user.getMobile());
        us.setAvatarUrl(user.getAvatarUrl());
        intent.putExtra("type", 2);
        intent.putExtra("user", us);
        intent.putExtra("host", host.getHostId());
        startActivityForResult(intent, 1000);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1000 == requestCode && resultCode == RESULT_OK) {


            limitStatus = data.getStringExtra("limitStatus");

            limitTime = data.getStringExtra("limitTime");

            pession = data.getStringExtra("pession");

            userApply();

        }
    }


    /**
     * 用户申请
     */
    private void userApply() {
        /** applyStatus: 1、用户申请 2、管理员分享
         // status : 0、待确认     1、用户同意   2、用户拒绝
         //          3、用户取消  　4、管理员同意 5、管理员拒绝
         //          6、管理员取消
         */
        toastUtils.showProgress("");
        FamilyManageController.getInstance().userApplyToAdmin(this,
                Constant.LOGIN_USER.getId(),
                user.getId(),
                "",
                "2",
                host.getHostId(),
                pession,
                "0", limitStatus, limitTime,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                toastUtils.showSuccessWithStatus(getResources().getString(R.string.save_success));
                            }

                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }


}
