package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.adapter.RelateDeviceAdapter;
import com.boer.delos.adapter.RelateDeviceGridAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.RelateDevice;
import com.boer.delos.model.RelateDeviceControls;
import com.boer.delos.model.RelateDeviceResult;
import com.boer.delos.model.RelateDevices;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.toast.ToastCommom;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 关联设备 popupWindow
 * create at 2016/7/12 13:41
 */
public class RelateDevicePopUpWindow extends PopupWindow implements View.OnClickListener {
    private RelateDevice mRelateDevice;
    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private View view;
    private Device mDevice;
    private android.widget.TextView tvPopupTitle;
    private android.widget.ImageView ivSelectedDevice;
    private android.widget.GridView gvLight;
    private android.widget.TextView tvCancle;
    private android.widget.TextView tvOk;
    public static RelateDevicePopUpWindow instance = null;
    private RelateDeviceGridAdapter gridAdapter;
    private ListView lvRelate;
    private List<RelateDevices> mRelateDevices;
    private RelateDeviceAdapter mRelateAdapter;
    private List<Map<String, Object>> mList;

    public RelateDevicePopUpWindow(Context context, Device device, RelateDevice relateDevice, ClickResultListener listener) {
        super(context);
        instance = this;
        this.context = context;
        this.listener = listener;
        this.mDevice = device;
        this.mRelateDevice = relateDevice;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_relate_device, null);
        setContentView(view);

        setProperty();
        initView();
        initData();
    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        this.tvOk = (TextView) view.findViewById(R.id.tvOk);
        this.tvCancle = (TextView) view.findViewById(R.id.tvCancle);
        this.lvRelate = (ListView) view.findViewById(R.id.lvRelate);
        this.gvLight = (GridView) view.findViewById(R.id.gvLight);
        this.ivSelectedDevice = (ImageView) view.findViewById(R.id.ivSelectedDevice);
        this.tvPopupTitle = (TextView) view.findViewById(R.id.tvPopupTitle);

