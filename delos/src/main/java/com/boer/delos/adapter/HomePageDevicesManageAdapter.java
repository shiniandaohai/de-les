package com.boer.delos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.recyclerHelper.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;


/**
 * Created by gaolong on 2017/3/31.
 */
public class HomePageDevicesManageAdapter extends RecyclerView.Adapter<HomePageDevicesManageAdapter.MyViewHold>  implements ItemTouchHelperAdapter {


    private Context context;

    private List<String> list;

    public HomePageDevicesManageAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public MyViewHold onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_name, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(layoutParams);
        MyViewHold myViewHold = new MyViewHold(view);

        return myViewHold;
    }


    @Override
    public void onBindViewHolder(final MyViewHold holder, int position) {

        holder.tvItemText.setText(list.get(position));
        holder.ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    itemTouchHelper.startDrag(holder);
                }
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemDismiss(int position) {
//        list.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    class MyViewHold extends RecyclerView.ViewHolder {
        TextView tvItemText;
        ImageView ivDrag;
        public MyViewHold(View itemView) {
            super(itemView);

            tvItemText = (TextView) itemView.findViewById(R.id.tv_name);
            ivDrag=(ImageView)itemView.findViewById(R.id.iv_drag);
        }
    }

    public List<String> getList() {
        return list;
    }

    private ItemTouchHelper itemTouchHelper;
    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper){
        this.itemTouchHelper=itemTouchHelper;
    }
}
