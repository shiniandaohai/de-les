package com.boer.delos.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/2 0002 14:31
 * @Modify:
 * @ModifyDate:
 */


public class ClassifyDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private Map<String, List<DeviceRelate>> mListMap;
    private List<String> mKeyList;

    public List<String> getKeyList() {
        return mKeyList;
    }

    public ClassifyDeviceAdapter(Context context, Map<String, List<DeviceRelate>> mListMap) {
        mContext = context;
        this.mListMap = mListMap;
        mKeyList = getKeyfromMap();
        this.mListMap = getListMap(mListMap);
    }

    public void setData(Map<String, List<DeviceRelate>> mListMap) {
        this.mListMap = getListMap(mListMap);
        notifyDataSetChanged();
    }

    private ArrayMap<String, List<DeviceRelate>> getListMap(Map<String, List<DeviceRelate>> maplist){
        ArrayMap<String, List<DeviceRelate>> newmaplist = new ArrayMap<String,List<DeviceRelate>>() ;
        List<String> keylist = getKeyfromMap();
        int airindex = keylist.indexOf(mContext.getString(R.string.air_clean));
        int waterindex = keylist.indexOf(mContext.getString(R.string.water_clean));
        int lightindex = keylist.indexOf(mContext.getString(R.string.light_control));
        int curtainindex = keylist.indexOf(mContext.getString(R.string.curtain_control));
        newmaplist.put(keylist.get(airindex),maplist.get(keylist.get(airindex))==null?new ArrayList<DeviceRelate>():maplist.get(keylist.get(airindex)));
        newmaplist.put(keylist.get(waterindex),maplist.get(keylist.get(waterindex))==null?new ArrayList<DeviceRelate>():maplist.get(keylist.get(waterindex)));
        newmaplist.put(mContext.getString(R.string.light_control),maplist.get(keylist.get(lightindex))==null?new ArrayList<DeviceRelate>():maplist.get(keylist.get(lightindex)));
        newmaplist.put(mContext.getString(R.string.curtain_control),maplist.get(keylist.get(curtainindex))==null?new ArrayList<DeviceRelate>():maplist.get(keylist.get(curtainindex)));
        for(int i=0; i<maplist.size();i++){
            if(i!=airindex && i!= waterindex && i!=lightindex && i!=curtainindex){
                newmaplist.put(keylist.get(i),maplist.get(keylist.get(i)) == null? new ArrayList<DeviceRelate>():maplist.get(keylist.get(i)));
            }
        }
        return newmaplist;
    }

    private List<String> getKeyfromMap() {
        List<String> keyList = new ArrayList<>();
        /*Set<String> keySet = mListMap.keySet();
        if (keySet == null) {
            return Collections.emptyList();
        }
        for (String s : keySet) {
            keyList.add(s);
        }*/
        for(int i=0; i< ConstantDeviceType.group_types.length;i++){
            keyList.add(ConstantDeviceType.group_types[i]);
        }
        return keyList;
    }

    @Override
    public int getCount() {
        return mListMap == null ? 0 : mListMap.size();
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

        private void updateHolder(String key, Map<String, List<DeviceRelate>> mListMap) {
            if (TextUtils.isEmpty(key)) {
                return;
            }
            if (mListMap == null || mListMap.size() == 0) {
                return;
            }

            tv_room_name.setText(key);
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
                if ( status.getOffline()==1) {
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
