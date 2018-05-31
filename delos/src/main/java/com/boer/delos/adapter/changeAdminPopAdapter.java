package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Family;
import com.boer.delos.model.User;
import com.boer.delos.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class changeAdminPopAdapter extends BaseAdapter {
    private Context mContext;
    private List<Family> familyList;
    private int currentCheckPos;

    public List<Integer> getIntegerList() {
        return integerList;
    }

    private List<Integer> integerList;

    public changeAdminPopAdapter(Context mContext, List<Family> familyList) {
        this.mContext = mContext;
        this.familyList = familyList;
        integerList = new ArrayList<>();
//        initIntegerList(familyList.size());
        currentCheckPos = AbsListView.INVALID_POSITION;

    }

    @Override
    public int getCount() {
        return familyList.size();
    }

    @Override
    public Family getItem(int position) {

//        initIntegerList(getCount());

        return familyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if (null == convertView) {
//            convertView = View.inflate(mContext, R.layout.item_myhome_family, null);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_changeadmin_pop, null);
            holder = new viewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
            if (holder == null) holder = new viewHolder(convertView);
        }

        String name = getItem(position).getUserAlias();
        if (StringUtil.isEmpty(name)) {
            User user = getItem(position).getUser();
            name = user.getName();
            if (StringUtil.isEmpty(name)) {
                name = user.getMobile();
            }
        }
        holder.tv_changeAdminPop_name.setText(name);
//        name = StringUtil.isEmpty(name) ? user.getRemark() : name;
//        name = StringUtil.isEmpty(name) ? user.getMobile() : name;
        boolean showCheck = (currentCheckPos == position);
        holder.iv_changeAdminPop.setImageResource(showCheck ? R.drawable.ic_check : R.drawable.ic_uncheck);

//        holder.iv_changeAdminPop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                judgeIsSelected(position);
//            }
//        });


        return convertView;
    }

    public void check(int position) {
        if (currentCheckPos == position) {
            currentCheckPos = AbsListView.INVALID_POSITION;
        } else {
            currentCheckPos = position;
        }
        notifyDataSetChanged();
    }

    class viewHolder {

        private ImageView iv_changeAdminPop;
        private TextView tv_changeAdminPop_name;

        public viewHolder(View convertView) {

            this.tv_changeAdminPop_name = (TextView) convertView.findViewById(R.id.tv_changeAdminPop_name);
            this.iv_changeAdminPop = (ImageView) convertView.findViewById(R.id.iv_changeAdminPop);

        }

    }


}
