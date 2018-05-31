package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.dao.entity.DeviceBrandEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 遥控器设备类型Adapter 具体型号
 * Created by dell on 2016/7/14.
 */
public class RemoteDeviceModeDetailAdapter extends BaseAdapter {

    private List<DeviceBrandEntity> datas = new ArrayList<>();
    private LayoutInflater inflater = null;
    private List<Integer> flagList;

    public RemoteDeviceModeDetailAdapter(Context context, List<DeviceBrandEntity> datas) {
        inflater = LayoutInflater.from(context);
        this.datas = datas;
        flagList = new ArrayList();
        initFlagList(-1);

    }


    public void setDatas(List<DeviceBrandEntity> datas) {
        this.datas = datas;
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.item_rc_deveice_mode, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvDeviceModeName.setText(datas.get(position).getBrandname());
        viewHolder.img_select_model.setVisibility(flagList.get(position) == 1 ? View.VISIBLE : View.GONE);
        viewHolder.img_detail_show.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder {
        TextView tvDeviceModeName;
        ImageView img_select_model;
        ImageView img_detail_show;

        public ViewHolder(View convertView) {

            tvDeviceModeName = (TextView) convertView.findViewById(R.id.tvDeviceModeName);
            img_select_model = (ImageView) convertView.findViewById(R.id.img_select_model);
            img_detail_show = (ImageView) convertView.findViewById(R.id.img_detail_show);
        }
    }

    public void initFlagList(int position) {
        flagList.clear();
        for (int i = 0; i < getCount(); i++) {
            if (position == i) flagList.add(i, 1);
            flagList.add(0);
        }
    }

}


