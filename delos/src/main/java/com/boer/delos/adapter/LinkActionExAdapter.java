package com.boer.delos.adapter;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.ModeDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 17/5/5.
 */

public class LinkActionExAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<Map<String, Object>> modeMapDeviceLists;
    private ISimpleInterfaceInt mListener;

    public LinkActionExAdapter(Context mContext, List<Map<String, Object>> modeMapDeviceLists,
                               ISimpleInterfaceInt mListener) {
        this.mContext = mContext;
        this.modeMapDeviceLists = modeMapDeviceLists;
        this.mListener = mListener;

    }

    public void setListData(List<Map<String, Object>> modeMapDeviceLists) {
        this.modeMapDeviceLists = modeMapDeviceLists;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return modeMapDeviceLists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return modeMapDeviceLists.get(i).size() > 1 ? 1 : modeMapDeviceLists.get(i).size();
    }

    @Override
    public ModeDevice getGroup(int i) {
        return (ModeDevice) modeMapDeviceLists.get(i).get("device");
    }

    @Override
    public ModeDevice getChild(int i, int i1) {
        return (ModeDevice) modeMapDeviceLists.get(i).get("device");
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean b, View convertView, ViewGroup viewGroup) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_scene_mode, null);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.updateGroupHolder(getGroup(position), position);
        groupHolder.groupClickListener(getGroup(position), position);
        return convertView;
    }

    @Override
    public View getChildView(int position, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_mode_timer_child, null);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        childHolder.updateChilderHolder(getGroup(position), position);
        childHolder.childClickListener(getGroup(position), position);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }


    class GroupHolder {
        private TextView tv_device_name;
        private TextView tv_device_room;
        private CheckedTextView ctv_choice;
        private ImageView iv_device_type;
        private CheckedTextView toggleButtonAll;

        public GroupHolder(View view) {

            tv_device_name = (TextView) view.findViewById(R.id.tv_device_name);
            tv_device_room = (TextView) view.findViewById(R.id.tv_device_classify);
            ctv_choice = (CheckedTextView) view.findViewById(R.id.ctv_choice);
            iv_device_type = (ImageView) view.findViewById(R.id.iv_device_type);
            toggleButtonAll = (CheckedTextView) view.findViewById(R.id.toggleButtonAll);

            ctv_choice.setVisibility(View.GONE);
        }

        private void updateGroupHolder(ModeDevice modeDevice, int position) {

            tv_device_name.setText(modeDevice.getDevicename());
            tv_device_room.setText(modeDevice.getRoomname());
            iv_device_type.setImageResource(Constant.getResIdWithType(modeDevice.getDevicetype()));


            toggleButtonAll.setChecked(modeDeviceGroupIsOpen(modeDevice));
        }

        private void groupClickListener(final ModeDevice modeDevice, final int position) {
            toggleButtonAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    settingControlDeviceOpenAll(modeDevice, toggleButtonAll.isChecked(), position);
                    toggleButtonAll.toggle();
                }
            });

        }

    }

    class ChildHolder {
        private CheckedTextView toggleButton1;
        private CheckedTextView toggleButton2;
        private CheckedTextView toggleButton3;
        private CheckedTextView toggleButton4;

        private LinearLayout ll_light_adjust;
        private SeekBar seekBar_adjustLight;

        private TextView tv_sin_1;
        private TextView tv_sin_2;
        private TextView tv_sin_3;
        private TextView tv_sin_4;

        public ChildHolder(View view) {
            toggleButton1 = (CheckedTextView) view.findViewById(R.id.toggleButton1);
            toggleButton2 = (CheckedTextView) view.findViewById(R.id.toggleButton2);
            toggleButton3 = (CheckedTextView) view.findViewById(R.id.toggleButton3);
            toggleButton4 = (CheckedTextView) view.findViewById(R.id.toggleButton4);
            ll_light_adjust = (LinearLayout) view.findViewById(R.id.ll_light_adjust);
            seekBar_adjustLight = (SeekBar) view.findViewById(R.id.seekBar_adjustLight);

            tv_sin_1 = (TextView) view.findViewById(R.id.tv_sin_1);
            tv_sin_2 = (TextView) view.findViewById(R.id.tv_sin_2);
            tv_sin_3 = (TextView) view.findViewById(R.id.tv_sin_3);
            tv_sin_4 = (TextView) view.findViewById(R.id.tv_sin_4);

            tv_sin_1.setVisibility(View.GONE);
            tv_sin_2.setVisibility(View.GONE);
            tv_sin_3.setVisibility(View.GONE);
            tv_sin_4.setVisibility(View.GONE);

        }

        private void updateChilderHolder(ModeDevice modeDevice, int position) {
            toggleButton1.setVisibility(View.GONE);
            toggleButton2.setVisibility(View.GONE);
            toggleButton3.setVisibility(View.GONE);
            toggleButton4.setVisibility(View.GONE);
            ll_light_adjust.setVisibility(View.GONE);


            modeDeviceChildOpen(modeDevice, position);

        }

        private void childClickListener(final ModeDevice modeDevice, int position) {

            toggleButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleButton1.toggle();
                    switch (modeDevice.getDevicetype()) {
                        case "CircadianLight":
                            modeDevice.getParams().setButtonName(mContext.getString(R.string.sin_sun));
                            toggleButton2.setChecked(false);
                            toggleButton3.setChecked(false);
                            toggleButton4.setChecked(false);
                            break;
                        default:
                            modeDevice.getParams().setState(modeDevice.getParams().getState().equals("1") ?
                                    "0" : "1");
                            break;

                    }

//                    notifyDataSetChanged();
                }
            });
            toggleButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleButton2.toggle();
                    switch (modeDevice.getDevicetype()) {
                        case "CircadianLight":
                            toggleButton1.setChecked(false);
                            toggleButton3.setChecked(false);
                            toggleButton4.setChecked(false);
                            modeDevice.getParams().setButtonName(mContext.getString(R.string.sin_muscle));
                            break;
                        default:
                            modeDevice.getParams().setState2(modeDevice.getParams().getState2().equals("1") ?
                                    "0" : "1");
                            break;

                    }

