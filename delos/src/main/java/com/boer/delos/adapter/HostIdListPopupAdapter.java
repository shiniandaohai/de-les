package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.model.Host;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 14:13
 * @Modify:
 * @ModifyDate:
 */

public class HostIdListPopupAdapter extends MyBaseAdapter<Host> {

    public HostIdListPopupAdapter(Context mContext, List<Host> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
    }

    @Override
    public void convert(MyViewHolder holder, Host item, int position) {

        TextView textView = holder.getView(android.R.id.text1);
        textView.setTextColor(mContext.getResources().getColor(R.color.gray_et_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

        String name = item.getName();
        if (TextUtils.isEmpty(name)) {
            name = item.getHostId();
        }

        textView.setText(name);

        final String isOnline = online.get(item.getHostId());
        if (isOnline != null || "1".equals(isOnline)) {
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTextColor(Color.LTGRAY);
        }
    }

    private Map<String, String> online = new HashMap<>();
    public void setOnline(Map<String, String> online) {
        this.online = online;
    }
}
