package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.urine.BaseUrineActivity;
import com.boer.delos.model.UrineResult;
import com.boer.delos.utils.TimeUtil;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 尿检历史记录
 * create at 2016/5/6 16:30
 */
public class UrinalysisHistoryAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<List<UrineResult.UrineBean>> mParentList;
    private static ArrayList<SwipeLayout> swipeLayoutChilds;

    public UrinalysisHistoryAdapter(Context context, List<List<UrineResult.UrineBean>> mParentList) {
        mContext = context;
        this.mParentList = mParentList;
    }

    public void setData(List<List<UrineResult.UrineBean>> mParentList) {
        this.mParentList = mParentList;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mParentList == null ? 0 : mParentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mParentList.get(groupPosition) == null ? 0 : mParentList.get(groupPosition).size();
    }

    @Override
    public List<UrineResult.UrineBean> getGroup(int groupPosition) {
        return mParentList.get(groupPosition);
    }

    @Override
    public UrineResult.UrineBean getChild(int groupPosition, int childPosition) {
        return mParentList.get(groupPosition).get(childPosition);
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
    public int getGroupType(int groupPosition) {
        return super.getGroupType(groupPosition);
    }

    @Override
    public int getGroupTypeCount() {
        return super.getGroupTypeCount();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_urinalysis_history, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.updateGroupHolder(getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_urinalysis_history_child, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.updateChildHolder(getChild(groupPosition, childPosition));
        childHolder.chileHolderControlSwipe();
        childHolder.childHolderClickListner(getChild(groupPosition, childPosition), groupPosition, childPosition);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupHolder {
        private TextView tvItemDate;

        private GroupHolder(View convertView) {
            tvItemDate = (TextView) convertView.findViewById(R.id.tvItemDate);
        }

        private void updateGroupHolder(List<UrineResult.UrineBean> item) {
            if (item != null && item.size() != 0) {
                String time = TimeUtil.formatStamp2Time(item.get(0).getMeasuretime(),
                        "yyyy-MM-dd");
                tvItemDate.setText(time);
            } else {
                tvItemDate.setText("");
            }

        }
    }

    class ChildHolder {
        private TextView tvItemTitle;
        private TextView tvItemState;
        private TextView tvItemTime;
        private SwipeLayout swipeLayout;
        private TextView tvDelete;

        private ChildHolder(View convertView) {
            tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            tvItemState = (TextView) convertView.findViewById(R.id.tvItemState);
            tvItemTime = (TextView) convertView.findViewById(R.id.tvItemTime);
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
        }

        private void updateChildHolder(UrineResult.UrineBean urineBean) {


            tvItemTitle.setText("尿检 " + urineBean.getScore() + "分");
            if (Integer.valueOf(urineBean.getScore()) > BaseUrineActivity.SCORE_STANDARD) {
                tvItemState.setText("正常");
                tvItemState.setTextColor(Color.parseColor("#71bd40"));
            } else
                tvItemState.setText("异常");
            tvItemState.setTextColor(Color.parseColor("#fe3000"));
            String time = TimeUtil.formatStamp2Time(urineBean.getMeasuretime(), null);
            tvItemTime.setText(time);
        }

        private void childHolderClickListner(final UrineResult.UrineBean item, final int groupPos, final int childPos) {
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {

                        mListener.deleteData(item, groupPos, childPos);
                    }
                }
            });
        }

        private void chileHolderControlSwipe() {
            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    closeChildMenu(layout);
                    swipeLayoutChilds.add(layout);

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    closeChildMenu(layout);
                }
            });

        }

        private void closeChildMenu(SwipeLayout swipLayout) {
            if (swipeLayoutChilds == null) {
                swipeLayoutChilds = new ArrayList<SwipeLayout>();
            }
            for (SwipeLayout s : swipeLayoutChilds) {
                if (s == swipLayout) {
                    continue;
                }
                s.close();
            }
        }

    }

    public interface OnDeleteDataClickListener {
        void deleteData(UrineResult.UrineBean data, int groupPos, int childPos);
    }

    private OnDeleteDataClickListener mListener;

    public void setListener(OnDeleteDataClickListener listener) {
        mListener = listener;
    }
}
