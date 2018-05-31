package com.boer.delos.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.scene.LightingControlListeningActivity;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NoDoubleClickUtils;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.BindEmailPopUpWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 灯设备界面的第二级list adapter
 * create at 2016/6/5 19:47
 */
public class LightingControlChildAdapter extends BaseAdapter {


    private LayoutInflater inflater = null;
    private Context context;
    private List<HashMap<String, String>> list;
    private Device device;
    private ViewHolder viewHolder;
    String coeff = "0";
    BindEmailPopUpWindow bindPopUpWindow = null;

    public LightingControlChildAdapter(Context context, List<HashMap<String, String>> list, Device device) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.device = device;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lighting_control_child, null);
            viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.ivItemImage = (ImageView) convertView.findViewById(R.id.ivItemImage);
            viewHolder.ivLightSwitch = (ImageView) convertView.findViewById(R.id.ivLightSwitch);
            viewHolder.sbLight = (SeekBar) convertView.findViewById(R.id.sbLight);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Map<String, String> map = list.get(position);
        final String state = map.get("state");

        if ("1".equals(state)) {
            viewHolder.ivLightSwitch.setImageResource(R.drawable.ic_settings_open);
            viewHolder.ivItemImage.setImageResource(R.drawable.ic_light_on);
        } else {
            viewHolder.ivLightSwitch.setImageResource(R.drawable.ic_settings_close);
            viewHolder.ivItemImage.setImageResource(R.drawable.ic_light_off);
        }

        final String name = map.get("name");
        if (name != null) {
            viewHolder.tvItemName.setText(name);
        } else {
            viewHolder.tvItemName.setText("灯" + (position + 1));
        }

        String type = "";

        try {
            type = map.get("type");
        } catch (Exception e) {
            e.printStackTrace();
            type = "";
        }
        if (type == null) {
            type = "";
        }
        if (type.equals("LightAdjust")) {
            if (position == 0) {
                viewHolder.sbLight.setVisibility(View.VISIBLE);
            } else {
                viewHolder.sbLight.setVisibility(View.GONE);
            }

        } else {
            viewHolder.sbLight.setVisibility(View.GONE);
        }

        final String finalType = type;
        //读取本地保存的调光灯数值
        coeff = SharedPreferencesUtils.readLightAdjustFromPreferences(context) + "";
        viewHolder.sbLight.setProgress(SharedPreferencesUtils.readLightAdjustFromPreferences(context));

        viewHolder.sbLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coeff = progress + "";

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferencesUtils.saveLightAdjustToPreferences(context, Integer.parseInt(coeff));
                if (map.get("state").equals("1")) {//调光灯打开，调节亮度
                    sendController(position, "1", finalType, coeff);

                }
            }
        });


        viewHolder.ivLightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ("1".equals(state)) {
                    map.put("state", "0");
                    //发送控制命令
                    sendController(position, "0", finalType, coeff);
                } else {
                    map.put("state", "1");
                    //发送控制命令
                    sendController(position, "1", finalType, coeff);
                }
                LightingControlChildAdapter.this.notifyDataSetChanged();
            }
        });

        popupModifyLightName(convertView, position, name);

        return convertView;
    }


    public static class ViewHolder {
        public TextView tvItemName;
        public ImageView ivItemImage;
        public ImageView ivLightSwitch;
        public SeekBar sbLight;
    }

    /**
     * 双击弹出对话框，修改灯光名称
     *
     * @param convertView
     */
    private void popupModifyLightName(View convertView, final int position, final String name) {
        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NoDoubleClickUtils.isFastDoubleClick()) {//是双击
                    try {
                        if (bindPopUpWindow != null) {
                            if (bindPopUpWindow.isShowing()) {
                                bindPopUpWindow.dismiss();
                            }
                        }
                        bindPopUpWindow = new BindEmailPopUpWindow(context, context.getString(R.string.my_center_bind_email), new BindEmailPopUpWindow.ClickResultListener() {
                            @Override
                            public void ClickResult(String tag) {
                                String name = tag;

                                if (name.equals("")) {
                                    Toast.makeText(context, "名称不能为空", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, String> map1 = device.getLightName();
                                    if (map1 == null) {
                                        map1 = new HashMap<String, String>();
                                    }
                                    if (position == 0) {
                                        map1.put("name1", name);
                                    } else if (position == 1) {
                                        map1.put("name2", name);
                                    } else if (position == 2) {
                                        map1.put("name3", name);
                                    } else if (position == 3) {
                                        map1.put("name4", name);
                                    }

                                    device.setLightName(map1);

                                    bindPopUpWindow.dismiss();
                                    updateProp(name, viewHolder.tvItemName);
                                }
                            }
                        });
                        bindPopUpWindow.setEditText(name);
                        bindPopUpWindow.setEditTextHint("请输入灯的名称");
                        bindPopUpWindow.setTextViewTitle("修改灯的名称");
                        bindPopUpWindow.showAtLocation(finalConvertView, Gravity.CENTER, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    /**
     * 修改灯的名称
     */
    private void updateProp(final String lightname, final TextView textView) {
        final ToastUtils toastUtils = new ToastUtils(context);
        toastUtils.showProgress("加载中...");
        DeviceController.getInstance().updateProp(context, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {

                    L.e("LightingControlChildAdapter updateProp onSuccess's json===" + Json);
                    String ret = JsonUtil.parseString(Json, "ret");
                    if (ret != null && "0".equals(ret)) {
                        textView.setText(lightname);
                        // notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                toastUtils.showSuccessWithStatus("修改成功");
                            }
                        }, 5000);
                    } else {
                        toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                try {
                    if (bindPopUpWindow != null) {
                        bindPopUpWindow.dismiss();
                    }
                    if (toastUtils != null)
                        toastUtils.showErrorWithStatus(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发送控制命令
     *
     * @param position type 作为判断是否是调光灯或者灯1
     *                 coeff 调光灯值
     */
    private void sendController(final int position, final String flag, final String type, final String coeff) {
        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        if (position == 0) {
            value.setState(flag);
            if (type.equals("LightAdjust")) {
                value.setCoeff(coeff);
            }
        } else if (position == 1) {
            value.setState2(flag);
        } else if (position == 2) {
            value.setState3(flag);
        } else {
            value.setState4(flag);
        }
        controlDevice.setValue(value);
        controlDevices.add(controlDevice);

        ((LightingControlListeningActivity) context).deviceLightControl(controlDevices);
    }


}
