package com.boer.delos.adapter.smartdoorbell;

import android.content.Context;
import android.view.View;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.daimajia.swipe.SwipeLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/1/19.
 */

public class DoorbellVisitorListAdapter extends MyBaseAdapter<JSONObject>{
    public DoorbellVisitorListAdapter(Context mContext, List<JSONObject> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
    }

    @Override
    public void convert(MyViewHolder holder, JSONObject item, final int position) {
        holder.setText(R.id.tvTime,getCurrentDateTimeString(item.optLong("ringtime")));
        holder.setOnClickListener(R.id.tvDel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDelListener!=null){
                    mOnDelListener.onDel(position);
                }
            }
        });
        SwipeLayout swipeLayout=(SwipeLayout)holder.getConvertView();
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.getView(R.id.ll_wrap));
    }

    public String getCurrentDateTimeString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public interface OnDelListener{
        void onDel(int position);
    }

    private OnDelListener mOnDelListener;

    public void setOnDelListener(OnDelListener l){
        mOnDelListener=l;
    }
}
