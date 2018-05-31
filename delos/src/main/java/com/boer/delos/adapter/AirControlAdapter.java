package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.AirClean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author gaolong
 */
public class AirControlAdapter extends BaseAdapter {


    private List<AirClean> datas;
    private Context context;

    public AirControlAdapter(Context context, List<AirClean> list) {
        datas = list;
        this.context = context;
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

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.item_air_control, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AirClean airClean = datas.get(position);

        viewHolder.tvName.setText(airClean.getName());


        if (airClean.isCheck()) {
            viewHolder.ivDevice.setImageResource(airClean.getRes());
            GradientDrawable gradientDrawable=(GradientDrawable)viewHolder.ivDevice.getBackground();
            gradientDrawable.setColor(airClean.getResSelector());
        } else {
            viewHolder.ivDevice.setImageResource(airClean.getAirRes());
            GradientDrawable gradientDrawable=(GradientDrawable)viewHolder.ivDevice.getBackground();
            gradientDrawable.setColor(Color.parseColor("#ddded9"));
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.iv_device)
        ImageView ivDevice;
        @Bind(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
