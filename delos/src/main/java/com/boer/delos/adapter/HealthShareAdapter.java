package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Family;
import com.boer.delos.model.User;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: "健康分享"popupWindow列表的适配器
 * create at 2016/5/26 13:28
 */
public class HealthShareAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Family> families;
    private List<Boolean> checkList;// 标记家人的选中状态

    public HealthShareAdapter(Context context, List<Family> families, List<Boolean> checkList) {
        this.context = context;
        this.families = families;
        this.checkList = checkList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCheckList(List<Boolean> checkList) {
        this.checkList = checkList;
        notifyDataSetChanged();
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
    public Family getItem(int position) {
        return families.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_health_share, null);
            holder = new ViewHolder(convertView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = getItem(position).getUser();
        String name = getItem(position).getUserAlias();
        if (StringUtil.isEmpty(name)) {
            name = user.getName();
            if (StringUtil.isEmpty(name)) {
                name = user.getMobile();
            }
        }
        holder.tvShareItemName.setText(name);
        if (!StringUtil.isEmpty(user.getAvatarUrl())
                || holder.ivAvatar.getTag() == null
                || StringUtil.isEmpty(holder.ivAvatar.getTag().toString())
                || !holder.ivAvatar.getTag().toString().equals(user.getAvatarUrl())) {
            ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(),
                    holder.ivAvatar, BaseApplication.getInstance().displayImageOptions);
            holder.ivAvatar.setTag(user.getAvatarUrl());

        }

        if (checkList.get(position)) {
            holder.ivItemChecked.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemChecked.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tvShareItemName;
        private ImageView ivItemChecked;
        private CircleImageView ivAvatar;

        public ViewHolder(View convertView) {
            ivAvatar = (CircleImageView) convertView.findViewById(R.id.ivAvatar);
            tvShareItemName = (TextView) convertView.findViewById(R.id.tvShareItemName);
            ivItemChecked = (ImageView) convertView.findViewById(R.id.ivItemChecked);
        }


    }
}
