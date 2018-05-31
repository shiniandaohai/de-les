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
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Room;
import com.boer.delos.model.SceneManage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.attr.type;
import static android.R.attr.value;
import static com.boer.delos.R.id.collapseActionView;
import static com.boer.delos.R.id.ll_item;
import static com.boer.delos.R.mipmap.open;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 20:54
 * @Modify:
 * @ModifyDate:
 */


public class CommonDeviceRoomRvAdapter extends RecyclerView.Adapter<CommonDeviceRoomRvAdapter.ThisViewHolder> {
    private Context mContext;
    private Map<Room, List<DeviceRelate>> mListMap;
    private List<Room> mKeyList;

    public CommonDeviceRoomRvAdapter(Context context, Map<Room, List<DeviceRelate>> list) {
        this.mContext = context;
        this.mListMap = list;
        this.mKeyList = getKeyfromMap();
    }


    public void setList(Map<Room, List<DeviceRelate>> list) {
        mListMap = list;
        mKeyList.clear();
        mKeyList.addAll(getKeyfromMap());

        notifyDataSetChanged();
    }

    private List<Room> getKeyfromMap() {
        List<Room> keyList = new ArrayList<>();
        Set<Room> keySet = mListMap.keySet();
        if (keySet == null) {
            return Collections.emptyList();
        }
        for (Room s : keySet) {
            keyList.add(s);
        }
        return keyList;
    }

    @Override
    public ThisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_home_show, null);
        ThisViewHolder holder = new ThisViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ThisViewHolder holder, int position) {

        holder.bindView(mListMap.get(mKeyList.get(position)), mKeyList.get(position));
        holder.itemClickListener(position);

    }

    @Override
    public int getItemCount() {
        return mKeyList.size();
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

            tv_device_num.setVisibility(View.VISIBLE);
        }

        public void bindView(List<DeviceRelate> list, Room key) {
            if (selectResIv(key.getType()) != -1) {
                iv_device_type.setImageResource(selectResIv(key.getType()));
            }

            tv_device_num.setText(getDeviceStatus(list));
//            if (list.size() != 0) {
            tv_room_name.setText(key.getName());
//            }
        }

        public void itemClickListener(final int position) {
            ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClickListenerOK(mListMap.get(mKeyList.get(position)),
                                position, mKeyList.get(position).getName());
                    }
                }
            });

        }


        private int selectResIv(String roomType) {
            if (TextUtils.isEmpty(roomType)) {
                return -1;
            }
            List<SceneManage> sceneManages = Constant.sceneTypeList();

            for (SceneManage manage : sceneManages) {

                if (!TextUtils.isEmpty(String.valueOf(manage.getType()))) {
                    if (mContext.getString(manage.getType()).equals(roomType)) {
                        return manage.getResId();
                    }
                }
            }
            return -1;
        }


        private StringBuffer getDeviceStatus(List<DeviceRelate> list) {
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
                    if (TextUtils.isEmpty(value.getState()) || value.getState().equals("0")) {
                        //一联灯

                    } else if (TextUtils.isEmpty(value.getState2()) || value.getState2().equals("0")) {
                        //二联灯

                    } else if (TextUtils.isEmpty(value.getState3()) || value.getState3().equals("0")) {
                        //三联灯

                    } else if (TextUtils.isEmpty(value.getState4()) || value.getState4().equals("0")) {
                        //四联灯

                    } else {
                        open++;
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

    public List<Room> getmKeyList() {
        return mKeyList;
    }
}
