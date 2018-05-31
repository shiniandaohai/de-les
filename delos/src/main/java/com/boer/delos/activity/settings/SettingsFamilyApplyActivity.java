package com.boer.delos.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.SystemMessage;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/30.
 */
public class SettingsFamilyApplyActivity extends CommonBaseActivity {

    @Bind(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvPhone)
    TextView tvPhone;
    @Bind(R.id.tvHost)
    TextView tvHost;
    @Bind(R.id.tvPass)
    TextView tvPass;
    @Bind(R.id.tvRefuse)
    TextView tvRefuse;
    @Bind(R.id.tvBlackList)
    TextView tvBlackList;
    @Bind(R.id.llOperation)
    LinearLayout llOperation;
    private SystemMessage.MsgListBean message;

    @Override
    protected int initLayout() {
        return R.layout.activity_settings_family_apply;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("家人申请");
    }

    @Override
    protected void initData() {
        message = (SystemMessage.MsgListBean) getIntent().getSerializableExtra("message");
        tvName.setText(message.getToUser().getName());
        tvPhone.setText(message.getToUser().getMobile());
        tvHost.setText("申请加入 " + message.getHostName());
        SystemMessage.MsgListBean.ToUserBean user = message.getToUser();
        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            } else if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            }
        }
    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.tvPass, R.id.tvRefuse, R.id.tvBlackList})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvPass:
                acceptMessage();
                break;
            case R.id.tvRefuse:
                rejectMessge();
                break;
            case R.id.tvBlackList:
                //加入黑名单，系统默认您拒绝该用户的加入申请，并且您将不再收到对方的申请信息
                popupwindow();
                break;
        }
    }

    private void acceptMessage() {
        //用户申请,管理员同意
        if (Constant.USERID.equals(message.getFromUser().getId())) {
            SystemMessage.MsgListBean.ToUserBean user = message.getToUser();
            Intent intent = new Intent(this,
                    NetGatePermisionSettingActivity.class);
            User us = new User();
            us.setName(user.getName());
            us.setId(user.getId());
            if (user.getAvatarUrl() != null)
                us.setAvatarUrl(user.getAvatarUrl().toString());
            intent.putExtra("type", 0);
            intent.putExtra("user", us);
            intent.putExtra("host", message.getHostRealId());
            intent.putExtra("message",message);
            startActivityForResult(intent, 1000);
            finish();
        }
    }

    private void rejectMessge() {
        String adminId = message.getFromUser().getId();
        String applayUserId = message.getToUser().getId();

        String applyStatus = message.getExtra().getApplyStatus() + "";
        String hostId = message.getHostRealId();
        String status = message.getExtra().getStatus() + "";

        String updateStatus = FamilyManageController.statusUserReject;

        //管理员拒绝
        if (Constant.USERID.equals(message.getFromUser().getId())) {

            updateStatus = FamilyManageController.statusAdminReject;
            message.getExtra().setStatus(5);

        } else
            message.getExtra().setStatus(2);

        updateApplyStatus(adminId, applayUserId, applyStatus, hostId, status, updateStatus);
    }

    private void updateApplyStatus(String adminId, String applayUserId, final String applyStatus,
                                   String hostId, final String status, final String updateStatus) {
        FamilyManageController.getInstance().updateUserApply(this,
                adminId,
                applayUserId,
                applyStatus,
                hostId,
                status,
                updateStatus,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("updateApplyStatus" + Json);
                        try {
                            JSONObject jsonObject = new JSONObject(Json);
                            int ret = jsonObject.getInt("ret");
                            if (ret == 0) {
                                ToastHelper.showShortMsg("已拒绝");
                                llOperation.setVisibility(View.GONE);
                                tvHost.setText("已拒绝");
                                pushNotification();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                ToastHelper.showShortMsg("拒绝失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastHelper.showShortMsg("拒绝失败");
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d("SystemMessageFragment" + Json);
                        ToastHelper.showShortMsg("网络错误");
                    }
                });
    }

    private void pushNotification() {
        AlarmController.getInstance().pushNotification(this, "", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    private CustomFragmentDialog deleteDialog;

    private void popupwindow() {
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        deleteDialog = CustomFragmentDialog.newInstanse(
                getString(R.string.text_prompt),
                "加入黑名单，系统默认您拒绝该用户的加入申请，并且您将不再收到对方的申请信息", false);
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                String adminId = message.getFromUser().getId();
                String applayUserId = message.getToUser().getId();

                String applyStatus = message.getExtra().getApplyStatus() + "";
                String hostId = message.getHostRealId();
                String status = message.getExtra().getStatus() + "";

                String updateStatus = FamilyManageController.statusUserReject;

                //管理员拒绝
                if (Constant.USERID.equals(message.getFromUser().getId())) {

                    updateStatus = FamilyManageController.statusAdminReject;
                    message.getExtra().setStatus(5);

                } else
                    message.getExtra().setStatus(2);

                FamilyManageController.getInstance().updateUserApply(SettingsFamilyApplyActivity.this,
                        adminId,
                        applayUserId,
                        applyStatus,
                        hostId,
                        status,
                        updateStatus,
                        new RequestResultListener() {
                            @Override
                            public void onSuccess(String Json) {
                                Loger.d("updateApplyStatus" + Json);
                                try {
                                    JSONObject jsonObject = new JSONObject(Json);
                                    int ret = jsonObject.getInt("ret");
                                    if (ret == 0) {
                                        doJoinBlackList();
                                    } else {
                                        ToastHelper.showShortMsg("加入黑名单失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastHelper.showShortMsg("加入黑名单失败");
                                }
                            }

                            @Override
                            public void onFailed(String Json) {
                                Loger.d("SystemMessageFragment" + Json);
                                ToastHelper.showShortMsg("网络错误");
                            }
                        });
                deleteDialog.dismiss();
            }
        });
    }

    private void doJoinBlackList() {
        String userId = message.getToUser().getId();
        FamilyManageController.getInstance().joinBlackList(this, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d("doJoinBlackList" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    if (ret == 0) {
                        ToastHelper.showShortMsg("已加入黑名单");
                        pushNotification();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastHelper.showShortMsg("加入黑名单失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("加入黑名单失败");
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d("doJoinBlackList" + json);
                ToastHelper.showShortMsg("网络错误");
            }
        });
    }
}
