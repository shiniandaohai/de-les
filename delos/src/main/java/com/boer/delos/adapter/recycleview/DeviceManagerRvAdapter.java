package com.boer.delos.adapter.recycleview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.DeviceManagerActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Room;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.recyclerHelper.ItemTouchHelperAdapter;
import com.boer.delos.view.popupWindow.CommonEditPopup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/5 0005 20:54
 * @Modify:
 * @ModifyDate:
 */


public class DeviceManagerRvAdapter extends RecyclerView.Adapter<DeviceManagerRvAdapter.ThisViewHolder>
        implements ItemTouchHelperAdapter {
    private Context mContext;
    private List<DeviceRelate> mList;
    private ArrayList<Boolean> flagList;
    private ArrayList<DeviceRelate> selectData;
    private CommonEditPopup commonEditPopup;
    private boolean wireOffline;//wire背影音乐离线状态

    public DeviceManagerRvAdapter(Context context, List<DeviceRelate> list) {
        mContext = context;
        mList = list;
        selectData = new ArrayList<>();
        initFlags(mList.size(), -1);

    }

    public void setList(List<DeviceRelate> list) {
        mList = list;
        initFlags(mList.size(), -1);
        notifyDataSetChanged();
    }

    public void setWireOffline(boolean wireOffline) {
        this.wireOffline = wireOffline;
    }

    @Override
    public ThisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_device_manager, null);
        ThisViewHolder holder = new ThisViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ThisViewHolder holder, int position) {
        holder.bindView(mList.get(position), position);
        holder.onItemCLickListener(mList.get(position), position);
        holder.settingSates(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        Collections.swap(flagList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    class ThisViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_device_type;
        private EditText et_device_name;
        private CheckedTextView ctv_choice;
        private CheckBox cb_device_status;
        private TextView tv_device_room;
        private ImageView iv_edit_name;
        private LinearLayout ll_item;

        public ThisViewHolder(View itemView) {
            super(itemView);

            ctv_choice = (CheckedTextView) itemView.findViewById(R.id.ctv_choice);
            iv_device_type = (ImageView) itemView.findViewById(R.id.iv_device_type);
            iv_edit_name = (ImageView) itemView.findViewById(R.id.iv_edit_name);

            et_device_name = (EditText) itemView.findViewById(R.id.tv_device_name);
            cb_device_status = (CheckBox) itemView.findViewById(R.id.cb_device_status);
            cb_device_status.setClickable(false);
            tv_device_room = (TextView) itemView.findViewById(R.id.tv_device_classify);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);

        }

        public void bindView(DeviceRelate deviceRelate, int position) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                return;
            }

            Device device = deviceRelate.getDeviceProp();
            if (device == null) {
                return;
            }
            if (device.getAddr().equals("-99")) {
                iv_device_type.setImageResource(R.mipmap.ic_device_edit);
                et_device_name.setText(mContext.getString(R.string.text_edit_common_device));
                return;
            }


            AddDevice tempAddDevice = null;
            for (AddDevice addDevice : Constant.blueDeviceList()) {
                if (StringUtil.isEmpty(device.getType())) {
                    continue;
                }
                if (addDevice.getType().equals(device.getType())) {
                    tempAddDevice = addDevice;
                    break;
                }
            }
            if (tempAddDevice == null) {
                return;
            }

            iv_device_type.setImageResource(tempAddDevice.getResId());
            et_device_name.setText(TextUtils.isEmpty(device.getName())
                    ? Constant.getDeviceTypeNameWithType(device.getType())
                    : device.getName());
            ctv_choice.setChecked(flagList.get(position));

            tv_device_room.setText(settingDeviceName(device));

        }

        public void onItemCLickListener(final DeviceRelate deviceRelate, final int position) {
            ctv_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctv_choice.toggle();
                    initFlags(-1, position);
                    if (mListener != null) {
                        mListener.onClickListenerOK(selectData, -1, null);
                    }
                }
            });
            if (commonEditPopup != null && commonEditPopup.isShowing())
                commonEditPopup.dismiss();
            et_device_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(deviceRelate, position);
                }
            });
            iv_edit_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(deviceRelate, position);
                }
            });
            //            et_device_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//                        et_device_name.setText("");