        this.tvOk.setOnClickListener(this);
        this.tvCancle.setOnClickListener(this);
    }

    private void initData() {
        tvPopupTitle.setText(mDevice.getName() + " (" + mDevice.getRoomname() + ")");
        this.ivSelectedDevice.setImageResource(Constant.getResIdWithType(mDevice.getType()));
        //list数据获取所有灯的数据，排除当前的灯
        mList = Constant.lightDeviceRelate(mDevice.getType());
        gridAdapter = new RelateDeviceGridAdapter(context, mList, mDevice,
                new RelateDeviceGridAdapter.ItemClickListener() {
                    @Override
                    public void click(int pos) {
                        clearAllSelected();
                        mRelateAdapter.notifyDataSetChanged();
                        queryRelateDeviceInfo(String.valueOf(pos));
                    }
                });
        this.gvLight.setNumColumns(mList.size());
        this.gvLight.setAdapter(gridAdapter);

        mRelateDevices = getRelateDevices(getLightList());
        mRelateAdapter = new RelateDeviceAdapter(context, mRelateDevices);
        this.lvRelate.setAdapter(mRelateAdapter);
    }

    /**
     * 查询已关联的两个设备
     */
    private void queryRelateDeviceInfo(String chanel) {
        DeviceController.getInstance().queryRelateDeviceInfo(context, chanel, mDevice.getAddr(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    RelateDeviceResult result = new Gson().fromJson(Json, RelateDeviceResult.class);
                    if (result.getRet() != 0) {
                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        //关联设备
                        List<RelateDevice> list = result.getResponse();
                        if (list.size() == 0) {
                            mRelateDevice = null;
                        } else {
                            mRelateDevice = result.getResponse().get(0);
                            relateHandler();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    /**
     * 关联设备处理
     */
    private void relateHandler() {
        for (RelateDeviceControls control : mRelateDevice.getControls()) {
            int chanel = Integer.valueOf(control.getChannel());
            //如果是当前设备地址,跳过
            if (control.getAddr().equals(mDevice.getAddr())) {
                continue;
            }
            for (RelateDevices device : mRelateDevices) {
                //判断是否选中设备
                if (device.getDevice().getAddr().equals(control.getAddr())) {
                    List<Map<String, Object>> map = device.getChildList();
                    map.get(chanel).put("isSelected", true);
                    break;
                }
            }
        }
        mRelateAdapter.notifyDataSetChanged();
    }


    /**
     * 清除所有选中的列表
     */
    private void clearAllSelected() {
        for (RelateDevices relateDevices : mRelateDevices) {
            for (Map<String, Object> map : relateDevices.getChildList()) {
                map.put("isSelected", false);
            }
        }
    }


    /**
     * 获取所有灯的设备,排除解绑和当前灯
     *
     * @return
     */
    private List<Device> getLightList() {
        List<Device> deviceList = new ArrayList<>();
        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            if (!device.getDismiss() && device.getType().contains("Light")
                    && !device.getAddr().equals(mDevice.getAddr())) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    /**
     * 取得关联设备信息
     *
     * @param deviceList
     * @return
     */
    private List<RelateDevices> getRelateDevices(List<Device> deviceList) {
        List<RelateDevices> list = new ArrayList<>();
        for (Device device : deviceList) {
            RelateDevices relateDevices = new RelateDevices();
            relateDevices.setGroupTitle(device.getName());
            relateDevices.setDevice(device);
            relateDevices.setResId(Constant.getNonCircleBlueResIdWithType(device.getType()));
            relateDevices.setChildList(Constant.lightDeviceRelate(device.getType()));
            list.add(relateDevices);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOk:
                updateRelateControls();
                break;
            case R.id.tvCancle:
                dismiss();
                break;
        }
    }

    /**
     * 主设备信息
     *
     * @return
     */
    private RelateDeviceControls getMastControls() {
        for (int i = 0; i < mList.size(); i++) {
            Map<String, Object> map = mList.get(i);
            if ((Boolean) map.get("isSelected")) {
                RelateDeviceControls controls = new RelateDeviceControls();
                controls.setType(mDevice.getType());
                controls.setAddr(mDevice.getAddr());
                controls.setChannel(String.valueOf(i));
                return controls;
            }
        }
        return null;
    }

    /**
     * 副设备信息
     *
     * @return
     */
    private RelateDeviceControls getClusterControls() {
        for (RelateDevices relateDevices : mRelateDevices) {
            for (int i = 0; i < relateDevices.getChildList().size(); i++) {
                Map<String, Object> map = relateDevices.getChildList().get(i);
                if ((Boolean) map.get("isSelected")) {
                    RelateDeviceControls controls = new RelateDeviceControls();
                    controls.setType(relateDevices.getDevice().getType());
                    controls.setAddr(relateDevices.getDevice().getAddr());
                    controls.setChannel(String.valueOf(i));
                    return controls;
                }
            }
        }
        return null;
    }

    /**
     * 更新设备关联
     */
    private void updateRelateControls() {
        //主设备通道
        RelateDeviceControls mastControls = getMastControls();
        if (mastControls == null) {
            Toast.makeText(context, "请选择主灯", Toast.LENGTH_SHORT).show();
            return;
        }
        //副设备信息
        RelateDeviceControls clusterControls = getClusterControls();
//        if (clusterControls == null) {
//            Toast.makeText(context, "请选择关联灯", Toast.LENGTH_SHORT).show();
//            return;
//        }

        final List<RelateDeviceControls> controls = new ArrayList<>();
        controls.add(mastControls);  //添加自身信息
        if (clusterControls != null) {
            controls.add(clusterControls);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("addr", mDevice.getAddr());
        map.put("controls", controls);
        if (mRelateDevice != null) {
            map.put("dbId", mRelateDevice.getDbId());
            map.put("timestamp", mRelateDevice.getTimestamp());
        }
        DeviceController.getInstance().updateRelateDevice(context, map,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.d("RelateDevicePopUpWindow updateRelateControls（）onSuccess（）" + Json);
                        try {

                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() == 0) {
//                                ((ModifyDeviceListeningActivity) context).toastUtils.showSuccessWithStatus("灯关联成功");
                                dismiss();
                            } else {
//                                ToastCommom.getInstance().toastShort(context, null, ToastCommom.TOAST_ERROR);
                                ToastHelper.showShortMsg("关联失败，请重试");
//                                CustomFragmentToast.newInstanse(-1, "关联失败，请重试");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        L.d("RelateDevicePopUpWindow updateRelateControls（）onFailed（）" + Json);
                        ToastHelper.showShortMsg("关联失败，请重试");
                    }
                });
        //TODO
//        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
//        dismiss();
    }


    public interface ClickResultListener {
        void clickResult(int tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ToastCommom.getInstance().toastDismiss();

    }
}
