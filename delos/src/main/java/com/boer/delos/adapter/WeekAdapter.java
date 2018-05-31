package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/8/19.
 */
public class WeekAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map<String, Object>> weekList;

    public WeekAdapter(Context context, List<Map<String, Object>> weekList) {
        this.context = context;
        this.weekList = weekList;
    }

    @Override
    public int getCount() {
        return weekList.size();
    }

    @Override
    public Object getItem(int i) {
        return weekList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_mode_week, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder =(ViewHolder)convertView.getTag();

        Map<String, Object> map = (Map<String, Object>)getItem(i);

        holder.textView.setText((String)map.get("name"));
        boolean checked = (boolean)map.get("checked");
        holder.imageView.setVisibility(checked ? View.VISIBLE : View.GONE);

        return convertView;
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;

        ViewHolder(View view){
            this.textView = (TextView) view.findViewById(R.id.tv2);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}