//                    notifyDataSetChanged();
                }
            });
            toggleButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleButton3.toggle();

                    switch (modeDevice.getDevicetype()) {
                        case "CircadianLight":
                            modeDevice.getParams().setButtonName(mContext.getString(R.string.sin_relax));
                            toggleButton2.setChecked(false);
                            toggleButton1.setChecked(false);
                            toggleButton4.setChecked(false);
                            break;
                        default:
                            modeDevice.getParams().setState3(modeDevice.getParams().getState3().equals("1") ?
                                    "0" : "1");
                            break;

                    }


//                    notifyDataSetChanged();
                }
            });
            toggleButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleButton4.toggle();


                    switch (modeDevice.getDevicetype()) {
                        case "CircadianLight":
                            toggleButton2.setChecked(false);
                            toggleButton1.setChecked(false);
                            toggleButton3.setChecked(false);

                            modeDevice.getParams().setButtonName(mContext.getString(R.string.sin_happy));
                            break;
                        default:
                            modeDevice.getParams().setState4(modeDevice.getParams().getState4().equals("1") ?
                                    "0" : "1");
                            break;

                    }

//                    notifyDataSetChanged();
                }
            });
            seekBar_adjustLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    seekBar.setProgress(seekBar.getProgress());
//                    settingControlDevice(modeDevice, controlDevice, -1, seekBar.getProgress() + "");

                    modeDevice.getParams().setCoeff(seekBar.getProgress() + "");
