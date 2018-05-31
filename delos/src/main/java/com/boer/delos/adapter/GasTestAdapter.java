package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "气体检测"等界面的adapter
 * create at 2016/6/6 16:33
 *
 */
public class GasTestAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<DeviceRelate> list = new ArrayList<>();

    public GasTestAdapter(Context context, List<DeviceRelate> list) {
        this.context = context;
        this.list = list;
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
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_gas_test, null);
            holder.llAlarm = (LinearLayout) convertView.findViewById(R.id.llAlarm);
            holder.llPM = (LinearLayout) convertView.findViewById(R.id.llPM);
            holder.llHumid = (LinearLayout) convertView.findViewById(R.id.llHumid);
            holder.llTemp = (LinearLayout) convertView.findViewById(R.id.llTemp);
            holder.llFormalin = (LinearLayout) convertView.findViewById(R.id.llFormalin);
            holder.llOxygen = (LinearLayout) convertView.findViewById(R.id.llOxygen);
            holder.llCarbon = (LinearLayout) convertView.findViewById(R.id.llCarbon);
            holder.llCO = (LinearLayout) convertView.findViewById(R.id.llCO);
            holder.llCH4 = (LinearLayout) convertView.findViewById(R.id.llCH4);
            holder.llConcentration = (LinearLayout) convertView.findViewById(R.id.llConcentration);
            holder.tvItemGroup = (TextView) convertView.findViewById(R.id.tvItemGroup);
            holder.tvPMValue = (TextView) convertView.findViewById(R.id.tvPMValue);
            holder.tvHumid = (TextView) convertView.findViewById(R.id.tvHumid);
            holder.tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
            holder.tvFormalin = (TextView) convertView.findViewById(R.id.tvFormalin);
            holder.tvOxygen = (TextView) convertView.findViewById(R.id.tvOxygen);
            holder.tvCarbon = (TextView) convertView.findViewById(R.id.tvCarbon);
            holder.tvCO = (TextView) convertView.findViewById(R.id.tvCO);
            holder.tvCH4 = (TextView) convertView.findViewById(R.id.tvCH4);
            holder.tvConcentration = (TextView) convertView.findViewById(R.id.tvConcentration);
            holder.ivBlackPoint = (ImageView) convertView.findViewById(R.id.ivBlackPoint);
            holder.ivRedPoint = (ImageView) convertView.findViewById(R.id.ivRedPoint);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置数据
        DeviceStatus deviceStatus = list.get(position).getDeviceStatus();// 设备状态
        Device device = list.get(position).getDeviceProp();// 设备属性
        if (deviceStatus != null) {
            DeviceStatusValue deviceStatusValue = deviceStatus.getValue();
            if ("Ch4CO".equals(device.getType())) {
                holder.tvItemGroup.setText("危险气体");
                holder.llAlarm.setVisibility(View.VISIBLE);
                holder.llCO.setVisibility(View.VISIBLE);
                holder.llCH4.setVisibility(View.VISIBLE);
                holder.llPM.setVisibility(View.GONE);
                holder.llHumid.setVisibility(View.GONE);
                holder.llTemp.setVisibility(View.GONE);
                holder.llFormalin.setVisibility(View.GONE);
                holder.llOxygen.setVisibility(View.GONE);
                holder.llCarbon.setVisibility(View.GONE);
                holder.llConcentration.setVisibility(View.GONE);

                if (deviceStatusValue != null) {
                    if (!StringUtil.isEmpty(deviceStatusValue.getState()) && "1".equals(deviceStatusValue.getState())) {
                        holder.ivBlackPoint.setVisibility(View.GONE);
                        holder.ivRedPoint.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivBlackPoint.setVisibility(View.VISIBLE);
                        holder.ivRedPoint.setVisibility(View.GONE);
                    }
                    if (StringUtil.isEmpty(deviceStatusValue.getCo())) {
                        holder.tvCO.setText("0");
                    } else {
                        holder.tvCO.setText(deviceStatusValue.getCo());
                    }

                    if (StringUtil.isEmpty(deviceStatusValue.getCh4())) {
                        holder.tvCH4.setText("0");
                    } else {
                        holder.tvCH4.setText(deviceStatusValue.getCh4());
                    }
                } else {
                    holder.tvCO.setText("0");
                    holder.tvCH4.setText("0");
                    holder.ivBlackPoint.setVisibility(View.VISIBLE);
                    holder.ivRedPoint.setVisibility(View.GONE);
                }
            }
            if ("Smoke".equals(device.getType())) {
                holder.tvItemGroup.setText("烟雾传感器");
                holder.llAlarm.setVisibility(View.VISIBLE);
                holder.llConcentration.setVisibility(View.VISIBLE);
                holder.llPM.setVisibility(View.GONE);
                holder.llHumid.setVisibility(View.GONE);
                holder.llTemp.setVisibility(View.GONE);
                holder.llFormalin.setVisibility(View.GONE);
                holder.llOxygen.setVisibility(View.GONE);
                holder.llCarbon.setVisibility(View.GONE);
                holder.llCO.setVisibility(View.GONE);
                holder.llCH4.setVisibility(View.GONE);

                if (deviceStatusValue != null) {
                    if (!StringUtil.isEmpty(deviceStatusValue.getState()) && "1".equals(deviceStatusValue.getState())) {
                        holder.ivBlackPoint.setVisibility(View.GONE);
                        holder.ivRedPoint.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivBlackPoint.setVisibility(View.VISIBLE);
                        holder.ivRedPoint.setVisibility(View.GONE);
                    }
                    if (StringUtil.isEmpty(deviceStatusValue.getSmoke())) {
                        holder.tvConcentration.setText("0");
                    } else {
                        holder.tvConcentration.setText(deviceStatusValue.getSmoke());
                    }
                } else {
                    holder.tvConcentration.setText("0");
                    holder.ivBlackPoint.setVisibility(View.VISIBLE);
                    holder.ivRedPoint.setVisibility(View.GONE);
                }
            }
            if ("Env".equals(device.getType())) {
                holder.tvItemGroup.setText("环境检测器");
                holder.llAlarm.setVisibility(View.VISIBLE);
                holder.llPM.setVisibility(View.VISIBLE);
                holder.llHumid.setVisibility(View.VISIBLE);
                holder.llTemp.setVisibility(View.VISIBLE);
                holder.llFormalin.setVisibility(View.VISIBLE);
                holder.llOxygen.setVisibility(View.GONE);
                holder.llCarbon.setVisibility(View.GONE);
                holder.llCO.setVisibility(View.GONE);
                holder.llCH4.setVisibility(View.GONE);
                holder.llConcentration.setVisibility(View.GONE);

                if (deviceStatusValue != null) {
                    if (!StringUtil.isEmpty(deviceStatusValue.getState()) && "1".equals(deviceStatusValue.getState())) {
                        holder.ivBlackPoint.setVisibility(View.GONE);
                        holder.ivRedPoint.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivBlackPoint.setVisibility(View.VISIBLE);
                        holder.ivRedPoint.setVisibility(View.GONE);
                    }
                    if (deviceStatusValue.getPm25() != null) {
                        holder.tvPMValue.setText(deviceStatusValue.getPm25() + "");
                    } else {
                        holder.tvPMValue.setText("0");
                    }
                    holder.tvHumid.setText(deviceStatusValue.getHumid());
                    holder.tvTemp.setText(deviceStatusValue.getTemp());
                    holder.tvFormalin.setText(deviceStatusValue.getPa());
                } else {
                    holder.tvPMValue.setText("0");
                    holder.tvHumid.setText("0");
                    holder.tvTemp.setText("0");
                    holder.tvFormalin.setText("0.0");
                    holder.ivBlackPoint.setVisibility(View.VISIBLE);
                    holder.ivRedPoint.setVisibility(View.GONE);
                }
            }
            if ("O2CO2".equals(device.getType())) {
                holder.tvItemGroup.setText("健康气体");
                holder.llOxygen.setVisibility(View.VISIBLE);
                holder.llCarbon.setVisibility(View.VISIBLE);
                holder.llAlarm.setVisibility(View.GONE);
                holder.llPM.setVisibility(View.GONE);
                holder.llHumid.setVisibility(View.GONE);
                holder.llTemp.setVisibility(View.GONE);
                holder.llFormalin.setVisibility(View.GONE);
                holder.llCO.setVisibility(View.GONE);
                holder.llCH4.setVisibility(View.GONE);
                holder.llConcentration.setVisibility(View.GONE);

                if (deviceStatusValue != null) {
                    if (!StringUtil.isEmpty(deviceStatusValue.getO2())) {
                        holder.tvOxygen.setText(deviceStatusValue.getO2());
                    } else {
                        holder.tvOxygen.setText("0");
                    }
                    if (deviceStatusValue.getCo2() != null) {
                        holder.tvCarbon.setText(deviceStatusValue.getCo2() + "");
                    } else {
                        holder.tvCarbon.setText("0");
                    }
                } else {
                    holder.tvOxygen.setText("0");
                    holder.tvCarbon.setText("0");
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        public LinearLayout llAlarm,  llPM, llHumid, llTemp, llFormalin, llOxygen, llCarbon, llCO, llCH4, llConcentration;
        public TextView tvItemGroup, tvPMValue, tvHumid,tvTemp, tvFormalin, tvOxygen, tvCarbon, tvCO, tvCH4, tvConcentration;
        public ImageView ivBlackPoint, ivRedPoint;
    }
}
