package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Room;

import java.util.List;
import java.util.Map;

/**
 * Created by apple on 17/5/4.
 */

public class LinkAlarmSettingAdapter extends MyBaseAdapter<DeviceRelate> {

    public LinkAlarmSettingAdapter(Context mContext, List<DeviceRelate> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);

        int resId = R.layout.item_alarm_link_plan;
    }

    @Override
    public void convert(MyViewHolder holder, DeviceRelate item, int position) {
        ImageView iv_device_type = holder.getView(R.id.iv_device_type);
        TextView tv_device_name = holder.getView(R.id.tv_device_name);
        TextView tv_device_classify = holder.getView(R.id.tv_device_classify);
        TextView tv_room_name = holder.getView(R.id.tv_room_name);
        Device device = item.getDeviceProp();
        iv_device_type.setImageResource(Constant.getDeviceNonCircleResWithType(device.getType()).getResId());
        tv_device_name.setText(device.getName());
        tv_device_classify.setText(Constant.getDeviceTypeNameWithType(device.getType()));

//        if (!TextUtils.isEmpty(device.getRoomId())&&!device.isDismiss()) {
//            if (!TextUtils.isEmpty(device.getRoomname())) {
//                tv_room_name.setText(device.getRoomname());
//            } else {
//                if (Constant.GATEWAY == null) {
//                    return;
//                }
//                for (Room room : Constant.GATEWAY.getRoom()) {
//                    if (room != null && !TextUtils.isEmpty(room.getRoomId())
//                            && room.getRoomId().equals(device.getRoomId())) {
//                        tv_room_name.setText(room.getName());
//                        break;
//                    }
//                }
//
//            }
//        }

        tv_room_name.setText(device.getRoomname());


    }


}
