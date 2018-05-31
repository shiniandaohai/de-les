package com.boer.delos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.model.PressureResult;
import com.boer.delos.utils.TimeUtil;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/16 0016 09:10
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressRecordexAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<List<PressureResult.PressureBean>> mParentLists;
    private static ArrayList<SwipeLayout> swipeLayoutChilds;

    public BloodPressRecordexAdapter(Context context, List<List<PressureResult.PressureBean>> parentDatas) {
        this.mContext = context;
        this.mParentLists = parentDatas;
    }

    public void setData(List<List<PressureResult.PressureBean>> mParentLists) {
        this.mParentLists = mParentLists;
        notifyDataSetChanged();
    }

    /**
     * 判断血压水平
     *
     * @param item
     * @return
     */
    private String[] judgeSate(PressureResult.PressureBean item) {
        ArrayList<Map> maps = DealWithValues.getInstance().dealWithPressure();
        int index = DealWithValues.judgeBloodPressureState(item.getValueH(), item.getValueL());

        return new String[]{maps.get(index).get("title").toString(), maps.get(index).get("color").toString()};
    }

    @Override
    public int getGroupCount() {
        return mParentLists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mParentLists.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mParentLists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mParentLists.get(groupPosition).get(childPosition);
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
            convertView = View.inflate(mContext, R.layout.item_urinalysis_history, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        String time = TimeUtil.formatStamp2Time(mParentLists.get(groupPosition).get(0).getMeasuretime(), "yyyy-MM-dd");

        groupHolder.tvTitle.setText(time);

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
        childHolder.childHolderUpdate((PressureResult.PressureBean) getChild(groupPosition, childPosition));
        childHolder.childHolderClickListner((PressureResult.PressureBean) getChild(groupPosition, childPosition), groupPosition, childPosition);
        childHolder.chileHolderControlSwipe();
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupHolder {
        private TextView tvTitle;

        public GroupHolder(View convertView) {
            tvTitle = (TextView) convertView.findViewById(R.id.tvItemDate);

        }
    }

    class ChildHolder {
        private ImageView ivItemChild;
        private TextView tvItemTitle;
        private TextView tvItemState;
        private TextView tvItemTime;
        private TextView tvDelete;
        private SwipeLayout swipeLayout;

        public ChildHolder(View convertView) {
            ivItemChild = (ImageView) convertView.findViewById(R.id.ivItemChild);
            tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            tvItemState = (TextView) convertView.findViewById(R.id.tvItemState);
            tvItemTime = (TextView) convertView.findViewById(R.id.tvItemTime);
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);

        }

        public void childHolderUpdate(PressureResult.PressureBean item) {

            String time = TimeUtil.formatStamp2Time(item.getMeasuretime(), "yyyy-MM-dd HH:mm:ss");

            tvItemTime.setText(time);
            tvItemTitle.setText("血压 " + item.getValueH() + "/" + item.getValueL() + " mmHg");

            tvItemState.setText(judgeSate(item)[0]);
            tvItemState.setTextColor(Integer.valueOf(judgeSate(item)[1]));

            if (ivItemChild.getDrawable() != null) {
                ivItemChild.setImageResource(R.mipmap.bloodpressureimg);
                ivItemChild.setTag(time);
            }
        }


        private void childHolderClickListner(final PressureResult.PressureBean item, final int groupPos, final int childPos) {
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
        void deleteData(PressureResult.PressureBean data, int groupPos, int childPos);
    }

    private OnDeleteDataClickListener mListener;

    public void setListener(OnDeleteDataClickListener listener) {
        mListener = listener;
    }

}

