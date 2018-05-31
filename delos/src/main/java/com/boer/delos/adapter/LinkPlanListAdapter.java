package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 联动预案配置界面的adapter
 * create at 2016/4/12 9:50
 */
public class LinkPlanListAdapter extends BaseAdapter {
    private Context context;
    private List<Device> datas = new ArrayList<>();
    private LayoutInflater inflater;


    public LinkPlanListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Device> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.item_link_plan, null);
            viewHolder = new ViewHolder();
            viewHolder.tvRoomName = (TextView) convertView.findViewById(R.id.tvRoomName);
            viewHolder.tvDeviceType = (TextView) convertView.findViewById(R.id.tvDeviceType);
            viewHolder.tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device linkPlan = datas.get(position);
        viewHolder.tvRoomName.setText(linkPlan.getRoomname());
        switch (linkPlan.getType()) {
            case "Water":
                viewHolder.tvDeviceType.setText(context.getString(R.string.water_name));
                break;
            case "Env":
                viewHolder.tvDeviceType.setText(context.getString(R.string.env_name));
                break;
            case "Fall":
                viewHolder.tvDeviceType.setText(context.getString(R.string.fall_name));
                break;
            case "Exist":
                viewHolder.tvDeviceType.setText(context.getString(R.string.exist_name));
                break;
            case "Smoke":
                viewHolder.tvDeviceType.setText(context.getString(R.string.smoke));
                break;
            case "Ch4CO":
                viewHolder.tvDeviceType.setText(context.getString(R.string.Ch4CO));
                break;
            case "SOS":
                viewHolder.tvDeviceType.setText(context.getString(R.string.sos_name));
                break;
            case "Lock":
                viewHolder.tvDeviceType.setText(context.getString(R.string.lock_name));
                break;
        }
        viewHolder.tvDeviceName.setText(linkPlan.getName());
        return convertView;
    }

    class ViewHolder {
        public TextView tvRoomName, tvDeviceType, tvDeviceName;
    }
}
