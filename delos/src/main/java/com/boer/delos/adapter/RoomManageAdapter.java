package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.SceneManage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaolong
 */
public class RoomManageAdapter extends BaseAdapter {
    private int tag;//标记来自哪个popup，编辑的popup需要在弹出的时候显示房间对应的区域，添加的不需要
    private int type = 0;//区域类型,根据editpop传递进来的类型来切换checkposition
    private LayoutInflater inflater = null;
    private List<SceneManage> datas = new ArrayList<>();
    private Integer checkPosition = null;

    public RoomManageAdapter(int tag, int type, Context context) {
        this.tag = tag;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<SceneManage> datas) {
        this.datas = datas;
    }

    public void setImageView(int checkPosition) {
        this.checkPosition = checkPosition;
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

            convertView = inflater.inflate(R.layout.item_scence_edit, null);
            viewHolder = new ViewHolder();
            viewHolder.itemSceneImage = (ImageView) convertView.findViewById(R.id.ivScenceItemImage);
            viewHolder.itemSceneText = (TextView) convertView.findViewById(R.id.tvScenceItemText);
            viewHolder.itemSceneCheck = (ImageView) convertView.findViewById(R.id.ivPopupItemCheck);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SceneManage sceneManage = datas.get(position);
        viewHolder.itemSceneImage.setImageResource(sceneManage.getResId());
        viewHolder.itemSceneText.setText(sceneManage.getItemName());
        viewHolder.itemSceneCheck.setTag(position);

        if (tag == 1) {
            //场景编辑popupwindow
            if (checkPosition!=-1){
                switchCheckItem(checkPosition);
                viewHolder.itemSceneCheck.setVisibility(checkPosition != null && checkPosition == position ? View.VISIBLE : View.GONE);
            }
        } else {
            viewHolder.itemSceneCheck.setVisibility(checkPosition != null && checkPosition == position ? View.VISIBLE : View.GONE);
        }
        return convertView;
    }

    private void switchCheckItem(int position) {
        switch (type) {
            case 0:
                checkPosition = position;
                break;
            case 1:
                checkPosition = position;
                break;
            case 2:
                checkPosition = position;
                break;
            case 3:
                checkPosition = position;
                break;
            case 4:
                checkPosition = position;
                break;
            case 5:
                checkPosition = position;
                break;
            case 6:
                checkPosition = position;
                break;
            case 7:
                checkPosition = position;
                break;
            case 8:
                checkPosition = position;
                break;
            case 9:
                checkPosition = position;
                break;
            case 10:
                checkPosition = position;
                break;
            case 11:
                checkPosition = position;
                break;
            case 12:
                checkPosition = position;
                break;
            case 13:
                checkPosition = position;
                break;
            case 14:
                checkPosition = position;
                break;
            case 15:
                checkPosition = position;
                break;
        }
    }


    static class ViewHolder {
        public ImageView itemSceneImage;
        public TextView itemSceneText;
        public ImageView itemSceneCheck;

    }


}
