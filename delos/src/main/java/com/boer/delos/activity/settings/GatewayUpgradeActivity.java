package com.boer.delos.activity.settings;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.system.SystemController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ToastHelper;

/**
 * Created by pengjiyang on 2016/4/5.
 */

/**
 * @author PengJiYang
 * @Description: "主机升级"界面
 * create at 2016/4/5 15:50
 */
public class GatewayUpgradeActivity extends BaseListeningActivity implements View.OnClickListener {

    private android.widget.TextView tvHardwareVersion;
    private android.widget.TextView tvSoftwareVersion;
    private TextView tvGatewayUpgradeBtn;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            connectTest();
        }
    };

    /**
     * 连接测试
     */
    private void connectTest() {
        GatewayController.getInstance().getGatewayProperties(this,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.showSuccessWithStatus("升级成功");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_upgrade);

        initView();

//        PackageManager pm = GatewayUpgradeActivity.this.getPackageManager();
//        PackageInfo pi = null;
//        try {
//            pi = pm.getPackageInfo(getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        tvSoftwareVersion.setText("软件版本：" + pi.versionName);
        String soft = Constant.GATEWAY.getSoftver();
        String hard = Constant.GATEWAY.getFirmver();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < soft.length(); i++) {
            stringBuffer.append(soft.substring(i, i + 1) + ".");
        }
        soft = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        stringBuffer.setLength(0);
        for (int i = 0; i < hard.length(); i++) {
            stringBuffer.append(hard.substring(i, i + 1) + ".");
        }
        hard = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        tvSoftwareVersion.setText(getString(R.string.software_version) + soft);
        tvHardwareVersion.setText(getString(R.string.hardware_version) + hard);

    }

    private void initView() {
        initTopBar(R.string.gateway_upgrade_title, null, true, false);
        this.tvSoftwareVersion = (TextView) findViewById(R.id.tvSoftwareVersion);
        this.tvHardwareVersion = (TextView) findViewById(R.id.tvHardwareVersion);
        this.tvGatewayUpgradeBtn = (TextView) findViewById(R.id.tvGatewayUpgradeBtn);

        this.tvGatewayUpgradeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGatewayUpgradeBtn:
//                softwareUpgrade();
                gatewayIsNeedUpgrade();
                break;
        }
    }


    /**
     * 软件升级
     */
    public void softwareUpgrade() {
        toastUtils.showProgress("主机升级中...");
        SystemController.getInstance().softwareUpgrade(this,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.dismiss();
                        Loger.d(Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d(Json);
                        toastUtils.dismiss();
                    }
                });
        //2分钟后重连
        mHandler.postDelayed(mRunnable, 2 * 60 * 1000);
    }

    private void gatewayIsNeedUpgrade() {
        toastUtils.showProgress("主机检测更新中...");
        SystemController.getInstance().softwareNeedUpgrade(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("主机版本", json);
                try {
                    toastUtils.dismiss();
                    String softver = JsonUtil.parseString(json, "softVer");
                    String firmver = JsonUtil.parseString(json, "firmver");
                    if (softver.equals(Constant.GATEWAY.getSoftver())) {
                        toastUtils.showSuccessWithStatus("主机版本已是最新");
                    } else {
                        //更新主机
                        softwareUpgrade();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastUtils.showErrorWithStatus("主机检查更新失败");
//                    Loger.d("主机检查更新失败");
                }
            }

            @Override
            public void onFailed(String json) {
//                ToastHelper.showShortMsg(json);
                toastUtils.dismiss();
                Loger.d(json);
            }
        });
    }
}
