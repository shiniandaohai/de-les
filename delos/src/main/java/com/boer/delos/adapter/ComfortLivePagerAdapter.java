package com.boer.delos.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "舒适生活"pagerAdapter
 * create at 2016/4/6 11:10
 *
 */
public class ComfortLivePagerAdapter extends PagerAdapter {

    List<View> viewList = new ArrayList<>();

    public ComfortLivePagerAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(viewList.get(position));
        return viewList.get(position);
    }
}
