package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceManage;
import com.boer.delos.model.DeviceManageChild;
import com.boer.delos.utils.L;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 设备管理"适配器
 * create at 2016/4/7 17:19
 */
public class DeviceManageAdapter extends BaseExpandableListAdapter {

    private List<DeviceManage> deviceList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private SwipeLayout currentExpandedSwipeLayout;
    private ClickListener listener;

    public DeviceManageAdapter(Context context, List<DeviceManage> deviceList, ClickListener listener) {
        this.context = context;
        this.deviceList = deviceList;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    public void setDeviceList(List<DeviceManage> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public int getGroupCount() {
        return deviceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return deviceList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return deviceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return deviceList.get(groupPosition).getChildList().get(childPosition);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_device_manage_group, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        if (groupPosition == 0) {
            groupHolder.viewTop.setVisibility(View.GONE);
        } else {
            groupHolder.viewTop.setVisibility(View.VISIBLE);
        }

        DeviceManage device = deviceList.get(groupPosition);
        groupHolder.tvGroupName.setText(device.getGroupTitle());
        groupHolder.tvGroupChildPosition.setText("" + device.getChildList().size());
        groupHolder.tvGroupChildSum.setText("/" + device.getChildList().size());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_device_manage_child, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        childHolder = (ChildHolder) convertView.getTag();

        final DeviceManageChild child = deviceList.get(groupPosition).getChildList().get(childPosition);
        final String groupname = deviceList.get(groupPosition).getGroupTitle();
        childHolder.ivChildImage.setImageResource(child.getResId());
        childHolder.tvChildTitle.setText(child.getDevice().getName());
        //解绑设备不显示房间名称和备注
        if (child.getDevice().getDismiss()) {
            childHolder.tvRoomName.setText("");
            childHolder.tvComment.setText("");
        } else {
            //modify by sunzhibin
            childHolder.tvRoomName.setText(Constant.getRoomByRoomId(child.getDevice().getRoomId()));
            childHolder.tvComment.setText(child.getDevice().getNote());
        }

        L.e("DeviceManageAdapter_child_dismiss===" + child.getDevice().getDismiss());
        if (child.getDevice().getDismiss()) {//解除绑定
//            childHolder.llRebind.setVisibility(View.VISIBLE);
            childHolder.llUnbind.setVisibility(View.GONE);
            childHolder.llDelete.setVisibility(View.VISIBLE);
        } else {
//            childHolder.llRebind.setVisibility(View.GONE);
            childHolder.llUnbind.setVisibility(View.VISIBLE);
            childHolder.llDelete.setVisibility(View.GONE);
        }

       /* childHolder.llRebind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceManageListeningActivity.instance.rebindDevice(child.getDevice());
            }
        });*/

        childHolder.llUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.unbindClick(child.getDevice());
//                DeviceManageListeningActivity.instance.dismissDevice(child.getDevice());
            }
        });

        childHolder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteClick(child.getDevice());
//                DeviceManageListeningActivity.instance.removeDevice(child.getDevice(),groupname);
            }
        });

        childHolder.mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                if (currentExpandedSwipeLayout != null && currentExpandedSwipeLayout != layout)
                    currentExpandedSwipeLayout.close(true);

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                currentExpandedSwipeLayout = layout;


            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        public TextView tvGroupName;
        public TextView tvGroupChildPosition;
        public TextView tvGroupChildSum;
        public View viewTop;

        public GroupHolder(View convertView) {
            this.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
            this.tvGroupChildPosition = (TextView) convertView.findViewById(R.id.tvGroupChildPosition);
            this.tvGroupChildSum = (TextView) convertView.findViewById(R.id.tvGroupChildSum);
            this.viewTop = convertView.findViewById(R.id.viewTop);
        }
    }

    class ChildHolder {
        public ImageView ivChildImage;
        public TextView tvChildTitle;
        public TextView tvRoomName;
        public TextView tvComment;
        public LinearLayout llUnbind, llDelete;
        public SwipeLayout mSwipeLayout;

        public ChildHolder(View convertView) {
            this.ivChildImage = (ImageView) convertView.findViewById(R.id.ivChildImage);
            this.tvChildTitle = (TextView) convertView.findViewById(R.id.tvChildTitle);
            this.tvRoomName = (TextView) convertView.findViewById(R.id.tvRoomName);
            this.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
            this.llUnbind = (LinearLayout) convertView.findViewById(R.id.llUnbind);
            this.llDelete = (LinearLayout) convertView.findViewById(R.id.llDelete);

            //滑动
            this.mSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            this.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            this.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, this.mSwipeLayout.findViewWithTag("Bottom2"));
        }
    }

    public interface ClickListener {
        void unbindClick(Device device);

        void deleteClick(Device device);
    }
}
