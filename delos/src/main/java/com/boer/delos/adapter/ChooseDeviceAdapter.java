package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 选择设备界面的adapter
 * create at 2016/4/12 14:49
 */
public class ChooseDeviceAdapter extends BaseAdapter {
    private List<Map<String, Object>> datas = new ArrayList<>();
    private LayoutInflater inflater;

    public ChooseDeviceAdapter(Context context, List<Map<String, Object>> list) {
        inflater = LayoutInflater.from(context);
        this.datas = list;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Map<String, Object> getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_scene_mode, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


//        Map<String, Object> map = datas.get(position);

        viewHolder.updateHolder(getItem(position), position);
//        Device chooseDevice = (Device) map.get("device");
//
//        Boolean isChecked = (Boolean) map.get("checked");

        return convertView;
    }

    class ViewHolder {
        private TextView tv_device_name;
        private TextView tv_device_classify;
        private CheckedTextView ctv_choice;
        private ImageView iv_device_type;
        private CheckedTextView toggleButtonAll;
        private TextView tv_device_room;

        public ViewHolder(View view) {

            tv_device_name = (TextView) view.findViewById(R.id.tv_device_name);
            tv_device_room = (TextView) view.findViewById(R.id.tv_device_room);
            tv_device_classify = (TextView) view.findViewById(R.id.tv_device_classify);
            ctv_choice = (CheckedTextView) view.findViewById(R.id.ctv_choice);
            iv_device_type = (ImageView) view.findViewById(R.id.iv_device_type);
            toggleButtonAll = (CheckedTextView) view.findViewById(R.id.toggleButtonAll);

            toggleButtonAll.setVisibility(View.GONE);
            tv_device_room.setVisibility(View.VISIBLE);
        }

        private void updateHolder(final Map<String, Object> item, int position) {
            Device chooseDevice = (Device) item.get("device");
//
            Boolean isChecked = (Boolean) item.get("checked");

            tv_device_name.setText(chooseDevice.getName());
            tv_device_room.setText(chooseDevice.getRoomname());
            tv_device_classify.setText(Constant.getControlTypeByType(chooseDevice.getType()));
            if (Constant.getDeviceNonCircleResWithType(chooseDevice.getType()) != null) {
                iv_device_type.setImageResource(Constant.getDeviceNonCircleResWithType(chooseDevice.getType()).getResId());
            }
            ctv_choice.setChecked(isChecked);

            ctv_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ctv_choice.toggle();
                    item.put("checked", ctv_choice.isChecked());
                }
            });
        }
    }
}
