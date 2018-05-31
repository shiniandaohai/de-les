package com.boer.delos.activity.smartdoorbell.imageloader;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.boer.delos.R;

public class MyHolder extends RecyclerView.ViewHolder {
    public ImageView icon;
    public MyHolder(View itemView) {
        super(itemView);
        icon= (ImageView) itemView.findViewById(R.id.ivPic);
    }
}