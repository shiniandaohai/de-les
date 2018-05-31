package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.widget.MyListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * （点击联动管理界面中的listview条目）模式界面list的adapter
 * Created by ACER~ on 2016/4/11.
 */
/**
 * @author XieQingTing
 * @Description: 联动模式界面的adapter
 * create at 2016/4/12 9:49
 *
 */
public class LinkModelAdapter extends BaseAdapter{

    private Context context;
    private List<Map<String, Object>> datas=new ArrayList<>();
    private LayoutInflater inflater;
    private LinkModelChildAdapter childAdapter;

    public LinkModelAdapter(Context context, List<Map<String, Object>> list) {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.datas = list;
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
        ViewHolder groupHolder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_link_model_group, null);
            groupHolder = new ViewHolder();
            groupHolder.tvLinkGroupName = (TextView) convertView.findViewById(R.id.tvLinkGroupName);
//            groupHolder.tvFromRoom= (TextView) convertView.findViewById(R.id.tvFromRoom);
            groupHolder.lvLinkChild= (MyListView) convertView.findViewById(R.id.lvLinkChild);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> map = (Map<String, Object>)getItem(position);
        ModeDevice device = (ModeDevice) map.get("device");
        String name = device.getDevicename() + "(" + device.getRoomname() + ")";
        groupHolder.tvLinkGroupName.setText(name);

        List<Map<String, String>> list = (List<Map<String, String>>) map.get("list");
        childAdapter=new LinkModelChildAdapter(context, list);
        groupHolder.lvLinkChild.setAdapter(childAdapter);
        return convertView;
    }

    class ViewHolder{
        public TextView tvLinkGroupName;
        public MyListView lvLinkChild;
    }

}
