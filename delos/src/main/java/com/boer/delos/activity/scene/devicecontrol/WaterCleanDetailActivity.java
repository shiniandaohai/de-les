package com.boer.delos.activity.scene.devicecontrol;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.baidu.location.c.a;
import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.fragment.WaterDiagnosisFragment;
import com.boer.delos.fragment.WaterFilterFragment;
import com.boer.delos.fragment.WaterStatisticsFragment;
import com.boer.delos.fragment.WaterTDSFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.model.Time;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:净水器
 * @CreateDate: 2017/4/12 0012 11:15
 * @Modify:
 * @ModifyDate:
 */


public class WaterCleanDetailActivity extends CommonBaseActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    private List<Fragment> mFragments;
    private int position;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private Timer mTimer;
    private static final int TIMER_PERIOD = 5 * 1000;
    private static final int TIMER_DELAY = 0;
    private List<Addr> mAddrList;
    private List<Time> mTimeList;
    private List<IObjectInterface> mListenerListens;
    private String mMD5;

    @Override
    protected int initLayout() {
        return R.layout.activity_viewpager;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDevice=mDeviceRelate.getDeviceProp();
        position = bundle.getInt("position");

    }

    @Override
    protected void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(WaterTDSFragment.newInstance(mDeviceRelate));
        mFragments.add(WaterStatisticsFragment.newInstance(mDeviceRelate));
        mFragments.add(WaterFilterFragment.newInstance(mDeviceRelate));
        mFragments.add(WaterDiagnosisFragment.newInstance(mDeviceRelate));

        for (Fragment fragment : mFragments) {
            addListener((IObjectInterface) fragment);
        }
        MyViewPagerAdapter myAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewpager.setAdapter(myAdapter);
        mViewpager.setCurrentItem(position);
        mViewpager.setOffscreenPageLimit(4);
        String title = "";
        switch (position) {
            case 0:
                title = getString(R.string.text_TDS);
                break;
            case 1:
                title = getString(R.string.text_water_statistics);
                break;
            case 2:
                title = getString(R.string.text_filter_status);
                break;
            case 3:
                title = getString(R.string.text_diagnose_test);
                break;


        }
        tlTitleLayout.setTitle(title);

        if (mAddrList == null) {
            mAddrList = new ArrayList<>();

            mAddrList.add(new Addr(mDevice.getAddr()));
//            mAddrList.add(new Addr("BF4AF50B004B12000000"));
        }

        lists = new ArrayList<>();
        lists.add(mDevice);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                queryWaterData();
                queryDeviceStatus(lists);
            }
        }, TIMER_DELAY, TIMER_PERIOD);
    }

    @Override
    protected void initAction() {

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String title = "";
                switch (position) {
                    case 0:
                        title = getString(R.string.text_TDS);
                        break;
                    case 1:
                        title = getString(R.string.text_water_statistics);
                        break;
                    case 2:
                        title = getString(R.string.text_filter_status);
                        break;
                    case 3:
                        title = getString(R.string.text_diagnose_test);
                        break;


                }
                tlTitleLayout.setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void queryWaterData() {
        if (mTimeList == null) {
            mTimeList = new ArrayList<>();
        }
        mTimeList.clear();
        int day=30;
        for (int i = 0; i < day; i++) {
            String time = TimeUtil.getTimesnight(-i) + "";
            mTimeList.add(new Time(time));

        }
//        mTimeList.add(new Time("1494307283"));
        DeviceController.getInstance().deviceTableWaterHistoryData(this, mAddrList, mTimeList,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {

                            WaterTDSResult result = GsonUtil.getObject(json, WaterTDSResult.class);
                            if (result.getRet() != 0) {
                                return;
                            }
                            if (!TextUtils.isEmpty(mMD5)
                                    && !TextUtils.isEmpty(result.getMd5())
                                    && result.getMd5().equals(mMD5)) {
                                return;
                            }
                            List<WaterTDSResult.WaterBean> waterBeanList = result.getResponse();

                            for (IObjectInterface<List> fragment : mListenerListens) {
                                if (fragment != null) {
                                    fragment.onClickListenerOK(waterBeanList, -1, "0");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });
    }

    public void addListener(IObjectInterface fragment) {
        if (mListenerListens == null) {
            mListenerListens = new ArrayList<>();
        }
        mListenerListens.add(fragment);
    }

    static class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;

        public MyViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        public MyViewPagerAdapter(FragmentManager fm) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
        mListenerListens.clear();
    }

    private List<Device> lists;
    private DeviceStatus mStatus;
    private void queryDeviceStatus(List<Device> list) {
        DeviceController.getInstance().queryDevicesStatus(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    DeviceStatusResult relateResult = GsonUtil.getObject(json, DeviceStatusResult.class);
                    if (relateResult.getRet() != 0) {
                        return;
                    }
                    mStatus = relateResult.getResponse().getDevices().get(0);
                    /*离线状态*/
                    if (mStatus.getOffline() == 1) {
                        return;
                    }
                    WaterDiagnosisFragment waterDiagnosisFragment=(WaterDiagnosisFragment)mFragments.get(3);
                    if(waterDiagnosisFragment!=null){
                        waterDiagnosisFragment.updateUI(mStatus.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }
}