//                        showKeyBroad(et_device_name);
//
//                    }
//                    et_device_name.setCursorVisible(hasFocus);
//                    iv_edit_name.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
//
//                    if (TextUtils.isEmpty(et_device_name.getText().toString()) && !hasFocus) {
//                        et_device_name.setText(deviceRelate.getDeviceProp().getName());
//                        return;
//                    }
//                    Device device = deviceRelate.getDeviceProp();
//                    if (!hasFocus &&
//                            (TextUtils.isEmpty(device.getName())
//                                    || !device.getName().equals(et_device_name.getText().toString()))) {
//                        //修改设备名称
//                        String oldname = device.getName();
//                        device.setName(et_device_name.getText().toString());
//
//                        resetDeviceName(device, oldname, position);
//                    }
//
//                }
//            });
//
//            iv_edit_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    et_device_name.findFocus();
//                    et_device_name.setFocusableInTouchMode(true);
//                    et_device_name.requestFocus();
//                    showKeyBroad(et_device_name);
//                    iv_edit_name.setVisibility(View.GONE);
//                }
//            });
//            ll_item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    hideKeyBroad(et_device_name);
//
//
//                }
//            });
        }

        private void showPopup(final DeviceRelate deviceRelate, final int position) {
            commonEditPopup = new CommonEditPopup(mContext, "请输入设备名称",
                    et_device_name.getText().toString());
            Device device = deviceRelate.getDeviceProp();
            commonEditPopup.setLayoutByType(device.getType(),device.getLightName());
            commonEditPopup.setStringListener(new ISimpleInterfaceString() {
                @Override
                public void clickListener(String tag) {
                    commonEditPopup.dismiss();
                    if (TextUtils.isEmpty(tag)) {
                        ToastHelper.showShortMsg("设备名称不能为空");
                        return;
                    }
                    Device device = deviceRelate.getDeviceProp();
                    device.setName(tag);

                    Map<String,String> map=commonEditPopup.getNames(device.getType());
                    if(map!=null){
                        device.setLightName(map);
                    }

                    resetDeviceName(device, et_device_name.getText().toString(), position);
                    et_device_name.setText(tag);

                }
            });
            commonEditPopup.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }

        public void settingSates(DeviceRelate item) {
            int open = 0;  //标记打开状态的个数
            if (null == item || null == item.getDeviceStatus()) {
                return;
            }
            DeviceStatus deviceStatus = item.getDeviceStatus();
            DeviceStatusValue value = deviceStatus.getValue();
            if (value != null) {
                if (deviceStatus.getType().contains("灯")) {
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
                    //窗帘
                } else if (item.getDeviceProp().getType().equals(ConstantDeviceType.CURTAIN)) {
                    if (!TextUtils.isEmpty(value.getOpen()) && value.getOpen().equals("1")) {
                        open++;
                    }
                    //部分传感器
                } else if (value.getSet() != null && value.getSet() == 1) {
                    open++;
                } else if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                    open++;
                }
            }
            if (wireOffline
                    && !TextUtils.isEmpty(item.getDeviceProp().getBrand())
                    && item.getDeviceProp().getType().equals(ConstantDeviceType.AUDIO)
                    && item.getDeviceProp().getBrand().equals(ConstantDeviceType.MUSIC_WISE)) {
                cb_device_status.setChecked(true);
                cb_device_status.setText(mContext.getString(R.string.status_device_on));
                cb_device_status.setTextColor(mContext.getResources().getColor(R.color.white));
                cb_device_status.setChecked(false);
                return;
            }


            if (deviceStatus.getOffline() == 1) {
                cb_device_status.setText(mContext.getString(R.string.status_device_offline));
                cb_device_status.setTextColor(mContext.getResources().getColor(R.color.gray_et_hint));
                cb_device_status.setChecked(false);
                return;
            }
            cb_device_status.setChecked(open != 0);
            cb_device_status.setText(cb_device_status.isChecked()
                    ? mContext.getString(R.string.status_device_on)
                    : mContext.getString(R.string.status_device_off));
            cb_device_status.setTextColor(cb_device_status.isChecked() ?
                    mContext.getResources().

                            getColor(R.color.white)
                    : mContext.getResources().

                    getColor(R.color.gray_et_hint));

        }

        private String settingDeviceName(Device device) {
            if (device == null || TextUtils.isEmpty(device.getRoomId())) {
                return "";
            }
            if (!TextUtils.isEmpty(device.getRoomname())) {
                return device.getRoomname();
            }
            if (Constant.GATEWAY != null) {
                List<Room> rooms = Constant.GATEWAY.getRoom();
                for (Room room : rooms) {
                    if (device.getRoomId().equals(room.getRoomId())) {
                        return room.getName();
                    }
                }
            }
            return "";
        }

    }

    /**
     * @param size
     * @param position -1 全不选 -100 全选
     */
    private void initFlags(int size, int position) {
        if (flagList == null) {
            flagList = new ArrayList<>();
        }
        if (position == -1) {
            flagList.clear();
            for (int i = 0; i < size; i++) {
                flagList.add(false);
            }
        } else if (position == -100) {
            flagList.clear();
            for (int i = 0; i < size; i++) {
                flagList.add(i, true);
            }
        } else {
            flagList.set(position, !flagList.get(position));
        }

        settingSelectDatas();
    }

    private void settingSelectDatas() {
        int index = 0;
        selectData.clear();
        for (boolean s : flagList) {
            if (s) {
                if (selectData == null) selectData = new ArrayList<>();
                selectData.add(mList.get(index));
            }
            index++;

        }
    }

    /**
     * @param tag -1:全不选 1:全选
     */
    public void clearAll(int tag) {
        initFlags(mList.size(), tag);
        notifyDataSetChanged();
    }

    private void resetDeviceName(final Device device, final String oldName, final int position) {
        DeviceController.getInstance().updateProp(mContext, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() != 0) {
                    device.setName(oldName);
                } else {
                    ((CommonBaseActivity) mContext).toastUtils.showSuccessWithStatus(mContext.getString(R.string.edit_success));
                    Constant.DEVICE_RELATE.clear();
                    Constant.DEVICE_RELATE.addAll(mList);
                }
                notifyItemChanged(position);
            }

            @Override
            public void onFailed(String json) {
                ((CommonBaseActivity) mContext).toastUtils.showErrorWithStatus(mContext.getString(R.string.edit_fail));
                notifyItemChanged(position);
            }
        });

    }


    private void showKeyBroad(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyBroad(EditText editText) {
        ((DeviceManagerActivity) mContext).hideInput();
        editText.clearFocus();

    }

    public ArrayList<DeviceRelate> getSelectData() {
        return selectData;
    }

    private IObjectInterface mListener;

    public void setListener(IObjectInterface listener) {
        mListener = listener;
    }

}
