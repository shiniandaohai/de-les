package com.boer.delos.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.system.SystemController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/31.
 */
public class SysSettingActivity extends CommonBaseActivity {
    @Bind(R.id.rlBackups)
    LinearLayout rlBackups;
    @Bind(R.id.rlRestore)
    LinearLayout rlRestore;
    @Bind(R.id.rlGatewayUpgrade)
    LinearLayout rlGatewayUpgrade;
    @Bind(R.id.tvBackupDescribe)
    TextView tvBackupDescribe;
    @Bind(R.id.tv_backup_time)
    TextView tv_backup_time;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            connectTest();
        }
    };


    @Override
    protected int initLayout() {
        return R.layout.activity_sys_setting;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.system_setting_title);

    }

    @Override
    protected void initData() {

        if (Constant.GATEWAY != null) {
            String time = getDateToString(Constant.GATEWAY.getLastbackup());
            if (!StringUtil.isEmpty(time)) {
                tv_backup_time.setText("备份时间:" + time);
            }
        }
        judgeIsAdmin();

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.rlBackups, R.id.rlRestore, R.id.rlGatewayUpgrade})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBackups:
                oneTouchBackup();
                break;
            case R.id.rlRestore:
                oneTouchRestore();
                break;
            case R.id.rlGatewayUpgrade:
                toGatewayUpgradeActivity();
                break;
        }
    }

    private void oneTouchBackup() {
        //如果是非主机管理员
        if (!Constant.ISMANAGER) {
            toastUtils.showInfoWithStatus("您不是主机管理员，无法操作此功能");
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
                    tv_backup_time.setText("备份时间:" + time);
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

    private void toGatewayUpgradeActivity() {
        if (!Constant.ISMANAGER) {
            toastUtils.showInfoWithStatus("非主机管理员,不可操作");
            return;
        }
        // 主机升级
        startActivity(new Intent(this, GatewayUpgradeActivity.class));
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


    private void judgeIsAdmin() {
        FamilyManageController.getInstance().userIsAdmin(this, Constant.LOGIN_USER.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    int isAdmin = jsonObject.getInt("isAdmin");
                    if (ret == 0 && isAdmin == 1) {
                        Constant.ISMANAGER = true;
                    } else {
                        Constant.ISMANAGER = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Constant.ISMANAGER = false;
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }
}
