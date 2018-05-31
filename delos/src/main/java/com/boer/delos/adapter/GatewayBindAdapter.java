package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author delos
 * @Description: "主机绑定"adapter
 * create at 2016/3/14 11:11
 *
 */
public class GatewayBindAdapter extends BaseAdapter {

    private List<GatewayInfo> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    public GatewayBindAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<GatewayInfo> datas) {
        this.datas = datas;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        L.e("GatewayBindAdapter's datas=======" + new Gson().toJson(datas));
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_gateway_bind, null);
            viewHolder.tvHomeName = (TextView) convertView.findViewById(R.id.tvHomeName);
            viewHolder.tvHostName = (TextView) convertView.findViewById(R.id.tvHostName);
            viewHolder.tvBind = (TextView) convertView.findViewById(R.id.tv_bind);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GatewayInfo gatewayInfo = datas.get(position);
        viewHolder.tvHomeName.setText(gatewayInfo.getHostName());
        viewHolder.tvHostName.setText(gatewayInfo.getHostId());
        viewHolder.tvBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnBindClickLisenter!=null){
                    mOnBindClickLisenter.onBindClick(position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView tvHomeName;
        public TextView tvHostName;
        public TextView tvBind;
    }

    public interface OnBindClickLisenter{
        public void onBindClick(int position);
    }

    private OnBindClickLisenter mOnBindClickLisenter;

    public void setOnBindClickLisenter(OnBindClickLisenter l){
        mOnBindClickLisenter=l;
    }
}
