package com.boer.delos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.expandablelistview.BaseSwipeMenuExpandableListAdapter;
import com.baoyz.swipemenulistview.ContentViewWrapper;
import com.boer.delos.R;
import com.boer.delos.activity.personal.MyHomeListeningActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.User;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author PengJiYang
 * @Description: 个人中心界面下的 家庭管理 adapter
 * create at 2016/5/25 21:38
 */
public class FamilyManageAdapter extends BaseSwipeMenuExpandableListAdapter {


    private Activity context;
    private LayoutInflater inflater;
    private List<Host> hostList;
    private Map<String, String> online = new HashMap<>();
    private ClickListener listener;

    public FamilyManageAdapter(Activity context, List<Host> hostList, ClickListener listener) {
        this.context = context;
        this.hostList = hostList;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        IsAdimn2ManagerAuthority();
    }

    public void setData(List<Host> hostList) {
        this.hostList = hostList;
        notifyDataSetChanged();
    }

    public void setOnline(Map<String, String> online) {
        this.online = online;
    }

    @Override
    public int getGroupCount() {
        return hostList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.v("gl", "hostList(groupPosition).getFamilies.size()==" + hostList.get(groupPosition).getFamilies().size());
        return hostList.get(groupPosition).getFamilies().size();
    }

    @Override
    public Host getGroup(int groupPosition) {

        return hostList.get(groupPosition);
    }

