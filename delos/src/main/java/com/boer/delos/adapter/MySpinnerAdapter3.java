package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.ShareUser;
import com.boer.delos.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 扫描设备
 * @CreateDate: 2017/2/16 0016 14:01
 * @Modify:
 * @ModifyDate:
 */


public class MySpinnerAdapter3 extends MyBaseAdapter<ShareUser.UserBean> {

    public MySpinnerAdapter3(Context mContext, List<ShareUser.UserBean> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int resId = R.layout.item_spinner_dropdown;
    }

    @Override
    public void setDatas(List<ShareUser.UserBean> listData) {
        super.setDatas(listData);
    }

    @Override
    public void convert(MyViewHolder holder, ShareUser.UserBean item, int position) {

        ImageView imageView = holder.getView(R.id.iv_avatar);
        TextView tv_user_name = holder.getView(R.id.tv_user_name);
        tv_user_name.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        String name = item.getUserName();
        if (StringUtil.isEmpty(name)) {
            name = item.getMobile();
        }
        tv_user_name.setText(name);
        String url = URLConfig.HTTP + item.getAvatarUrl();
        if (imageView.getTag() == null
                || imageView.getTag() == null
                || TextUtils.isEmpty(imageView.getTag().toString())
                || url.equals(imageView.getTag().toString())) {
            ImageLoader.getInstance().displayImage(url, imageView,
                    BaseApplication.getInstance().displayImageOptions);
            imageView.setTag(url);

        }
    }

}
