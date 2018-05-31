package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.utils.StringUtil;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/30 0030 17:16
 * @Modify:delos
 * @ModifyDate:
 */

public class BindingGatewayAdapter extends MyBaseAdapter<GatewayInfo> {

    public BindingGatewayAdapter(Context mContext, List<GatewayInfo> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int layoutId = R.layout.item_gateway_binding;
    }


    @Override
    public void convert(MyViewHolder holder, GatewayInfo item, int position) {

        TextView tvGatewayName = holder.getView(R.id.tv_gateway_name);
        String name = item.getHostName();
        if (TextUtils.isEmpty(name)) {
            name = item.getHostId();
        }
        tvGatewayName.setText(name);

    }
}
