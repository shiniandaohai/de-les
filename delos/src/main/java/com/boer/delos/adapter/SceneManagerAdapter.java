package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Link;

import java.util.List;

import static android.R.attr.searchIcon;
import static android.R.attr.type;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/8 0008 15:30
 * @Modify:
 * @ModifyDate:
 */


public class SceneManagerAdapter extends MyBaseAdapter<Link> {
    public SceneManagerAdapter(Context mContext, List<Link> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);

        int resId = R.layout.item_scene_manager;

    }

    @Override
    public void convert(MyViewHolder holder, Link item, int position) {
        ImageView iv_mode = holder.getView(R.id.iv_mode);
        TextView tv_mode_name = holder.getView(R.id.tv_mode_name);
        ImageView iv_timer = holder.getView(R.id.iv_timer);
        //全局模式
        if (TextUtils.isEmpty(item.getFlag())) {
            iv_mode.setImageResource(Constant.modeImageWithModeName2(item.getName()));
            String name=TextUtils.isEmpty(item.getTag()) ? item.getName() : item.getTag();
            if(!name.endsWith("模式")){
                name+="模式";
            }
            tv_mode_name.setText(name);
            iv_timer.setVisibility(item.getHasActiveTask() ? View.VISIBLE : View.GONE);
            return;
        }
        if (item.getFlag().equals("alarmLinkPlan")) {
            iv_mode.setImageResource(R.mipmap.scene_alarm_link);
            tv_mode_name.setText(mContext.getString(R.string.text_alarm_link_plan_setting));
            iv_timer.setVisibility(View.GONE);

            holder.getView(R.id.spilt_line).setVisibility(View.VISIBLE);

        }
        if (item.getFlag().equals("room")) {
            // 房间模式
            String name=TextUtils.isEmpty(item.getTag()) ? item.getName() : item.getTag();
            if(!name.endsWith("模式")){
                name+="模式";
            }
            iv_mode.setImageResource(Constant.modeImageWithModeName2(item.getName()));
            tv_mode_name.setText(name);
            iv_timer.setVisibility(item.getHasActiveTask() ? View.VISIBLE : View.GONE);
        }

    }


}
