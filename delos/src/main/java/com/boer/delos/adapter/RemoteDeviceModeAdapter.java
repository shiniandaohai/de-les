package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.dao.entity.DeviceModelEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 遥控器设备类型Adapter
 * Created by dell on 2016/7/14.
 */
public class RemoteDeviceModeAdapter extends BaseAdapter {

    private List<DeviceModelEntity> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    public RemoteDeviceModeAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }
    public RemoteDeviceModeAdapter(Context context, List<DeviceModelEntity> datas) {
        inflater = LayoutInflater.from(context);
        this.datas = datas;
    }
    public void setDatas(List<DeviceModelEntity> datas) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_rc_deveice_mode, null);
            viewHolder.tvDeviceModeName = (TextView) convertView.findViewById(R.id.tvDeviceModeName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvDeviceModeName.setText(datas.get(position).getmBrandanModel());

        return convertView;
    }

    class ViewHolder {
        TextView tvDeviceModeName;
    }
}


