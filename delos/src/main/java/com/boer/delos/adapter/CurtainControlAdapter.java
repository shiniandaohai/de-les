package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: 窗帘adapter
 * create at 2016/5/5 16:42
 *
 */
public class CurtainControlAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context context;
    private ViewHolder viewHolder;
    private List<DeviceRelate> list;
    private ClickResultListener listener;

    public CurtainControlAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<DeviceRelate> list,ClickResultListener listener) {
        this.listener=listener;
        this.list = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_curtain_control, null);
            viewHolder.tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            viewHolder.ivItemLeft = (ImageView) convertView.findViewById(R.id.ivItemLeft);
            viewHolder.ivItemCenter = (ImageView) convertView.findViewById(R.id.ivItemCenter);
            viewHolder.ivItemRight = (ImageView) convertView.findViewById(R.id.ivItemRight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device curtain = list.get(position).getDeviceProp();
        viewHolder.tvItemTitle.setText(curtain.getName());

        viewHolder.ivItemLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ClickResult(position, 0);
            }
        });
        viewHolder.ivItemCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ClickResult(position, 1);
            }
        });
        viewHolder.ivItemRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ClickResult(position, 2);
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView tvItemTitle;
        public ImageView ivItemLeft, ivItemCenter, ivItemRight;
    }

    public interface ClickResultListener{
        void ClickResult(int position, int tag);
    }
}
