package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.dao.entity.DeviceTypeEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 遥控器设备类型Adapter
 * Created by dell on 2016/7/14.
 */
public class RemoteDeviceTypeAdapter extends BaseAdapter {

    private List<DeviceTypeEntity> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    private Map<String ,Integer> mDevice2IconMap = new HashMap<String, Integer>();
    {
        mDevice2IconMap.put("空调", new Integer(R.drawable.ic_remote_aircontition));
        mDevice2IconMap.put("电视", new Integer(R.drawable.ic_remote_tv));
        mDevice2IconMap.put("DVD", new Integer(R.drawable.ic_remote_dvd));
        mDevice2IconMap.put("IPTV", new Integer(R.drawable.ic_remote_iptv));
        mDevice2IconMap.put("电风扇", new Integer(R.drawable.ic_remote_fan));
        mDevice2IconMap.put("机顶盒", new Integer(R.drawable.ic_remote_topbox));
        mDevice2IconMap.put("空气净化器", new Integer(R.drawable.ic_remote_airfilter));
        mDevice2IconMap.put("投影仪", new Integer(R.drawable.ic_remote_projector));
    }


    public RemoteDeviceTypeAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }
    public RemoteDeviceTypeAdapter(Context context, List<DeviceTypeEntity> datas) {
        inflater = LayoutInflater.from(context);
        this.datas = datas;
    }
    public void setDatas(List<DeviceTypeEntity> datas) {
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
            convertView = inflater.inflate(R.layout.item_rc_deveice_type, null);
            viewHolder.icDeviceType = (ImageView) convertView.findViewById(R.id.icDeviceType);
            viewHolder.tvDeviceTypeName = (TextView) convertView.findViewById(R.id.tvDeviceTypeName);
            //viewHolder.icRight = (ImageView) convertView.findViewById(R.id.icRight);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String deviceName = datas.get(position).getDevice_name();
        viewHolder.tvDeviceTypeName.setText(deviceName);
        viewHolder.icDeviceType.setImageResource(mDevice2IconMap.get(deviceName).intValue());

        return convertView;
    }

    class ViewHolder {
        ImageView icDeviceType;
        TextView tvDeviceTypeName;
        ImageView icRight;
    }




}


