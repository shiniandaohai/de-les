package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/7/29.
 */
public class RoomModeAdapter extends BaseAdapter {


    private final List<Map<String, Object>> list;
    private final Context context;
    private final LayoutInflater inflater;

    public RoomModeAdapter(Context context, List<Map<String, Object>> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
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
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_mode_room, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder(convertView);
        }

        Map<String, Object> map = list.get(i);
//        viewHolder.ivImage.setBackgroundResource((Integer) map.get("bg"));
        viewHolder.ivImage.setImageResource((Integer) map.get("image"));
        viewHolder.tvText.setText((String) map.get("text"));

        return convertView;
    }


    class ViewHolder{
        public LinearLayout llbg;
        public ImageView ivImage;
        public TextView tvText;

        public ViewHolder(View convertView) {
            this.llbg = (LinearLayout) convertView.findViewById(R.id.llbg);
            this.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            this.tvText = (TextView) convertView.findViewById(R.id.tvText);
        }
    }


}
