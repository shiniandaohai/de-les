package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.interf.ISimpleInterfaceString2;
import com.boer.delos.model.Device;
import com.boer.delos.model.ModeAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunzhibin on 2017/8/9.
 */

public class PanelSettingAdapter extends BaseExpandableListAdapter {
    private String selectKey; //选中的模式
    private List<String> groupKeys = new ArrayList<>();
    private Map<String, List<ModeAct>> mModeMaps;
    private Context mContext;

    public PanelSettingAdapter(Context mContext, Map<String, List<ModeAct>> mModeMaps) {
        this.mModeMaps = mModeMaps;
        this.mContext = mContext;
        groupKeys.addAll(mModeMaps.keySet());
    }

    public void setmListData(Map<String, List<ModeAct>> mModeMaps, String selectKey) {
        this.mModeMaps = mModeMaps;
        this.selectKey = selectKey;
        groupKeys.addAll(mModeMaps.keySet());
        updateShow(selectKey);
    }

    public void updateShow(String selectKey) {
        this.selectKey = selectKey;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mModeMaps.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mModeMaps.get(groupKeys.get(groupPosition)).size();
    }

    @Override
    public List<ModeAct> getGroup(int groupPosition) {
        return mModeMaps.get(groupKeys.get(groupPosition));
    }

    @Override
    public ModeAct getChild(int groupPosition, int childPosition) {
        return mModeMaps.get(groupKeys.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_panel_group, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.groupHolderUpdate(groupKeys.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChilderHolder childerHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_panel_child, null);
            childerHolder = new ChilderHolder(convertView);
            convertView.setTag(childerHolder);
        } else {
            childerHolder = (ChilderHolder) convertView.getTag();
        }
        childerHolder.childHolderUpdate(getChild(groupPosition, childPosition), childPosition);
        Log.d("wode", childerHolder.toString() + "  " + childPosition + " " + groupPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        private TextView tv_group_name;

        public GroupHolder(View convertView) {
            tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
        }

        public void groupHolderUpdate(String groupName) {
            if (groupName.contains("globalMode")) {
                tv_group_name.setText(mContext.getString(R.string.mode_global));
            } else
                tv_group_name.setText(groupName);
        }
    }

    class ChilderHolder {
        private TextView tv_mode_name;
        private TextView tv_model_sel;

        public ChilderHolder(View convertView) {
            tv_mode_name = (TextView) convertView.findViewById(R.id.tv_mode_name);
            tv_model_sel = (TextView) convertView.findViewById(R.id.tv_model_sel);
        }

        public void childHolderUpdate(ModeAct modeAct, int position) {
            tv_mode_name.setText(TextUtils.isEmpty(modeAct.getTag())
                    ? modeAct.getName() : modeAct.getTag());

            if (!TextUtils.isEmpty(selectKey) && selectKey.equals(modeAct.getModeId())) {
                tv_model_sel.setVisibility(View.VISIBLE);
            } else {
                tv_model_sel.setVisibility(View.GONE);
            }
        }

    }

}
