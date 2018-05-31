package com.boer.delos.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.jpush.JpushController;
import com.boer.delos.request.system.SystemController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SoundAndVibratorPreferences;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.text.SimpleDateFormat;

/**
 * @author PengJiYang
 * @Description: "系统设置"界面
 * create at 2016/4/5 13:22
 */
public class SystemSettingsListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private PercentRelativeLayout rlBackups;
    private PercentRelativeLayout rlRestore;
    private PercentRelativeLayout rlGatewayUpgrade;
    private PercentRelativeLayout rlShockHint;
    private PercentRelativeLayout rlVoiceHint;
    private PercentRelativeLayout rlHelpCenter;
    private PercentRelativeLayout rlAboutUs;
    private TextView tvBackupDescribe;
    private ToggleButton tbShockHint;
    private String isVibrator = "0";
    private SoundAndVibratorPreferences soundAndVibratorPreferences;
    private String[] value;
    //是否允许点击返回键退出，一键还原和一键备份都不允许退出
    private boolean isAllowBack = true;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String restoreMessage = "一键还原失败";//还原信息
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            connectTest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        //读取本地文件，获取是否震动，改变是否震动列表项ToggleButton的状态
        soundAndVibratorPreferences = new SoundAndVibratorPreferences();
        value = soundAndVibratorPreferences.readSoundAndVibratorXml(SystemSettingsListeningActivity.this).split(",");

        initView();

        //非空判断
        if (Constant.GATEWAY != null)
            initData();
    }

    private void initData() {
        if (value[1].equals("1")) {
            tbShockHint.setChecked(true);
        } else {
            tbShockHint.setChecked(false);
        }


        String time = getDateToString(Constant.GATEWAY.getLastbackup());
        if (!StringUtil.isEmpty(time)) {
            tvBackupDescribe.setText("备份时间:" + time);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "0";
                if (tbShockHint.isChecked()) {
                    tmp = "1";
                } else {
                    tmp = "0";
                }
                //如果设置震动的值跟本地一致，不需要提交服务器
                if (tmp.equals(value[1])) {
                    finish();
                } else {
                    toastUtils.showProgress("正在保存设置...");
                    updateExtend(SystemSettingsListeningActivity.this, value[0], tmp);
                }

            }
        });

        //查询用户是否为管理员
        isHostAdmin();
    }

    /**
     * 查询用户是否为管理员
     **/
    private void isHostAdmin() {
        //查询当前用户是否为管理员
        FamilyManageController.getInstance().adminInfoWithHostId(this, Constant.CURRENTHOSTID,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            UserResult result = new Gson().fromJson(Json, UserResult.class);
                            if (result.getRet() == 0) {
                                if (result.getUser().getId().equals(Constant.USERID)) {
                                    Constant.ISMANAGER = true;
                                } else {
                                    Constant.ISMANAGER = false;
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


    private void initView() {
        vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
        } else {
            vTitle.setVisibility(View.GONE);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvRight = (TextView) findViewById(R.id.tvRight);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRight = (ImageView) findViewById(R.id.ivRight);

        tvTitle.setText(R.string.system_setting_title);
        ivRight.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);


        rlAboutUs = (PercentRelativeLayout) findViewById(R.id.rlAboutUs);
        rlHelpCenter = (PercentRelativeLayout) findViewById(R.id.rlHelpCenter);
        rlVoiceHint = (PercentRelativeLayout) findViewById(R.id.rlVoiceHint);
        rlShockHint = (PercentRelativeLayout) findViewById(R.id.rlShockHint);
        tbShockHint = (ToggleButton) findViewById(R.id.tbShockHint);
        rlGatewayUpgrade = (PercentRelativeLayout) findViewById(R.id.rlGatewayUpgrade);
        rlRestore = (PercentRelativeLayout) findViewById(R.id.rlRestore);
        rlBackups = (PercentRelativeLayout) findViewById(R.id.rlBackups);

        tvBackupDescribe = (TextView) findViewById(R.id.tvBackupDescribe);

        rlBackups.setOnClickListener(this);
        rlRestore.setOnClickListener(this);
        rlGatewayUpgrade.setOnClickListener(this);
        tbShockHint.setOnClickListener(this);
        rlVoiceHint.setOnClickListener(this);
        rlHelpCenter.setOnClickListener(this);
        rlAboutUs.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlBackups:
                oneTouchBackup();
                break;
            case R.id.rlRestore:
                oneTouchRestore();
                break;
            case R.id.rlGatewayUpgrade:
                toGatewayUpgradeActivity();
                break;
            case R.id.tbShockHint:
                // 震动提示
                break;
            case R.id.rlVoiceHint:
                // 声音提示
                startActivity(new Intent(this, SetPushSoundListeningActivity.class));
                break;
            case R.id.rlHelpCenter:
                // 帮助中心
                startActivity(new Intent(this, HelpCenterListeningActivity.class));
                break;
            case R.id.rlAboutUs:
                // 关于我们
                startActivity(new Intent(this, AboutUsListeningActivity.class));
                break;
        }
    }

    /**
     * 去主机升级页面
     */
    private void toGatewayUpgradeActivity() {
        if (!Constant.ISMANAGER) {
            toastUtils.showInfoWithStatus("非主机管理员,不可操作");
            return;
        }
        // 主机升级
        startActivity(new Intent(this, GatewayUpgradeActivity.class));
    }

    /**
     * 一键还原
     */
    private void oneTouchRestore() {
        if (!Constant.ISMANAGER) {
            toastUtils.showInfoWithStatus("非主机管理员,不可操作");
            return;
        }
        toastUtils.showProgress("正在还原中,请稍候...");
        SystemController.getInstance().vistaGhost(this, Constant.CURRENTHOSTID,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d(Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d(Json);
                    }
                });

        //1分钟后进行连接测试
        mHandler.postDelayed(mRunnable, 60000);
    }

    /**
     * 连接测试
     */
    private void connectTest() {
        GatewayController.getInstance().getGatewayProperties(this,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.showSuccessWithStatus("还原成功");
                        mHandler.removeCallbacks(mRunnable);
                    }

                    @Override
                    public void onFailed(String Json) {
                        try {
                            //10秒重连
                            mHandler.postDelayed(mRunnable, 10000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 更新铃声和震动请求
     */
    private void updateExtend(Context mContext, final String tone, final String vibration) {
        JpushController.getInstance().updateExtend(mContext, tone, vibration,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("update_extend json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            L.e("成功");
                            //保存在本地
                            new SoundAndVibratorPreferences().writeSoundAndVibratorDataXml(SystemSettingsListeningActivity.this, tone, vibration);
                            finish();
                        } else {
                            toastUtils.dismiss();
                            L.e("失败");
                            finish();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        try {
                            if (toastUtils != null)
                                toastUtils.dismiss();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 一键备份
     */
    public void oneTouchBackup() {
        //如果是非主机管理员
        if (!Constant.ISMANAGER) {
            toastUtils.showInfoWithStatus("非主机管理员,不可操作");
            return;
        }
        // 一键备份
        toastUtils.showProgress("正在备份中...");
        SystemController.getInstance().oneTouchBackup(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                try {
                    GatewayResult gatewayResult = new Gson().fromJson(Json, GatewayResult.class);
                    if (gatewayResult.getRet() != 0) {
                        toastUtils.showErrorWithStatus("一键备份失败");
                        return;
                    }
                    toastUtils.showSuccessWithStatus("一键备份成功");
                    String time = getDateToString(gatewayResult.getResponse().getLastbackup());
                    tvBackupDescribe.setText("备份时间:" + time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils != null)
                    toastUtils.dismiss();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isAllowBack) {
                return false;
            }

            String tmp = "0";

            if (tbShockHint.isChecked()) {
                tmp = "1";
            } else {
                tmp = "0";
            }
            //如果设置震动的值跟本地一致，不需要提交服务器
            if (tmp.equals(value[1])) {
                finish();
            } else {
                toastUtils.showProgress("正在保存设置...");
                updateExtend(SystemSettingsListeningActivity.this, value[0], tmp);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    //时间戳转换为时间
    private String getDateToString(String timestampString) {
        String date = "";
        if (timestampString == null) {
            date = "";
        } else {
            Long timestamp = Long.parseLong(timestampString) * 1000;
            date = sf.format(new java.util.Date(timestamp));
        }

        return date;
    }
}
