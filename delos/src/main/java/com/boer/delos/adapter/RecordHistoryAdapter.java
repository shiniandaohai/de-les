package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.HistoryRecord;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "录像记录"adapter
 * create at 2016/3/14 11:13
 *
 */
public class RecordHistoryAdapter extends BaseAdapter {

    private List<HistoryRecord> datas = new ArrayList<>();
    private boolean isSelect = true;
    private LayoutInflater inflater = null;
    private Context context;

    private ViewHolder viewHolder;

    public RecordHistoryAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<HistoryRecord> datas) {
        this.datas = datas;
        L.e("RecordHistoryAdapter's datas=======" + new Gson().toJson(datas));
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
            convertView = inflater.inflate(R.layout.item_record_history, null);
            viewHolder.ivRecordImage = (ImageView) convertView.findViewById(R.id.ivRecordImage);
            viewHolder.ivPlayBtn = (ImageView) convertView.findViewById(R.id.ivPlayBtn);
            viewHolder.ivVideoRecordChecked = (ImageView) convertView.findViewById(R.id.ivVideoRecordChecked);
            viewHolder.tvContentTop = (TextView) convertView.findViewById(R.id.tvContentTop);
            viewHolder.tvContentCenter = (TextView) convertView.findViewById(R.id.tvContentCenter);
            viewHolder.tvContentBottom = (TextView) convertView.findViewById(R.id.tvContentBottom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final HistoryRecord record = datas.get(position);
        viewHolder.ivRecordImage.setImageResource(record.getImageName());
        viewHolder.tvContentTop.setText(record.getTop());
        viewHolder.tvContentCenter.setText("时长：" + record.getCenter());
        viewHolder.tvContentBottom.setText("拍摄时间：" + record.getBottom());
        if(isSelect) {
            viewHolder.ivPlayBtn.setVisibility(View.VISIBLE);
            viewHolder.ivVideoRecordChecked.setVisibility(View.GONE);
        } else {
            viewHolder.ivPlayBtn.setVisibility(View.GONE);
            viewHolder.ivVideoRecordChecked.setVisibility(View.VISIBLE);
            if(!record.isDelete()) {
                viewHolder.ivVideoRecordChecked.setImageResource(R.drawable.ic_video_record_unchecked);
            } else {
                viewHolder.ivVideoRecordChecked.setImageResource(R.drawable.ic_video_record_checked);
            }
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView ivRecordImage, ivPlayBtn,ivVideoRecordChecked;
        public TextView tvContentTop, tvContentCenter, tvContentBottom;
    }
}
