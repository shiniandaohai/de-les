package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.Loger;

import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 场景显示界面的adapter
 * create at 2016/4/12 9:51
 */
public class SceneDisplayAdapter extends BaseAdapter {
    //    private List<SceneDisplay> datas=new ArrayList<>();
    private LayoutInflater inflater;
    private List<Map<String, Object>> datas;

    private static final int TYPE_NORMAL = 1; //正常type
    private static final int TYPE_FOOTER = 2; //修正type
    private static final int TYPE_COUNT = 2; //type总数

    public SceneDisplayAdapter(Context context, List<Map<String, Object>> maps) {
        this.inflater = LayoutInflater.from(context);
        this.datas = maps;
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
            convertView = inflater.inflate(R.layout.item_scene_display, null);
            viewHolder = new ViewHolder();
            viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.ivItem);
            viewHolder.tvItemText = (TextView) convertView.findViewById(R.id.tvItemText);
            viewHolder.tvItemCount = (TextView) convertView.findViewById(R.id.tvCount);
//            viewHolder.ivError = (ImageView) convertView.findViewById(R.id.ivError);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> map = datas.get(position);

        viewHolder.ivItem.setImageResource((int) map.get("image"));
        viewHolder.tvItemText.setText((String) map.get("name"));
        viewHolder.tvItemCount.setText((String) map.get("count"));

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        Loger.d("getItemViewType " + position);

        return position == getCount() && position % 2 != 0 ? TYPE_FOOTER : TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }


    class ViewHolder {
        public ImageView ivItem, ivError;
        public TextView tvItemText;
        public TextView tvItemCount;

    }
}
