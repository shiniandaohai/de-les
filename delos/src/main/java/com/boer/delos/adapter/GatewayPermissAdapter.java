package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/8/2.
 */
public class GatewayPermissAdapter extends BaseAdapter {

    private final List<Map<String, String>> list;
    private final Context context;
    private final LayoutInflater inflater;

    public GatewayPermissAdapter(Context context, List<Map<String, String>> list){
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = inflater.inflate(R.layout.item_gateway, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();

        Map<String, String> map = (Map<String, String>) getItem(i);
        viewHolder.tvTitle.setText(map.get("hostName"));
        if("0".equals(map.get("checked"))){
            viewHolder.ivChecked.setVisibility(View.GONE);
        }else{
            viewHolder.ivChecked.setVisibility(View.VISIBLE);
        }

        return view;
    }

    class ViewHolder{
        TextView tvTitle;
        ImageView ivChecked;

        public ViewHolder(View view) {
            tvTitle = (TextView)view.findViewById(R.id.tvItemText);
            ivChecked = (ImageView)view.findViewById(R.id.ivSelect);
        }
    }
}
