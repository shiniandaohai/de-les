package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/12 0012 19:31
 * @Modify:
 * @ModifyDate:
 */


public class ClassDeviceRoomAdapter extends BaseAdapter {
    private Context mContext;
    private Map<Room, List<DeviceRelate>> mListMap;
    private List<Room> mKeyList;

    public List<Room> getKeyList() {
        return mKeyList;
    }

    public ClassDeviceRoomAdapter(Context context, Map<Room, List<DeviceRelate>> mListMap) {
        mContext = context;
        this.mListMap = mListMap;
        mKeyList = getKeyfromMap();
    }

    public void setData(Map<Room, List<DeviceRelate>> mListMap) {
        this.mListMap = mListMap;
        mKeyList.clear();
        mKeyList.addAll(getKeyfromMap());
        notifyDataSetChanged();
    }

    private List<Room> getKeyfromMap() {
        List<Room> keyList = new ArrayList<>();
        Set<Room> keySet = mListMap.keySet();
        if (keySet == null) {
            return Collections.emptyList();
        }
        for (Room s : keySet) {
            keyList.add(s);
        }
        return keyList;
    }

    @Override
    public int getCount() {
        return mKeyList == null ? 0 : mKeyList.size();
    }

    @Override
    public List<DeviceRelate> getItem(int position) {
        return mListMap.get(mKeyList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_classify_func, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.updateHolder(mKeyList.get(position), mListMap);

        return convertView;
    }

    static class ViewHolder {
        private TextView tv_room_name;
        private TextView tv_device_num;

        public ViewHolder(View convertView) {
            tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
            tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num);
        }

        private void updateHolder(Room key, Map<Room, List<DeviceRelate>> mListMap) {
            if (TextUtils.isEmpty(key.getType())) {
                return;
            }
            if (mListMap == null || mListMap.size() == 0) {
                return;
            }

            tv_room_name.setText(key.getName());
            tv_device_num.setText(getDeviceStatus(mListMap.get(key)));

        }

        private StringBuffer getDeviceStatus(List<DeviceRelate> list) {
            int open = 0;
            for (DeviceRelate deviceRelate : list) {
                if (deviceRelate == null || deviceRelate.getDeviceStatus() == null) {
                    continue;
                }
                DeviceStatus status = deviceRelate.getDeviceStatus();
                if (status == null) {
                    continue;
                }
                if (status.getOffline() == 1) {
                    continue;
                }
                DeviceStatusValue value = status.getValue();
                if (value == null) {
                    continue;
                }
                if (status.getType().contains("Light")) {
                    if (TextUtils.isEmpty(value.getState()) || value.getState().equals("0")) {
                        //一联灯

                    } else if (TextUtils.isEmpty(value.getState2()) || value.getState2().equals("0")) {
                        //二联灯

                    } else if (TextUtils.isEmpty(value.getState3()) || value.getState3().equals("0")) {
                        //三联灯

                    } else if (TextUtils.isEmpty(value.getState4()) || value.getState4().equals("0")) {
                        //四联灯

                    } else {
                        open++;
                    }

                    value.getState();
                } else if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                    open++;
                }

            }
            StringBuffer sb = new StringBuffer();
            sb.append("(" + open + "/" + list.size() + ")");
            return sb;
        }
    }

}
