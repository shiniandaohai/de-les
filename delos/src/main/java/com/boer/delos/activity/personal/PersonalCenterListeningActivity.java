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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.ImageUtils;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.UploadFileManger;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.BindEmailPopUpWindow;
import com.boer.delos.view.popupWindow.ExitConfirmPopUpWindow;
import com.boer.delos.view.popupWindow.ResetPasswordPopUpWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.File;

//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author XieQingTing
 * @Description: 个人中心界面
 * create at 2016/4/12 9:45
 */
public class PersonalCenterListeningActivity extends BaseListeningActivity implements View.OnClickListener, UploadFileManger.UploadFileListener {
    private PercentLinearLayout llUserInfo;
    private ImageView ivMyCenterOpenUserInfo, ivAvatar;
    private PercentLinearLayout llFamilyManage;
    private PercentLinearLayout llBindPhone;
    private PercentLinearLayout llBindEmail;
    private PercentLinearLayout llAlarmPhone;
    private PercentLinearLayout llResetPassword;
    private TextView tvExitAccount, tvUserName, tvSignature, tvBindMobile, tvEmail;

    private View view;
    private User user;
    private String newEmail;
    private ResetPasswordPopUpWindow resetPopUpWindow;
    private BindEmailPopUpWindow bindPopUpWindow;
    private ExitConfirmPopUpWindow exitPopUpWindow;

    private String oldPassword;
    private String newPassword;
    private PopupWindow popuWindow;
    public static final int PICTURE = 10000;
    public static final int CAMERA = 10001;
    public static final int REQUESTCODE_CUTOUT = 10002;
    public static final int GET_USER = 10003;
    public static final int FAMILY = 10004;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_personal_center_listening, null);
        setContentView(view);

        initTopBar(R.string.my_center_title, null, true, false);
        initView();
        user = Constant.LOGIN_USER;
        setUserInfo();
        queryUserInfo();
    }

    private void initView() {
        this.llResetPassword = (PercentLinearLayout) findViewById(R.id.llResetPassword);
        this.llAlarmPhone = (PercentLinearLayout) findViewById(R.id.llAlarmPhone);
        this.llBindEmail = (PercentLinearLayout) findViewById(R.id.llBindEmail);
        this.llBindPhone = (PercentLinearLayout) findViewById(R.id.llBindPhone);
        this.llFamilyManage = (PercentLinearLayout) findViewById(R.id.llFamilyManage);
        this.llUserInfo = (PercentLinearLayout) findViewById(R.id.llUserInfo);
        this.tvExitAccount = (TextView) findViewById(R.id.tvExitAccount);

        this.ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);
        this.tvUserName = (TextView) findViewById(R.id.tvUserName);
        this.tvSignature = (TextView) findViewById(R.id.tvSignature);
        this.tvBindMobile = (TextView) findViewById(R.id.tvBindMobile);
        this.tvEmail = (TextView) findViewById(R.id.tvEmail);

        this.llUserInfo.setOnClickListener(this);
        this.llFamilyManage.setOnClickListener(this);
        this.llBindPhone.setOnClickListener(this);
        this.llBindEmail.setOnClickListener(this);
        this.llAlarmPhone.setOnClickListener(this);
        this.llResetPassword.setOnClickListener(this);
        this.tvExitAccount.setOnClickListener(this);
        this.ivAvatar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llUserInfo:
                startActivityForResult(new Intent(this, PersonalDataListeningActivity.class), GET_USER);
                break;
            case R.id.llFamilyManage:
                //直连或断网不能进入家庭管理界面
                if (NetUtil.checkNet(this) && Constant.IS_INTERNET_CONN) {
                    startActivityForResult(new Intent(this, FamilyManageListeningActivity.class), FAMILY);
                } else if (!NetUtil.checkNet(this)) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
                } else {
                    toastUtils.showInfoWithStatus("请检查是否连接因特网");
                }
                break;
            case R.id.llBindPhone:
//                Toast.makeText(this, "去往绑定手机界面", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, CheckPasswordListeningActivity.class));
                break;
            case R.id.llBindEmail:
                bindPopUpWindow = new BindEmailPopUpWindow(this, getString(R.string.my_center_bind_email), new BindEmailPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String tag) {
                        newEmail = tag;
                        updateBindEmail();
                    }
                });
                String oldEmail = tvEmail.getText().toString();
                if (!StringUtil.isEmpty(oldEmail)) {
                    bindPopUpWindow.setEditText(oldEmail);
                }
                bindPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.llAlarmPhone:
                startActivity(new Intent(this, AlarmPhoneListeningActivity.class));
                break;
            case R.id.llResetPassword:
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
            case R.id.tvExitAccount:
