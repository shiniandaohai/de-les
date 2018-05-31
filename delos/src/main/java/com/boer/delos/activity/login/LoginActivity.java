package com.boer.delos.activity.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.ApplyExistResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.LocalUserResult;
import com.boer.delos.model.ThirdUser;
import com.boer.delos.model.TokenResult;
import com.boer.delos.model.User;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ad.AdController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.jpush.JpushController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.JudgeNetworkTypeUtils;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.PublicMethod;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.HostSelectPopUpWindow;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * @author PengJiYang
 * @Description: "登陆"界面 第三方登录TODO
 * create at 2016/3/8 10:55
 */
public class LoginActivity extends CommonBaseActivity implements TextWatcher {

    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.iv_username_delete)
    ImageView mIvUsernameDelete;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.iv_password_delete)
    ImageView mIvPasswordDelete;
    @Bind(R.id.tv_forget_psd)
    TextView mTvForgetPsd;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    @Bind(R.id.iv_weixin)
    CheckBox mIvWeixin;
    @Bind(R.id.iv_qq)
    CheckBox mIvQq;
    @Bind(R.id.iv_sina)
    CheckBox mIvSina;
    @Bind(R.id.login_bg)
    LinearLayout mLoginBg;
    @Bind(R.id.ll_parent)
    LinearLayout ll_parent;

    private ArrayList<SnsPlatform> platforms;
    private boolean IS_SAME_NET; //判断是否在同一网段下
    private HostSelectPopUpWindow hostSelectPopUpWindow;

    //MainTabActivity
    String tabPos = "-1";
    //
    private String thirdPartyType = Constant.LOGIN_TYPE_WEBCHAT;
    ;
    ThirdUser thirdUser;

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setVisibility(View.GONE);

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        adaptTheme(true);
        statusBarTheme(false, getResources().getColor(R.color.layout_title_bg));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void adaptTheme(final boolean isTranslucentStatusFitSystemWindowTrue) {
        if (isTranslucentStatusFitSystemWindowTrue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        tabPos = intent.getStringExtra("tabPos");
        if (tabPos == null) {
            tabPos = "0";
        }

        platforms = new ArrayList<>();
        platforms.add(SHARE_MEDIA.WEIXIN.toSnsPlatform());
        platforms.add(SHARE_MEDIA.QQ.toSnsPlatform());
        platforms.add(SHARE_MEDIA.SINA.toSnsPlatform());

        readXMLUserNameAndPsd();

        UDPUtils.startUDPBroadCast(null);


    }

    @OnClick(R.id.tv_right)
    public void rightViewClick() {
        // 注册
        Intent intent = new Intent(this, RegisterListeningActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    public void leftViewClick() {
        setResult(RESULT_OK);
        finish();
    }

    private void readXMLUserNameAndPsd() {
        //读取登录用户
        SharedPreferencesUtils.readLoginUserNameAndPassword(this);
        mEtUsername.setText(Constant.LOGIN_USERNNME);
        mEtPassword.setText(Constant.LOGIN_PASSWORD);

        if (!StringUtil.isEmpty(mEtUsername.getText().toString())) {
            mIvUsernameDelete.setVisibility(View.VISIBLE);
            formatUsername(mEtUsername);
        }

        if (!StringUtil.isEmpty(mEtPassword.getText().toString())) {
            mIvPasswordDelete.setVisibility(View.VISIBLE);
            formatUsername(mEtPassword);
        }
    }

    private String formatUsername(TextView editText) {

        if (StringUtil.isEmpty(editText.getText().toString())) {
            return "";
        } else {
            // 将光标置于密码文本的末尾
            CharSequence username = editText.getText();
            if (username instanceof Spannable) {
                Spannable spannable = (Spannable) username;
                Selection.setSelection(spannable, username.length());
            }
            return editText.getText().toString().replaceAll(" ", "");
        }


    }

    @Override
    protected void initAction() {
        initListener();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(mEtPassword.getText())
                && mEtPassword.isFocused()
                && mIvUsernameDelete.getVisibility() == View.GONE) {
            mIvUsernameDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtil.isEmpty(mEtPassword.getText().toString())
                || StringUtil.isEmpty(mEtPassword.getText().toString())) {
            mBtnCommit.setEnabled(false);
            mBtnCommit.setBackgroundResource(R.drawable.btn_commit_unclick);

        } else {
            mBtnCommit.setEnabled(true);
//            mBtnCommit.setClickable(true);
            mBtnCommit.setBackgroundResource(R.drawable.btn_blue_save);
        }
    }

    private void initListener() {
//        mEtPassword.addTextChangedListener(this);
//        mEtUsername.addTextChangedListener(this);

        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mIvPasswordDelete.setVisibility(View.GONE);
                } else if (!StringUtil.isEmpty(mEtPassword.getText().toString())) {
                    mIvPasswordDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        mEtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String psd = mEtPassword.getText().toString();
                String username = mEtUsername.getText().toString();

                if (!hasFocus) {
                    mIvUsernameDelete.setVisibility(View.GONE);

//                    if (StringUtil.isEmpty(username)) {
//                        toastUtils.showErrorWithStatus(getString(R.string.text_username_null));
//                        return;
//                    }
//                    if (username.contains(" ")) {
//                        toastUtils.showErrorWithStatus(getString(R.string.text_username_contains_null));
//                    }
                } else {
                    mIvUsernameDelete.setVisibility(View.VISIBLE);
                }
                if (StringUtil.isEmpty(username)) {
                    mBtnCommit.setEnabled(false);
                    mBtnCommit.setBackgroundResource(R.drawable.btn_commit_unclick);

                } else {
//                    mBtnCommit.setEnabled(true);
//                    mBtnCommit.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    mBtnCommit.setEnabled(true);
                    mBtnCommit.setBackgroundResource(R.drawable.btn_blue_save);
                }
            }
        });

    }

    @OnClick({R.id.iv_username_delete, R.id.iv_password_delete, R.id.login_bg,
            R.id.tv_forget_psd, R.id.btn_commit, R.id.iv_weixin, R.id.iv_qq, R.id.iv_sina})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_bg:
                hideInput();
                break;
            case R.id.iv_username_delete:
                mEtUsername.setText("");
                mEtPassword.setText("");
                break;
            case R.id.iv_password_delete:
                mEtPassword.setText("");
                break;
            case R.id.tv_forget_psd:
                //忘记密码
                startActivity(new Intent(this, LoginErrorActivity.class));
                break;
            case R.id.btn_commit:
                //无主机，有主机
                if (!NetUtil.checkNet(this)) {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                    return;
                }
                hideInput();
                if (!beforeJudgeLogin()) return; //判断手机号、密码 是否符合登录规范
                toastUtils.showProgress(getString(R.string.toast_logining));
                testInternet();
                break;
            case R.id.iv_weixin:
                loginThree(0);
                break;
            case R.id.iv_qq:
                loginThree(1);
                break;
            case R.id.iv_sina:
                loginThree(2);
                break;
        }
    }

    private void loginThree(int type) {

        switch (type) {

            case 0:
                thirdPartyType = Constant.LOGIN_TYPE_WEBCHAT;
                break;
            case 1:
                thirdPartyType = Constant.LOGIN_TYPE_QQ;
                break;
            case 2:
                thirdPartyType = Constant.LOGIN_TYPE_SINA;
                break;
            default:
                break;
        }
        UMShareAPI.get(this).getPlatformInfo(this, platforms.get(type).mPlatform, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String temp = "";
            for (String key : data.keySet()) {
                temp = temp + key + " : " + data.get(key) + "\n";
            }

            Log.v("gl", "onComplete===" + temp);
            thirdUser = new ThirdUser();
            thirdUser.setGender(data.get("gender"));
            thirdUser.setIconurl(data.get("iconurl"));
            thirdUser.setName(data.get("name"));
            thirdUser.setThirdPartyType(thirdPartyType);
            thirdUser.setUid(data.get("uid"));

            Constant.THIRDUSER=thirdUser;
            negotiate();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if(t.getMessage().contains("错误码：2008")){
                toastUtils.showInfoWithStatus("没有安装应用");
            }
            else{
                toastUtils.showInfoWithStatus("错误" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };

    /**
     * 测试外网络是否连接
     */
    private void testInternet() {
        //判断是否有网络
        AdController.getInstance().testToInternet(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Constant.IS_INTERNET_CONN = true;
                btnLoginClick();
                Loger.d("NET");

            }

            @Override
            public void onFailed(String Json) {
                Constant.IS_INTERNET_CONN = false;
                if (Constant.gatewayInfos.size() == 0) {
                    UDPUtils.startUDPBroadCast(null);
                    if (toastUtils != null)
                        toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
                    return;
                }
                btnLoginClick();
                Loger.d("NET");
            }
        });
    }

    private void btnLoginClick() {
        //如果有网，且主机IP 和 手机IP在同一个局域网下，走直联方式
        IS_SAME_NET = JudgeNetworkTypeUtils.judgeIsSameNetwork(getApplicationContext());
        if (Constant.IS_INTERNET_CONN) {

            if (mEtUsername == null || mEtUsername.getText() == null
                    || !StringUtil.isMobile(mEtUsername.getText().toString())) {
                toastUtils.showErrorWithStatus(getString(R.string.toast_error_mobile));
                return;
            }
            negotiate();

        } else if (NetUtil.checkNet(this)) {
            if (StringUtil.getTextViewString(mEtUsername) != null && StringUtil.getTextViewString(mEtUsername).equals("admin")) {
                if (StringUtil.isEmpty(mEtPassword.getText().toString())) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_input_pwd));
                    return;
                }
                if (!StringUtil.checkPassword(mEtPassword.getText().toString())&&!StringUtil.getTextViewString(mEtUsername).equals("admin")) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_error_pwd));
                    return;
                }
            }
            if (mEtUsername.getText() == null
                    || !StringUtil.isMobile(mEtUsername.getText().toString())&&!StringUtil.getTextViewString(mEtUsername).equals("admin")) {
                toastUtils.showErrorWithStatus(getString(R.string.toast_error_mobile));
                return;
            }
            if (toastUtils != null)
                toastUtils.dismiss();
            //判断是否有直连主机
            if (Constant.gatewayInfos.size() > 0) {
                //弹出选择主机视图
                popUpGatewaySelectWindow();
            } else {
                UDPUtils.startUDPBroadCast(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toastUtils.showErrorWithStatus(getString(R.string.toast_host_null));
                    }
                }, 500);
            }
        } else {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
        }
    }

    /**
     * 校验密码和用户名格式是否正确
     */
    private boolean beforeJudgeLogin() {
        String username = formatUsername(mEtUsername);
        String password = formatUsername(mEtPassword);
        if (StringUtil.isEmpty(username)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_mobile));
            return false;
        }

        if (!StringUtil.checkPhoneNum(username)&&!username.equals("admin")) {
            toastUtils.showInfoWithStatus("请输入正确的手机号码");
            return false;
        }

        if (StringUtil.isEmpty(password)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_pwd));
            return false;
        }
        if (!StringUtil.checkPassword(password)&&!username.equals("admin")) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_pwd));
            return false;
        }
        return true;
    }

    /**
     * 发送握手请求，获取握手令牌
     */
    private void negotiate() {
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

                        //TODO 三方登录
                        if (thirdUser != null && !TextUtils.isEmpty(thirdUser.getUid())) {

                            thirdLogin();
                            return;
                        }

                        commitLogin(StringUtil.getTextViewString(mEtUsername), StringUtil.getTextViewString(mEtPassword));
//
                    }
                } catch (Exception e) {
                    Loger.e("beforeJudgeLogin" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils == null) return;
                if (StringUtil.isEmpty(json)) {
                    toastUtils.showErrorWithStatus(getString(R.string.toast_query_token_fail));
                } else {
                    toastUtils.showErrorWithStatus(json);
                }
            }
        });
    }

    private void thirdLogin() {
        MemberController.getInstance().thirdLogin(LoginActivity.this, thirdUser.getIconurl(), thirdUser.getGender(), thirdUser.getName(), thirdUser.getUid(), thirdUser.getThirdPartyType(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {




                UserResult userResult = new Gson().fromJson(URLDecoder.decode(json), UserResult.class);

//                Log.v("gl", "json++thirdLogin===" + URLDecoder.decode(json));
                if (userResult.getRet() != 0) {
                    toastUtils.showErrorWithStatus(userResult.getMsg());
                } else {
                    toastUtils.dismiss();
                    Constant.LOGIN_USER = userResult.getUser();
                    Constant.CURRENTPHONE = userResult.getUser().getMobile();
                    Constant.TOKEN = userResult.getToken();
                    Constant.USERID = userResult.getUser().getId();
                    Constant.PERMISSIONS = userResult.getPermissions();
                    //将极光推送与服务器关联
                    PublicMethod.registrationID = JPushInterface.getRegistrationID(LoginActivity.this);
                    updateToken(LoginActivity.this, PublicMethod.registrationID);
                    //将用户信息保存在手机里
                    SharedPreferencesUtils.saveUserInfoToPreferences(getBaseContext());
                    SharedPreferencesUtils.saveTokenToPreferences(getBaseContext());
                    SharedPreferencesUtils.saveKeyToPreferences(getBaseContext());

                    getUserHostId();
                }



            }

            @Override
            public void onFailed(String json) {
                Log.d("thirdLogin",json);
            }
        });
    }

    /**
     * 弹出主机选择
     */
    private void popUpGatewaySelectWindow() {
        if (hostSelectPopUpWindow != null && hostSelectPopUpWindow.isShowing()) {
            hostSelectPopUpWindow.dismiss();
            hostSelectPopUpWindow = null;
        }
        hostSelectPopUpWindow = new HostSelectPopUpWindow(this, Constant.gatewayInfos, new HostSelectPopUpWindow.ClickResultListener() {
            @Override
            public void result(GatewayInfo gatewayInfo) {
                if (gatewayInfo == null) {
                    toastUtils.dismiss();
                    return;
                }
                //直连登录
                Constant.IS_LOCAL_CONNECTION = Boolean.TRUE;
                Constant.LOCAL_CONNECTION_IP = gatewayInfo.getIp();
                Constant.CURRENTHOSTID = gatewayInfo.getHostId();
                localLogin();
            }
        });
        hostSelectPopUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 发送登录请求
     */
    private void commitLogin(final String username, final String password) {
        MemberController.getInstance().login(this, username,
                password, "Android", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        String pswd = password;
                        UserResult userResult = new Gson().fromJson(Json, UserResult.class);
                        if (userResult.getRet() != 0) {
                            toastUtils.showErrorWithStatus(userResult.getMsg());
                        } else {
                            toastUtils.dismiss();
                            //保存当前登录用户
                            Constant.LOGIN_USERNNME = username;
                            Constant.LOGIN_PASSWORD = password;
                            SharedPreferencesUtils.saveLoginUserNameAndPassword(LoginActivity.this);

                            Constant.LOGIN_USER = userResult.getUser();
                            Constant.CURRENTPHONE = userResult.getUser().getMobile();
                            Constant.TOKEN = userResult.getToken();
                            Constant.USERID = userResult.getUser().getId();
                            Constant.PERMISSIONS = userResult.getPermissions();
                            SharedUtils.getInstance().saveJsonByTag(SharedTag.user_old_password, Md5Encrypt.stringMD5(pswd));
                            //将极光推送与服务器关联

                            PublicMethod.registrationID = JPushInterface.getRegistrationID(LoginActivity.this);
                            updateToken(LoginActivity.this, PublicMethod.registrationID);
                            //将用户信息保存在手机里
                            SharedPreferencesUtils.saveUserInfoToPreferences(getBaseContext());
                            SharedPreferencesUtils.saveTokenToPreferences(getBaseContext());
                            SharedPreferencesUtils.saveKeyToPreferences(getBaseContext());

                            getUserHostId();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils == null) return;
                        if (StringUtil.isEmpty(json)) {
                            toastUtils.showErrorWithStatus(getString(R.string.toast_login_fail));

                        } else {
                            toastUtils.showErrorWithStatus(json);
                        }
                    }
                });
    }

    /**
     * 直连登录
     */
    private void localLogin() {
        String password = StringUtil.getTextViewString(mEtPassword);
        final String username = StringUtil.getTextViewString(mEtUsername);

        if (StringUtil.isEmpty(username)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_mobile));
            return;
        }
