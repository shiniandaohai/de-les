package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.GatewayInfo;

import java.util.List;

/**
 * Created by zhukang on 16/7/22.
 */
public class GatewayAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private Context context;
    private List<GatewayInfo> list;

    public GatewayAdapter(Context context, List<GatewayInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_select_phone, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();

        GatewayInfo gatewayInfo =(GatewayInfo)getItem(i);
        holder.tvItem.setText(gatewayInfo.getHostName() + "-" + gatewayInfo.getHostId());
        return view;
    }

    class ViewHolder {
        public TextView tvItem;

        public ViewHolder(View view) {
            this.tvItem = (TextView) view.findViewById(R.id.tvItem);
        }
    }
}
