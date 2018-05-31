package com.boer.delos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.utils.JudgeNetworkTypeUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/1 0001 10:10
 * @Modify:
 * @ModifyDate:
 */


public class DeviceHomeAdapter extends MyBaseAdapter<DeviceRelate> {
    private boolean wireOffline = false;//背景音乐设备的离线状态

    public DeviceHomeAdapter(Context mContext, List<DeviceRelate> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        //int layoutId = R.layout.item_device_home_show;
    }

    public void setWireOffline(boolean wireOffline) {
        this.wireOffline = wireOffline;
    }

    @Override
    public void convert(MyViewHolder holder, DeviceRelate item, int position) {
        if (item == null) {
            return;
        }
        Device device = item.getDeviceProp();
        DeviceStatus deviceStatus = item.getDeviceStatus();

        holderSetting(holder, device, deviceStatus, position);

        holderUpdate(holder, item);
        clickListener(holder, item);
    }

    private void holderSetting(MyViewHolder holder, Device device, DeviceStatus deviceStatus, int position) {

        ImageView iv_device_type = holder.getView(R.id.iv_device_type);
        TextView tv_device_name = holder.getView(R.id.tv_device_name);
        TextView tv_device_room = holder.getView(R.id.tv_device_classify);

        if (device == null) {
            return;
        }
        if (iv_device_type.getTag() == null
                || StringUtil.isEmpty(iv_device_type.getTag().toString())
                || iv_device_type.getTag().toString().equals(device.getAddr())) {
            iv_device_type.setTag(device.getAddr());
        }

        AddDevice tempAddDevice = null;
        for (AddDevice addDevice : Constant.blueDeviceList()) {
            if (StringUtil.isEmpty(device.getType())) {
                continue;
            }
            if (addDevice.getType().equals(device.getType())) {
                tempAddDevice = addDevice;
                break;
            }
        }
        if (tempAddDevice == null) {
            return;
        }

        tv_device_name.setText(StringUtil.isEmpty(device.getName())
                ? mContext.getString(tempAddDevice.getItemText())
                : device.getName());

        tv_device_room.setText(device.getRoomname());
        iv_device_type.setImageResource(tempAddDevice.getResId());

    }

