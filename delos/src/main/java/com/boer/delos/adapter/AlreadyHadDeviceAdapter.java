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
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 选择已有的"设备管理"适配器
 * create at 2016/4/7 17:19
 */
public class AlreadyHadDeviceAdapter extends BaseExpandableListAdapter {

    private List<DeviceManage> deviceList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private ClickListener listener;
    private SwipeLayout currentExpandedSwipeLayout;

    public AlreadyHadDeviceAdapter(Context context, List<DeviceManage> deviceList, ClickListener listener) {
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
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.item_already_had_device_group, null);
            groupHolder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
            groupHolder.tvGroupChildPosition = (TextView) convertView.findViewById(R.id.tvGroupChildPosition);
            groupHolder.tvGroupChildSum = (TextView) convertView.findViewById(R.id.tvGroupChildSum);
            groupHolder.viewTop = convertView.findViewById(R.id.viewTop);
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
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.item_already_had_device_child, null);
            childHolder.ivChildImage = (ImageView) convertView.findViewById(R.id.ivChildImage);
            childHolder.tvChildTitle = (TextView) convertView.findViewById(R.id.tvChildTitle);
            childHolder.tvRoomName = (TextView) convertView.findViewById(R.id.tvRoomName);
            childHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment);
            childHolder.llRebind = (LinearLayout) convertView.findViewById(R.id.llRebind);
            childHolder.llUnbind = (LinearLayout) convertView.findViewById(R.id.llUnbind);
            childHolder.llDelete = (LinearLayout) convertView.findViewById(R.id.llDelete);
            childHolder.mSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
            childHolder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            childHolder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, childHolder.mSwipeLayout.findViewWithTag("Bottom2"));

            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        final DeviceManageChild child = deviceList.get(groupPosition).getChildList().get(childPosition);
        childHolder.ivChildImage.setImageResource(child.getResId());
        childHolder.tvChildTitle.setText(child.getDevice().getName());
        childHolder.tvComment.setText(child.getDevice().getNote());
        if (child.getDevice().getDismiss()) {
            //true---已解除绑定
            childHolder.llRebind.setVisibility(View.VISIBLE);
            childHolder.llDelete.setVisibility(View.VISIBLE);
            childHolder.llUnbind.setVisibility(View.GONE);
            childHolder.tvRoomName.setText("");
        } else {//false---未解除绑定
            childHolder.llRebind.setVisibility(View.GONE);
            childHolder.llDelete.setVisibility(View.GONE);
            childHolder.llUnbind.setVisibility(View.VISIBLE);
            childHolder.tvRoomName.setText(Constant.getRoomByRoomId(child.getDevice().getRoomId()));
        }

        childHolder.llRebind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.rebindClick(child.getDevice());
                Device device = child.getDevice();
//                Room currentRoom = AlreadyHadDeviceListeningActivity.instance.getRoom();
//                device.setRoomId(currentRoom.getRoomId());
//                device.setRoomname(currentRoom.getName());
//                L.e("AlreadyHadDeviceAdapter_Rebind===" + new Gson().Object2Json(device));
//                AlreadyHadDeviceListeningActivity.instance.rebindDevice(device);
            }
        });

        childHolder.llUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.unbindClick(child.getDevice());
//                AlreadyHadDeviceListeningActivity.instance.dismissDevice(child.getDevice());
            }
        });

        childHolder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteClick(child.getDevice());
//                AlreadyHadDeviceListeningActivity.instance.removeDevice(child.getDevice());
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
    }

    class ChildHolder {
        public SwipeLayout mSwipeLayout;
        public ImageView ivChildImage;
        public TextView tvChildTitle;
        public TextView tvRoomName;
        public TextView tvComment;
        public LinearLayout llRebind, llUnbind, llDelete;
    }

    public interface ClickListener {
        void rebindClick(Device device);

        void unbindClick(Device device);

        void deleteClick(Device device);
    }
}
