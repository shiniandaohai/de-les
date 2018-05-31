package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 15:44
 * @Modify:
 * @ModifyDate:
 */


public class CommonDeviceAdapter extends MyBaseAdapter<DeviceRelate> {
    private List<Boolean> flagList;
    private List<DeviceRelate> selectData;
    private ISimpleInterfaceInt listener;

    public CommonDeviceAdapter(Context mContext, List<DeviceRelate> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int resId = R.layout.item_common_device;
        selectData = new ArrayList<>();
        initFlags(listData.size(), -1);
    }

    @Override
    public void setDatas(List<DeviceRelate> listData) {
        super.setDatas(listData);
        initFlags(listData.size(), -1);
    }

    @Override
    public void convert(MyViewHolder holder, DeviceRelate item, final int position) {
        ImageView iv_device = holder.getView(R.id.iv_device);
        TextView tv_device_name = holder.getView(R.id.tv_device_name);
        TextView tv_device_room = holder.getView(R.id.tv_device_classify);
        final CheckedTextView ctv_choice = holder.getView(R.id.ctv_choice);

        if (item == null || item.getDeviceProp() == null) {
            return;
        }
        Device tempDevice = item.getDeviceProp();
        AddDevice addDevice = settingItem(tempDevice.getType());

        if (addDevice == null) {
            return;
        }

        iv_device.setImageResource(addDevice.getResId());
        tv_device_name.setText(TextUtils.isEmpty(tempDevice.getName()) ? addDevice.getType() : tempDevice.getName());
        tv_device_room.setText(tempDevice.getRoomname());

        ctv_choice.setVisibility(!flagList.get(position) ? View.GONE : View.VISIBLE);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctv_choice.toggle();
                initFlags(-1, position);
                ctv_choice.setVisibility(!flagList.get(position) ? View.GONE : View.VISIBLE);

                if (listener != null) {
                    listener.clickListener(-1);

                }
            }
        });

    }

    private AddDevice settingItem(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        List<AddDevice> addDeviceList = Constant.blueDeviceList();
        for (AddDevice addDevice : addDeviceList) {

            if (addDevice.getType().equals(type)) {
                return addDevice;
            }
        }
        return null;
    }

    private void initFlags(int size, int position) {
        if (flagList == null) {
            flagList = new ArrayList<>();
        }
        if (position == -1) {
            flagList.clear();

            for (int i = 0; i < size; i++) {
                DeviceRelate deviceRelate = listData.get(i);
                if (deviceRelate != null
                        && deviceRelate.getDeviceProp() != null
                        && !TextUtils.isEmpty(deviceRelate.getDeviceProp().getFavorite())
                        && deviceRelate.getDeviceProp().getFavorite().equals("1")) {
                    flagList.add(true);
                } else
                    flagList.add(false);
            }
        } else {
            flagList.set(position, !flagList.get(position));
        }

        settingSelectDatas();
    }

    private void settingSelectDatas() {
        int index = 0;
        selectData.clear();
        for (boolean s : flagList) {
            if (s) {
                if (selectData == null) selectData = new ArrayList<>();
                selectData.add(listData.get(index));
            }
            index++;

        }
    }

    public List<DeviceRelate> getSelectData() {
        return selectData;
    }

    public void clearAll() {
        initFlags(listData.size(), -1);
        notifyDataSetChanged();
    }

    public void setListener(ISimpleInterfaceInt listener) {
        this.listener = listener;
    }
}
