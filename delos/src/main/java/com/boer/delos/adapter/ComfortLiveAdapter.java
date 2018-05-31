package com.boer.delos.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Room;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author PengJiYang
 * @Description: 首页舒适生活适配器
 * create at 2016/3/14 10:53
 */
public class ComfortLiveAdapter extends BaseAdapter {

    private static final int ROW_NUMBER = 2; //2*4网格
    private ViewPager vp;
    private Context context;
    private List<Room> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    public ComfortLiveAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public ComfortLiveAdapter(Context context, ViewPager vp) {

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.vp = vp;

    }

    public void setDatas(List<Room> datas) {
        this.datas = datas;
        L.e("ComfortLiveAdapter datas======" + new Gson().toJson(datas));
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Room getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comfort_live, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Room device = getItem(position);
        if (null != device.getType()) {
            int deviceImage = Constant.setImage(device.getType(), 0);
            viewHolder.itemImage.setImageResource(deviceImage);
        }
        viewHolder.itemText.setText(device.getName());

        viewHolder.holderUpdate(getItem(position));
        viewHolder.light_show.setVisibility(getItem(position).isLightShow() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    static class ViewHolder {
        private ImageView itemImage;
        private TextView itemText;
        private ImageView light_show;

        public ViewHolder(View convertView) {
            itemImage = (ImageView) convertView.findViewById(R.id.ivItemImage);
            light_show = (ImageView) convertView.findViewById(R.id.light_show);
            itemText = (TextView) convertView.findViewById(R.id.tvItemText);
        }

        public void holderUpdate(Room item) {
            //第三种：推荐，尤其是容量大时
            for (Map.Entry<String, Object> entry : Constant.sRoomLighting.entrySet()) {
                if (item.getRoomId().equals(entry.getKey()) && entry.getValue().equals("on")) {
                    item.setLightShow(true);
                    return;
//                    light_show.setVisibility(View.VISIBLE);
                }
//                    System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            }
            item.setLightShow(false);

        }
    }
}
