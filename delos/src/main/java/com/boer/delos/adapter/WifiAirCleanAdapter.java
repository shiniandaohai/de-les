package com.boer.delos.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.model.WifiAirCleanDeviceInfo;

import java.util.List;

public class WifiAirCleanAdapter extends MyBaseAdapter<WifiAirCleanDeviceInfo> {
    private int curPosition=-1;
    private Activity context;
    public WifiAirCleanAdapter(Activity mContext, List<WifiAirCleanDeviceInfo> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        context=mContext;
    }
    @Override
    public void convert(MyViewHolder holder, WifiAirCleanDeviceInfo item, int position) {
        if (item == null) {
            return;
        }
        holderUpdate(holder, item,position);
        clickListener(holder,item, position);
    }

    private void holderUpdate(MyViewHolder holder, WifiAirCleanDeviceInfo item,int position) {
        holder.setText(R.id.tv_title,item.getNickName());
        holder.setText(R.id.tv_device_num,item.getSn());
        if(item.getDeviceName().equals("T30S")){
            holder.setImageResource(R.id.iv_icon,R.mipmap.ic_t30);
        }
        else if(item.getDeviceName().equals("T66_II")){
            holder.setImageResource(R.id.iv_icon,R.mipmap.ic_t66);
        }
        if(position==curPosition){
            holder.setVisible(R.id.ll_option,true);
        }
        else{
            holder.setVisible(R.id.ll_option,false);
        }
    }

    private void clickListener(final MyViewHolder holder, final WifiAirCleanDeviceInfo item, final int position) {
        holder.setOnClickListener(R.id.iv_option, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curPosition==position){
                    curPosition=-1;
                }
                else{
                    curPosition=position;
                }
                notifyDataSetChanged();
            }
        });

        holder.setOnClickListener(R.id.tv_reName, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemViewOnclickListener!=null){
                    mItemViewOnclickListener.onReName(item,((TextView)holder.getView(R.id.tv_title)).getText().toString());
                }
            }
        });

        holder.setOnClickListener(R.id.tv_unbind, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemViewOnclickListener!=null){
                    mItemViewOnclickListener.onUnBind(item);
                }
            }
        });

        holder.setOnClickListener(R.id.tv_resetNetwork, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemViewOnclickListener!=null){
                    mItemViewOnclickListener.onResetNetwork(item);
                }
            }
        });
    }

    public interface ItemViewOnclickListener{
        void onReName(WifiAirCleanDeviceInfo item,String oldName);
        void onUnBind(WifiAirCleanDeviceInfo item);
        void onResetNetwork(WifiAirCleanDeviceInfo item);
    }

    private ItemViewOnclickListener mItemViewOnclickListener;

    public void setItemViewOnclickListener(ItemViewOnclickListener l){
        mItemViewOnclickListener=l;
    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }
}
