package com.boer.delos.activity.smartdoorbell.imageloader;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boer.delos.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {
    private List<String> list;

    public MyAdapter(ArrayList<String> infos) {
        list=infos;
    }

    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_door_bell_show_pic_item, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    public void onBindViewHolder(MyHolder holder, int position) {
        Uri uri = Uri.parse("file://" + list.get(position));
        holder.icon.setImageURI(uri);
    }

    public int getItemCount() {
        return list.size();
    }
}