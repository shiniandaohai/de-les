package com.boer.delos.activity.healthylife.urine;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseActivity;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.shake.bluetooth.conn.ScaleConn;

/**
 * @author PengJiYang
 * @Description: 尿检界面
 * create at 2016/5/6 14:59
 */
public class UrinalysisListeningActivity extends BaseActivity implements View.OnClickListener {

    private android.widget.TextView tvUrinalysisTime;
    private android.widget.TextView tvUrinalysisScore;
    private android.widget.TextView tvUrinalysisType;
    private android.widget.TextView tvLEUState;
    private android.widget.ImageView ivLEURight;
    private android.widget.ImageView ivLEUCenter;
    private android.widget.ImageView ivLEULeft;

    private android.widget.TextView tvNITState;
    private android.widget.ImageView ivNITRight;
    private android.widget.ImageView ivNITCenter;

    private android.widget.TextView tvUBGState;
    private android.widget.ImageView ivUBGRight;
    private android.widget.ImageView ivUBGCenter;
    private android.widget.ImageView ivUBGLeft;
    private android.widget.TextView tvPROState;

    private android.widget.ImageView ivPRORight;
    private android.widget.ImageView ivPROCenter;
    private android.widget.ImageView ivPROLeft;
    private android.widget.TextView tvRBCState;
    private android.widget.ImageView ivRBCRight;

    private android.widget.ImageView ivRBCCenter;
    private android.widget.ImageView ivRBCLeft;
    private android.widget.TextView tvPHState;
    private android.widget.SeekBar sbPh;
    private android.widget.TextView tvBLDState;

    private android.widget.ImageView ivBLDRight;
    private android.widget.ImageView ivBLDCenter;
    private android.widget.ImageView ivBLDLeft;
    private com.zhy.android.percent.support.PercentLinearLayout pllLEU;
    private com.zhy.android.percent.support.PercentLinearLayout pllNIT;
    private com.zhy.android.percent.support.PercentLinearLayout pllUBG;
    private com.zhy.android.percent.support.PercentLinearLayout pllPRO;
    private com.zhy.android.percent.support.PercentLinearLayout pllRBC;
    private com.zhy.android.percent.support.PercentLinearLayout pllPH;
    private com.zhy.android.percent.support.PercentLinearLayout pllBLD;
    private BluetoothAdapter mBluetoothAdapter;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urinalysis);

        initView();
        initData();
        initListener();
        Intent gattServiceIntent = new Intent(this, ScaleConn.class);
        bindService(gattServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        initTopBar(R.string.urinalysis_title, null, true, true);
        ivRight.setImageResource(R.drawable.ic_health_live_more);
        pllBLD = (PercentLinearLayout) findViewById(R.id.pllBLD);
        ivBLDLeft = (ImageView) findViewById(R.id.ivBLDLeft);
        ivBLDCenter = (ImageView) findViewById(R.id.ivBLDCenter);
        ivBLDRight = (ImageView) findViewById(R.id.ivBLDRight);
        tvBLDState = (TextView) findViewById(R.id.tvBLDState);
        pllPH = (PercentLinearLayout) findViewById(R.id.pllPH);
        sbPh = (SeekBar) findViewById(R.id.sbPh);
        tvPHState = (TextView) findViewById(R.id.tvPHState);
        pllRBC = (PercentLinearLayout) findViewById(R.id.pllRBC);
        ivRBCLeft = (ImageView) findViewById(R.id.ivRBCLeft);
        ivRBCCenter = (ImageView) findViewById(R.id.ivRBCCenter);
        ivRBCRight = (ImageView) findViewById(R.id.ivRBCRight);
        tvRBCState = (TextView) findViewById(R.id.tvRBCState);
        pllPRO = (PercentLinearLayout) findViewById(R.id.pllPRO);
        ivPROLeft = (ImageView) findViewById(R.id.ivPROLeft);
        ivPROCenter = (ImageView) findViewById(R.id.ivPROCenter);
        ivPRORight = (ImageView) findViewById(R.id.ivPRORight);
        tvPROState = (TextView) findViewById(R.id.tvPROState);
        pllUBG = (PercentLinearLayout) findViewById(R.id.pllUBG);
        ivUBGLeft = (ImageView) findViewById(R.id.ivUBGLeft);
        ivUBGCenter = (ImageView) findViewById(R.id.ivUBGCenter);
        ivUBGRight = (ImageView) findViewById(R.id.ivUBGRight);
        tvUBGState = (TextView) findViewById(R.id.tvUBGState);
        pllNIT = (PercentLinearLayout) findViewById(R.id.pllNIT);
        ivNITCenter = (ImageView) findViewById(R.id.ivNITCenter);
        ivNITRight = (ImageView) findViewById(R.id.ivNITRight);
        tvNITState = (TextView) findViewById(R.id.tvNITState);
        pllLEU = (PercentLinearLayout) findViewById(R.id.pllLEU);
        ivLEULeft = (ImageView) findViewById(R.id.ivLEULeft);
        ivLEUCenter = (ImageView) findViewById(R.id.ivLEUCenter);
        ivLEURight = (ImageView) findViewById(R.id.ivLEURight);
        tvLEUState = (TextView) findViewById(R.id.tvLEUState);
        tvUrinalysisType = (TextView) findViewById(R.id.tvUrinalysisType);
        tvUrinalysisScore = (TextView) findViewById(R.id.tvUrinalysisScore);
        tvUrinalysisTime = (TextView) findViewById(R.id.tvUrinalysisTime);

        sbPh.setEnabled(false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initData() {

    }

    private void initListener() {
        ivRight.setOnClickListener(this);
        this.pllLEU.setOnClickListener(this);
        this.pllNIT.setOnClickListener(this);
        this.pllUBG.setOnClickListener(this);
        this.pllPRO.setOnClickListener(this);
        this.pllRBC.setOnClickListener(this);
        this.pllPH.setOnClickListener(this);
        this.pllBLD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRight:
                startActivity(new Intent(UrinalysisListeningActivity.this, UrinalysisHistoryActivity.class));
                break;
            case R.id.pllLEU:
                toastUtils.showInfoWithStatus("白细胞");
                break;
            case R.id.pllNIT:
                toastUtils.showInfoWithStatus("亚硝酸盐");
                break;
            case R.id.pllUBG:
                toastUtils.showInfoWithStatus("尿胆原");
                break;
            case R.id.pllPRO:
                toastUtils.showInfoWithStatus("蛋白质");
                break;
            case R.id.pllRBC:
                toastUtils.showInfoWithStatus("红细胞");
                break;
            case R.id.pllPH:
                toastUtils.showInfoWithStatus("PH值");
                break;
            case R.id.pllBLD:
                toastUtils.showInfoWithStatus("潜血");
                break;
        }
    }


}
