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
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.Loger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 关联设备
 * create at 2016/7/12 13:52
 */
public class RelateDeviceGridAdapter extends BaseAdapter {
    private final ItemClickListener mListener;
    private final Context context;
    private final Device mDevice;
    private LayoutInflater inflater;
    private List<Map<String, Object>> list;

    public RelateDeviceGridAdapter(Context context, List<Map<String, Object>> list, Device device, ItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.mListener = listener;
        this.mDevice = device;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_relate_device, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        final Map<String, Object> map = list.get(position);
        holder.tvLightName.setText((String) map.get("name"));
        holder.ivSwitch.setChecked((Boolean) map.get("isOpen"));

        if ((Boolean) map.get("isSelected")) {
            holder.ivSelected.setImageResource(R.drawable.ic_check);
        } else {
            holder.ivSelected.setImageResource(R.drawable.ic_uncheck);
        }

        holder.llSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //该方法保证每次点击主灯都选中，不能取消选中状态
//                if ((boolean) map.get("isSelected")) {
//                    clearSelected();
//                } else {
                    clearSelected();
                    map.put("isSelected", !(Boolean) map.get("isSelected"));
//                }

                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.click(position);
                }
            }
        });

        holder.ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.put("isOpen", !(Boolean) map.get("isOpen"));
                notifyDataSetChanged();

                boolean open = (Boolean) map.get("isOpen");
                sendControl(mDevice, position + 1, open ? "1" : "0");
            }
        });

        return convertView;
    }

    private void clearSelected() {
        for (Map<String, Object> map : list) {
            map.put("isSelected", false);
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
        DeviceController.getInstance().deviceControl(context, controlDevices, false,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d(Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d(Json);
                    }
                });
    }

    public interface ItemClickListener {
        void click(int pos);
    }

    class ViewHolder {
        public TextView tvLightName;
        public ToggleButton ivSwitch;
        public ImageView ivSelected;
        public LinearLayout llSelected;

        public ViewHolder(View convertView) {
            this.tvLightName = (TextView) convertView.findViewById(R.id.tvLightName);
            this.ivSwitch = (ToggleButton) convertView.findViewById(R.id.ivSwitch);
            this.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
            this.llSelected = (LinearLayout) convertView.findViewById(R.id.llSelected);
        }
    }
}
