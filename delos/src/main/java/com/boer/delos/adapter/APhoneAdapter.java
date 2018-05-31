package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allen.expandablelistview.BaseSwipeMenuExpandableListAdapter;
import com.baoyz.swipemenulistview.BaseSwipeListAdapter;
import com.baoyz.swipemenulistview.ContentViewWrapper;
import com.baoyz.swipemenulistview.SwipeMenuAdapter;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.boer.delos.R;

import java.util.List;

/**
 * Created by gaolong on 2017/4/28.
 */
public class APhoneAdapter  extends BaseSwipeListAdapter {
    private Context context;
    private List<String> phoneList;
      public APhoneAdapter(Context context,List<String> phonelist){
            this.context = context;
            this.phoneList = phonelist;
        }


    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarmphone_swipelistview, null);
            viewHolder = new ViewHolder();
            viewHolder.phonetv = (TextView)convertView.findViewById(R.id.tv_phone);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.phonetv.setText(phoneList.get(position));
        return convertView;
    }

    @Override
    public ContentViewWrapper getViewAndReusable(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarmphone_swipelistview, null);
            viewHolder = new ViewHolder();
            viewHolder.phonetv = (TextView)convertView.findViewById(R.id.tv_phone);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.phonetv.setText(phoneList.get(position));
        return new ContentViewWrapper(convertView,true);
    }

    class ViewHolder{
        TextView phonetv;
    }
}
