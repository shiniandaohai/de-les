package com.boer.delos.commen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 使用 搭配SwipeLayout:https://github.com/daimajia/AndroidSwipeLayout
 * @CreateDate: 2017/3/15 0015 17:47
 * @Modify:
 * @ModifyDate:
 */


public abstract class MyExpandableAdapter<T> extends BaseExpandableListAdapter {
    private Context mContext;
    private List<List<T>> mParentDatas;
    private int mGroupInflaterId;
    private int mChildInflaterId;

    public MyExpandableAdapter(Context context, List<List<T>> mParentLists, int mGroupInflaterId, int mChildInflaterId) {
        this.mContext = context;
        this.mParentDatas = mParentLists;
        this.mGroupInflaterId = mGroupInflaterId;
        this.mChildInflaterId = mChildInflaterId;

    }
    @Override
    public int getGroupCount() {
        return mParentDatas != null ? mParentDatas.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mParentDatas.get(groupPosition) != null ? mParentDatas.get(groupPosition).size() : 0;
    }

    @Override
    public List<T> getGroup(int groupPosition) {
        return mParentDatas.get(groupPosition);
    }

    @Override
    public T getChild(int groupPosition, int childPosition) {
        return mParentDatas.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//
        MySwipeLayoutHolder groupViewHolder = MySwipeLayoutHolder.get(mContext, convertView, null, mGroupInflaterId, groupPosition);
        groupConvert(groupViewHolder, getGroup(groupPosition), groupPosition, isExpanded);

        return groupViewHolder.getConvertView();

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MySwipeLayoutHolder childViewHolder = MySwipeLayoutHolder.get(mContext, convertView, null, mChildInflaterId, childPosition);

        childConvert(childViewHolder, getChild(groupPosition, childPosition), groupPosition, childPosition, isLastChild);
        return childViewHolder.getConvertView();

    }

    protected abstract void groupConvert(MySwipeLayoutHolder holder, List<T> item, int position, boolean isExpanded);

    protected abstract void childConvert(MySwipeLayoutHolder holder, T item, int groupPosition, int childPosition, boolean isLastChild);

}

