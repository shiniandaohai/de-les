package com.boer.delos.adapter.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.AirClean;

import java.util.List;

/**
 * Created by gaolong on 2018/1/25.
 */

public class AirSystemStatusAdapter extends RecyclerView.Adapter<AirSystemStatusAdapter.ViewHolder> {

    public OnItemClickListener onItemClickListener;

    private Context context;

    private List<AirClean> list;

    public AirSystemStatusAdapter(Context context) {

        this.context = context;


    }


    public void setData(List<AirClean> list) {

        this.list = list;
        notifyDataSetChanged();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_air_system_status, null);

        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tvName.setText(list.get(position).getName());
        if (list.get(position).isCheck()) {
            holder.rlItem.setBackgroundResource(list.get(position).getResSelector());
            holder.tvName.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.rlItem.setBackgroundResource(list.get(position).getRes());
            holder.tvName.setTextColor(context.getResources().getColor(R.color.gray_text));
        }


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        RelativeLayout rlItem;


        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            rlItem = (RelativeLayout) itemView.findViewById(R.id.rl_item);

        }
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

    }


}
