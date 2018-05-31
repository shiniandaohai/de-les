package com.boer.delos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by Administrator on 2016/10/25 0025.
 */

public class MyHomeAdapter extends BaseAdapter {
    private List<Family> familyArrayList;
    private LayoutInflater inflater;

    public MyHomeAdapter(LayoutInflater inflater, List<Family> familyArrayList) {
        this.inflater = inflater;
        this.familyArrayList = familyArrayList;
    }

    public void setData(List<Family> familyArrayList) {
        this.familyArrayList = familyArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return familyArrayList.size();
    }

    @Override
    public Family getItem(int position) {
        return familyArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_myhome_family, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.ivItemAdministrator.setVisibility(View.GONE);

        Family family = familyArrayList.get(position);
        User user = family.getUser();
        String name = family.getUserAlias();
        if (StringUtil.isEmpty(name)) {
            name = user.getName();
            if (StringUtil.isEmpty(name)) {
                name = user.getMobile();
            }
        }
        childHolder.tvItemName.setText(name);
        //加载头像
        if (!StringUtil.isEmpty(user.getAvatarUrl())) {
            if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
            } else if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
            }
            // TODO 如果当前成员的头像url不为空，则加载其头像
            //ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
        } else {
            childHolder.ivUserHead.setImageResource(R.drawable.ic_avatar);
        }
        //显示管理员图标
        if (1 == family.getAdmin()) {
            childHolder.ivItemAdministrator.setVisibility(View.VISIBLE);
        } else {
            childHolder.ivItemAdministrator.setVisibility(View.GONE);
        }

        return convertView;
    }

     class ChildHolder {
        private LinearLayout llItemContent;
        private CircleImageView ivUserHead;
        private ImageView ivItemAdministrator, imgShowDetail;
        private TextView tvItemName;

        private ChildHolder(View convertView) {

            ivUserHead = (CircleImageView) convertView.findViewById(R.id.ivUserHead);
            ivItemAdministrator = (ImageView) convertView.findViewById(R.id.ivItemAdministrator);
            tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            llItemContent = (LinearLayout) convertView.findViewById(R.id.llItemContent);
            imgShowDetail = (ImageView) convertView.findViewById(R.id.img_show_detail);
        }
    }

}
