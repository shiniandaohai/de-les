package com.boer.delos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: "绿色生活"日期适配器
 * create at 2016/4/11 17:49
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mDatas;
    private SparseArray<Boolean> flagLists;
    private TextView lastItem;

    public RecyclerAdapter(Context context, List<String> datas) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mDatas = datas;
    }

    public void setDatas(List<String> datas) {
        mDatas = datas;
        settingChoise(-1);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        View view = mInflater.inflate(R.layout.item_recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tvItemText.setText(mDatas.get(position));
        if (position == mDatas.size() - 1) {
            lastItem = viewHolder.tvItemText;
        }
        viewHolder.tvItemText.setTextColor(flagLists.get(position)
                ? mContext.getResources().getColor(R.color.blue_title)
                : mContext.getResources().getColor(R.color.gray_text));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    settingChoise(position);

                    notifyDataSetChanged();
                    mOnItemClickListener.onItemClick(mDatas.get(position), position + 1);
                }
            });

        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemText;

        public ViewHolder(View view) {
            super(view);
            tvItemText = (TextView) view.findViewById(R.id.tvItemText);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

    /**
     * @param position
     */
    public void settingChoise(int position) {
        if (flagLists == null) {
            flagLists = new SparseArray<>();
        }
        flagLists.clear();
        for (int i = 0; i < mDatas.size(); i++) {
            if (position == i) {
                flagLists.append(i, true);
            } else
                flagLists.append(i, false);
        }

    }


    public TextView getLastItem() {
        return lastItem;
    }
}
