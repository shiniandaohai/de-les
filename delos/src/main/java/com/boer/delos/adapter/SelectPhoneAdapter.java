package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家人申请登录界面，选择管理员手机号adapter
 * create at 2016/6/3 11:30
 *
 */
public class SelectPhoneAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> phoneList;
    private int selectedPosition = 0;

    public SelectPhoneAdapter(Context context, List<String> phoneList) {
        this.context = context;
        this.phoneList = phoneList;
        inflater = LayoutInflater.from(context);
    }

    public void setSelectedPosition(int num) {
        selectedPosition = num;
    }

    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return phoneList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_select_phone, null);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == selectedPosition) {
            holder.tvItem.setTextColor(Color.parseColor("#007AFF"));
        } else {
            holder.tvItem.setTextColor(Color.parseColor("#333333"));
        }
        holder.tvItem.setText(phoneList.get(position));
        return convertView;
    }

    class ViewHolder {
        public TextView tvItem;
    }
}
