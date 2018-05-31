package com.boer.delos.adapter.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 20:54
 * @Modify:
 * @ModifyDate:
 */


public class CommonDeviceRvAdapter extends RecyclerView.Adapter<CommonDeviceRvAdapter.ThisViewHolder> {
    private Context mContext;
    private List<DeviceRelate> mList;

    public CommonDeviceRvAdapter(Context context, List<DeviceRelate> list) {
        mContext = context;
        mList = list;

    }

    public void setList(List<DeviceRelate> list) {
        mList = list;

        notifyDataSetChanged();
    }

    @Override
    public ThisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_home_show, null);
        ThisViewHolder holder = new ThisViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ThisViewHolder holder, int position) {
        holder.bindView(mList.get(position));
        holder.onItemCLickListener(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_device_type;
        private TextView tv_device_num;
        private TextView tv_room_name;
        private LinearLayout ll_item;

        public ThisViewHolder(View itemView) {
            super(itemView);

            iv_device_type = (ImageView) itemView.findViewById(R.id.iv_device_type);
            tv_room_name = (TextView) itemView.findViewById(R.id.tv_room_name);
            tv_device_num = (TextView) itemView.findViewById(R.id.tv_device_num);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
            tv_device_num.setVisibility(View.GONE);

        }

        public void bindView(DeviceRelate deviceRelate) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                return;
            }
            Device device = deviceRelate.getDeviceProp();
            if (device.getAddr().equals("-99")) {
                iv_device_type.setImageResource(R.drawable.ic_device_add);
                tv_room_name.setText(mContext.getString(R.string.text_edit_common_device));
                return;
            }


            iv_device_type.setImageResource(Constant.getNonCircleBlueResIdWithType(device.getType()));
            tv_room_name.setText(TextUtils.isEmpty(device.getName())
                    ? Constant.getDeviceTypeNameWithType(device.getType())
                    : device.getName());
        }

        public void onItemCLickListener(final DeviceRelate deviceRelate, final int position) {
            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
//                        DeviceStatus status = deviceRelate.getDeviceStatus();
//                        if ( status.getOffline()==1) {
//                            ToastHelper.showShortMsg(mContext.getString(R.string.text_device_offline));
//                            return;
//                        }
                        mListener.onClickListenerOK(deviceRelate, position, null);
                    }

                }
            });
        }
    }

    private IObjectInterface mListener;

    public void setListener(IObjectInterface listener) {
        mListener = listener;
    }
}
