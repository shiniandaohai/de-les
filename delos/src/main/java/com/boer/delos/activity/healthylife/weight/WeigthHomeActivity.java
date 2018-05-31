package com.boer.delos.activity.healthylife.weight;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.pressure.BloodPressureInputActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.fragment.WeightFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Time;
import com.boer.delos.model.User;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/20 0020 10:20
 * @Modify:
 * @ModifyDate:
 */


public class WeigthHomeActivity extends CommonBaseActivity {
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(R.id.iv_point1)
    CheckBox mIvPoint1;
    @Bind(R.id.iv_point2)
    CheckBox mIvPoint2;

    @Bind(R.id.tv_muscle)
    TextView mTvMuscle;
    @Bind(R.id.tv_water)
    TextView mTvWater;
    @Bind(R.id.tv_bone)
    TextView mTvBone;
    @Bind(R.id.tv_bmr)
    TextView mTvBmr;
    @Bind(R.id.tv_bmi)
    TextView mTvBmi;
    @Bind(R.id.tv_fat_rate)
    TextView mTvFateRate;
    @Bind(R.id.tv_weight)
    TextView mTvWeight;

    @Bind(R.id.btn_auto_measure)
    Button mBtnAutoMeasure;
    @Bind(R.id.btn_hand_input)
    Button mBtnHandInput;
    @Bind(R.id.btn_history_record)
    Button mBtnHistoryRecord;
    @Bind(R.id.btn_major_info)
    Button mBtnMajorInfo;

    private List<WeightBean> mWeightList;
    private User mUser;

    private List<String> valsX1;
    private List<String> valsX2;
    private List<Float> valsY1;
    private List<Float> valsY2;

    private List<IObjectInterface> mListenerList;
    private IObjectInterface<List<WeightBean>> mListener;
    private List<Fragment> mFragmentList;

    public void addLlistener(IObjectInterface<List<WeightBean>> listListener) {
        mListenerList.add(listListener);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_weight_home;
    }

    @Override
    protected void initView() {
//        tlTitleLayout.setRightText(getString(R.string.text_weight));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }
        tlTitleLayout.setTitle(getString(R.string.health_weight));
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryRecentHealth(mUser.getId(), HealthController.HEATHY_WEIGHT, "7");
    }

    protected void initData() {
        mListenerList = new ArrayList<>();
        mWeightList = new ArrayList<>();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(WeightFragment.newInstance(0));
        mFragmentList.add(WeightFragment.newInstance(1));
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(myPagerAdapter);

    }

    @Override
    protected void initAction() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    @OnClick({R.id.iv_point1, R.id.iv_point2, R.id.btn_auto_measure, R.id.btn_hand_input,
            R.id.btn_history_record, R.id.btn_major_info})
    public void onClick(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.getSerializable("user");
        intent.putExtras(bundle);
        switch (view.getId()) {
            case R.id.iv_point1:
                mIvPoint1.setChecked(true);
                mIvPoint2.setChecked(false);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.iv_point2:
                mIvPoint1.setChecked(false);
                mIvPoint2.setChecked(true);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.btn_auto_measure:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {
                    intent.setClass(this, ScaleConnActivity.class);
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }

                startActivity(intent);
                break;
            case R.id.btn_hand_input:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {
                    intent.setClass(this, WeightInputActivity.class);
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }
                startActivity(intent);

                break;
            case R.id.btn_history_record:
                intent.setClass(this, WeightRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_major_info:
                if (mWeightList != null && mWeightList.size() != 0) {
                    intent.putExtra("weight", mWeightList.get(0));
                }

                intent.setClass(this, WeightMajorInfoActivity.class);
//                intent.setClass(this,CurrentWeightActivity.class);
                startActivity(intent);

                break;


        }
    }

    /**
     * 请求健康数据
     */
    private void queryRecentHealth(String userId, final String healthyType, String recent) {
        Calendar c = Calendar.getInstance();
        long millTime = c.getTimeInMillis() / 1000;
        HealthController.getInstance().queryRecentHealth(this, millTime + "", healthyType, recent, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i(Json);
                try {
                    BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showInfoWithStatus(getString(R.string.toast_query_data_failure));
                        return;
                    }

                    WeightResult Weight = new Gson().fromJson(Json, WeightResult.class);
                    mWeightList.clear();
                    mWeightList.addAll(Weight.getData());

//                    initChartData(mWeightList);

                    int index = 0;
                    for (IObjectInterface i : mListenerList) {
                        i.onClickListenerOK(mWeightList, index, null);
                        index++;
                    }
                    updateUI(mWeightList);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String Json) {
                L.i(Json);
            }
        });
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> lists;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> lists) {
            super(fm);
            this.lists = lists;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

    }

    private void updateUI(List<WeightBean> weightLists) {

        if (weightLists == null || weightLists.size() == 0) {
            return;
        }
        WeightBean weightBean = weightLists.get(0);
        if (weightBean == null) {
            return;
        }
        String time = TimeUtil.formatStamp2Time(weightBean.getMeasuretime(), "yyyy/MM/dd HH:mm");
        mTvDate.setText(time);
        mTvFateRate.setText(" " + weightBean.getFatrate() + " %");
        mTvWeight.setText(" " + weightBean.getWeight() + " kg");
        WeightBean.WeightDetailBean detailBean = GsonUtil.getObject(weightBean.getDetail(), WeightBean.WeightDetailBean.class);
        if (detailBean == null) {
            return;
        }
        try {
            float bmi = Float.valueOf(detailBean.getBMI());
            DealWithValue2.judgeWeightColor(this, mTvState, bmi+"", false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mTvMuscle.setText(detailBean.getMuscle() + " %");
        mTvWater.setText(detailBean.getWater() + " %");
        mTvBone.setText(detailBean.getBone() + " %");
        mTvBmr.setText(detailBean.getKcal() + " Kcal");
        mTvBmi.setText(detailBean.getBMI() + " kg/m³");

    }
//    private void initChartData(List<WeightBean> weightLists) {
//        valsX1.clear();
//        valsX2.clear();
//        valsY1.clear();
//        valsY2.clear();
//        if (weightLists == null || weightLists.size() == 0) {
//            mTvDate.setText("");
//
//            MPandroidChartHelper.setBarChartData(mChart1, valsX1, valsY1);
//            MPandroidChartHelper.setBarChartData(mChart2, valsX2, valsY2);
//            calculateData(null);
//            return;
//        }
//        for (WeightBean bean : weightLists) {
//            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "MM.dd");
//            valsX1.add(time);
//            valsX2.add(time);
//
//            valsY1.add(bean.getWeight());
//            valsY2.add(bean.getFatrate());
//
//            if (weightLists.indexOf(bean) == weightLists.size() - 1) {
//                String time1 = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy-MM-dd");
//                mTvTime1.setText(time1);
//                mTvTime2.setText(time1);
//
//                mTvNum1.setText(String.valueOf(bean.getWeight()));
//                mTvNum2.setText(String.valueOf(bean.getFatrate()));
//            }
//        }
//
//        MPandroidChartHelper.setBarChartData(mChart1, valsX1, valsY1);
//        MPandroidChartHelper.setBarChartData(mChart2, valsX2, valsY2);
//
//        calculateData(weightLists.get(weightLists.size() - 1).getDetail());
//
//    }

}
