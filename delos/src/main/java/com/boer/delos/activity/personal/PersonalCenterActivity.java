package com.boer.delos.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.main.InformationCenterListeningActivity;
import com.boer.delos.activity.settings.HelpAndFeedbackActivity;
import com.boer.delos.activity.settings.SettingsActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.service.PollService;
import com.boer.delos.utils.ImageUtils;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.UploadFileManger;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.ExitConfirmPopUpWindow;
import com.boer.delos.view.popupWindow.ResetPasswordPopUpWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/29.
 */
public class PersonalCenterActivity extends CommonBaseActivity implements UploadFileManger.UploadFileListener {


    @Bind(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.tvSignature)
    TextView tvSignature;
    @Bind(R.id.llUserInfo)
    LinearLayout llUserInfo;
    @Bind(R.id.llayout_family)
    LinearLayout llayoutFamily;
    @Bind(R.id.llayout_message)
    LinearLayout llayoutMessage;
    @Bind(R.id.llayout_modify_pwd)
    LinearLayout llayoutModifyPwd;
    @Bind(R.id.llayout_help)
    LinearLayout llayoutHelp;
    @Bind(R.id.llayout_setting)
    LinearLayout llayoutSetting;
    @Bind(R.id.tv_exit)
    TextView tvExit;

    //
    private User user;
    private PopupWindow popuWindow;
    public static final int PICTURE = 10000;
    public static final int CAMERA = 10001;
    public static final int REQUESTCODE_CUTOUT = 10002;
    public static final int GET_USER = 10003;
    public static final int FAMILY = 10004;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ResetPasswordPopUpWindow resetPopUpWindow;
    private ExitConfirmPopUpWindow exitPopUpWindow;
    private String oldPassword;
    private String newPassword;