    private void holderUpdate(MyViewHolder holder, DeviceRelate item) {
        int open = 0;  //标记打开状态的个数
        final CheckBox cb_device_status = holder.getView(R.id.cb_device_status);
        cb_device_status.setBackgroundResource(R.drawable.bg_gray_blue);
        if (null == item || null == item.getDeviceStatus()) {
            return;
        }
        DeviceStatus status = item.getDeviceStatus();//music  Audio

        if(status.getType().equals("N4")||status.getType().equals("CircadianLight")||status.getType().equals("Curtain")){
            cb_device_status.setVisibility(View.GONE);
            return;
        }
        else{
            cb_device_status.setVisibility(View.VISIBLE);
        }

        if (item.getDeviceProp().getType().equals(ConstantDeviceType.AUDIO)
                && !TextUtils.isEmpty(item.getDeviceProp().getBrand())
                && item.getDeviceProp().getBrand().equals(ConstantDeviceType.MUSIC_WISE)) {
            status.setOffline(wireOffline ? 0 : 1); //0:在线。1:离线
            cb_device_status.setChecked(wireOffline);
            cb_device_status.setEnabled(false);

            if(status.getType().equals("Exist")||
                    status.getType().equals("Gsm")||
                    status.getType().equals("CurtainSensor")||
                    status.getType().equals("Fall")||
                    status.getType().equals("Water")||
                    status.getType().equals("Sov")||
                    status.getType().equals("SOS")||
                    status.getType().equals("AcoustoOpticAlarm")){
                if(TextUtils.isEmpty(status.getValue().getState())){
                    cb_device_status.setText("未报警");
                    cb_device_status.setTextColor(mContext.getResources().getColor(R.color.gray_et_hint));
                    cb_device_status.setBackgroundResource(R.drawable.shape_corner_gray_offline);
                }
                else{
                    cb_device_status.setText(status.getValue().getState().equals("1")
                            ? "报警中"
                            : "未报警");
                    cb_device_status.setTextColor(status.getValue().getState().equals("1") ?
                            mContext.getResources().getColor(R.color.white)
                            : mContext.getResources().getColor(R.color.gray_et_hint));
                    cb_device_status.setBackgroundResource(status.getValue().getState().equals("1") ?
                            R.drawable.shape_corner_red_alarm:R.drawable.shape_corner_gray_offline);
                }
            }
            else{
                cb_device_status.setText(cb_device_status.isChecked()
                        ? mContext.getString(R.string.status_device_on)
                        : mContext.getString(R.string.status_device_off));

                cb_device_status.setTextColor(cb_device_status.isChecked() ?
                        mContext.getResources().getColor(R.color.white)
                        : mContext.getResources().getColor(R.color.gray_et_hint));
            }
            return;
        }
        if (status.getOffline() == 1) {
            cb_device_status.setText(mContext.getString(R.string.status_device_offline));
            cb_device_status.setTextColor(mContext.getResources().getColor(R.color.gray_et_hint));
            cb_device_status.setChecked(false);
            return;
        }

        DeviceStatusValue value = status.getValue();
        if (value == null) {
            return;
        }
        if (status.getType().contains("Light")) {
            if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                //一联灯
                open++;
            } else if (!TextUtils.isEmpty(value.getState2()) && value.getState2().equals("1")) {
                //二联灯
                open++;
            } else if (!TextUtils.isEmpty(value.getState3()) && value.getState3().equals("1")) {
                //三联灯
                open++;
            } else if (!TextUtils.isEmpty(value.getState4()) && value.getState4().equals("1")) {
                //四联灯
                open++;
            }
            //窗帘
        } else if (item.getDeviceProp().getType().equals(ConstantDeviceType.CURTAIN)) {
            if (!TextUtils.isEmpty(value.getOpen()) && value.getOpen().equals("1")) {
                open++;
            }
            //部分传感器
        } else if (value.getSet() != null && value.getSet() == 1) {
            open++;
        } else if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
            open++;
        }

        cb_device_status.setChecked(open != 0);
        cb_device_status.setEnabled(false);

        Integer mod = value.getMode();
        if(mod!=null&&status.getType().equals("AirFilter")){
            if (mod == 255 || mod == 1 || mod == 0) {
                cb_device_status.setChecked(false);
            } else {
                cb_device_status.setChecked(true);
            }
        }

        if(status.getType().equals("Exist")||
                status.getType().equals("Gsm")||
                status.getType().equals("CurtainSensor")||
                status.getType().equals("Fall")||
                status.getType().equals("Water")||
                status.getType().equals("Sov")||
                status.getType().equals("SOS")||
                status.getType().equals("AcoustoOpticAlarm")){
                if(TextUtils.isEmpty(status.getValue().getState())){
                    cb_device_status.setText("未报警");
                    cb_device_status.setTextColor(mContext.getResources().getColor(R.color.gray_et_hint));
                    cb_device_status.setBackgroundResource(R.drawable.shape_corner_gray_offline);
                }
                else{
                    cb_device_status.setText(status.getValue().getState().equals("1")
                            ? "报警中"
                            : "未报警");
                    cb_device_status.setTextColor(status.getValue().getState().equals("1") ?
                            mContext.getResources().getColor(R.color.white)
                            : mContext.getResources().getColor(R.color.gray_et_hint));
                    cb_device_status.setBackgroundResource(status.getValue().getState().equals("1") ?
                            R.drawable.shape_corner_red_alarm:R.drawable.shape_corner_gray_offline);
                }
        }
        else{
            cb_device_status.setText(cb_device_status.isChecked()
                    ? mContext.getString(R.string.status_device_on)
                    : mContext.getString(R.string.status_device_off));
            cb_device_status.setTextColor(cb_device_status.isChecked() ?
                    mContext.getResources().getColor(R.color.white)
                    : mContext.getResources().getColor(R.color.gray_et_hint));
//            cb_device_status.setBackgroundResource(R.drawable.bg_gray_blue);
        }
    }

    private void clickListener(MyViewHolder holder, final DeviceRelate item) {
        holder.setOnClickListener(R.id.tv_device_classify, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置房间
                Bundle bundle = new Bundle();
                bundle.putSerializable("device", item);
                String type = item.getDeviceProp().getType()
                        + (TextUtils.isEmpty(item.getDeviceProp().getBrand())
                        ? ""
                        : item.getDeviceProp().getBrand());

                if(type.equals("AudioWise")){
                    if(!JudgeNetworkTypeUtils.judgeIsSameNetwork(mContext.getApplicationContext())){
                        ToastHelper.showShortMsg("请确认您的手机和华尔斯背景音乐设备在同一wifi下！");
                        return;
                    }
                }

                Intent intent = ConstantDeviceType.startActivityByType(mContext, type);
                if (intent != null) {
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
    }


}
