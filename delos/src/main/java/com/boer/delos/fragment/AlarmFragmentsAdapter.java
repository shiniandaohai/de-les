package com.boer.delos.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/1/4 0004.
 *告警信息的Fragment管理器，用于ViewPager显示
 */
public class AlarmFragmentsAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public AlarmFragmentsAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);

        mFragments=fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFragments.size();
    }


}