    @Override
    protected int initLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(getString(R.string.my_center_title));

    }

    @Override
    protected void initData() {


        user = Constant.LOGIN_USER;
        setUserInfo();
        queryUserInfo();

    }

    @Override
    protected void initAction() {

    }

    private void queryUserInfo() {
        if (user == null || user.getId() == null || user.getMobile() == null) {
            return;
        }
        MemberController.getInstance().queryUserInfo(this, user.getId(), user.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Loger.d("PersonalCenterListeningActivity queryUserInfo===" + Json); //JSON 解析失败， 用户不存在
                String ret = JsonUtil.parseString(Json, "ret");
                toastUtils.dismiss();
                if ("0".equals(ret)) {
                    user = JsonUtil.parseDataObject(Json, User.class, "user");
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    setUserInfo();
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


    private void setUserInfo() {
        if (user != null) {
            if (user.getName() != null) {
                this.tvUserName.setText(user.getName());
            }
            if (user.getSignature() != null) {
                this.tvSignature.setText(user.getRemark());
            }

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
    }


    /**
     * 退出当前账号
     */
    private void memberLogout() {
        toastUtils.dismiss();
        logout();
        SharedPreferencesUtils.clear(this);
        //用户退出
        MemberController.getInstance().logout(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

            }

            @Override
            public void onFailed(String json) {

            }
        });

        Intent intent = new Intent(PersonalCenterActivity.this, LoginActivity.class);
        if (user != null) {
            intent.putExtra("latestPhone", user.getMobile());
        }
        intent.putExtra("ActivityName", "PersonalCenterListeningActivity");
        startActivity(intent);

    }

    public void showPhotoViewPopuWindow() {
        View parentView = LayoutInflater.from(PersonalCenterActivity.this).inflate(
                R.layout.view_photo_camera, null);

        if (popuWindow == null) {
            popuWindow = new PopupWindow(parentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popuWindow.setFocusable(true);
            popuWindow.setOutsideTouchable(true);
            popuWindow.update();
            popuWindow.setBackgroundDrawable(new BitmapDrawable());
            popuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                    popuWindow = null;
                }
            });
        }
        parentView.findViewById(R.id.photo).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        photoClick();

                    }
                });
        parentView.findViewById(R.id.camera).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        cameraClick();

                    }
                });
        parentView.findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissPopuWindow();
                    }
                });
        popuWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popuWindow.showAtLocation(ivAvatar, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);


    }

    public void dismissPopuWindow() {
        if (popuWindow != null) {
            popuWindow.dismiss();
        }

    }


    private void photoClick() {
        dismissPopuWindow();
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, PICTURE);
    }

    private void cameraClick() {
        if (popuWindow.isShowing())
            dismissPopuWindow();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
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

        ActivityCustomManager.getAppManager().finishAllActivity();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case GET_USER:
                    user = Constant.LOGIN_USER;
                    setUserInfo();
                    break;
                case FAMILY:
                    finish();
                    break;

                case PERMISSION_REQUEST_CODE: //TODO

            }


        }
    }


    private void initCameraData(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            // 获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (bitmap != null) {
                String sNewImagePath = Constant.PATH + System.currentTimeMillis() + ".cache";
                boolean isSave = ImageUtils.saveImage(bitmap, sNewImagePath);
//            ivAvatar.setImageBitmap(bitmap);
                startActivityForResult(new Intent(PersonalCenterActivity.this,
                                ImageCutOutListeningActivity.class).putExtra(
                        Constant.KEY_PATH, sNewImagePath),
                        REQUESTCODE_CUTOUT);
            }
        }
    }

    private void initPictureData(Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (!StringUtil.isEmpty(picturePath)) {
                startActivityForResult(new Intent(PersonalCenterActivity.this,
                                ImageCutOutListeningActivity.class).putExtra(
                        Constant.KEY_PATH, picturePath),
                        REQUESTCODE_CUTOUT);
            }
        }
    }


    private void uploadImage(String filePath) {
        UploadFileManger.getInstance().uploadAvatar(PersonalCenterActivity.this, filePath, this);
    }

    @Override
    public void uploadSuccess(String url) {
        if (!TextUtils.isEmpty(url) && user != null) {
            user.setAvatarUrl(url);

        }
    }

    @Override
    public void uploadFailed(int status) {

    }

    private void resetPassword() {
        String userOldPassword = SharedUtils.getInstance().getTagSp(SharedTag.user_old_password);
        if (userOldPassword == null) {
            userOldPassword = "";
        }
        String inputOldPswd = Md5Encrypt.stringMD5(oldPassword);//输入的旧密码
        if (StringUtil.isEmpty(oldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_old_pwd));
            return;
        } else if (!StringUtil.checkPassword(oldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
            return;
        } else if (!inputOldPswd.equals(userOldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_diff_old));
            return;
        }

        if (StringUtil.isEmpty(newPassword)) {
            ToastHelper.showShortMsg(getString(R.string.input_new_pwd));
            return;
        } else if (!StringUtil.checkPassword(newPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
            return;
        }
        resetPopUpWindow.dismiss();

        MemberController.getInstance().resetCloudPassword(this, Md5Encrypt.stringMD5(oldPassword), Md5Encrypt.stringMD5(newPassword), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    ToastHelper.showShortMsg(getString(R.string.edit_success));
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_old_password, Md5Encrypt.stringMD5(newPassword));
                    memberLogout();//退出，重新登录
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }


    @OnClick({R.id.ivAvatar, R.id.tvUserName, R.id.tvSignature, R.id.llUserInfo, R.id.llayout_family, R.id.llayout_message, R.id.llayout_modify_pwd, R.id.llayout_help, R.id.llayout_setting, R.id.tv_exit})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.ivAvatar:
//                showPhotoViewPopuWindow();
//                break;
            case R.id.tvUserName:
                break;
            case R.id.tvSignature:
                break;
            case R.id.llUserInfo:
                startActivityForResult(new Intent(this, PersonalInfoActivity.class), GET_USER);
                break;
            case R.id.llayout_family:
                //直连或断网不能进入家庭管理界面
                if (NetUtil.checkNet(this) && Constant.IS_INTERNET_CONN) {
                    startActivityForResult(new Intent(this, FamilyManageListeningActivity.class), FAMILY);
                } else if (!NetUtil.checkNet(this)) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                }
                break;
            case R.id.llayout_message:
                startActivity(new Intent(PersonalCenterActivity.this, InformationCenterListeningActivity.class));
                break;
            case R.id.llayout_modify_pwd:
                resetPopUpWindow = new ResetPasswordPopUpWindow(this, new ResetPasswordPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        if (tag == 0) {
                            oldPassword = resetPopUpWindow.getOldPassword();
                            newPassword = resetPopUpWindow.getNewPassword();
                            resetPassword();
                        }

                    }
                });
                resetPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.llayout_help:
                startActivity(new Intent(this, HelpAndFeedbackActivity.class));
                break;
            case R.id.llayout_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.tv_exit:
                exitPopUpWindow = new ExitConfirmPopUpWindow(this, new ExitConfirmPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        if (tag == 1) {
                            toastUtils.showProgress(getString(R.string.family_family_exiting));
                            memberLogout();
                            exitPopUpWindow.dismiss();
                        }
                    }
                });
                break;
        }
    }
}
