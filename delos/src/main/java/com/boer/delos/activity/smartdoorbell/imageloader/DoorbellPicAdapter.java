package com.boer.delos.activity.smartdoorbell.imageloader;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.boer.delos.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouhuang on 2017/7/28.
 */

public class DoorbellPicAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> infos;
    private List<Boolean> selects;
    public DoorbellPicAdapter(Context ctx, ArrayList<String> infos){
        this.infos = infos;
        mInflater = LayoutInflater.from(ctx);
        selects=new ArrayList<>(infos.size());
        for(String str:infos){
            selects.add(false);
        }
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
        FrameLayout flSelect;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_door_bell_pic_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_alarmImage = (SimpleDraweeView) convertView.findViewById(R.id.ivPic);
            viewHolder.flSelect = (FrameLayout) convertView.findViewById(R.id.flSelect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Uri uri=Uri.parse("file://"+infos.get(position));
        viewHolder.iv_alarmImage.setImageURI(uri);
        if (selects.get(position)) {
            viewHolder.flSelect.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.flSelect.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setSelected(int i){
        selects.set(i,!selects.get(i));
        notifyDataSetChanged();
    }

    public void setAllSelectedOrNot(Boolean allSelected){
//        isAllSelect=!isAllSelect;
        for(int i=0;i<selects.size();i++){
            selects.set(i,allSelected);
        }
        notifyDataSetChanged();
    }

    public void delPic(){
        for(int i = 0,len= selects.size();i<len;++i){
            if(selects.get(i)){
                String fileName=infos.get(i);
                FileHelper.delFile(fileName);
                infos.remove(i);
                selects.remove(i);
                --len;
                --i;
            }
        }
        notifyDataSetChanged();

//        for(int i=0;i<selects.size();i++){
//            if(selects.get(i)){
//                String fileName=infos.get(i);
//                FileHelper.delFile(fileName);
//                infos.remove(i);
//                selects.remove(i);
//                i--;
//            }
//        }
//        notifyDataSetChanged();
    }

    public boolean isSelect(){
        for(boolean value:selects){
            if(value){
                return true;
            }
        }
        return false;
    }
}
