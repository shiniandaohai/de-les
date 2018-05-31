package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.boer.delos.R;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastUtils;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 电源管理list的adapter
 * create at 2016/5/25 16:47
 */
public class PowerManageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private List<DeviceRelate> list;
    public ViewHolder viewHolder;
    private ToastUtils toastUtils;

    public PowerManageAdapter(Context context, ClickResultListener listener) {
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        toastUtils = new ToastUtils(context);
    }

    public void setList(List<DeviceRelate> list) {
        L.e("PowerManageAdapter_list====" + new Gson().toJson(list));
        this.list = list;
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

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_power_manage, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DeviceRelate power = list.get(position);
        viewHolder.tvPowerTitle.setText(power.getDeviceProp().getName());
        viewHolder.tvPower.setText("0.0W");
        viewHolder.tvElectricEnergy.setText("0.0kWh");
        viewHolder.tvPowerElectric.setText("0.0A");
        viewHolder.tvPowerVoltage.setText("0.0V");

        //设备状态
        final DeviceStatus deviceStatus = power.getDeviceStatus();

        if (deviceStatus != null && deviceStatus.getValue() != null) {

            if (!StringUtil.isEmpty(power.getDeviceStatus().getValue().getP()))
//                viewHolder.tvPower.setText(deviceStatus.getValue().getP() + "W");
                viewHolder.tvPower.setText(saveValidDigits(deviceStatus.getValue().getP(), "W"));

            if (!StringUtil.isEmpty(power.getDeviceStatus().getValue().getEnergy()))
//                viewHolder.tvElectricEnergy.setText(deviceStatus.getValue().getEnergy() + "kWh");
                viewHolder.tvElectricEnergy.setText(saveValidDigits(deviceStatus.getValue().getEnergy(), "Kwh"));

            if (!StringUtil.isEmpty(power.getDeviceStatus().getValue().getI()))
//                viewHolder.tvPowerElectric.setText(deviceStatus.getValue().getI() + "A");
                viewHolder.tvPowerElectric.setText(saveValidDigits(deviceStatus.getValue().getI(), "A"));

            if (!StringUtil.isEmpty(power.getDeviceStatus().getValue().getU()))
//                viewHolder.tvPowerVoltage.setText(deviceStatus.getValue().getU() + "V");
                viewHolder.tvPowerVoltage.setText(saveValidDigits(deviceStatus.getValue().getU(), "V"));
        }

        //开关状态
        if (deviceStatus.getValue() != null && deviceStatus.getValue().getState() != null) {
            String state = deviceStatus.getValue().getState();
            if ("1".equals(state)) {
                viewHolder.tbSwitch.setImageResource(R.drawable.ic_settings_open);
                viewHolder.ivPowerImage.setImageResource(R.drawable.ic_light_on);
            } else {
                viewHolder.tbSwitch.setImageResource(R.drawable.ic_settings_close);
                viewHolder.ivPowerImage.setImageResource(R.drawable.ic_light_off);
            }
        } else {
            viewHolder.tbSwitch.setImageResource(R.drawable.ic_settings_close);
            viewHolder.ivPowerImage.setImageResource(R.drawable.ic_light_off);
        }


        //开关点击事件
        viewHolder.tbSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开关状态
                if (deviceStatus.getValue() != null && deviceStatus.getValue().getState() != null) {
                    String state = deviceStatus.getValue().getState();
                    if ("1".equals(state)) {
                        deviceStatus.getValue().setState("0");
                    } else {
                        deviceStatus.getValue().setState("1");
                    }
                } else {
                    DeviceStatusValue value = new DeviceStatusValue();
                    value.setState("1");
                    deviceStatus.setValue(value);
                }

                PowerManageAdapter.this.notifyDataSetChanged();

                //点击后回调
                listener.clickSwitch(position);
            }
        });
        viewHolder.tvEnergyConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastUtils.showInfoWithStatus("能耗详情被点击了");
            }
        });

        return convertView;
    }

    static class ViewHolder {
        private TextView tvEnergyConsumption, tvElectricEnergy, tvPower,
                tvPowerVoltage, tvPowerElectric, tvPowerTitle;
        private ImageView tbSwitch;
        private ImageView ivPowerImage;

        public ViewHolder(View convertView) {
            tvEnergyConsumption = (TextView) convertView.findViewById(R.id.tvEnergyConsumption);
            tbSwitch = (ImageView) convertView.findViewById(R.id.tbSwitch);
            tvElectricEnergy = (TextView) convertView.findViewById(R.id.tvElectricEnergy);
            tvPower = (TextView) convertView.findViewById(R.id.tvPower);
            tvPowerVoltage = (TextView) convertView.findViewById(R.id.tvPowerVoltage);
            tvPowerElectric = (TextView) convertView.findViewById(R.id.tvPowerElectric);
            ivPowerImage = (ImageView) convertView.findViewById(R.id.ivPowerImage);
            tvPowerTitle = (TextView) convertView.findViewById(R.id.tvPowerTitle);
        }
    }

    public interface ClickResultListener {
        void clickSwitch(int position);
    }

    private synchronized String saveValidDigits(String data, String type) {
        String result = "0";
        result += type;
        try {

            float oldTempData = Float.valueOf(data);
            float newTempData = 0f;

            if (data.contains(".")) {
                String[] tempString = data.split("\\.");
                if (tempString[0].length() >= 3) {
                    if (tempString[0].length() == 3) {
                        newTempData = decimal(oldTempData, 2);
                        return newTempData + type;
                    } else if (tempString[0].length() == 4) {
                        newTempData = decimal(oldTempData, 1);
                        return newTempData + type;
                    } else if (tempString[0].length() == 5) {
                        newTempData = decimal(oldTempData, 0);
                        return newTempData + type;
                    } else {
                        newTempData = oldTempData / 1000;
                        newTempData = decimal(newTempData, 1);
                    }
                    if (type.contains("k") || type.contains("K")) {
                        return newTempData + "mWh";
                    }
                    return newTempData + "k" + type;
                } else {
                    result = data + type;
                    return result;
                }

            } else {
                if (data.length() > 6) {
                    newTempData = decimal(oldTempData, 1);
                }
                if (type.contains("k") || type.contains("K")) {
                    return newTempData + "mWh";
                }
                return newTempData + "k" + type;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * 保存小数点位数
     *
     * @param oldDouble
     * @param scale
     * @return
     */
    private float decimal(float oldDouble, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(oldDouble);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
