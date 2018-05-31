package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.RelateDevices;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.widget.MyListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 关联设备
 * create at 2016/7/14 15:23
 */
public class RelateDeviceAdapter extends BaseAdapter {
    private final Context mContext;
    private LayoutInflater inflater;
    private List<RelateDevices> deviceList;

    public RelateDeviceAdapter(Context context, List<RelateDevices> deviceList) {
        this.mContext = context;
        this.deviceList = deviceList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public RelateDevices getItem(int i) {
        return deviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_relate_device_group, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }
        groupHolder = (GroupHolder) convertView.getTag();

        final RelateDevices devices = getItem(i);
        groupHolder.ivItem.setImageResource(devices.getResId());
        groupHolder.tvItemTitle.setText(devices.getGroupTitle());

        groupHolder.settingChildListView(devices, i);

        return convertView;
    }

    /*
        清除所有选中的列表，保留点击的item的状态 by sun
     */
    private void clearAllSelected(int groupPos, int position) {
        for (RelateDevices relateDevices : deviceList) {
            for (int i = 0; i < relateDevices.getChildList().size(); i++) {
                if (i == position)
                    continue;
                relateDevices.getChildList().get(i).put("isSelected", false);
            }
        }
    }

    /**
     * 发送控制命令
     */
    private void sendControl(Device device, int position, String flag) {
        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        if (position == 1) {
            value.setState(flag);
        } else if (position == 2) {
            value.setState2(flag);
        } else if (position == 3) {
            value.setState3(flag);
        } else {
            value.setState4(flag);
        }
        controlDevice.setValue(value);
        controlDevices.add(controlDevice);
        DeviceController.getInstance().deviceControl(mContext, controlDevices, false,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {

                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }


    class ChildAdapter extends BaseAdapter {

        private ItemClickListener mListener;
        private LayoutInflater inflater;
        private List<Map<String, Object>> list;
        private int groupPos;

        public ChildAdapter(Context mContext, List<Map<String, Object>> childList, int groupPos, ItemClickListener listener) {
            this.inflater = LayoutInflater.from(mContext);
            this.list = childList;
            this.mListener = listener;
            this.groupPos = groupPos;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ChildHolder childHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_relate_device_child_list, null);
                childHolder = new ChildHolder(convertView);
                convertView.setTag(childHolder);
            }
            childHolder = (ChildHolder) convertView.getTag();

            childHolder.update(getItem(i), groupPos, i, mListener);

            return convertView;
        }
    }


    public interface ItemClickListener {
        void relateClick(int groupPos, int pos);

        void controlClick(int pos, boolean open);
    }

    class GroupHolder {
        public ImageView ivItem;
        public TextView tvItemTitle;
        public MyListView myListView;

        public GroupHolder(View convertView) {
            this.ivItem = (ImageView) convertView.findViewById(R.id.ivItem);
            this.tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            this.myListView = (MyListView) convertView.findViewById(R.id.lvLightChildList);
        }

        public void settingChildListView(final RelateDevices devices, int groupPos) {

            if (myListView.getAdapter() != null
                    && !StringUtil.isEmpty(myListView.getTag().toString())
                    && myListView.getTag().equals(groupPos + "")) {
                BaseAdapter adapter = (BaseAdapter) myListView.getAdapter();
                adapter.notifyDataSetChanged();
                return;
            }
            ChildAdapter childAdapter = new ChildAdapter(mContext, devices.getChildList(), groupPos,
                    new ItemClickListener() {
                        @Override
                        public void relateClick(int groupPos, int pos) {
                            clearAllSelected(groupPos, pos); //change 保证唯一单选

                            Map<String, Object> map = devices.getChildList().get(pos);
//                        map.put("isSelected", true);
                            map.put("isSelected", !(Boolean) map.get("isSelected"));
                            notifyDataSetChanged();
                        }

                        @Override
                        public void controlClick(int pos, boolean open) {
                            sendControl(devices.getDevice(), pos + 1, open ? "1" : "0");
                        }
                    });

            myListView.setAdapter(childAdapter);
            myListView.setTag(groupPos);

        }
    }

    class ChildHolder {
        private ImageView ivRelate;
        private TextView tvRelateName;
        private ToggleButton ivSwitch;
        private LinearLayout llRelate;

        public ChildHolder(View convertView) {
            ivRelate = (ImageView) convertView.findViewById(R.id.ivRelate);
            tvRelateName = (TextView) convertView.findViewById(R.id.tvRelateName);
            ivSwitch = (ToggleButton) convertView.findViewById(R.id.ivSwitch);
            llRelate = (LinearLayout) convertView.findViewById(R.id.llRelate);
        }

        public void update(final Map<String, Object> map, final int groupPos, final int i, final ItemClickListener mListener) {

            ivRelate.setImageResource((Boolean) map.get("isSelected") ? R.drawable.ic_check : R.drawable.ic_uncheck);
            tvRelateName.setText((String) map.get("name"));
            ivSwitch.setChecked((Boolean) map.get("isOpen"));
            llRelate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mListener != null) {
                        mListener.relateClick(groupPos, i);
                    }
                }
            });
            ivSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.put("isOpen", !(Boolean) map.get("isOpen"));
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.controlClick(i, (Boolean) map.get("isOpen"));
                    }
                }
            });
        }
    }
}
