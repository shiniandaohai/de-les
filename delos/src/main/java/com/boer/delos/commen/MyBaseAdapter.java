package com.boer.delos.commen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2016/12/15 0015 08:38
 * @Modify:
 * @ModifyDate:
 */


public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> listData;
    protected int mItemLayoutId;

    public MyBaseAdapter(Context mContext, List<T> listData, int itemLayoutId) {
        this.mContext = mContext;
        this.listData = listData;
        this.mItemLayoutId = itemLayoutId;
    }

    public void setDatas(List<T> listData) {
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder myViewHolder = getViewHolder(position, convertView,
                parent);
        convert(myViewHolder, getItem(position), position);
        return myViewHolder.getConvertView();
    }

    public abstract void convert(MyViewHolder holder, T item, int position);


    private MyViewHolder getViewHolder(int position, View convertView,
                                       ViewGroup parent) {
        return MyViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

}
