package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Link;
import com.boer.delos.model.ModeAct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 联动管理界面list的adapter
 * create at 2016/4/12 9:47
 */
public class LinkListAdapter extends BaseAdapter {
    private Context context;
    private List<Link> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private static final String Tag = "LinkListAdapter";

    private ModeAct modeAct;
    private Map<Integer, Boolean> flagMap = new HashMap<>();

    public void setFlagMap(Map<Integer, Boolean> flagMap) {
        this.flagMap = flagMap;
        notifyDataSetChanged();
    }

    public LinkListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setDatas(List<Link> datas) {
        this.datas = datas;
        notifyDataSetChanged();
//        L.e("LinkListAdapter's datas=================================" + new Gson().Object2Json(datas));
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
            convertView = inflater.inflate(R.layout.item_link_manage, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Link linkManage = datas.get(position);
        String tag = linkManage.getTag();
        if (null == tag) {
            viewHolder.tvModel.setText(linkManage.getName());
        } else {
            if (!tag.contains("模式") || !tag.contains("模式配置")) {
                tag += "模式";
            }
            viewHolder.tvModel.setText(tag);
        }
//        viewHolder.tvModel.append(context.getResources().readString(R.string.model));
        if (position != 0 && position == datas.size() - 1) {
            viewHolder.line.setVisibility(View.GONE);
        } else {
            viewHolder.line.setVisibility(View.VISIBLE);
        }

        if (flagMap.size() != 0 && flagMap.size() == datas.size() && flagMap.get(position)) {
            viewHolder.img_timer_task_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img_timer_task_status.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tvModel;
        private ImageView img_timer_task_status;
        private View line;

        public ViewHolder(View convertView) {
            tvModel = (TextView) convertView.findViewById(R.id.tvModel);
            line = (View) convertView.findViewById(R.id.line);
            img_timer_task_status = (ImageView) convertView.findViewById(R.id.img_timer_task_status);
        }


    }

}
