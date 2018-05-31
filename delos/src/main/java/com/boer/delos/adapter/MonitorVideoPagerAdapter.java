package com.boer.delos.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 监控快照和录像记录的ViewPager适配器
 * create at 2016/3/31 11:13
 *
 */
public class MonitorVideoPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> list = new ArrayList<>();

    public MonitorVideoPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }
}
