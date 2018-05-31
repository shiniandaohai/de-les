package com.boer.delos.activity.main.security;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.MonitorVideoPagerAdapter;
import com.boer.delos.utils.ScreenUtils;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "监控快照/录像记录"界面
 * create at 2016/3/31 11:02
 *
 */
public class MonitorVideoActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView ivBackArrow;
    private PercentLinearLayout llCameraListCenter;
    private TextView tvMonitorPicture, tvVideoRecord, tvCameraListSelectBtn, tvCameraListCancelBtn;
    private ViewPager vpMonitorVideoPager;
    private MonitorVideoPagerAdapter adapter;

    private List<Fragment> fragments = new ArrayList<>();
    MonitorPictureFragment monitorPictureFragment;
    RecordHistoryFragment recordHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_video);
        View vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
        } else {
            vTitle.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
        initViewPager();
    }

    private void initView() {
        this.ivBackArrow = (ImageView) findViewById(R.id.ivBackArrow);
        this.llCameraListCenter = (PercentLinearLayout) findViewById(R.id.llCameraListCenter);
        this.tvMonitorPicture = (TextView) findViewById(R.id.tvMonitorPicture);
        this.tvVideoRecord = (TextView) findViewById(R.id.tvVideoRecord);
        this.tvCameraListSelectBtn = (TextView) findViewById(R.id.tvCameraListSelectBtn);
        this.tvCameraListCancelBtn = (TextView) findViewById(R.id.tvCameraListCancelBtn);
        this.vpMonitorVideoPager = (ViewPager) findViewById(R.id.vpMonitorVideoPager);

        this.ivBackArrow.setOnClickListener(this);
        this.tvMonitorPicture.setOnClickListener(this);
        this.tvVideoRecord.setOnClickListener(this);
        this.tvCameraListSelectBtn.setOnClickListener(this);
        this.tvCameraListCancelBtn.setOnClickListener(this);
    }

    private void initViewPager() {
        monitorPictureFragment = new MonitorPictureFragment();
        recordHistoryFragment = new RecordHistoryFragment();
        fragments.add(monitorPictureFragment);
        fragments.add(recordHistoryFragment);
        adapter = new MonitorVideoPagerAdapter(this.getSupportFragmentManager(), fragments);
        this.vpMonitorVideoPager.setAdapter(adapter);
        this.vpMonitorVideoPager.setCurrentItem(1);
        this.vpMonitorVideoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        // 设置头部中间的按钮
                        tvMonitorPicture.setTextColor(getResources().getColor(R.color.blue_text));
                        tvVideoRecord.setTextColor(Color.WHITE);
                        llCameraListCenter.setBackground(getResources().getDrawable(R.drawable.ic_video_record_left));

                        // 设置头部右侧的按钮
                        tvCameraListSelectBtn.setVisibility(View.VISIBLE);
                        tvCameraListCancelBtn.setVisibility(View.GONE);
                        monitorPictureFragment.setPlay();
                        break;
                    case 1:
                        // 设置头部中间的按钮
                        tvMonitorPicture.setTextColor(Color.WHITE);
                        tvVideoRecord.setTextColor(getResources().getColor(R.color.blue_text));
                        llCameraListCenter.setBackground(getResources().getDrawable(R.drawable.ic_video_record_right));

                        // 设置头部右侧的按钮
                        tvCameraListSelectBtn.setVisibility(View.VISIBLE);
                        tvCameraListCancelBtn.setVisibility(View.GONE);
                        recordHistoryFragment.setPlay();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBackArrow:
                finish();
                break;
            case R.id.tvMonitorPicture:
                tvMonitorPicture.setTextColor(getResources().getColor(R.color.blue_text));
                tvVideoRecord.setTextColor(Color.WHITE);
                llCameraListCenter.setBackground(getResources().getDrawable(R.drawable.ic_video_record_left));
                this.vpMonitorVideoPager.setCurrentItem(0);
                tvCameraListSelectBtn.setVisibility(View.VISIBLE);
                tvCameraListCancelBtn.setVisibility(View.GONE);
                monitorPictureFragment.setPlay();
                break;
            case R.id.tvVideoRecord:
                tvMonitorPicture.setTextColor(Color.WHITE);
                tvVideoRecord.setTextColor(getResources().getColor(R.color.blue_text));
                llCameraListCenter.setBackground(getResources().getDrawable(R.drawable.ic_video_record_right));
                this.vpMonitorVideoPager.setCurrentItem(1);
                tvCameraListSelectBtn.setVisibility(View.VISIBLE);
                tvCameraListCancelBtn.setVisibility(View.GONE);
                recordHistoryFragment.setPlay();
                break;
            case R.id.tvCameraListSelectBtn:
                // 标题栏的选择按钮
                tvCameraListSelectBtn.setVisibility(View.GONE);
                tvCameraListCancelBtn.setVisibility(View.VISIBLE);
                select();
                break;
            case R.id.tvCameraListCancelBtn:
                // 标题栏的取消按钮
                tvCameraListSelectBtn.setVisibility(View.VISIBLE);
                tvCameraListCancelBtn.setVisibility(View.GONE);
                cancel();
                break;
        }
    }

    /**
     * 选择
     */
    private void select() {
        switch (vpMonitorVideoPager.getCurrentItem()) {
            case 0:
                monitorPictureFragment.setDelete();
                break;
            case 1:
                recordHistoryFragment.setDelete();
                break;
        }
    }

    /**
     * 取消
     */
    private void cancel() {
        switch (vpMonitorVideoPager.getCurrentItem()) {
            case 0:
                monitorPictureFragment.setPlay();
                break;
            case 1:
                recordHistoryFragment.setPlay();
                break;
        }
    }
}
