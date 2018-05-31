package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Family;

import java.util.List;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class MemberAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Family> families;

    public MemberAdapter(Context context, List<Family> families) {
        this.context = context;
        this.families = families;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return families.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return families.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_health_share, null);
            holder.tvShareItemName = (TextView) convertView.findViewById(R.id.tvShareItemName);
            holder.ivItemChecked = (ImageView) convertView.findViewById(R.id.ivItemChecked);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivItemChecked.setVisibility(View.GONE);
        if(families.get(position)!=null&&families.get(position).getUser()!=null&&!TextUtils.isEmpty(families.get(position).getUser().getName())){
            holder.tvShareItemName.setText(families.get(position).getUser().getName());
            convertView.setVisibility(View.VISIBLE);
        }else{
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tvShareItemName;
        public ImageView ivItemChecked;
    }
}

