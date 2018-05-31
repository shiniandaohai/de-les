package com.boer.delos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.security.MonitorVideoActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.model.CameraList;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "摄像头列表"adapter
 * create at 2016/3/14 11:09
 *
 */
public class CameraListAdapter extends BaseAdapter {

    private List<CameraList> datas = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public CameraListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<CameraList> datas) {
        this.datas = datas;
        L.e("CameraListAdapter's datas=======" + new Gson().toJson(datas));
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
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_camera_list, null);
            viewHolder.ivCameraList = (ImageView) convertView.findViewById(R.id.ivCameraList);
            viewHolder.tvItemTextCenter = (TextView) convertView.findViewById(R.id.tvItemTextCenter);
            viewHolder.tvItemTextRight = (TextView) convertView.findViewById(R.id.tvItemTextRight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CameraList camera = datas.get(position);
        viewHolder.ivCameraList.setImageResource(camera.getResId());
        viewHolder.tvItemTextCenter.setText(camera.getCenterText());
        viewHolder.tvItemTextRight.setText(camera.getRightText());

        viewHolder.tvItemTextRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.showToast("第" + (position + 1) + "个摄像头的历史记录");
                context.startActivity(new Intent(context, MonitorVideoActivity.class));
            }
        });

        return convertView;
    }

    class ViewHolder {
        public ImageView ivCameraList;
        public TextView tvItemTextCenter, tvItemTextRight;
    }
}
