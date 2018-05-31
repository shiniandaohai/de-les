package com.boer.delos.activity.weather;

import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.fragment.WeatherIndoorFragment;
import com.boer.delos.fragment.WeatherOutdoorFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/10 0010 13:52
 * @Modify:
 * @ModifyDate:
 */


public class WeatherActivity extends CommonBaseActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.iv_point1)
    CheckBox mIvPoint1;
    @Bind(R.id.iv_point2)
    CheckBox mIvPoint2;

    public String weatherStr;
    private List<Fragment> mFragmentList;
    private LocationClient mLocationClient;

    @Override
    protected int initLayout() {
        return R.layout.activity_weather;
    }

    @Override
    protected void initView() {
        statusBarTheme(false, -1);
        tlTitleLayout.setVisibility(View.GONE);

    }


    @Override
    protected void initData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new WeatherOutdoorFragment());
        mFragmentList.add(new WeatherIndoorFragment());

        weatherStr = getIntent().getStringExtra("weather");
        int type=getIntent().getIntExtra("type",0);

        VPAdapter mAdapter = new VPAdapter(getSupportFragmentManager(), mFragmentList);
        mViewpager.setAdapter(mAdapter);
        if(type==1){
            mViewpager.setCurrentItem(1);
            mIvPoint1.setChecked(false);
            mIvPoint2.setChecked(true);
        }
        startLocation();
    }

    //开启定位
    private void startLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener((WeatherOutdoorFragment) mFragmentList.get(0));
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 60 * 10;//十分钟
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    @Override
    protected void initAction() {

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mIvPoint1.setChecked(true);
                        mIvPoint2.setChecked(false);

                        break;
                    case 1:
                        mIvPoint1.setChecked(false);
                        mIvPoint2.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    static class VPAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;

        public VPAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        public VPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient!=null&&mLocationClient.isStarted())
            mLocationClient.stop();
        if (mLocationClient!=null&&mFragmentList.size() != 0)
            mLocationClient.unRegisterLocationListener((WeatherOutdoorFragment) mFragmentList.get(0));
        mLocationClient = null;
    }

}
