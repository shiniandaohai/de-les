package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.model.Furniture;
import com.boer.delos.model.FurnitureChild;
import com.boer.delos.utils.Loger;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家具列表adapter
 * create at 2016/4/15 14:06
 */
public class FurnitureListAdapter extends BaseExpandableListAdapter {

    private List<Furniture> furnitureList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private ClickListener listener;
    private List<SwipeLayout> swipeLayoutChilds;
    private List<SwipeLayout> swipeLayoutGroups;

    public FurnitureListAdapter(Context context, List<Furniture> furnitureList, ClickListener listener) {
        this.context = context;
        this.furnitureList = furnitureList;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        swipeLayoutChilds = new ArrayList<>();
        swipeLayoutGroups = new ArrayList<>();
    }

    public void setFurnitureList(List<Furniture> furnitureList) {
        this.furnitureList = furnitureList;
        swipeLayoutChilds = new ArrayList<>();
        swipeLayoutGroups = new ArrayList<>();
    }

    @Override
    public int getGroupCount() {
        return furnitureList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return furnitureList.get(groupPosition).getChildList().size();
    }

    @Override
    public Furniture getGroup(int groupPosition) {
        return furnitureList.get(groupPosition);
    }

    @Override
    public FurnitureChild getChild(int groupPosition, int childPosition) {
        return furnitureList.get(groupPosition).getChildList().get(childPosition);
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_furniture_group, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }
        groupHolder = (GroupHolder) convertView.getTag();

        /*if(groupPosition == 0) {
            groupHolder.viewTop.setVisibility(View.GONE);
        } else {
            groupHolder.viewTop.setVisibility(View.VISIBLE);
        }*/

        Furniture device = furnitureList.get(groupPosition);
        groupHolder.tvGroupName.setText(device.getGroupTitle());
        groupHolder.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupEditListener != null) {
                    groupEditListener.areaEditClick(getGroup(groupPosition).getAreaId(), groupPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_furniture_child, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        childHolder = (ChildHolder) convertView.getTag();

        final FurnitureChild child = furnitureList.get(groupPosition).getChildList().get(childPosition);


        // add by sunzhibin
        childHolder.mSwipeLayout.setSwipeEnabled(true);

        childHolder.ivChildImage.setImageResource(child.getResId());

        childHolder.tvChildTitle.setText(child.getChildTitle());
        childHolder.tvComment.setText(child.getComment());
        if (child.isOnline()) {
            childHolder.tvIsOnline.setTextColor(Color.parseColor("#128ce3"));
        } else {
            childHolder.tvIsOnline.setTextColor(Color.parseColor("#888888"));
        }

        childHolder.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editClick(child.getDevice());
//                    FurnitureListListeningActivity.instance.editDevice(child.getAreaId(), child.getAreaName(), child.getDevice());
            }
        });

        childHolder.llUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.dismissClick(child.getDevice());
//                    FurnitureListListeningActivity.instance.dismissDevice(child.getDevice());
            }
        });
        childHolder.chileHolderControlSwipe();
        return convertView;
    }

    //    public interface ClickResultListener{
//        void ClickResult(int tag,int childPosition);
//    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        private View llEdit;
        private TextView tvGroupName;

        public GroupHolder(View convertView) {
            tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
            llEdit = convertView.findViewById(R.id.llEdit);


        }
    }

    class ChildHolder {
        private ImageView ivChildImage;
        private TextView tvChildTitle;
        private TextView tvIsOnline;
        private TextView tvComment;
        private LinearLayout llChildContent;
        private LinearLayout llEdit;
        private LinearLayout llUnbind;

        private SwipeLayout mSwipeLayout;

        public ChildHolder(View convertView) {
            ivChildImage = (ImageView) convertView.findViewById(R.id.ivChildImage);
            tvChildTitle = (TextView) convertView.findViewById(R.id.tvChildTitle);
            tvIsOnline = (TextView) convertView.findViewById(R.id.tvIsOnline);
            tvComment = (TextView) convertView.findViewById(R.id.tvComment);
            llChildContent = (LinearLayout) convertView.findViewById(R.id.llChildContent);
            llEdit = (LinearLayout) convertView.findViewById(R.id.llEdit);
            llUnbind = (LinearLayout) convertView.findViewById(R.id.llUnbind);
            //滑动
            mSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, this.mSwipeLayout.findViewWithTag("Bottom2"));
        }

        public void chileHolderControlSwipe() {
            mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    closeChildMenu(layout);
                    swipeLayoutChilds.add(layout);
                    Loger.d("FFF onStartOpen " + layout.toString());

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    closeChildMenu(layout);
//                    swipeLayoutChilds.add(layout);
//                    layout.open();
                    Loger.d("FFF onOpen " + layout.toString());
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
            //关闭组侧滑
            if (swipeLayoutGroups == null) {
                swipeLayoutGroups = new ArrayList<SwipeLayout>();
            }
            for (SwipeLayout s : swipeLayoutGroups) {
                s.close();
            }
            swipeLayoutGroups.clear();
        }
    }

    public interface ClickListener {
        void editClick(Device device);

        void dismissClick(Device device);
    }

    public interface GroupEditListener {
        void areaEditClick(String areaId, int groupPos);
    }

    private GroupEditListener groupEditListener;

    public void setListner(GroupEditListener groupEditListener) {
        this.groupEditListener = groupEditListener;
    }
}
