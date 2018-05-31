package com.boer.delos.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/1 0001 13:48
 * @Modify:
 * @ModifyDate:
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<Fragment> mFragmentList;
    private List<String> titleList;

    public ViewPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragmentList) {
        super(fm);
        mContext = context;
        mFragmentList = fragmentList;
    }

    public ViewPagerAdapter(FragmentManager fm, Context context,
                            List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.mContext = context;
        this.mFragmentList = fragmentList;
        this.titleList = titleList;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /**
         * 每个页面的title
         */
        return titleList.get(position);
    }
}
