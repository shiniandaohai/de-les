package com.boer.delos.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.boer.delos.activity.login.BindPhoneNumActivity;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.main.InformationCenterListeningActivity;
import com.boer.delos.activity.personal.AlarmPhoneListeningActivity;
import com.boer.delos.activity.personal.FamilyManageListeningActivity;
import com.boer.delos.activity.personal.ImageCutOutListeningActivity;
import com.boer.delos.activity.personal.PersonalInfoActivity;
import com.boer.delos.activity.settings.HelpAndFeedbackActivity;
import com.boer.delos.activity.settings.PwdModifyActivity;
import com.boer.delos.activity.settings.SettingsActivity;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.service.PollService;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.ImageUtils;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.UploadFileManger;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.TitleLayout;
import com.boer.delos.view.popupWindow.ExitConfirmPopUpWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.boer.delos.R.id.tv_exit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements UploadFileManger.UploadFileListener {
    @Bind(R.id.ll_login)
    LinearLayout llLogin;
    @Bind(R.id.ll_content)
    LinearLayout llContent;
    @Bind(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.tvSignature)
    TextView tvSignature;
    @Bind(R.id.tvCount)
    TextView tvCount;
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
    @Bind(tv_exit)
    TextView tvExit;
    @Bind(R.id.tl_title_layout)
    TitleLayout tlTitleLayout;
    @Bind(R.id.llayout_bind_phonenum)
    LinearLayout llayout_bind_phonenum;
    @Bind(R.id.divider_bind_phonenum)
    View divider_bind_phonenum;

    //
    private User user;
    private PopupWindow popuWindow;
    public static final int PICTURE = 10000;
    public static final int CAMERA = 10001;
    public static final int REQUESTCODE_CUTOUT = 10002;
    public static final int GET_USER = 10003;
    public static final int FAMILY = 10004;
    private static final int MODIFY_PWD = 200;
    private ExitConfirmPopUpWindow exitPopUpWindow;
    public ToastUtils toastUtils;
    public Activity context;


    public MineFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity) context;

        mLocalBroadcastManager=LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MainFragment.ACTION_DELETE_HOST);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        if (!Constant.isLogin) {
            llContent.setVisibility(View.GONE);
        } else {
            llLogin.setVisibility(View.GONE);
        }

        initData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryAllDevice();
    }

    private void initData() {
        user = Constant.LOGIN_USER;
        toastUtils = new ToastUtils(getActivity());
        tlTitleLayout.setTitle(getString(R.string.my_center_title));
        tvCount.setText(Constant.DEVICE_RELATE.size() + getString(R.string.device_count));

        if(TextUtils.isEmpty(user.getMobile())){
            divider_bind_phonenum.setVisibility(View.VISIBLE);
            llayout_bind_phonenum.setVisibility(View.VISIBLE);
        }
        else{
            divider_bind_phonenum.setVisibility(View.GONE);
            llayout_bind_phonenum.setVisibility(View.GONE);
        }

        setUserInfo();
        queryUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void queryUserInfo() {
        if (user == null || user.getId() == null || user.getMobile() == null) {
            return;
        }
        MemberController.getInstance().queryUserInfo(context, user.getId(), user.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Loger.d("PersonalCenterListeningActivity queryUserInfo===" + Json); //JSON 解析失败， 用户不存在
                String ret = JsonUtil.parseString(Json, "ret");
                toastUtils.dismiss();
                if ("0".equals(ret)) {
                    user = JsonUtil.parseDataObject(URLDecoder.decode(Json), User.class, "user");
//                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    Constant.LOGIN_USER = user;
                    SharedPreferencesUtils.saveUserInfoToPreferences(getActivity()); // change by sunzhibin
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
                tvUserName.setText(user.getName());
            }
            if (user.getSignature() != null) {
                tvSignature.setText(user.getRemark());
            }

            if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                    ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
                } else if (user.getAvatarUrl().contains("http")) {
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

        //用户退出
        MemberController.getInstance().logout(context, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                logout();
                if(Constant.THIRDUSER!=null){
                    SHARE_MEDIA platform=null;
                    if(Constant.THIRDUSER.getThirdPartyType().equals(Constant.LOGIN_TYPE_WEBCHAT)){
                        platform=SHARE_MEDIA.WEIXIN;
                    }
                    else if(Constant.THIRDUSER.getThirdPartyType().equals(Constant.LOGIN_TYPE_QQ)){
                        platform=SHARE_MEDIA.QQ;
                    }
                    else if(Constant.THIRDUSER.getThirdPartyType().equals(Constant.LOGIN_TYPE_SINA)){
                        platform=SHARE_MEDIA.SINA;
                    }
                    UMShareAPI.get(getActivity()).deleteOauth(getActivity(), platform,
                            new UMAuthListener() {
                                @Override
                                public void onStart(SHARE_MEDIA share_media) {

                                }

                                @Override
                                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                                @Override
                                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                                    ToastHelper.showShortMsg("第三方登录注销失败");
                                }

                                @Override
                                public void onCancel(SHARE_MEDIA share_media, int i) {

                                }
                            });
                }
                else{
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailed(String json) {
                ToastHelper.showShortMsg("退出失败");
                toastUtils.dismiss();
            }
        });
    }

    public void showPhotoViewPopuWindow() {
        View parentView = LayoutInflater.from(context).inflate(
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
                    WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                    lp.alpha = 1f;
                    context.getWindow().setAttributes(lp);
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
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.7f;
        context.getWindow().setAttributes(lp);


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
        SharedPreferencesUtils.clearAll(context);
        //停止5秒轮询
        context.stopService(new Intent(context, PollService.class));
        //发送退出广播
        context.sendBroadcast(new Intent(Constant.ACTION_EXIT));

        //主机信息清空 add by sunzhibin
        Constant.gatewayInfos.clear();
        Constant.CURRENTHOSTID = null;
        Constant.LOCAL_CONNECTION_IP = null;
        Constant.LOGIN_USER=null;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case GET_USER:
                    user = Constant.LOGIN_USER;
                    setUserInfo();
                    break;
                case FAMILY:
                    context.finish();
                    break;

                case MODIFY_PWD: //TODO
                    Constant.isLogin = false;
                    ActivityCustomManager.getAppManager().finishActivity();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    context.finish();
                    break;


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
                startActivityForResult(new Intent(context,
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

            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (!StringUtil.isEmpty(picturePath)) {
                startActivityForResult(new Intent(context,
                                ImageCutOutListeningActivity.class).putExtra(
                        Constant.KEY_PATH, picturePath),
                        REQUESTCODE_CUTOUT);
            }
        }
    }


    private void uploadImage(String filePath) {
        UploadFileManger.getInstance().uploadAvatar(context, filePath, this);
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


    @OnClick({ R.id.tvUserName, R.id.tvSignature, R.id.llUserInfo, R.id.llayout_family, R.id.llayout_message,
            R.id.llayout_modify_pwd, R.id.llayout_help, R.id.llayout_setting, tv_exit, R.id.ll_login, R.id.llayout_alarm,R.id.llayout_bind_phonenum})
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
                startActivityForResult(new Intent(context, PersonalInfoActivity.class), GET_USER);
                break;
            case R.id.llayout_family:
                //直连或断网不能进入家庭管理界面
                if (NetUtil.checkNet(context) && Constant.IS_INTERNET_CONN) {
                    startActivityForResult(new Intent(context, FamilyManageListeningActivity.class), FAMILY);
                } else if (!NetUtil.checkNet(context)) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                }
                break;
            case R.id.llayout_message:
                startActivity(new Intent(context, InformationCenterListeningActivity.class));
                break;
            case R.id.llayout_modify_pwd:
                startActivityForResult(new Intent(context, PwdModifyActivity.class), MODIFY_PWD);
                break;
            case R.id.llayout_help:
                startActivity(new Intent(context, HelpAndFeedbackActivity.class));
                break;
            case R.id.llayout_setting:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            case tv_exit:
                exitPopUpWindow = new ExitConfirmPopUpWindow(context, new ExitConfirmPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        if (tag == 1) {
                            toastUtils.showProgress(getString(R.string.family_family_exiting));
                            memberLogout();
                            exitPopUpWindow.dismiss();
                        }
                    }
                });
                exitPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.ll_login:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.llayout_alarm:
                Intent in = new Intent(getActivity(), AlarmPhoneListeningActivity.class);
                startActivity(in);
                break;
            case R.id.llayout_bind_phonenum:
                startActivity(new Intent(getActivity(), BindPhoneNumActivity.class));
                break;
        }
    }

    private void queryAllDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                json = StringUtil.deviceStateStringReplaceMap(json);
                DeviceRelateResult result = GsonUtil.getObject(json, DeviceRelateResult.class);
                if(result.getRet()==20005){
                    if (Constant.DEVICE_RELATE == null) {
                        Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                    }
                    else{
                        Constant.DEVICE_RELATE.clear();
                    }
                    tvCount.setText(Constant.DEVICE_RELATE.size() + getString(R.string.device_count));
                    return;
                }
                if (result.getRet() != 0) {
                    return;
                }
                if (null == Constant.DEVICE_RELATE) {
                    Constant.DEVICE_RELATE = new ArrayList<>();
                }
                Constant.DEVICE_RELATE = result.getResponse();
                if (Constant.DEVICE_RELATE == null) {
                    Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                }
                tvCount.setText(Constant.DEVICE_RELATE.size() + getString(R.string.device_count));
            }

            @Override
            public void onFailed(String json) {
                if(json.equals("主机不在线")){
                    if (Constant.DEVICE_RELATE == null) {
                        Constant.DEVICE_RELATE = new ArrayList<DeviceRelate>();
                    }
                    else{
                        Constant.DEVICE_RELATE.clear();
                    }
                    tvCount.setText(Constant.DEVICE_RELATE.size() + getString(R.string.device_count));
                }
            }
        });
    }

    private LocalBroadcastManager mLocalBroadcastManager;
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MainFragment.ACTION_DELETE_HOST)){
                queryAllDevice();
            }
        }
    };
}
