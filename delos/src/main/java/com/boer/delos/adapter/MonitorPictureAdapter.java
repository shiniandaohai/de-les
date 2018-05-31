package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.model.MonitorPicture;
import com.boer.delos.utils.L;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 监控快照adapter
 * create at 2016/3/14 11:12
 *
 */
public class MonitorPictureAdapter extends BaseAdapter {

    private List<MonitorPicture> datas = new ArrayList<>();
    private boolean isSelect = true;
    private LayoutInflater inflater = null;
    private Context context;

    private ViewHolder viewHolder;

    public MonitorPictureAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<MonitorPicture> datas) {
        this.datas = datas;
        L.e("MonitorPictureAdapter's datas=======" + new Gson().toJson(datas));
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
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
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_monitor_picture, null);
            viewHolder.flMonitorPic = (PercentFrameLayout) convertView.findViewById(R.id.flMonitorPic);
            viewHolder.ivMonitorPic = (ImageView) convertView.findViewById(R.id.ivMonitorPic);
            viewHolder.ivMonitorPicChecked = (ImageView) convertView.findViewById(R.id.ivMonitorPicChecked);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MonitorPicture data = datas.get(position);
        viewHolder.ivMonitorPic.setImageResource(data.getImageName());
        if(!isSelect) {
            if(!data.isDelete()) {
                viewHolder.ivMonitorPicChecked.setVisibility(View.GONE);
            } else {
                viewHolder.ivMonitorPicChecked.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.ivMonitorPicChecked.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        public PercentFrameLayout flMonitorPic;
        public ImageView ivMonitorPic, ivMonitorPicChecked;
    }
}
