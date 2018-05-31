package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BasePopupWindow;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Room;
import com.boer.delos.model.SceneManage;

import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/27 0027 15:59
 * @Modify:
 * @ModifyDate:
 */


public class AddBatchChoiseRoom extends BasePopupWindow {

    private List<Room> mRoomList;
    private GridView gv_room;

    private IObjectInterface<Room> listener;

    public AddBatchChoiseRoom(Context context, @LayoutRes int resId) {
        super(context, resId);
//        R.layout.popup_addbatch_add_room
    }

    @Override
    public int setPopWindowResId() {
        return 0;
    }

    public void setRoomList(List<Room> roomList) {
        mRoomList = roomList;
        roomList.add(new Room("-99"));
        initView();

    }

    private void initView() {
        gv_room = getView(R.id.gv_room);
        GridAdapter adapter = new GridAdapter(mContext, mRoomList, R.layout.item_scence_edit);
        gv_room.setAdapter(adapter);
        gv_room.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                Room r = mRoomList.get(position);
                if (listener != null) {
                    listener.onClickListenerOK(r, -1, null);
                }

            }
        });

    }

    public void setListener(IObjectInterface<Room> listener) {
        this.listener = listener;
    }

    static class GridAdapter extends MyBaseAdapter<Room> {
        public GridAdapter(Context mContext, List<Room> listData, int itemLayoutId) {
            super(mContext, listData, itemLayoutId);
        }

        @Override
        public void convert(MyViewHolder holder, Room item, int position) {
            ImageView ivScenceItemImage = holder.getView(R.id.ivScenceItemImage);
            TextView tvScenceItemText = holder.getView(R.id.tvScenceItemText);


            for (SceneManage scenemanage : Constant.sceneTypeList()) {
                if (mContext.getString(scenemanage.getType()).equals(item.getType())) {
                    ivScenceItemImage.setImageResource(scenemanage.getResId());
                }
            }
            tvScenceItemText.setText(item.getName());

        }
    }


}
