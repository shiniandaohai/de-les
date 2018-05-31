package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.AirClean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author gaolong
 */
public class SkinControlAdapter extends BaseAdapter {


    private List<AirClean> datas;
    private Context context;

    public SkinControlAdapter(Context context, List<AirClean> list) {
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

        if(TextUtils.isEmpty(airClean.getName())){
            viewHolder.llItem.setVisibility(View.GONE);
        }
        else{
            viewHolder.llItem.setVisibility(View.VISIBLE);
        }

        viewHolder.tvName.setText(airClean.getName());


        if (airClean.isCheck()) {
            viewHolder.ivDevice.setImageResource(airClean.getResSelector());
        } else {
            viewHolder.ivDevice.setImageResource(airClean.getRes());
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.iv_device)
        ImageView ivDevice;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.ll_item)
        LinearLayout llItem;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
