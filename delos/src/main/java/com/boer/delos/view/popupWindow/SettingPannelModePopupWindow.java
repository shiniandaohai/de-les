package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.ModeAct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/11 0011.
 * 配置场景面板的显示选择模式的pop
 */

public class SettingPannelModePopupWindow extends PopupWindow {
    ExpandableListView expandableListView;
    private View view;
    private Map<String, List<ModeAct>> mDatas; //数据
    private List<String> mapKey; //Map 的key
    private Context context;
    private List<List<ModeAct>> modeList;
    private OnSelectListener listener;
    private EXlistViewAdapter adapter;
    public void setListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public SettingPannelModePopupWindow(Context context, Map<String, List<ModeAct>> mDatas, List<String> mapKey) {
        super(context);
        this.context = context;
        this.mapKey = mapKey;
        this.mDatas = mDatas;

        view = View.inflate(context, R.layout.popup_setting_pannel_mode, null);
        setContentView(view);
        setProperty();
        modeList = new ArrayList<>();
        initView();
        initData();
    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        expandableListView = (ExpandableListView) view.findViewById(R.id.ExpandableListViewPannelSetting);
        adapter = new EXlistViewAdapter(context, modeList, mapKey);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (listener != null) {
                    String modeId = modeList.get(groupPosition).get(childPosition).getModeId();
                    listener.selectChildren(modeId);
                }
                return false;
            }
        });
        //设置默认展开
        int groupCount = expandableListView.getCount();
        for (int i=0; i<groupCount; i++) {

            expandableListView.expandGroup(i);

        }
    }

    private void initData() {
        if (modeList == null) modeList = new ArrayList<>();
        for (String key : mapKey) {
            modeList.add(mDatas.get(key));
        }
        adapter.setData(modeList);
    }

    public interface OnSelectListener {
         void selectChildren(String modeId);
    }


    private class EXlistViewAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private List<List<ModeAct>> mAdapterDatas; //数据
        private List<String> mAdaptermapKey; //Map 的key

        public EXlistViewAdapter(Context mContext, List<List<ModeAct>> mAdapterDatas, List<String> mAdaptermapKey) {
            this.mContext = mContext;
            this.mAdapterDatas = mAdapterDatas;
            this.mAdaptermapKey = mAdaptermapKey;
        }

        public void setData(List<List<ModeAct>> mAdapterDatas) {
            this.mAdapterDatas = mAdapterDatas;
            notifyDataSetChanged();

        }
        @Override
        public int getGroupCount() {
            return mAdapterDatas.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mAdapterDatas.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mAdapterDatas.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mAdapterDatas.get(groupPosition).get(childPosition);
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
            GroupHolder groupHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_setting_pannel_group, null);
                groupHolder = new GroupHolder(convertView);
                convertView.setTag(groupHolder);

            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            String groupName = mAdaptermapKey.get(groupPosition);
            groupHolder.tvGroupName.setText(groupName);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_setting_pannel_child, null);
                childHolder = new ChildHolder(convertView);
                convertView.setTag(childHolder);

            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }

            String groupName = mAdapterDatas.get(groupPosition).get(childPosition).getTag();
            childHolder.tvChildName.setText(groupName);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class GroupHolder {
            private TextView tvGroupName;

            public GroupHolder(View convertView) {
                tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
            }
        }

        class ChildHolder {
            private TextView tvChildName;

            public ChildHolder(View convertView) {
                tvChildName = (TextView) convertView.findViewById(R.id.tvChildName);
            }
        }
    }
}

