package com.boer.delos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.AddBatchAddRoomPopupWindow;
import com.boer.delos.view.popupWindow.BindEmailPopUpWindow;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 批量添加的adapter
 * @CreateDate: 2017/3/24 0024 13:49
 * @Modify:
 * @ModifyDate:
 */

public class AddBatchDeviceAdapter extends MyBaseAdapter<Device> {
    private AddBatchAddRoomPopupWindow popupWindow;
    BindEmailPopUpWindow bindPopUpWindow;
    private Context context;

    public AddBatchDeviceAdapter(Context mContext, List<Device> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        this.context = mContext;
    }

    @Override
    public void convert(MyViewHolder holder, final Device item, final int position) {
        final CheckedTextView checkedTextView = holder.getView(R.id.cb_choice);
        ImageView iv_device = holder.getView(R.id.iv_device);
        final TextView tv_device_name = holder.getView(R.id.tv_device_name);
        TextView tv_device_type = holder.getView(R.id.tv_device_type);
        TextView tv_device_state = holder.getView(R.id.tv_device_state);
        final ImageView iv_edit = holder.getView(R.id.iv_edit);
        ToggleButton toggleButton = holder.getView(R.id.toggleButtonAll);
        final TextView tv_device_area = holder.getView(R.id.tv_device_area);

        iv_device.setImageResource(Constant.getNonCircleBlueResIdWithType(item.getType()));
        tv_device_name.setText(item.getName());
        tv_device_type.setText(Constant.getControlTypeByType(item.getType()));

        if (TextUtils.isEmpty(item.getRoomname())){
            tv_device_area.setText(R.string.add_room_tip);
            tv_device_area.setSelected(false);
            tv_device_area.setTextColor(Color.BLACK);
        }
        else{
            tv_device_area.setText(item.getRoomname());
            tv_device_area.setSelected(true);
            tv_device_area.setTextColor(Color.WHITE);
        }

        checkedTextView.setChecked(item.isChecked());

        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkedTextView.toggle();

                if (mListener != null)
                    mListener.onCheck(position, checkedTextView.isChecked());

            }
        });


        if (toggleButton.isChecked()) {
            // 开灯
            if (mListener != null) {
                mListener.clickListener2(position, "on");
            }
        } else {
            //关灯
            if (mListener != null) {
                mListener.clickListener2(position, "off");
            }
        }

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDeviceNameDialog(position, tv_device_name);
            }
        });


        tv_device_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加到房间中
                if (mListener != null) {
                    mListener.clickListener(position);
                }
            }
        });
    }

    private void changeDeviceNameDialog(final int position, final TextView tv_nam) {


        bindPopUpWindow = new BindEmailPopUpWindow(context, context.getString(R.string.change_name), new BindEmailPopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(String tag) {
                    String name = tag;
                    tv_nam.setText(name);

                    if (mListener != null)
                        mListener.onEditChange(position, name);
                    bindPopUpWindow.dismiss();
            }
        });
        String name = tv_nam.getText().toString();
        if (!StringUtil.isEmpty(name)) {
            bindPopUpWindow.setEditText(name);
        }
        bindPopUpWindow.showAtLocation(tv_nam, Gravity.BOTTOM, 0, 0);
    }


    public interface OnControlListener {

        void clickListener(int pos);

        void clickListener2(int pos, String flag);


        void onCheck(int pos, boolean check);


        void onEditChange(int pos, String name);

    }

    private OnControlListener mListener;

    public void setListener(OnControlListener listener) {
        mListener = listener;
    }
}
