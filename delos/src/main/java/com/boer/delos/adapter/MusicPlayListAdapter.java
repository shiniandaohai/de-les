package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.model.MusicResult;

import java.util.List;

/**
 * Created by sunzhibin on 2017/8/15.
 */

public class MusicPlayListAdapter extends MyBaseAdapter<MusicResult.ResponseBean.MusicBean> {
    private Context context;
    public MusicPlayListAdapter(Context mContext, List<MusicResult.ResponseBean.MusicBean> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        context=mContext;
        itemLayoutId = R.layout.item_music_play;

    }

    @Override
    public void convert(MyViewHolder holder, MusicResult.ResponseBean.MusicBean item, int position) {
        ImageView iv_album = holder.getView(R.id.iv_album);
        TextView tv_album = holder.getView(R.id.tv_album);
        TextView tv_author = holder.getView(R.id.tv_author);

        tv_album.setText(item.getTitle());
        tv_author.setText(item.getArtist());

        if(selectedPosition==position){
            tv_album.setTextColor(Color.BLUE);
            tv_author.setTextColor(Color.BLUE);
        }
        else{
            tv_album.setTextColor(context.getResources().getColor(R.color.gray_et_text));
            tv_author.setTextColor(context.getResources().getColor(R.color.gray_et_text));
        }
    }

    private int selectedPosition=-1;
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }
}
