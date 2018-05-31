package com.boer.delos.activity.healthylife.weight;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.WeightBean;
import com.boer.delos.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by apple on 17/5/15.
 */

public class WeightMajorInfoActivity extends CommonBaseActivity {
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.vp_device)
    ViewPager mViewpager;
    private WeightBean mWeightBean;
    private WeightBean.WeightDetailBean mDetailBean;
    private ViewPagerAdapter mVpAdapter;
    private List<Fragment> fragmentList;
    private List<String> mTitleList;

    @Override
    protected int initLayout() {
        return R.layout.activity_weight_major_info;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_major_info));

        mWeightBean = (WeightBean) getIntent().getSerializableExtra("weight");

        if (mWeightBean != null && !TextUtils.isEmpty(mWeightBean.getDetail())) {
            mDetailBean = GsonUtil.getObject(mWeightBean.getDetail(),
                    WeightBean.WeightDetailBean.class);

        }

        if (mWeightBean == null) {
            mWeightBean = new WeightBean();
            mWeightBean.setFatrate(0f);
            mWeightBean.setWeight(0f);
            mWeightBean.setMeasuretime(0);


        }
        if (mDetailBean == null) {
            mDetailBean = new WeightBean.WeightDetailBean();
            mDetailBean.setBMI("0");
            mDetailBean.setBone("0");
            mDetailBean.setKcal("0");
            mDetailBean.setMuscle("0");
            mDetailBean.setWater("0");
            mDetailBean.setBMI("0");
        }
    }

    @Override
    protected void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(FragmentFatRate.newInstance(mWeightBean.getFatrate() + "", "fatrate"));
        fragmentList.add(FragmentFatRate.newInstance(mDetailBean.getBone(), "bone"));
        fragmentList.add(FragmentFatRate.newInstance(mDetailBean.getMuscle(), "muscle"));
        fragmentList.add(FragmentFatRate.newInstance(mDetailBean.getWater(), "water"));
        fragmentList.add(FragmentFatRate.newInstance(mDetailBean.getKcal(), "bmr"));
        fragmentList.add(FragmentFatRate.newInstance(mDetailBean.getBMI(), "bmi"));

        mTitleList = new ArrayList<>();

        mTitleList.add(getString(R.string.text_fat_rate));
        mTitleList.add(getString(R.string.text_bone));
        mTitleList.add(getString(R.string.text_muscle_rate));
        mTitleList.add(getString(R.string.text_water));
        mTitleList.add(getString(R.string.text_BMR));
        mTitleList.add(getString(R.string.text_BMI));

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
        mVpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, fragmentList, mTitleList);
        mViewpager.setAdapter(mVpAdapter);
        mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。

    }

    @Override
    protected void initAction() {

    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentLists;
        private List<String> listTitles;
        private Context mContext;

        public ViewPagerAdapter(FragmentManager fm, Context mContext, List<Fragment> fragmentLists,
                                List<String> listTitles) {
            super(fm);
            this.mContext = mContext;
            this.fragmentLists = fragmentLists;
            this.listTitles = listTitles;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentLists.get(i);
        }

        @Override
        public int getCount() {
            return fragmentLists.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return listTitles.get(position);
        }

        public void setFragmentLists(List<Fragment> fragmentLists) {
            this.fragmentLists = fragmentLists;
        }

        public void setListTitles(List<String> listTitles) {
            this.listTitles = listTitles;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
