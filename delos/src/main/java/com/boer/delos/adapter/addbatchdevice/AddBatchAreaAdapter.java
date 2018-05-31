package com.boer.delos.adapter.addbatchdevice;

import android.content.Context;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.model.Area;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/27 0027 14:39
 * @Modify:
 * @ModifyDate:
 */


public class AddBatchAreaAdapter extends MyBaseAdapter<Area> {

    public AddBatchAreaAdapter(Context mContext, List<Area> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
//        R.layout.item_addbatch_popup_area
    }

    @Override
    public void convert(MyViewHolder holder, final Area item, int position) {
        final ImageView iv_edit = holder.getView(R.id.iv_edit);
        EditText tv_area_name = holder.getView(R.id.tv_area_name);
        CheckedTextView ctv_select = holder.getView(R.id.ctv_select);

        ctv_select.setVisibility(ctv_select.isChecked() ? View.GONE : View.VISIBLE);

        tv_area_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                iv_edit.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
            }
        });


    }

}
