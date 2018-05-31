package com.boer.delos.adapter.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.GridViewGalleryActivity;
import com.boer.delos.model.Device;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2016/6/23.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> bitmaps;
    private List<Device> deviceList;
    public DeviceAdapter(Context context, List<Device> deviceList, List<Bitmap> bitlist){
        this.context = context;
        this.bitmaps=bitlist;
        this.deviceList = deviceList;
    }
    // add by sunzhibin
    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_device,null);
            viewHolder.deviceImg = (ImageButton)convertView.findViewById(R.id.img_device);
            viewHolder.nametv = (TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.hisbtn = (Button)convertView.findViewById(R.id.btn_history);
            viewHolder.hisbtn.setPadding(5,5,5,5);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Device device = deviceList.get(position);//HomepageListeningActivity.DeviceList.get(position);
        final String uid = device.getAddr();
        viewHolder.nametv.setText(device.getName().toString());
        if(bitmaps.get(position)!=null){
            viewHolder.deviceImg.setImageBitmap(bitmaps.get(position));
        }
        viewHolder.hisbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder_img = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Snapshot/"+uid);
                File folder_video = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Record/"+uid);
                Intent intent = new Intent();
                intent.putExtra("images_path",folder_img.getAbsolutePath());
                intent.putExtra("videos_path",folder_video.getAbsolutePath());
                intent.setClass(context, GridViewGalleryActivity.class);
                context.startActivity(intent);
            }
        });
        viewHolder.deviceImg.setClickable(false);
        viewHolder.deviceImg.setFocusableInTouchMode(false);
        return convertView;
    }
    public class ViewHolder{
        ImageButton deviceImg;
        TextView nametv;
        Button hisbtn;
    }
}
