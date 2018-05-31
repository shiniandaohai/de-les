package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.L;

import java.util.List;


/**
 * @author XieQingTing
 * @Description: 血糖界面grigview的adapter
 * create at 2016/5/26 17:32
 *
 */
public class BloodSugarGridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
    private List<TextView> itemList;

    public BloodSugarGridAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    public void setList(List<String> list) {
        L.e("BloodSugarGridAdapter_list==="+list.size());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        L.i("AAAB"+list.toString());

        ViewHolder viewHolder;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.item_blood_sugar_grid,null);
            viewHolder=new ViewHolder();
            viewHolder.tvBloodGridItem= (TextView) convertView.findViewById(R.id.tvBloodGridItem);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        String num = list.get(position);
        viewHolder.tvBloodGridItem.setText(num);
        return convertView;
    }


    class ViewHolder{
        public TextView tvBloodGridItem;


    }
}
