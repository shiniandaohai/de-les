package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.LightingControlAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 照明控制界面
 * create at 2016/5/5 14:50
 */
public class LightingControlListeningActivity extends BaseListeningActivity {

    private ImageView ivLightTop;
    private ListView lvLightList;

    private LightingControlAdapter adapter;
    private List<DeviceRelate> list = new ArrayList<>();
    private String roomId;
    private boolean isLightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_control);
        roomId = getIntent().getStringExtra("roomId");
        initView();
        initData();
    }

    @Override
    protected void deviceStatusUpdate() {
        setLightInfo();
    }

    private void initView() {
        initTopBar(R.string.lighting_control, null, true, false);
        this.lvLightList = (ListView) findViewById(R.id.lvLightList);
        this.ivLightTop = (ImageView) findViewById(R.id.ivLightTop);

    }

    private void initData() {
        adapter = new LightingControlAdapter(this);
        adapter.setList(list);
        this.lvLightList.setAdapter(adapter);

        setLightInfo();
    }

    /**
     * 设定灯光信息
     */
    private void setLightInfo() {
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<DeviceRelate> filterList = filterDismissDeviceList(deviceRelateList);
        if (filterList.size() > 0) {
            list.clear();
            list.addAll(filterList);
            adapter.notifyDataSetChanged();
        }

        //顶部大图更新
        lightImageViewUpdate();
    }

    /**
     * 过滤解绑和不是当前房间设备
     *
     * @param list
     * @return
     */
    private List<DeviceRelate> filterDismissDeviceList(List<DeviceRelate> list) {
        List<DeviceRelate> filterList = new ArrayList<>();
        //过滤灯,未解绑, 当前房间
        for (DeviceRelate deviceRelate : list) {
            Device device = deviceRelate.getDeviceProp();
            if (device.getType().contains("Light")
                    && !device.getDismiss()
                    && roomId.equals(device.getRoomId())) {
                filterList.add(deviceRelate);
            }
        }
        return filterList;
    }

    /**
     * 更新顶部大图
     */
    private void lightImageViewUpdate() {
        //判断是否有灯打开
        isLightOn = checkLightOn(list);
        if (isLightOn) {
            ivLightTop.setImageResource(R.drawable.ic_light_on_b);
        } else {
            ivLightTop.setImageResource(R.drawable.ic_light_off_b);
        }
    }

    /**
     * 检查是否有灯打开
     *
     * @param list
     * @return
     */
    private Boolean checkLightOn(List<DeviceRelate> list) {
        for (DeviceRelate devicerelate : list) {
            DeviceStatus deviceStatus = devicerelate.getDeviceStatus();
            if (deviceStatus.getValue() != null) {
                DeviceStatusValue value = deviceStatus.getValue();
                if (value.getState() != null && "1".equals(value.getState())) {
                    return Boolean.TRUE;
                } else if (value.getState2() != null && "1".equals(value.getState2())) {
                    return Boolean.TRUE;
                } else if (value.getState3() != null && "1".equals(value.getState3())) {
                    return Boolean.TRUE;
                } else if (value.getState4() != null && "1".equals(value.getState4())) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public void deviceLightControl(List<ControlDevice> controlDevices) {
        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
                        //表明控制成功
//                        getDeviceStatusInfo();
                        ivLightTop.setImageResource(!isLightOn ? R.drawable.ic_light_on_b : R.drawable.ic_light_off_b);
                        isLightOn = !isLightOn;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                L.i("LightingControlChildAdapter sendController() onSuccess()" + Json);
            }

            @Override
            public void onFailed(String json) {
                L.d("LightingControlChildAdapter sendController() onFailed():" + json);
            }
        });
    }

//    @Override
//    protected void getDeviceStatusInfo() {
//        super.getDeviceStatusInfo();
//        setLightInfo();
//    }
}
