package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.widget.BatteryViewSelf;

import java.util.ArrayList;
import java.util.List;


/**
 * @author PengJiYang
 * @Description: "安全告警"界面的adapter
 * create at 2016/6/8 11:25
 */
public class SecurityAlarmAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private List<DeviceRelate> list = new ArrayList<>();

    public SecurityAlarmAdapter(Context context, List<DeviceRelate> list, ClickResultListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public DeviceRelate getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_security_alarm, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        DeviceStatus deviceStatus = getItem(position).getDeviceStatus();
        Device device = getItem(position).getDeviceProp();

        holder.itemHolderShow(device);
        holder.holderSetting(deviceStatus, device);
        holder.holderClickistener(deviceStatus, device, position);
        return convertView;
    }

    class ViewHolder {
        private LinearLayout llSecurityAlarm, llSecurityDefence, llGsmStstus, llElectricity;
        private TextView tvSecurityItemGroup, tvGsmStatus, tvBatteryPercent;
        private ImageView ivSecurityAlarm, iv_lvolt;
        private Button tbSecurityDefence;
        private BatteryViewSelf battery_view;

        public ViewHolder(View convertView) {
            llSecurityAlarm = (LinearLayout) convertView.findViewById(R.id.llSecurityAlarm);
            llSecurityDefence = (LinearLayout) convertView.findViewById(R.id.llSecurityDefence);
            llElectricity = (LinearLayout) convertView.findViewById(R.id.llElectricity);

            tvSecurityItemGroup = (TextView) convertView.findViewById(R.id.tvSecurityItemGroup);
            ivSecurityAlarm = (ImageView) convertView.findViewById(R.id.ivSecurityBlackPoint);
            tbSecurityDefence = (Button) convertView.findViewById(R.id.tbSecurityDefence);
            tbSecurityDefence.setTag(false);

            llGsmStstus = (LinearLayout) convertView.findViewById(R.id.ll_gsmStatus);//门窗磁状态
            tvGsmStatus = (TextView) convertView.findViewById(R.id.tvGsmStatus);
            battery_view = (BatteryViewSelf) convertView.findViewById(R.id.battery_view);
            iv_lvolt = (ImageView) convertView.findViewById(R.id.iv_lvolt);
            tvBatteryPercent = (TextView) convertView.findViewById(R.id.tv_battery_percent);

        }

        protected void itemHolderShow(Device device) {
            if (device == null) return;

            tvSecurityItemGroup.setText(device.getName());
            llGsmStstus.setVisibility(View.GONE);
            llElectricity.setVisibility(View.GONE);
            // 设置数据
            switch (device.getType()) {
                case "Exist":
                    llSecurityAlarm.setVisibility(View.VISIBLE);
                    llSecurityDefence.setVisibility(View.VISIBLE);
                    break;
                case "Fall":
                    llSecurityAlarm.setVisibility(View.GONE);
                    llSecurityDefence.setVisibility(View.GONE);
                    break;
                case "Water":
                    llSecurityAlarm.setVisibility(View.VISIBLE);
                    llSecurityDefence.setVisibility(View.GONE);
                    break;
                case "Sov":
                    llSecurityAlarm.setVisibility(View.GONE);
                    llSecurityDefence.setVisibility(View.VISIBLE);
                    break;
                case "SOS":
                    llSecurityAlarm.setVisibility(View.VISIBLE);
                    llSecurityDefence.setVisibility(View.GONE);
                    break;
                case "Gsm":
                    llSecurityAlarm.setVisibility(View.VISIBLE);
                    llSecurityDefence.setVisibility(View.VISIBLE);

                    llGsmStstus.setVisibility(View.VISIBLE); //打开关闭
                    llElectricity.setVisibility(View.VISIBLE); //电量
                    break;
                case "CurtainSensor":
                    llSecurityAlarm.setVisibility(View.VISIBLE);
                    llSecurityDefence.setVisibility(View.VISIBLE);
                    break;
            }
        }

        protected void holderClickistener(final DeviceStatus deviceStatus,
                                          final Device device, final int position) {
            tbSecurityDefence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /////存在传感器,set是开关,value是状态
                    if ("Exist".equals(device.getType())) {
                        if (deviceStatus != null && deviceStatus.getValue() != null) {
                            if (1 == deviceStatus.getValue().getSet()) {
                                tbSecurityDefence.setTag(false);
                                deviceStatus.getValue().setSet(0);
                            } else {
                                tbSecurityDefence.setTag(true);
                                deviceStatus.getValue().setSet(1);
                            }
                        } else {
                            tbSecurityDefence.setTag(true);

                            DeviceStatusValue value = new DeviceStatusValue();
                            value.setSet(1);
                            deviceStatus.setValue(value);
                        }
                    } else {
                        if (deviceStatus != null && deviceStatus.getValue() != null) {
                            if (1 == deviceStatus.getValue().getSet()) {
                                tbSecurityDefence.setTag(false);
                                deviceStatus.getValue().setSet(0);
                            } else {
                                tbSecurityDefence.setTag(true);
                                deviceStatus.getValue().setSet(1);
                            }
                        } else {
                            tbSecurityDefence.setTag(true);

                            DeviceStatusValue value = new DeviceStatusValue();
                            value.setState("1");
                            deviceStatus.setValue(value);
                        }
                    }
                    tbSecurityDefence.setBackgroundResource((boolean) tbSecurityDefence.getTag()
                            ? R.drawable.ic_settings_open : R.drawable.ic_settings_close);
                    notifyDataSetChanged();

                    listener.clickSwitch(position);
                }
            });

        }

        protected void holderSetting(DeviceStatus deviceStatus, Device device) {
            //布防
            if (deviceStatus != null && deviceStatus.getValue() == null) {
                tbSecurityDefence.setTag(false);
                ivSecurityAlarm.setBackgroundResource(R.mipmap.ic_alarm_off);
                tvGsmStatus.setText("关闭");
                tvBatteryPercent.setText("100%");
                battery_view.setPower(100);
                iv_lvolt.setVisibility(View.GONE);
                return;
            }
            /*布防*/
            if (deviceStatus.getValue().getSet() != null) {
                if (1 == deviceStatus.getValue().getSet()) {
                    tbSecurityDefence.setTag(true);
//                    battery_view.setColor(Color.GREEN);
                } else {
                    tbSecurityDefence.setTag(false);
//                    battery_view.setColor(Color.GRAY);

                }
            } else {
                tbSecurityDefence.setTag(false);
            }
            tbSecurityDefence.setBackgroundResource((boolean) tbSecurityDefence.getTag()
                    ? R.drawable.ic_settings_open : R.drawable.ic_settings_close);

               /*报警*/
            if (!StringUtil.isEmpty(deviceStatus.getValue().getState())) {
                boolean alarm = deviceStatus.getValue().getState().equals("1");
                Loger.d("报警status " + alarm);

                ivSecurityAlarm.setBackgroundResource(alarm
                        ? R.mipmap.ic_alarm_on : R.mipmap.ic_alarm_off);
            } else {
                ivSecurityAlarm.setBackgroundResource(R.mipmap.ic_alarm_off);
            }
               /*状态*/
            if (!StringUtil.isEmpty(deviceStatus.getValue().getPosition())) {
                tvGsmStatus.setText(deviceStatus.getValue().getPosition().equals("1") ? "打开" : "关闭");
            }
             /*电量*/
            if (!StringUtil.isEmpty(deviceStatus.getValue().getLvolt())
                    && deviceStatus.getValue().getLvolt().equals("1")) {
                iv_lvolt.setVisibility(View.VISIBLE);
                battery_view.setVisibility(View.GONE);
            } else if (!StringUtil.isEmpty(deviceStatus.getValue().getLvolt())) {
                tvBatteryPercent.setText(deviceStatus.getValue().getPowerPercent() + "%");
                battery_view.setPower(Integer.valueOf(deviceStatus.getValue().getPowerPercent()));
            }
        }
    }

    //回调接口
    public interface ClickResultListener {
        void clickSwitch(int position);
    }

}
