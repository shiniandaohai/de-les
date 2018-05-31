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

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 绿色生活界面中日期的适配器
 * create at 2016/3/14 11:11
 *
 */
public class GreenLiveAdapter extends BaseAdapter {

    private List<String> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    public GreenLiveAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
        L.e("GreenLiveAdapter's datas=======" + new Gson().toJson(datas));
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_green_live, null);
            viewHolder.itemText = (TextView) convertView.findViewById(R.id.tvItemText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemText.setText(datas.get(position));

        return convertView;
    }

    class ViewHolder {
        public TextView itemText;
    }
}
