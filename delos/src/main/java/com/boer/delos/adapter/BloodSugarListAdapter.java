package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.List;


/**
 * @author XieQingTing
 * @Description: 血糖日期list的adapter
 * create at 2016/5/26 16:32
 */
public class BloodSugarListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
//    private String[] datas;

    public BloodSugarListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<String> list) {
        L.e("BloodSugarListAdapter_datas===" + new Gson().toJson(list));
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_blood_sugar_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tvBloodItemDate = (TextView) convertView.findViewById(R.id.tvBloodItemDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String date = list.get(position);
        viewHolder.tvBloodItemDate.setText(date);
        return convertView;
    }

    class ViewHolder {
        public TextView tvBloodItemDate;
    }

}
