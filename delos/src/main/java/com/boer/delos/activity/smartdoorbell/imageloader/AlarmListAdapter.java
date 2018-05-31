package com.boer.delos.activity.smartdoorbell.imageloader;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.alarm.AlarmType;
import com.eques.icvss.utils.ELog;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ouhuang on 2017/7/28.
 */

public class AlarmListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<AlarmMessageInfo> infos=new ArrayList<>();
    private ICVSSUserInstance icvss;
    private ImageLoaderThreadPool imageLoaderThreadPool;
    private List<Boolean> selects=new ArrayList<>();
    private boolean isEdit;
    public AlarmListAdapter(Context ctx, ICVSSUserInstance icvss){
        this.mContext = ctx;
        this.icvss = icvss;
        mInflater = LayoutInflater.from(ctx);
        imageLoaderThreadPool = ImageLoaderThreadPool.getInstance(3, ImageLoaderThreadPool.Type.LIFO);
    }

    public int getCount() {
        return infos.size();
    }

    public AlarmMessageInfo getItem(int i) {
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
        ImageView iv_alarmImage;
        TextView tv_alarmTime;
        TextView tv_alarmTitle;
        ImageView ivChoice;
        ImageView ivFlag;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_door_bell_alarm_info_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_alarmImage = (ImageView) convertView.findViewById(R.id.ivPic);
            viewHolder.tv_alarmTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tv_alarmTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.ivChoice = (ImageView) convertView.findViewById(R.id.ivChoice);
            viewHolder.ivFlag = (ImageView) convertView.findViewById(R.id.ivFlag);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AlarmMessageInfo info = (AlarmMessageInfo) getItem(position);
        if(info != null){
            if(info.getAlarmTime() > 0){
                viewHolder.tv_alarmTime.setText(getCurrentDateTimeString(info.getAlarmTime()));
            }

            List<String> pvids = info.getPvids();
            if(!pvids.isEmpty()){
                String imageUrlStr = null;
                String imagePathTemp = null;
                
                if (StringUtils.isNotBlank(pvids.get(0)) && StringUtils.isNotBlank(info.getBid())) {
                    imageUrlStr = icvss.equesGetThumbUrl(pvids.get(0), info.getBid()).toString();
                    imagePathTemp = getAlarmPath() + pvids.get(0);
                }
                ELog.i("alarmList", "imageUrlStr: ", imageUrlStr);
                ELog.i("alarmList", "imagePathTemp: ", imagePathTemp);
                
                viewHolder.iv_alarmImage.setTag(imagePathTemp);
                if(StringUtils.isNotBlank(imageUrlStr) && StringUtils.isNotBlank(imagePathTemp)){
                    imageLoaderThreadPool.loadImage(imageUrlStr, viewHolder.iv_alarmImage, imagePathTemp, imagePathTemp);     
                }else{
                    viewHolder.iv_alarmImage.setImageResource(R.drawable.image_empty_photo);
                }
            }
            if(info.getType()==AlarmType.PIR_VIDEO.code){
                viewHolder.ivFlag.setImageResource(R.drawable.ic_door_bell_alarm_info_item_righttop);
            }
            else{
                viewHolder.ivFlag.setImageResource(R.drawable.ic_pic);
            }
        }

        if(isEdit){
            viewHolder.ivChoice.setVisibility(View.VISIBLE);
            if (selects.get(position)) {
                viewHolder.ivChoice.setImageResource(R.drawable.ic_door_bell_pic_item_tick);
            }
            else{
                viewHolder.ivChoice.setImageResource(R.drawable.ic_ring_blue);
            }
        }
        else{
            viewHolder.ivChoice.setVisibility(View.GONE);
        }
        return convertView;
    }

    public String getCurrentDateTimeString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }
    
    public boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/";
        }
    }
    
    public String getAlarmPath() {
        String camPicPath = getRootFilePath() + "com.equessdk.app" + File.separator + "alarm_image" + File.separator;
        return camPicPath;
    }

    public void setSelected(int i){
        selects.set(i,!selects.get(i));
        notifyDataSetChanged();
    }

    public void setAllSelectedOrNot(Boolean allSelected){
        for(int i=0;i<selects.size();i++){
            selects.set(i,allSelected);
        }
        notifyDataSetChanged();
    }

    public void delAlarm(){
        for(int i = 0,len= selects.size();i<len;++i){
            if(selects.get(i)){
                String fileName=getAlarmPath() + infos.get(i).getPvids().get(0);
                FileHelper.delFile(fileName);
                infos.remove(i);
                selects.remove(i);
                --len;
                --i;
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelect(){
        for(boolean value:selects){
            if(value){
                return true;
            }
        }
        return false;
    }

    public void setIsEdit(boolean isEdit){
        this.isEdit=isEdit;
        notifyDataSetChanged();
    }

    public void clearData(){
        infos.clear();
        selects.clear();
        notifyDataSetChanged();
    }

    public void addData(ArrayList<AlarmMessageInfo> pInfos){
        infos.addAll(pInfos);
        for(AlarmMessageInfo info:pInfos){
            selects.add(false);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelected(){
        List<Integer> selected=new ArrayList<>();
        for(int i=0;i<selects.size();i++){
            if(selects.get(i)){
                selected.add(i);
            }
        }
        return selected;
    }
}
