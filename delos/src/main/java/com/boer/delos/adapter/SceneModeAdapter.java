package com.boer.delos.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.ModeDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/9 0009 09:10
 * @Modify:
 * @ModifyDate:
 */


public class SceneModeAdapter extends MyBaseAdapter<Map<String, Object>> {
    private ArrayList<Boolean> flagList;

    public SceneModeAdapter(Context mContext, List<Map<String, Object>> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int resId = R.layout.item_scene_mode;
    }

    @Override
    public void setDatas(List<Map<String, Object>> listData) {
        super.setDatas(listData);
        initFlags(listData.size(), -1);

    }

    @Override
    public void convert(final MyViewHolder holder, final Map<String, Object> item, final int position) {

        TextView tv_device_name = holder.getView(R.id.tv_device_name);
        TextView tv_device_room = holder.getView(R.id.tv_device_classify);
        final CheckedTextView ctv_choice = holder.getView(R.id.ctv_choice);
        ImageView iv_device_type = holder.getView(R.id.iv_device_type);

        ModeDevice modeDevice = (ModeDevice) item.get("device");
        if (modeDevice.isHave()) {
            flagList.set(position, true);
        }

        openDevcieAll(holder, item, position);



//        openDevice(holder, item, position);
        refreshListener(holder, item, position);

        ctv_choice.setChecked(flagList.get(position));

        tv_device_name.setText(modeDevice.getDevicename());
        tv_device_room.setText(modeDevice.getRoomname());
        iv_device_type.setImageResource(Constant.getResIdWithType(modeDevice.getDevicetype()));

        ctv_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctv_choice.toggle();
                initFlags(-1, position);
//                openDevice(holder, item, position);
                openDevcieAll(holder, item, position);
            }
        });


    }


    private void openDevcieAll(MyViewHolder holder, Map<String, Object> item, int position) {
        ToggleButton toggleButtonAll = holder.getView(R.id.toggleButtonAll);
        toggleButtonAll.setVisibility(flagList.get(position) ? View.VISIBLE : View.GONE);

        ToggleButton toggleButton1 = holder.getView(R.id.toggleButton1);
        ToggleButton toggleButton2 = holder.getView(R.id.toggleButton2);
        ToggleButton toggleButton3 = holder.getView(R.id.toggleButton3);
        ToggleButton toggleButton4 = holder.getView(R.id.toggleButton4);

        LinearLayout ll_light_adjust = holder.getView(R.id.ll_light_adjust);

        toggleButton1.setVisibility(View.GONE);
        toggleButton2.setVisibility(View.GONE);
        toggleButton3.setVisibility(View.GONE);
        toggleButton4.setVisibility(View.GONE);

        ll_light_adjust.setVisibility(View.GONE);

        List<Map<String, Object>> mapList = (List<Map<String, Object>>) item.get("list");
        ModeDevice modeDevice = (ModeDevice) item.get("device");

        switch (modeDevice.getDevicetype()) {
            case "Light1":
                if (modeDevice.getParams().getState().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;
            case "Light2":
                if (modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;
            case "Light3":
                if (modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1")
                        || modeDevice.getParams().getState3().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;
            case "Light4":
                if (modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1")
                        || modeDevice.getParams().getState3().equals("1")
                        || modeDevice.getParams().getState3().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;
            case "LightAdjust":
                if (modeDevice.getParams().getState().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;
            default:
                if (modeDevice.getParams().getState().equals("1")) {
                    toggleButtonAll.setChecked(true);
                }
                break;


        }


    }

    private void openDevice(MyViewHolder holder, Map<String, Object> item, int position) {
        ToggleButton toggleButtonAll = holder.getView(R.id.toggleButtonAll);
        ToggleButton toggleButton1 = holder.getView(R.id.toggleButton1);
        ToggleButton toggleButton2 = holder.getView(R.id.toggleButton2);
        ToggleButton toggleButton3 = holder.getView(R.id.toggleButton3);
        ToggleButton toggleButton4 = holder.getView(R.id.toggleButton4);
        SeekBar seekBar = holder.getView(R.id.seekBar_adjustLight);
        LinearLayout ll_light_adjust = holder.getView(R.id.ll_light_adjust);

        toggleButton1.setVisibility(View.GONE);
        toggleButton2.setVisibility(View.GONE);
        toggleButton3.setVisibility(View.GONE);
        toggleButton4.setVisibility(View.GONE);

        ll_light_adjust.setVisibility(View.GONE);

        List<Map<String, Object>> mapList = (List<Map<String, Object>>) item.get("list");
        ModeDevice modeDevice = (ModeDevice) item.get("device");

        switch (modeDevice.getDevicetype()) {
            case "Light1":
//                toggleButton1.setVisibility(View.VISIBLE);
//                toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                break;
            case "Light2":

                toggleButton1.setVisibility(View.VISIBLE);
                toggleButton2.setVisibility(View.VISIBLE);

                toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton2.setChecked(modeDevice.getParams().getState().equals("1"));
                break;
            case "Light3":

                toggleButton1.setVisibility(View.VISIBLE);
                toggleButton2.setVisibility(View.VISIBLE);
                toggleButton3.setVisibility(View.VISIBLE);

                toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton2.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton3.setChecked(modeDevice.getParams().getState().equals("1"));
                break;
            case "Light4":

                toggleButton1.setVisibility(View.VISIBLE);
                toggleButton2.setVisibility(View.VISIBLE);
                toggleButton3.setVisibility(View.VISIBLE);
                toggleButton4.setVisibility(View.VISIBLE);

                toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton2.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton3.setChecked(modeDevice.getParams().getState().equals("1"));
                toggleButton4.setChecked(modeDevice.getParams().getState().equals("1"));
                break;
            case "LightAdjust":

                ll_light_adjust.setVisibility(View.VISIBLE);
//                    seekBar.setVisibility(View.VISIBLE);

                String coeff = modeDevice.getParams().getCoeff();
                int adjust = 0;
                try {
                    adjust = Integer.valueOf(coeff);
                } catch (NumberFormatException e) {
                    adjust = 0;
                }
                seekBar.setProgress(adjust);
                if (seekBar.getProgress() > 0) {
                    toggleButtonAll.setChecked(true);
                } else {
                    toggleButtonAll.setChecked(false);
                }

                break;
            default:

                toggleButton1.setVisibility(View.VISIBLE);

                if (modeDevice.getParams().getState() == null) {
                    return;
                }
                toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                break;
        }


    }

    private void refreshListener(final MyViewHolder hoder, final Map<String, Object> item, final int position) {
        final ModeDevice modeDevice = (ModeDevice) item.get("device");
//        final List<ControlDevice> list = new ArrayList<>();
        final ControlDevice controlDevice = new ControlDevice();

        hoder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hoder.getView(R.id.toggleButtonAll).getVisibility() == View.VISIBLE
                        && ((ToggleButton) hoder.getView(R.id.toggleButtonAll)).isChecked()) {
                    openDevice(hoder, item, position);
                }
            }
        });

        hoder.setOnClickListener(R.id.toggleButtonAll, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingControlDeviceOpenAll(modeDevice,
                        ((ToggleButton) hoder.getView(R.id.toggleButtonAll)).isChecked());
            }
        });

        hoder.setOnClickListener(R.id.toggleButton1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingControlDevice(modeDevice, controlDevice, 0, "");

//                list.add(device);
//                controlDevice(list, modeDevice);

            }
        });
        hoder.setOnClickListener(R.id.toggleButton2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingControlDevice(modeDevice, controlDevice, 1, "");
//                list.add(device);
//                controlDevice(list, modeDevice);

            }
        });
        hoder.setOnClickListener(R.id.toggleButton3, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingControlDevice(modeDevice, controlDevice, 2, "");
//                list.add(device);
//                controlDevice(list, modeDevice);


            }
        });
        hoder.setOnClickListener(R.id.toggleButton4, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingControlDevice(modeDevice, controlDevice, 3, "");
//                list.add(device);
//                controlDevice(list, modeDevice);

            }
        });
        SeekBar seekBar = hoder.getView(R.id.seekBar_adjustLight);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seekBar.getProgress());
                settingControlDevice(modeDevice, controlDevice, -1, seekBar.getProgress() + "");