//                    notifyDataSetChanged();
                }
            });

        }

        private void modeDeviceChildOpen(ModeDevice modeDevice, int position) {
            switch (modeDevice.getDevicetype()) {
                case "Light1":
//                    toggleButton1.setVisibility(View.VISIBLE);
//                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                    break;
                case "Light2":
                    toggleButton1.setVisibility(View.VISIBLE);
                    toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                    toggleButton2.setChecked(modeDevice.getParams().getState2().equals("1"));

                    break;
                case "Light3":
                    toggleButton1.setVisibility(View.VISIBLE);
                    toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton3.setVisibility(View.VISIBLE);

                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                    toggleButton2.setChecked(modeDevice.getParams().getState2().equals("1"));
                    toggleButton3.setChecked(modeDevice.getParams().getState3().equals("1"));

                    break;

                case "Light4":
                    toggleButton1.setVisibility(View.VISIBLE);
                    toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton3.setVisibility(View.VISIBLE);
                    toggleButton4.setVisibility(View.VISIBLE);

                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));
                    toggleButton2.setChecked(modeDevice.getParams().getState2().equals("1"));
                    toggleButton3.setChecked(modeDevice.getParams().getState3().equals("1"));
                    toggleButton4.setChecked(modeDevice.getParams().getState4().equals("1"));
                    break;

                case "LightAdjust":
//                    toggleButton1.setVisibility(View.GONE);
//                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));

                    ll_light_adjust.setVisibility(View.VISIBLE);
                    seekBar_adjustLight.setProgress(Integer.valueOf(modeDevice.getParams().getCoeff())
                            , true);
                    break;
                case "CircadianLight":
                    toggleButton1.setVisibility(View.VISIBLE);
                    toggleButton2.setVisibility(View.VISIBLE);
                    toggleButton3.setVisibility(View.VISIBLE);
                    toggleButton4.setVisibility(View.VISIBLE);

                    tv_sin_1.setVisibility(View.VISIBLE);
                    tv_sin_2.setVisibility(View.VISIBLE);
                    tv_sin_3.setVisibility(View.VISIBLE);
                    tv_sin_4.setVisibility(View.VISIBLE);
                    String buttonName = modeDevice.getParams().getButtonName();
                    if (TextUtils.isEmpty(buttonName)) {

                    } else if (buttonName.equals(mContext.getString(R.string.sin_sun))) {
                        toggleButton1.setChecked(true);
                    } else if (buttonName.equals(mContext.getString(R.string.sin_muscle))) {
                        toggleButton2.setChecked(true);
                    } else if (buttonName.equals(mContext.getString(R.string.sin_relax))) {
                        toggleButton3.setChecked(true);
                    } else if (buttonName.equals(mContext.getString(R.string.sin_happy))) {
                        toggleButton4.setChecked(true);
                    }
                    break;
                default:
//                    toggleButton1.setVisibility(View.GONE);
//                    toggleButton1.setChecked(modeDevice.getParams().getState().equals("1"));

                    break;


            }
        }

    }

    private boolean modeDeviceGroupIsOpen(ModeDevice modeDevice) {
        boolean isOpen = false;
        switch (modeDevice.getDevicetype()) {
            case "Light1": //state

                isOpen = modeDevice.getParams().getState().equals("1");
                break;
            case "Light2":
                isOpen = modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1");
                break;
            case "Light3":
                isOpen = modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1")
                        || modeDevice.getParams().getState3().equals("1");
                break;
            case "Light4":
                isOpen = modeDevice.getParams().getState().equals("1")
                        || modeDevice.getParams().getState2().equals("1")
                        || modeDevice.getParams().getState3().equals("1")
                        || modeDevice.getParams().getState4().equals("1");
                break;
            case "LightAdjust": // 打开 并且 调光不是0  表示开
                isOpen = modeDevice.getParams().getState().equals("1")
                        && !modeDevice.getParams().getCoeff().equals("0");
                break;
            case "CircadianLight":
                isOpen = true;

                break;
            case "Exist":
                isOpen = modeDevice.getParams().getState().equals("1");

                break;
            case "Gsm":
                isOpen = modeDevice.getParams().getState().equals("1");

                break;
            default:
                //state
                isOpen = modeDevice.getParams().getState().equals("1");
                break;
        }


        return isOpen;
    }


    /**
     * 全开、全关
     *
     * @param modeDevice
     * @param open
     */
    private void settingControlDeviceOpenAll(ModeDevice modeDevice, boolean open, int position) {
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
                modeDevice.getParams().setState(open ? "1" : "0");
                break;
        }
        modeMapDeviceLists.get(position).put("device", modeDevice);
        if (mListener != null && !open) {
            mListener.clickListener(position);
        }
        notifyDataSetChanged();
    }

}
