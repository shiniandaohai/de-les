package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;

import java.util.List;

/**
 * Created by sunzhibin on 2017/5/25.
 */

public class LightControlAdapter extends MyBaseAdapter<String> {

    public LightControlAdapter(Context mContext, List<String> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);

    }

    @Override
    public void convert(MyViewHolder holder, String item, int position) {
        CheckBox cbLight = holder.getView(R.id.cbLight);
        TextView tv_light_name = holder.getView(R.id.tv_light_name);

        getName(position, tv_light_name);
        cbLight.setChecked(listData.get(position).equals("1"));


    }

    private void getName(int position, TextView textView) {
        switch (position) {
            case 0:
                textView.setText(mContext.getString(R.string.light1_name));
                break;
            case 1:
                textView.setText(mContext.getString(R.string.light2_name));
                break;
            case 2:
                textView.setText(mContext.getString(R.string.light3_name));
                break;
            case 3:
                textView.setText(mContext.getString(R.string.light4_name));
                break;

        }
    }
}
