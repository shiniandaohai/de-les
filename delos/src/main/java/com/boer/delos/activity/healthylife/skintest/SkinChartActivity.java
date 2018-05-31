package com.boer.delos.activity.healthylife.skintest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.pressure.BloodPressureConnActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.SkinDetailChart;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.ShowYearMonthPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/19.
 */
public class SkinChartActivity extends CommonBaseActivity {
    @Bind(R.id.ctv_time)
    CheckedTextView ctvTime;
    @Bind(R.id.ll_anchor)
    LinearLayout llAnchor;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.btn_reset)
    Button btnReset;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.titlebar)
    View titlebar;

    private ShowYearMonthPopupWindow showYearMonthPopupWindow;

    private long fromTime;
    private long toTime;
    private int year;
    private int month;
    private List<SkinDetailChart> sKinAreaDetails;
    private String[] CONTENT;
    FragmentPagerAdapter adapter;
    private User mUser;


    @Override
    protected int initLayout() {

        return R.layout.activity_skin_chart;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setVisibility(View.GONE);

        mUser = (User) getIntent().getSerializableExtra("user");

        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_SKIN);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void initData() {

        sKinAreaDetails = new ArrayList<>();
        CONTENT = getResources().getStringArray(R.array.listSkin);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        ctvTime.setText(year + getString(R.string.pick_year) + month + getString(R.string.pick_month));
        getTime(year, month);


        queryMonthData(fromTime + "", toTime + "", HealthController.HEATHY_SKIN);


        showYearMonthPopupWindow = new ShowYearMonthPopupWindow(this, titlebar);
        showYearMonthPopupWindow.setShowTimePopupWindowInterface(new ShowYearMonthPopupWindow.ShowTimePopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String startTime, String endTime) {
                year = Integer.parseInt(startTime);
                month = Integer.parseInt(endTime);
                int tempMonth=Calendar.getInstance().get(Calendar.MONTH);
                if(tempMonth+1<month){
                    ToastHelper.showShortMsg("不能选择未来的时间");
                    return;
                }

                ctvTime.setText(startTime + getString(R.string.pick_year) + endTime + getString(R.string.pick_month));

                getTime(year, month);

                queryMonthData(fromTime + "", toTime + "", HealthController.HEATHY_SKIN);

            }
        });


    }


    private void getTime(int y, int m) {

        fromTime = TimeUtil.getTargetTimeStamp(y, m, 1, 0, 0, 0);
        int daysOfMonth = TimeUtil.getDaysByYearMonth(y, m);
        toTime = TimeUtil.getTargetTimeStamp(y, m, daysOfMonth, 23, 59, 59);

        Log.v("gl", TimeUtil.formatStamp2Time(fromTime, TimeUtil.FORMAT_DATE_TIME2));
        Log.v("gl", TimeUtil.formatStamp2Time(toTime, TimeUtil.FORMAT_DATE_TIME2));


    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void initAction() {

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.v("gl", "onPageSelected====" + position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    private void queryMonthData(String fromTime, final String toTime, String healthyType) {
        sKinAreaDetails.clear();
        toastUtils.showProgress("");
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime,
                healthyType, Constant.LOGIN_USER.getId(), new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {

                            toastUtils.dismiss();

                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject bean = data.getJSONObject(i);

                                String detail = bean.getString("detail");

                                String measuretime = bean.getString("measuretime");

                                SkinDetailChart skinarea = new Gson().fromJson(detail, new TypeToken<SkinDetailChart>() {
                                }.getType());

                                skinarea.setMeasuretime(measuretime);

                                Log.v("gl", skinarea.getMeasuretime() + "==============" + skinarea.getSkin_brow().getWater());

                                sKinAreaDetails.add(skinarea);

                            }

                            adapter = new GoogleMusicAdapter(getSupportFragmentManager());
                            pager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(pager, true);
                            pager.setOffscreenPageLimit(6);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                    }
                });
    }


    @OnClick({R.id.iv_back, R.id.btn_reset, R.id.ctv_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_reset:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {
//                    startActivityForResult(new Intent(this, SkinWaterAndOilActivity.class),1000);
                    startActivity(new Intent(this, SkinTestActivity.class));
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }

                break;
            case R.id.ctv_time:

                showYearMonthPopupWindow.showPopupWindow();

                break;
        }
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TestFragment testFragment = new TestFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            bundle.putSerializable("list", (Serializable) sKinAreaDetails);
            testFragment.setArguments(bundle);
            return testFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==1000){
                queryMonthData(fromTime + "", toTime + "", HealthController.HEATHY_SKIN);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private LocalBroadcastManager mLocalBroadcastManager;
    public static String ACTION_SKIN="com.boer.delos.SkinChartActivity.skin";
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_SKIN)){
                queryMonthData(fromTime + "", toTime + "", HealthController.HEATHY_SKIN);
            }
        }
    };
}
