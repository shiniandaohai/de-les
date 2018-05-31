package com.boer.delos.activity.main.adv;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangkai
 * @Description: 图片预览
 * create at 2016/1/25 10:08
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {
    private List<String> picList = new ArrayList<>();
    private int width;

    public SlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return SlidePageFragment.newInstance(picList.get(i));
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    public void addAll(List<String> picList) {
        this.picList = picList;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