    @Override
    public Family getChild(int groupPosition, int childPosition) {

        return hostList.get(groupPosition).getFamilies().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isGroupSwipable(int groupPosition) {


        return true;
    }

    @Override
    public boolean isChildSwipable(int groupPosition, int childPosition) {
//        Host host = hostList.get(groupPosition);
//        for (Family family : host.getFamilies())
//            if (family.getAdmin() == 1 && family.getUserId().equals(Constant.USERID))
//                return true;
//        return false;
        if(Constant.userIsAdmin(hostList.get(groupPosition))){
            Family family = hostList.get(groupPosition).getFamilies().get(childPosition);
            if (family.getAdmin() == 1){
                return false;
            }
            else if(family.getUserId().equals("-99")){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return false;
        }
    }

    @Override
    public ContentViewWrapper getGroupViewAndReUsable(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        boolean reUseable = true;
        GroupHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_family_manage_group, null);
            holder = new GroupHolder(convertView);
            convertView.setTag(holder);
            reUseable = false;
        }
        holder = (GroupHolder) convertView.getTag();
        if (holder == null) {
            holder = new GroupHolder(convertView);
        }
        holder.ivItemArrow.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        final Host host = hostList.get(groupPosition);
//        if (host.getFamilies().size() > 0
//                && host.getFamilies().get(0) != null) {
//            Family family = host.getFamilies().get(0);
//            String hostName = family.getHostAlias();
//            if (!TextUtils.isEmpty(hostName)) {
////                holder.tvHostName.setText(hostName);
//            } else{
//                holder.tvHostName.setText(host.getName());
//            }
//        } else{
//            holder.tvHostName.setText(host.getName());
//        }
        String hostName=host.getName();
        holder.tvHostName.setText(TextUtils.isEmpty(hostName)?"":hostName);
        String hostAlias="";
        for(int i=0;i<host.getFamilies().size();i++){
            Family family=host.getFamilies().get(i);
            if(family.getUserId()!=null&&family.getUserId().equals(Constant.USERID)){
                hostAlias=family.getHostAlias();
                break;
            }
        }
        holder.tvHomeName.setText(TextUtils.isEmpty(hostAlias)?"我的主机":hostAlias);
        holder.tvHostName.append("("+host.getHostId()+")");
        //是否当前主机
        if (Constant.CURRENTHOSTID.equals(host.getHostId())) {
            holder.ivItemCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemCheck.setVisibility(View.GONE);
        }
        //是否在线
        final String isOnline = online.get(host.getHostId());
        if (isOnline != null || "1".equals(isOnline)) {
            holder.ivItemOnline.setImageResource(R.mipmap.ic_family_manage_gateway_online);
            holder.tvHomeName.setTextColor(Color.BLACK);
        } else {
            holder.ivItemOnline.setImageResource(R.mipmap.ic_family_manage_gateway_offline);
            holder.tvHomeName.setTextColor(Color.LTGRAY);
        }
        //跳转管理管理家庭成员界面
        holder.ivItemOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //TODO  只有管理员进入 MyHOme
                if (!IsAdimn2ManagerAuthority()) { //只有管理员进入
                    return;
                }

                for (Family family : host.getFamilies()) {

                    if (family.getUserId().equals(Constant.USERID) && family.getAdmin() == 1) {

                        Intent intent = new Intent(context, MyHomeListeningActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("host", host);
                        intent.putExtras(bundle);
                        context.startActivityForResult(intent,1000);
                    }
                }
            }
        });
        //判断主机在线并且不为当前主机
        if (isOnline != null && "1".equals(isOnline) && !Constant.CURRENTHOSTID.equals(host.getHostId())) {
            holder.tvChangeHost.setVisibility(View.VISIBLE);
            holder.tvChangeHost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.changeHostClick(host);
                    }
                }
            });
        } else {
            holder.tvChangeHost.setVisibility(View.GONE);
        }

        return new ContentViewWrapper(convertView, reUseable);
    }

    //判断是否是当前主机的管理员
    private boolean IsAdimn2ManagerAuthority() {
        if (hostList == null)
            return false;
        if (hostList.size() == 0) {
            return false;

        }
        for (Host host : hostList) {
            if (!Constant.userIsAdmin(host)) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public ContentViewWrapper getChildViewAndReUsable(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        boolean reUseable = true;
        ChildHolder childHolder;
        convertView = inflater.inflate(R.layout.item_family_manage_child, null);
        childHolder = new ChildHolder(convertView);
        convertView.setTag(childHolder);
        reUseable = false;


        Family family = hostList.get(groupPosition).getFamilies().get(childPosition);

        User user = family.getUser();
        String name = family.getUserAlias();
        if (StringUtil.isEmpty(name)) {
            name = user.getName();
            if (StringUtil.isEmpty(name)) {
                name = user.getMobile();
            }
        }
        childHolder.tvItemName.setText(name);
        if (!StringUtil.isEmpty(user.getAvatarUrl())) {
            if (user.getAvatarUrl().equals("-99")) {
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.addhost, childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
            }
        } else {
            childHolder.ivUserHead.setImageResource(R.drawable.ic_avatar);
        }

        if (0 != family.getApplyStatus()) { // 申请状态1为待确定
            childHolder.tvItemName.setText(user.getMobile());
            childHolder.ivItemShare.setVisibility(View.GONE);
            childHolder.ivItemAdministrator.setVisibility(View.GONE);
            childHolder.tvUnConfirm.setVisibility(View.VISIBLE);
        } else {
            childHolder.ivItemShare.setVisibility(View.VISIBLE);
            childHolder.tvUnConfirm.setVisibility(View.GONE);
        }
//当前用户才显示分享图标
        if (Constant.USERID.equals(user.getId())) {
            childHolder.ivItemShare.setVisibility(View.VISIBLE);// 若未分享，则显示分享图标
            if (family.getShare() == 1) {  // 0:未分享，1：已分享
                childHolder.ivItemShare.setImageResource(R.mipmap.ic_family_manage_share_blue);
            }
            if (family.getShare() == 0) {  // 0:未分享，1：已分享
                childHolder.ivItemShare.setImageResource(R.mipmap.ic_family_manage_share_gray);
            }

        } else {
            childHolder.ivItemShare.setVisibility(View.GONE);// 若已分享，则隐藏分享图标
        }
        childHolder.ivItemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareListener == null) return;
                shareListener.clickShareData(groupPosition);

            }
        });


        if (1 == family.getAdmin()) {
            childHolder.ivItemAdministrator.setVisibility(View.VISIBLE);
        } else {
            childHolder.ivItemAdministrator.setVisibility(View.GONE);
        }


        childHolder.llItemContent.setVisibility(View.VISIBLE);


        if (family.getLimitStatus() == 1) {

            childHolder.tv_limit_time.setText(computeRevertMillis(family.getLimitTime()));

        } else {
            childHolder.tv_limit_time.setText("");
        }

        return new ContentViewWrapper(convertView, reUseable);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupHolder {
        ImageView ivItemOnline, ivItemCheck, ivItemArrow;
        TextView tvHomeName, tvHostName;
        TextView tvChangeHost;
        LinearLayout llHostName, llUnbindGateway;

        public GroupHolder(View convertView) {
            this.ivItemOnline = (ImageView) convertView.findViewById(R.id.ivItemOnline);
            this.ivItemCheck = (ImageView) convertView.findViewById(R.id.ivItemCheck);
            this.tvHomeName = (TextView) convertView.findViewById(R.id.tvHomeName);
            this.tvHostName = (TextView) convertView.findViewById(R.id.tvHostName);
            this.tvChangeHost = (TextView) convertView.findViewById(R.id.tvChangeHost);
            this.llHostName = (LinearLayout) convertView.findViewById(R.id.llHostName);
            this.llUnbindGateway = (LinearLayout) convertView.findViewById(R.id.llUnbindGateway);
            this.ivItemArrow = (ImageView) convertView.findViewById(R.id.ivItemArrow);
        }
    }

    class ChildHolder {
        public LinearLayout llItemContent;
        public CircleImageView ivUserHead;
        ImageView ivItemShare, ivItemAdministrator;
        public TextView tvItemName, tvUnConfirm, tv_limit_time;

        public ChildHolder(View convertView) {
            this.ivUserHead = (CircleImageView) convertView.findViewById(R.id.ivUserHead);
            this.ivItemShare = (ImageView) convertView.findViewById(R.id.ivItemShare);
            this.ivItemAdministrator = (ImageView) convertView.findViewById(R.id.ivItemAdministrator);
            this.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            this.tvUnConfirm = (TextView) convertView.findViewById(R.id.tvUnConfirm);
            this.tv_limit_time = (TextView) convertView.findViewById(R.id.tv_limit_time);
            this.llItemContent = (LinearLayout) convertView.findViewById(R.id.llItemContent);
        }
    }

    public interface ClickListener {
        void changeHostClick(Host host);
    }

    public interface OnClick2ShareListener {
        void clickShareData(int flag);
    }

    private OnClick2ShareListener shareListener;

    public void setOnClick2ShareListener(OnClick2ShareListener shareListener) {
        this.shareListener = shareListener;
    }


    public String computeRevertMillis(String second) {

        long ms = Long.parseLong(second) - System.currentTimeMillis() / 1000;

        Integer mi = 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute=(ms-day*dd-hour*hh)/mi;

        StringBuffer sb = new StringBuffer();
        if (day >= 0) {
            sb.append(day + context.getString(R.string.pick_day));
        }
        if (hour >= 0) {
            sb.append(hour + context.getString(R.string.pick_hour));
        }
        if(minute>=0){
            sb.append(minute+"分");
        }


        return sb.toString();


    }


}
