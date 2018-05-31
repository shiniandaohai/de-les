package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 区域管理界面的adapter
 * create at 2016/4/18 14:48
 */
public class SceneManageAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<Room> datas = new ArrayList<>();
    private OnClickListener listener;

    public SceneManageAdapter(Context context, List<Room> list, OnClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.datas = list;
        this.listener = listener;
    }

    public void setDatas(List<Room> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {

        return datas.size();

    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_scence_manage, null);
            viewHolder = new ViewHolder();
            viewHolder.ivSceneItem = (ImageView) convertView.findViewById(R.id.ivSceneItem);
            viewHolder.tvSceneItemText = (TextView) convertView.findViewById(R.id.tvSceneItemText);
            viewHolder.ivDeleteItem = (ImageView) convertView.findViewById(R.id.ivDeleteRoom);
            viewHolder.layout_item=(LinearLayout)convertView.findViewById(R.id.layout_item);
            viewHolder.ivPopupItemCheck=(ImageView)convertView.findViewById(R.id.ivPopupItemCheck);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Room room = datas.get(position);
        //是否编辑
        if(room.isEdit()){
            viewHolder.ivDeleteItem.setVisibility(View.VISIBLE);
            viewHolder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.click(room);
                    }
                }
            });
        }else{
            viewHolder.ivDeleteItem.setVisibility(View.GONE);
        }

        if (null!=room.getType()){
            viewHolder.ivSceneItem.setImageResource(Constant.setImage(room.getType(),0));
        }
        if(null!=room.getType()&&room.getType().equals("")){
            viewHolder.tvSceneItemText.setVisibility(View.GONE);
        }
        else{
            viewHolder.tvSceneItemText.setVisibility(View.VISIBLE);
        }
        viewHolder.tvSceneItemText.setText(room.getName());

//        if(!TextUtils.isEmpty(deviceRoomId)&&deviceRoomId.equals(room.getRoomId())){
//            viewHolder.layout_item.setBackgroundColor();
//        }
//        else{
//            viewHolder.layout_item.setBackgroundColor(Color.WHITE);
//        }


        if(isSelected.size()>0)
        viewHolder.ivPopupItemCheck.setVisibility(isSelected.get(position)?View.VISIBLE:View.GONE);


        return convertView;
    }

    static class ViewHolder {
        public ImageView ivSceneItem;
        public TextView tvSceneItemText;
        public ImageView ivDeleteItem;
        public LinearLayout layout_item;
        public ImageView ivPopupItemCheck;
    }

    public interface OnClickListener{
        void click(Room room);
    }

    private String deviceRoomId;
    public void setRoomId(String roomId){
        deviceRoomId=roomId;
    }

    private List<Boolean> isSelected=new ArrayList<>();
    public void setIsSelected(List<Boolean> isSelected){
        this.isSelected=isSelected;
    }
    public List<Boolean> getIsSelected(){
        return isSelected;
    }
}
