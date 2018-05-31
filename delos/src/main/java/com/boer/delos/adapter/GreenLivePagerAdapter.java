package com.boer.delos.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 绿色生活的ViewPager适配器
 * create at 2016/3/31 11:12
 *
 */
public class GreenLivePagerAdapter extends FragmentPagerAdapter {

    List<Fragment> list = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    public GreenLivePagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
