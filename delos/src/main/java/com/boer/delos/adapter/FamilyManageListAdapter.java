package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.FamilyManageChild;
import com.boer.delos.model.FamilyManange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 家庭管理界面的adapter
 * create at 2016/4/12 9:47
 *
 */
public class FamilyManageListAdapter extends BaseExpandableListAdapter {
    private List<FamilyManange> datas=new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public FamilyManageListAdapter(Context context) {
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    public void setDatas(List<FamilyManange> datas) {
        this.datas = datas;
    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return datas.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datas.get(groupPosition).getChildList().get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if(convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.item_gateway_group, null);
            groupHolder.tvGateway = (TextView) convertView.findViewById(R.id.tvGateway);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        FamilyManange familyManange = datas.get(groupPosition);
        groupHolder.tvGateway.setText(familyManange.getGroupTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if(convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.item_gateway_child, null);
            childHolder.tvContacts = (TextView) convertView.findViewById(R.id.tvContacts);
            childHolder.ivHead= (ImageView) convertView.findViewById(R.id.ivHead);
            childHolder.ivShare= (ImageView) convertView.findViewById(R.id.ivShare);
            childHolder.ivPersonHead= (ImageView) convertView.findViewById(R.id.ivPersonHead);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }


        childHolder.ivShare.setVisibility(childPosition==0?View.VISIBLE:View.GONE);
        childHolder.ivPersonHead.setVisibility(childPosition==0?View.VISIBLE:View.GONE);

        FamilyManageChild child = datas.get(groupPosition).getChildList().get(childPosition);
        childHolder.tvContacts.setText(child.getContacts());
        childHolder.ivHead.setImageResource(child.getResID());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        public TextView tvGateway;
    }

    class ChildHolder {
        public ImageView ivHead,ivShare,ivPersonHead;
        public TextView tvContacts;
    }


   /* @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.item_family_manage,null);
            viewHolder=new MyViewHolder();
            viewHolder.ivHead= (ImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.tvRight= (TextView) convertView.findViewById(R.id.tvRight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        viewHolder.ivHead.setImageResource(datas.get(position).getResId());
        viewHolder.tvRight.setText(datas.get(position).getRightText());
        return convertView;
    }
    class MyViewHolder{
        public ImageView ivHead;
        public TextView tvRight;
    }*/
}
