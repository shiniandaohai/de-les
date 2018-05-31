package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.ToastUtils;

/**
 * @author PengJiYang
 * @Description: 投影仪列表adapter
 * create at 2016/6/1 15:34
 *
 */
public class ProjectorListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;

    public ProjectorListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_projector_list, null);
            holder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            holder.ivLeft = (ImageView) convertView.findViewById(R.id.ivLeft);
            holder.ivCenter = (ImageView) convertView.findViewById(R.id.ivCenter);
            holder.ivRight = (ImageView) convertView.findViewById(R.id.ivRight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastUtils(context).showInfoWithStatus("video");
            }
        });
        holder.ivCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastUtils(context).showInfoWithStatus("power");
            }
        });
        holder.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ToastUtils(context).showInfoWithStatus("computer");
            }
        });
        // TODO 设置数据
        return convertView;
    }

    class ViewHolder {
        public TextView tvItemName;
        public ImageView ivLeft, ivCenter, ivRight;
    }
}
