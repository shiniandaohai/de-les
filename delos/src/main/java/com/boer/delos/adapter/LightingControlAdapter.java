package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.utils.L;
import com.boer.delos.widget.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 照明控制adapter
 * create at 2016/5/5 15:16
 *
 */
public class LightingControlAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context context;
    private List<DeviceRelate> list;
    private LightingControlChildAdapter childAdapter;

    public LightingControlAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<DeviceRelate> list) {
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
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lighting_control, null);
            viewHolder.tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
            viewHolder.lvLightChildList= (MyListView) convertView.findViewById(R.id.lvLightChildList);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DeviceRelate deviceRelate = list.get(position);
        Device device = deviceRelate.getDeviceProp();
        DeviceStatus deviceStatus = deviceRelate.getDeviceStatus();

        viewHolder.tvItemTitle.setText(device.getName());

        final List<HashMap<String, String>> childList=new ArrayList<>();
//        String type = deviceStatus.getType();
        String type = device.getType();
        HashMap<String, String> map = null;
        DeviceStatusValue value = deviceStatus.getValue();
        switch (type) {
            case "Light1":
                if(value == null){
                    value = new DeviceStatusValue();
                    value.setState("0");
                }

                //灯1
                String state = value.getState();
                map = new HashMap<>();
                String name = "灯1";
                if(device.getLightName() != null && device.getLightName().get("name1") != null){
                    name = device.getLightName().get("name1");
                }
                L.e("Light1"+name);
                map.put("name", name);
                map.put("lightName", "name1");
                //加入一个标记，判断调光灯或者灯1，进行不同的控制
                map.put("type","Light1");
                if (state != null && "1".equals(state)) {
                    map.put("state", "1");
                } else {
                    map.put("state", "0");
                }
                childList.add(map);
                break;
            case "LightAdjust":
                if(value == null){
                    value = new DeviceStatusValue();
                    value.setState("0");
                }

                //灯1
                state = value.getState();
                map = new HashMap<>();
                name = "灯1";
                if(device.getLightName() != null && device.getLightName().get("name1") != null){
                    name = device.getLightName().get("name1");
                }
                L.e("LightAdjust"+name);
                map.put("name", name);
                map.put("lightName", "name1");
                //加入一个标记，判断调光灯或者灯1，进行不同的控制
                map.put("type","LightAdjust");
                if (state != null && "1".equals(state)) {
                    map.put("state", "1");
                } else {
                    map.put("state", "0");
                }
                childList.add(map);

                break;
            case "Light2":
                if(value == null){
                    value = new DeviceStatusValue();
                    value.setState("0");
                    value.setState2("0");
                }
                //灯1
                state  = value.getState();
                map = new HashMap<>();
                name = "灯1";
                if(device.getLightName() != null && device.getLightName().get("name1") != null){
                    name = device.getLightName().get("name1");
                }
                map.put("name", name);
                map.put("lightName", "name1");
                if(state != null && "1".equals(state)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯2
                String state2 = value.getState2();
                map = new HashMap<>();
                name = "灯2";
                if(device.getLightName() != null && device.getLightName().get("name2") != null){
                    name = device.getLightName().get("name2");
                }
                map.put("name", name);
                map.put("lightName", "name2");
                if(state2 != null && "1".equals(state2)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);

                break;
            case "Light3":
                if(value == null){
                    value =  new DeviceStatusValue();
                    value.setState("0");
                    value.setState2("0");
                    value.setState3("0");
                }
                //灯1
                state = value.getState();
                map = new HashMap<>();
                name = "灯1";
                if(device.getLightName() != null && device.getLightName().get("name1") != null){
                    name = device.getLightName().get("name1");
                }
                map.put("name", name);
                map.put("lightName", "name1");
                if(state != null && "1".equals(state)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯2
                state2 = value.getState2();
                map = new HashMap<>();
                name = "灯2";
                if(device.getLightName() != null && device.getLightName().get("name2") != null){
                    name = device.getLightName().get("name2");
                }
                map.put("name", name);
                map.put("lightName", "name2");
                if(state2 != null && "1".equals(state2)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯3
                String state3 = value.getState3();
                map = new HashMap<>();
                name = "灯3";
                if(device.getLightName() != null && device.getLightName().get("name3") != null){
                    name = device.getLightName().get("name3");
                }
                map.put("name", name);
                map.put("lightName", "name3");
                if(state3 != null && "1".equals(state3)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                break;
            case "Light4":
                if(value == null){
                    value = new DeviceStatusValue();
                    value.setState("0");
                    value.setState2("0");
                    value.setState3("0");
                    value.setState4("0");
                }

                //灯1
                state = value.getState();
                map = new HashMap<>();
                name = "灯1";
                if(device.getLightName() != null && device.getLightName().get("name1") != null){
                    name = device.getLightName().get("name1");
                }
                map.put("name", name);
                map.put("lightName", "name1");
                if(state != null && "1".equals(state)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯2
                state2 = value.getState2();
                map = new HashMap<>();
                name = "灯2";
                if(device.getLightName() != null && device.getLightName().get("name2") != null){
                    name = device.getLightName().get("name2");
                }
                map.put("name", name);
                map.put("lightName", "name2");
                if(state2 != null && "1".equals(state2)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯3
                state3 = value.getState3();
                map = new HashMap<>();
                name = "灯3";
                if(device.getLightName() != null && device.getLightName().get("name3") != null){
                    name = device.getLightName().get("name3");
                }
                map.put("name", name);
                map.put("lightName", "name3");
                if(state3 != null && "1".equals(state3)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);
                //灯4
                String state4 = value.getState4();
                map = new HashMap<>();
                name = "灯4";
                if(device.getLightName() != null && device.getLightName().get("name4") != null){
                    name = device.getLightName().get("name4");
                }
                map.put("name", name);
                map.put("lightName", "name4");
                if(state4 != null && "1".equals(state4)){
                    map.put("state", "1");
                }else{
                    map.put("state", "0");
                }
                childList.add(map);

                break;
        }

        childAdapter=new LightingControlChildAdapter(this.context,
                childList,
                deviceRelate.getDeviceProp());
        viewHolder.lvLightChildList.setAdapter(childAdapter);
        return convertView;
    }

    public class ViewHolder {
        public TextView tvItemTitle;
        public MyListView lvLightChildList;
    }

}
