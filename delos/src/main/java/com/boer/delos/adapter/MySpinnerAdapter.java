package com.boer.delos.adapter;

import android.content.Context;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 扫描设备
 * @CreateDate: 2017/2/16 0016 14:01
 * @Modify:
 * @ModifyDate:
 */


public class MySpinnerAdapter extends MyBaseAdapter<String> {

    public MySpinnerAdapter(Context mContext, List<String> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
    }

    @Override
    public void convert(MyViewHolder holder, String item, int position) {
        TextView textView = holder.getView(R.id.tvChildName);
        textView.setText(item);
    }
}
