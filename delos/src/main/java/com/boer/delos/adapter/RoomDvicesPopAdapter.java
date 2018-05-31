package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.Device;
import com.boer.delos.utils.StringUtil;

import java.util.List;

public class RoomDvicesPopAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<Device> devices;
    private int tag;
    private Context mContext;
    public RoomDvicesPopAdapter(Context context, int tag) {
        this.tag = tag;
        inflater = LayoutInflater.from(context);
        mContext=context;
    }

    public void setDatas(List<Device> devices) {
        this.devices = devices;
    }


    @Override
    public int getCount() {

        return devices.size();

    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_scence_edit, null);
            viewHolder = new ViewHolder();
            viewHolder.itemSceneImage = (ImageView) convertView.findViewById(R.id.ivScenceItemImage);
            viewHolder.itemSceneText = (TextView) convertView.findViewById(R.id.tvScenceItemText);
            viewHolder.itemSceneCheck = (ImageView) convertView.findViewById(R.id.ivPopupItemCheck);
            viewHolder.tvScenceItemRoom=(TextView) convertView.findViewById(R.id.tvScenceItemRoom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device device = devices.get(position);

        AddDevice addDevice = Constant.getDeviceNonCircleResWithType(device.getType());

        viewHolder.itemSceneText.setText("");
        if (addDevice != null) {
            viewHolder.itemSceneImage.setImageResource(addDevice.getResId());
            viewHolder.itemSceneCheck.setTag(position);
        }

        viewHolder.itemSceneText.setText(StringUtil.isEmpty(device.getName())?mContext.getString(addDevice.getItemText()):device.getName());





        if (tag == 1) {
            viewHolder.itemSceneCheck.setVisibility(device.isChecked() ? View.VISIBLE : View.GONE);
        } else {
            viewHolder.itemSceneCheck.setVisibility(device.isChecked() ? View.VISIBLE : View.GONE);
        }

        if(device!=null&&device.getDismiss()!=null&&!device.getDismiss()&&!device.getRoomId().equals("")){
            viewHolder.tvScenceItemRoom.setText(device.getRoomname()==null?"":device.getRoomname());
        }
        return convertView;
    }


    static class ViewHolder {
        public ImageView itemSceneImage;
        public TextView itemSceneText;
        public ImageView itemSceneCheck;
        public TextView tvScenceItemRoom;
    }


}
