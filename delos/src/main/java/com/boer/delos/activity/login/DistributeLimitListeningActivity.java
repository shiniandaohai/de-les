package com.boer.delos.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.widget.MyListView;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 * @author PengJiYang
 * @Description: "分配权限"界面
 * create at 2016/3/30 10:53
 *
 */
public class DistributeLimitListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private com.boer.delos.widget.MyListView lvDistributeList;
    private com.zhy.android.percent.support.PercentRelativeLayout llComfortLive;
    private com.zhy.android.percent.support.PercentRelativeLayout llHomeSecurity;
    private com.zhy.android.percent.support.PercentRelativeLayout llHealthLive;
    private com.zhy.android.percent.support.PercentRelativeLayout llGreenLive;
    private com.zhy.android.percent.support.PercentRelativeLayout llDeviceManage;
    private com.zhy.android.percent.support.PercentRelativeLayout llAreaManage;
    private com.zhy.android.percent.support.PercentRelativeLayout llLinkManage;
    private android.widget.ImageView ivComfortLiveChecked;
    private android.widget.ImageView ivHomeSecurityChecked;
    private android.widget.ImageView ivHealthLiveChecked;
    private android.widget.ImageView ivGreenLiveChecked;
    private android.widget.ImageView ivDeviceManageChecked;
    private android.widget.ImageView ivAreaManageChecked;
    private android.widget.ImageView ivLinkManageChecked;

    private android.widget.TextView tvConfirmLimitBtn;// "确定"按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribute_limit);

        initView();
    }

    private void initView() {
        initTopBar(R.string.distribute_limit_title, null, true, false);
        this.tvConfirmLimitBtn = (TextView) findViewById(R.id.tvConfirmLimitBtn);
        this.llLinkManage = (PercentRelativeLayout) findViewById(R.id.llLinkManage);
        this.ivLinkManageChecked = (ImageView) findViewById(R.id.ivLinkManageChecked);
        this.llAreaManage = (PercentRelativeLayout) findViewById(R.id.llAreaManage);
        this.ivAreaManageChecked = (ImageView) findViewById(R.id.ivAreaManageChecked);
        this.llDeviceManage = (PercentRelativeLayout) findViewById(R.id.llDeviceManage);
        this.ivDeviceManageChecked = (ImageView) findViewById(R.id.ivDeviceManageChecked);
        this.llGreenLive = (PercentRelativeLayout) findViewById(R.id.llGreenLive);
        this.ivGreenLiveChecked = (ImageView) findViewById(R.id.ivGreenLiveChecked);
        this.llHealthLive = (PercentRelativeLayout) findViewById(R.id.llHealthLive);
        this.ivHealthLiveChecked = (ImageView) findViewById(R.id.ivHealthLiveChecked);
        this.llHomeSecurity = (PercentRelativeLayout) findViewById(R.id.llHomeSecurity);
        this.ivHomeSecurityChecked = (ImageView) findViewById(R.id.ivHomeSecurityChecked);
        this.llComfortLive = (PercentRelativeLayout) findViewById(R.id.llComfortLive);
        this.ivComfortLiveChecked = (ImageView) findViewById(R.id.ivComfortLiveChecked);
        this.lvDistributeList = (MyListView) findViewById(R.id.lvDistributeList);

        this.llComfortLive.setOnClickListener(this);
        this.llHealthLive.setOnClickListener(this);
        this.llGreenLive.setOnClickListener(this);
        this.llHomeSecurity.setOnClickListener(this);
        this.llAreaManage.setOnClickListener(this);
        this.llDeviceManage.setOnClickListener(this);
        this.llLinkManage.setOnClickListener(this);
        this.tvConfirmLimitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llComfortLive:
                break;
            case R.id.llHealthLive:
                break;
            case R.id.llGreenLive:
                break;
            case R.id.llHomeSecurity:
                break;
            case R.id.llAreaManage:
                break;
            case R.id.llDeviceManage:
                break;
            case R.id.llLinkManage:
                break;
            case R.id.tvConfirmLimitBtn:
                BaseApplication.showToast("确定");
                break;
        }
    }
}
