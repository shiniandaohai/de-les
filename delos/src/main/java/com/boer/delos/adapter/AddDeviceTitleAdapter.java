package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "新增设备"设备类型适配器
 * create at 2016/4/18 9:54
 * @Modify:  //默认第一个设备类型选中
 */
public class AddDeviceTitleAdapter extends RecyclerView.Adapter<AddDeviceTitleAdapter.ViewHolder> implements View.OnClickListener{

    private OnItemClickListener mOnItemClickListener = null;

    private LayoutInflater mInflater;
    private List<String> mDatas;
    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色
    public AddDeviceTitleAdapter(Context context, List<String> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        isClicks = new ArrayList<>();
        for(int i = 0;i<mDatas.size();i++){
            isClicks.add(false);
        }
        //默认第一个设备类型选中
        isClicks.set(0,true);  //modify by sunzhibin
    }

    public void setDatas(List<String> datas) {
        mDatas = datas;
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_add_device_title, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
       // viewHolder.viewSpace = view.findViewById(R.id.viewSpace);
        viewHolder.tvAddDeviceTitle = (TextView) view.findViewById(R.id.tvAddDeviceTitle);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.tvAddDeviceTitle.setText(mDatas.get(i));
        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(i);
        if(isClicks.get(i)){
            viewHolder.tvAddDeviceTitle.setTextColor(Color.parseColor("#00a0e9"));
        }else{
            viewHolder.tvAddDeviceTitle.setTextColor(Color.parseColor("#ffffff"));
        }
 // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0; i <isClicks.size();i++){
                        isClicks.set(i,false);
                    }
                    isClicks.set(i,true);
                    notifyDataSetChanged();
                    mOnItemClickListener.onItemClick(viewHolder.itemView, i);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

       // View viewSpace;
        TextView tvAddDeviceTitle;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
