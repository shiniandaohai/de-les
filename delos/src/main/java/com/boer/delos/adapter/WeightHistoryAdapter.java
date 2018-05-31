package com.boer.delos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.model.WeightBean;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
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
public class WeightHistoryAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<List<WeightBean>> mParentList;
    private static ArrayList<SwipeLayout> swipeLayoutChilds;

    public WeightHistoryAdapter(Context context, List<List<WeightBean>> mParentList) {
        mContext = context;
        this.mParentList = mParentList;
    }

    public void setData(List<List<WeightBean>> mParentList) {
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
    public List<WeightBean> getGroup(int groupPosition) {
        return mParentList.get(groupPosition);
    }

    @Override
    public WeightBean getChild(int groupPosition, int childPosition) {
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

        private void updateGroupHolder(List<WeightBean> item) {
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
        private TextView tvWeightBMI;
        private SwipeLayout swipeLayout;
        private TextView tvDelete;
        private ImageView ivItemChild;

        private ChildHolder(View convertView) {
            tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            tvItemState = (TextView) convertView.findViewById(R.id.tvItemState);
            tvItemTime = (TextView) convertView.findViewById(R.id.tvItemTime);
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
            ivItemChild = (ImageView) convertView.findViewById(R.id.ivItemChild);
            tvWeightBMI = (TextView) convertView.findViewById(R.id.tvWeightBMI);
        }

        private void updateChildHolder(WeightBean bean) {

            ivItemChild.setImageResource(R.mipmap.weightimage);
            tvItemTitle.setText(String.format("%.1f%s", bean.getWeight(), " kg"));

            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), null);
            tvItemTime.setText(time);

            calculate(bean, tvItemState);
        }

        /**
         * 计算BMI是否正常
         *
         * @param bean
         * @param tvItemState
         */
        private void calculate(WeightBean bean, TextView tvItemState) {
            WeightBean.WeightDetailBean detailBean = GsonUtil.getObject(bean.getDetail(), WeightBean.WeightDetailBean.class);
            if (detailBean == null || StringUtil.isEmpty(detailBean.getBMI())) {
                return;
            }
            float bmi = 0f;
            try {
                bmi = Float.valueOf(detailBean.getBMI());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            int index = DealWithValues.judgeWeightState(bmi);

            String temp = String.format("%s%.1f ", " ", bmi);

            tvWeightBMI.setText(String.format("%s%.1f", "BMI: ", bmi));

            switch (index) {
                case 0:
                    tvItemState.setText(mContext.getString(R.string.text_fat_super));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.red_bp_1));
                    break;
                case 1:
                    tvItemState.setText(mContext.getString(R.string.text_fat_severe));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.red_bp_2));
                    break;
                case 2:
                    tvItemState.setText(mContext.getString(R.string.text_fat));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.red_bp_3));
                    break;
                case 3:
                    tvItemState.setText(mContext.getString(R.string.text_weight_over));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.red_bp_4));
                    break;
                case 4:
                    tvItemState.setText(mContext.getString(R.string.text_weight_normal));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.green_bp));
                    break;
                case 5:
                    tvItemState.setText(mContext.getString(R.string.text_weight_pink));
                    tvItemState.setTextColor(mContext.getResources().getColor(R.color.yellow_bp));
                    break;

            }

        }

        private void childHolderClickListner(final WeightBean item, final int groupPos, final int childPos) {
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
        void deleteData(WeightBean data, int groupPos, int childPos);
    }

    private OnDeleteDataClickListener mListener;

    public void setListener(OnDeleteDataClickListener listener) {
        mListener = listener;
    }
}
