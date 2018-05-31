package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.ModeAct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunzhibin on 2017/8/16.
 */

public class MusicModeLinkSettingAdapter extends BaseExpandableListAdapter {
    private String selectKey; //选中的歌曲
    private List<String> groupKeys = new ArrayList<>();//group
    private Map<String, List<ModeAct>> mModeMaps;//child
    private Context mContext;

    public MusicModeLinkSettingAdapter(Context mContext, Map<String, List<ModeAct>> mModeMaps) {
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
            convertView = View.inflate(mContext, R.layout.item_music_mode_link_group, null);
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
        ChildHolder childerHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_music_mode_link_child, null);
            childerHolder = new ChildHolder(convertView);
            convertView.setTag(childerHolder);
        } else {
            childerHolder = (ChildHolder) convertView.getTag();
        }
        childerHolder.childHolderUpdate(getChild(groupPosition, childPosition));
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
            tv_group_name = (TextView) convertView.findViewById(R.id.tv_mode_group);
        }

        public void groupHolderUpdate(String groupName) {
            if (groupName.contains("globalMode")) {
                tv_group_name.setText(mContext.getString(R.string.mode_global));
            } else
                tv_group_name.setText(groupName);
        }
    }

    class ChildHolder {
        private TextView tv_mode_name;
        private ImageView iv_mode_sel;

        public ChildHolder(View convertView) {
            tv_mode_name = (TextView) convertView.findViewById(R.id.tv_mode_name);
            iv_mode_sel = (ImageView) convertView.findViewById(R.id.iv_mode_sel);
        }

        public void childHolderUpdate(ModeAct modeAct) {
            tv_mode_name.setText(TextUtils.isEmpty(modeAct.getTag())
                    ? modeAct.getName() : modeAct.getTag());
        }
    }

}