//                Toast.makeText(this, "退出当前账号", Toast.LENGTH_SHORT).show();
                exitPopUpWindow = new ExitConfirmPopUpWindow(this, new ExitConfirmPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        if (tag == 1) {
                            toastUtils.showProgress("正在退出...");
                            memberLogout();
                            exitPopUpWindow.dismiss();
                        }
                    }
                });
                exitPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.ivAvatar:
                showPhotoViewPopuWindow();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        setUserInfo();
    }

    private void resetPassword() {
        String userOldPassword = SharedUtils.getInstance().getTagSp(SharedTag.user_old_password);
        if (userOldPassword == null) {
            userOldPassword = "";
        }
        String inputOldPswd = Md5Encrypt.stringMD5(oldPassword);//输入的旧密码
        if (StringUtil.isEmpty(oldPassword)) {
//            toastUtils.showInfoWithStatus("请输入旧密码");
            ToastHelper.showShortMsg("请输入旧密码");
            return;
        } else if (!StringUtil.checkPassword(oldPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
//            toastUtils.showErrorWithStatus(readString(R.string.error_password_form));
            return;
        } else if (!inputOldPswd.equals(userOldPassword)) {
//            toastUtils.showErrorWithStatus("旧密码与登录密码不一致");
            ToastHelper.showShortMsg("旧密码与登录密码不一致");
            return;
        }

        if (StringUtil.isEmpty(newPassword)) {
//            toastUtils.showInfoWithStatus("请输入新密码");
            ToastHelper.showShortMsg("请输入新密码");
            return;
        } else if (!StringUtil.checkPassword(newPassword)) {
            ToastHelper.showShortMsg(getString(R.string.error_password_form));
//            toastUtils.showErrorWithStatus(readString(R.string.error_password_form));
            return;
        }
        resetPopUpWindow.dismiss();

        MemberController.getInstance().resetCloudPassword(this, Md5Encrypt.stringMD5(oldPassword), Md5Encrypt.stringMD5(newPassword), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    ToastHelper.showShortMsg("修改成功");
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

    /**
     * 更新绑定邮箱
     */
    private void updateBindEmail() {
        if (StringUtil.isEmpty(newEmail)) {
            BaseApplication.showToast("请输入邮箱");
            return;
//            toastUtils.showInfoWithStatus("请输入邮箱");
        } else if (!StringUtil.isEmail(newEmail)) {
            BaseApplication.showToast("邮箱格式不正确");
            return;
//            toastUtils.showInfoWithStatus("邮箱格式不正确");
        }
        user.setEmail(newEmail);
        bindPopUpWindow.dismiss();
        MemberController.getInstance().updateUserInfo(this, user, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("PersonalCenterListeningActivity updateBindEmail===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    user = JsonUtil.parseDataObject(Json, User.class, "user");
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    toastUtils.showSuccessWithStatus("绑定成功");
                    setUserInfo();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 查询个人信息
     */
    private void queryUserInfo() {
        if (user == null || user.getId() == null || user.getMobile() == null) {
            return;
        }
        MemberController.getInstance().queryUserInfo(this, user.getId(), user.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.d("PersonalCenterListeningActivity queryUserInfo===" + Json); //JSON 解析失败， 用户不存在
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
//                toastUtils.showErrorWithStatus("请求失败，请重试！");
            }
        });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo() {
        L.d("PersonalCenterListeningActivity setUserInfo===" + new Gson().toJson(user));
        if (user != null) {
            if (user.getName() != null) {
                this.tvUserName.setText(user.getName());
            }
            if (user.getSignature() != null) {
                this.tvSignature.setText(user.getRemark());
            }
            if (user.getMobile() != null) {
                this.tvBindMobile.setText(user.getMobile());

            }
            if (user.getEmail() != null) {
                this.tvEmail.setText(user.getEmail());
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

        Intent intent = new Intent(PersonalCenterListeningActivity.this, LoginActivity.class);
        if (user != null) {
            intent.putExtra("latestPhone", user.getMobile());
        }
        intent.putExtra("ActivityName", "PersonalCenterListeningActivity");
        startActivity(intent);

    }

    public void showPhotoViewPopuWindow() {
        View parentView = LayoutInflater.from(PersonalCenterListeningActivity.this).inflate(
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA:
                    initCameraData(data);
                    break;
                case PICTURE:
                    initPictureData(data);
                    break;
                case REQUESTCODE_CUTOUT:
                    if (data != null) {
                        String path = data.getStringExtra(Constant.KEY_PATH);
                        if (!StringUtil.isEmpty(path) && new File(path).exists()) {
                            ImageLoader.getInstance().displayImage(
                                    "file://" + path, ivAvatar);

                            uploadImage(path);
                        } else {
                            Toast.makeText(this, "图片选择失败，请重新选择一张头像图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case GET_USER:
                    user = Constant.LOGIN_USER;
                    toastUtils.showProgress("更新个人信息...");
                    queryUserInfo();
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
                startActivityForResult(new Intent(PersonalCenterListeningActivity.this,
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
                startActivityForResult(new Intent(PersonalCenterListeningActivity.this,
                                ImageCutOutListeningActivity.class).putExtra(
                        Constant.KEY_PATH, picturePath),
                        REQUESTCODE_CUTOUT);
            }
        }
    }

    private void uploadImage(String filePath) {
        UploadFileManger.getInstance().uploadAvatar(PersonalCenterListeningActivity.this, filePath, this);
    }

    @Override
    public void uploadSuccess(String url) {
        if (!TextUtils.isEmpty(url) && user != null) {
            user.setAvatarUrl(url);
            MemberController.getInstance().updateUserInfo(this, user, new RequestResultListener() {
                @Override
                public void onSuccess(String Json) {
                    L.e("PersonalDataListeningActivity updateUserInfo===" + Json);
                    String ret = JsonUtil.parseString(Json, "ret");
                    if ("0".equals(ret)) {
                        user = JsonUtil.parseDataObject(Json, User.class, "user");
                        SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    } else {
                        BaseApplication.showToastShort(JsonUtil.parseString(Json, "msg"));
                    }
                }

                @Override
                public void onFailed(String json) {
                    try {

                        BaseApplication.showToastShort(json);
                        finish();
                    } catch (Exception e) {
                        Loger.d(e.toString());
                    }
                }
            });
        }
    }

    @Override
    public void uploadFailed(int status) {

    }


}