//        if (!StringUtil.isMobile(username)) {
//            toastUtils.showErrorWithStatus(readString(R.string.toast_error_mobile));
//            return;
//        }
        if (StringUtil.isEmpty(password)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_pwd));
            return;
        }
//        if (!StringUtil.checkPassword(password)) {
//            toastUtils.showErrorWithStatus(readString(R.string.toast_error_pwd));
//        }
        if (username == null || password == null) {
            return;
        }
        //保存当前登录用户
        Constant.LOGIN_USERNNME = username;
        Constant.LOGIN_PASSWORD = password;

        SharedPreferencesUtils.saveLoginUserNameAndPassword(this);

        toastUtils.showProgress(getString(R.string.toast_logining));
        MemberController.getInstance().localLogin(LoginActivity.this, username, password, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    LocalUserResult result = new Gson().fromJson(Json, LocalUserResult.class);
                    if (result.getRet() != 0) {
                        if (result.getMessage() != null)
                            toastUtils.showErrorWithStatus(result.getMessage());
                    } else {
                        toastUtils.dismiss();
                        Constant.LOGIN_USER = result.getUserInfo();
                        if(Constant.LOGIN_USER==null){
                            Constant.LOGIN_USER=new User();
                            Constant.LOGIN_USER.setName(username);

                            List<Object> hostids=new ArrayList<Object>();
                            hostids.add(Constant.CURRENTHOSTID);
                            Constant.LOGIN_USER.setHostId(hostids);
                        }
                        if (result.getUserInfo() != null) {
                            Constant.USERID = result.getUserInfo().getId();
                        }
                        if (result.getUserInfo() != null && result.getUserInfo().getMobile() != null) {
                            Constant.CURRENTPHONE = result.getUserInfo().getMobile();
                        }
                        SharedPreferencesUtils.saveUserInfoToPreferences(getBaseContext());
                        Constant.IS_LOCAL_CONNECTION = true;  // add by sunzhibin 2016/11/24
                        startMainTabActivity();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    Loger.e("localLogin:" + e.toString());
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils == null) return;
                toastUtils.dismiss();
            }
        });
    }

    /**
     * 获取用户的currentId
     */
    private void getUserHostId() {
        GatewayController.getInstance().queryUserHost(getBaseContext(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                HostResult result = new Gson().fromJson(Json, HostResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(result.getMsg());
                } else {
                    String currentHostId = result.getCurrentHostId();
                    //如果没有主机信息
                    if (StringUtil.isEmpty(currentHostId)) {
                        Constant.CURRENTHOSTID = "";
                        //查询是否有用户申请或分享
                        requestIsUserHaveApplyOrShare();
                    } else {
                        //保存当前主机Id
                        Constant.CURRENTHOSTID = currentHostId;
                        SharedPreferencesUtils.saveCurrentHostIdToPreferences(getBaseContext());
                        boolean IS_SAME_NET = JudgeNetworkTypeUtils.judgeIsSameNetwork(getApplicationContext());
                        if (IS_SAME_NET) Constant.IS_LOCAL_CONNECTION = true;
                        startMainTabActivity();
                    }
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    /**
     * 查询用户是否有未处理申请或分享
     */
    private void requestIsUserHaveApplyOrShare() {
        FamilyManageController.getInstance().isUserHaveApplyOrShare(this, Constant.USERID,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            ApplyExistResult result = new Gson().fromJson(Json, ApplyExistResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                int exists = result.getExists();
                                //如果有申请,跳到主页
                                if (exists == 1) {
                                    startMainTabActivity();
                                }
                                //没有申请跳到选择登录
                                else {
                                    toastUtils.dismiss();
                                    startMainTabActivity();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    /**
     * 更新推送设备码请求
     */
    private void updateToken(Context mContext, String deviceToken) {
        JpushController.getInstance().updateToken(mContext,
                deviceToken, "jpush", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                        } else {
                            Loger.e("失败");
                        }
                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });
    }


    public void startMainTabActivity() {
        Intent intent = new Intent(getBaseContext(), MainTabActivity.class);
        intent.putExtra("tabPos", tabPos);
        startActivity(intent);
        finish();
        Constant.isLogin = true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK, new Intent(this, MainTabActivity.class));
            //exitBy2Click();
            finish();
        }
        return false;
    }


    protected static Boolean isExit = false;

    protected void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {

            ActivityStack.getInstance().finishActivities();
            Constant.isLogin = false;
            System.exit(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//完成回调
    }
}
