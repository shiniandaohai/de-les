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
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;

import org.w3c.dom.ls.LSException;

import java.util.List;
import java.util.Map;

import static android.R.attr.codes;
import static android.R.attr.value;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 20:54
 * @Modify:
 * @ModifyDate:
 */


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ThisViewHolder> {
    private Context mContext;
    private List<Map<String, Object>> mList;

    public RecycleViewAdapter(Context context, List<Map<String, Object>> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ThisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_home_show, null);
        ThisViewHolder holder = new ThisViewHolder(view);

        return holder;
    }

    public void setList(List<Map<String, Object>> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ThisViewHolder holder, int position) {
        holder.bindView(mList.get(position));
        holder.onItemClick(mList.get(position), position);

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

        }

        public void bindView(Map<String, Object> map) {
            if (map.get("name").equals("-99")) {
                iv_device_type.setImageResource(R.mipmap.ic_device_edit);
                tv_room_name.setVisibility(View.GONE);
                tv_device_num.setVisibility(View.GONE);

            } else {
                tv_room_name.setText((String) map.get("name"));
                tv_device_num.setText(getDeviceStatus(map).toString());
                iv_device_type.setImageResource((int) map.get("image"));
            }
        }

        public void onItemClick(final Map<String, Object> map, final int pos) {
            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClickListenerOK(map.get("child"), pos, (String) map.get("name"));

                    }
                }
            });

        }

        private StringBuffer getDeviceStatus(Map<String, Object> map) {
            List<DeviceRelate> list = (List<DeviceRelate>) map.get("child");
            int open = 0;
            for (DeviceRelate deviceRelate : list) {
                if (deviceRelate == null || deviceRelate.getDeviceStatus() == null) {
                    continue;
                }
                DeviceStatus status = deviceRelate.getDeviceStatus();
                if (status == null) {
                    continue;
                }
                if ( status.getOffline()==1) {
                    continue;
                }
                DeviceStatusValue value = status.getValue();
                if (value == null) {
                    continue;
                }
                if (status.getType().contains("Light")) {
                    if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                        //一联灯
                        open++;
                    } else if (!TextUtils.isEmpty(value.getState2()) && value.getState2().equals("1")) {
                        //二联灯
                        open++;
                    } else if (!TextUtils.isEmpty(value.getState3()) && value.getState3().equals("1")) {
                        //三联灯
                        open++;
                    } else if (!TextUtils.isEmpty(value.getState4()) && value.getState4().equals("1")) {
                        //四联灯
                        open++;
                    } else {
                        //open++;
                    }

                    value.getState();
                } else if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                    open++;
                }

            }
            StringBuffer sb = new StringBuffer();
            sb.append("(" + open + "/" + list.size() + ")");
            return sb;
        }
    }

    private IObjectInterface mListener;

    public void setListener(IObjectInterface listener) {
        mListener = listener;
    }
}