//                list.add(device);
//                controlDevice(list, modeDevice);

            }
        });

    }

    private ModeDevice settingControlDevice(ModeDevice modeDevice,
                                            ControlDevice device,
                                            int pos,
                                            String coeff) {

        device.setAddr(modeDevice.getDeviceAddr());
        device.setAreaName(modeDevice.getAreaname());
        device.setDeviceName(modeDevice.getDevicename());
        device.setRoomName(modeDevice.getRoomname());
        device.setType(modeDevice.getDevicetype());

        ControlDeviceValue value = modeDevice.getParams();

        switch (pos) {
            case 0:
                if (modeDevice.getParams().getState() != null) {
                    value.setState(modeDevice.getParams().getState().equals("1") ? "0" : "1");
                } else {
                    value.setState("1");
                }
                break;
            case 1:
                if (modeDevice.getParams().getState2() != null) {
                    value.setState2(modeDevice.getParams().getState2().equals("1") ? "0" : "1");
                } else
                    value.setState("1");

                break;
            case 2:
                if (modeDevice.getParams().getState3() != null) {
                    value.setState3(modeDevice.getParams().getState3().equals("1") ? "0" : "1");
                } else
                    value.setState("1");
                break;
            case 3:
                if (modeDevice.getParams().getState4() != null) {
                    value.setState4(modeDevice.getParams().getState4().equals("1") ? "0" : "1");
                } else
                    value.setState("1");
                break;
            case -1:
                if (modeDevice.getParams().getState() != null) {
                    value.setState(modeDevice.getParams().getState().equals("1") ? "0" : "1");
                    value.setCoeff(coeff);
                } else {
                    value.setState("1");
                    value.setCoeff(coeff);
                }

                break;

        }

        modeDevice.setParams(value);

        return modeDevice;
    }

    /**
     * 全开、全关
     *
     * @param modeDevice
     * @param open
     */
    private void settingControlDeviceOpenAll(ModeDevice modeDevice, boolean open) {
        String deviceType = modeDevice.getDevicetype();
        switch (deviceType) {
            case "Light1":
                modeDevice.getParams().setState(open ? "1" : "0");
                break;
            case "Light2":
                modeDevice.getParams().setState(open ? "1" : "0");
                modeDevice.getParams().setState2(open ? "1" : "0");
                break;
            case "Light3":
                modeDevice.getParams().setState(open ? "1" : "0");
                modeDevice.getParams().setState2(open ? "1" : "0");
                modeDevice.getParams().setState3(open ? "1" : "0");
                break;
            case "Light4":
                modeDevice.getParams().setState(open ? "1" : "0");
                modeDevice.getParams().setState2(open ? "1" : "0");
                modeDevice.getParams().setState3(open ? "1" : "0");
                modeDevice.getParams().setState4(open ? "1" : "0");
                break;
            default:
                break;

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

    }

    public ArrayList<Boolean> getFlagList() {
        return flagList;
    }
}
