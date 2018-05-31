package com.boer.delos.activity.main;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.activity.login.LoginActivity;
import com.boer.delos.activity.login.LoginSelectListeningActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ApplyExistResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ad.AdController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StatusBarUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.PreferencesUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreTag;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 *
 */
public class SplashActivity extends CommonBaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tlTitleLayout.setVisibility(View.GONE);
        statusBarTheme(false, -1);

//        UDPUtils.startUDPBroadCast(null);
//        checkInternet();
//        readXMLInfo();
        loadingAnimation();

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initAction() {
    }

    private void loadingAnimation() {
        ImageView splash = (ImageView) findViewById(R.id.splash);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        animation.setDuration(2000);
        splash.startAnimation(animation);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, MainTabActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void checkInternet() {
        //测试网络
        AdController.getInstance().testToInternet(getApplication(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Constant.IS_INTERNET_CONN = true;
                Loger.d("Splash checkInternet onSuccess()");
            }

            @Override
            public void onFailed(String Json) {
                Constant.IS_INTERNET_CONN = false;
                Loger.d("Splash checkInternet onSuccess()");
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
                                if (result.getRet() == 10105) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                } else
                                    toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                int exists = result.getExists();
                                //如果有申请,跳到主页
                                if (exists == 1) {
                                    startActivity(new Intent(SplashActivity.this, MainTabActivity.class));
                                    finish();
                                }
                                //没有申请跳到选择登录
                                else {
                                    startActivity(new Intent(SplashActivity.this, LoginSelectListeningActivity.class));
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d(Json);
                    }
                });
    }

    /**
     * 判断用户是否有当前主机ID
     *
     * @return
     */
    private boolean isUserHaveCurrentHostId() {
        if (!StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return true;
        }
        return false;
    }


    /**
     * 检查是否有保存的用户信息和加密密钥
     *
     * @return
     */
    private void readXMLInfo() {
        SharedPreferencesUtils.readKeyFromPreferences(this);
        SharedPreferencesUtils.readTokenFromPreferences(this);
        SharedPreferencesUtils.readUserInfoFromPreferences(this);
        SharedPreferencesUtils.readCurrentHostIdFromPreferences(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
