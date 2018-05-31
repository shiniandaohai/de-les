package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ACER~ on 2016/6/12.
 */
public class LinkModelChildAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> list;
    private LayoutInflater inflater;
    ViewHolder viewHolder;

    public LinkModelChildAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_link_model_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder(convertView);
        }

        final Map<String, String> map = list.get(position);

        viewHolder.tvItemName.setText(map.get("name"));
        String status = map.get("status");
        if("1".equals(status)){
            viewHolder.tbItemSwitch.setChecked(true);
        }else{
            viewHolder.tbItemSwitch.setChecked(false);
        }

        viewHolder.tbItemSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.tbItemSwitch.isChecked()){
                    map.put("status", "1");
                }else{
                    map.put("status", "0");
                }
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    public interface ClickResultListener {
        void ClickResult(int tag, int position);
    }

    public static class ViewHolder {
        public TextView tvItemName;
        public ToggleButton tbItemSwitch;
        public SeekBar sbLight;

        public ViewHolder(View convertView) {
            this.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            this.tbItemSwitch = (ToggleButton) convertView.findViewById(R.id.tbItemSwitch);
            this.sbLight = (SeekBar) convertView.findViewById(R.id.sbLight);
        }
    }
}
