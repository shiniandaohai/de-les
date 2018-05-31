package com.boer.delos.activity.smartdoorbell.imageloader;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.boer.delos.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> infos;
    public GalleryAdapter(Context ctx, ArrayList<String> infos){
        this.infos = infos;
        mInflater = LayoutInflater.from(ctx);
    }

    public int getCount() {
        return infos.size();
    }

    public Object getItem(int i) {
        if(infos != null && !infos.isEmpty()){
            return infos.get(i);
        }else{
            return null;
        }
    }

    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder {
        SimpleDraweeView iv_alarmImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DoorbellPicAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_door_bell_show_pic_item, null);
            viewHolder = new DoorbellPicAdapter.ViewHolder();
            viewHolder.iv_alarmImage = (SimpleDraweeView) convertView.findViewById(R.id.ivPic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DoorbellPicAdapter.ViewHolder) convertView.getTag();
        }
        Uri uri=Uri.parse("file://"+infos.get(position));
        viewHolder.iv_alarmImage.setImageURI(uri);
        return convertView;
    }
}