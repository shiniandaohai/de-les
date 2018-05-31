package com.boer.delos.activity.healthylife.wifiAirClean;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.AirControlAdapter;
import com.boer.delos.model.AirClean;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class WifiAirControlFragment extends Fragment {
    @Bind(R.id.gv_air_control)
    GridView gvAirControl;
    AirControlAdapter airControlAdapter;
    List<AirClean> list;
    OnControlFragmentClickItem onClickItem;
    int speed = 8;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.from(getActivity()).inflate(R.layout.fragment_air_control, null);
        ButterKnife.bind(this, view);
        initAction();
        return view;
    }

    private void initAction() {

        list = new ArrayList<>();
        airControlAdapter = new AirControlAdapter(getActivity(), list);
        gvAirControl.setAdapter(airControlAdapter);
        airControlAdapter.notifyDataSetChanged();
        gvAirControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initMod(position);

                if (position == 0) {
                    airControlValue(position);
                    if (list.get(0).isCheck()) {
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    } else {
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    }
                } else {
                    if (list.get(0).isCheck()) {
                        airControlValue(position);
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    } else {
                        ToastHelper.showShortMsg(getString(R.string.tip_take_on));
                    }
                }
            }
        });

        for (int i = 0; i < 6; i++) {
            AirClean airClean = new AirClean();
            switch (i) {

                case 0:
                    airClean.setResSelector(WifiAirCleanDetailsActivity.skinColor);
                    airClean.setRes(R.mipmap.air_take_on_selector);
                    airClean.setAirRes(R.mipmap.air_take_on);
                    airClean.setName(getString(R.string.air_txt_toggle));
                    break;
                case 1:
                    airClean.setResSelector(getResources().getColor(R.color.transparent));
                    airClean.setRes(R.mipmap.air_wind_down);
                    airClean.setAirRes(R.mipmap.air_wind_down);
                    airClean.setName(getString(R.string.air_txt_wind_down));
                    break;
                case 2:
                    airClean.setResSelector(getResources().getColor(R.color.transparent));
                    airClean.setRes(R.mipmap.air_wind_up);
                    airClean.setAirRes(R.mipmap.air_wind_up);
                    airClean.setName(getString(R.string.air_txt_wind_up));
                    break;
                case 3:
                    airClean.setResSelector(WifiAirCleanDetailsActivity.skinColor);
                    airClean.setRes(R.mipmap.air_auto_mod_selector);
                    airClean.setAirRes(R.mipmap.air_auto_mod);
                    airClean.setName(getString(R.string.air_txt_auto_mode));
                    break;
                case 4:
                    airClean.setResSelector(WifiAirCleanDetailsActivity.skinColor);
                    airClean.setAirRes(R.mipmap.air_wind_mod);
                    airClean.setRes(R.mipmap.air_wind_mod_selector);
                    airClean.setName(getString(R.string.air_txt_hurricane_mode));
                    break;
                case 5:
                    airClean.setResSelector(WifiAirCleanDetailsActivity.skinColor);
                    airClean.setAirRes(R.mipmap.air_sleep_mod);
                    airClean.setRes(R.mipmap.air_sleep_mod_selector);
                    airClean.setName(getString(R.string.air_txt_sleep_mode));
                    break;

            }
            list.add(airClean);
        }
        airControlAdapter.notifyDataSetChanged();
    }

    private void airControlValue(int pos) {
        String cmdType = null;
        int cmdValue=0;
        switch (pos) {
            case 0:
                if (list.get(0).isCheck()) {
                    cmdType="AirPower";
                    cmdValue = 0;
                } else {
                    cmdType="AirPower";
                    cmdValue = 3;
                }
                break;
            case 1:
                if (list.get(0).isCheck()) {
                    if(speed-8<0){
                        speed=0;
                    }
                    else{
                        speed-=8;
                    }
                    cmdType="AirSpeed";
                    cmdValue = speed;
                }
                break;
            case 2:
                if (list.get(0).isCheck()) {
                    if(speed+8>100){
                        speed=100;
                    }
                    else{
                        speed+=8;
                    }
                    cmdType="AirSpeed";
                    cmdValue = speed;
                }
                break;
            case 3:
                if (list.get(0).isCheck()) {
                    cmdType="Power";
                    cmdValue = 1;
                }
                break;
            case 4:
                if (list.get(0).isCheck()) {
                    cmdType="Power";
                    cmdValue = 0;
                }

                break;
            case 5:
                if (list.get(0).isCheck()) {
                    cmdType="Power";
                    cmdValue = 2;
                }
                break;
        }
        if(cmdType==null){
            return;
        }
        onClickItem.posCmd(cmdType,cmdValue);
    }


    private void initMod(int position) {

        boolean toggle = list.get(0).isCheck();
        boolean isCheck = list.get(position).isCheck();

        isCheck = !isCheck;

        if (position == 0) {
            list.get(position).setCheck(isCheck);
        } else {
            list.get(position).setCheck(isCheck && toggle);
        }

        if (!list.get(0).isCheck()) {
            list.get(3).setCheck(false);
            list.get(4).setCheck(false);
            list.get(5).setCheck(false);
            if (position != 0)
                ToastHelper.showShortMsg(getString(R.string.tip_take_on));
        } else {
            if (list.get(3).isCheck() && position == 3) {
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
            }
            if (list.get(4).isCheck() && position == 4) {
                list.get(3).setCheck(false);
                list.get(5).setCheck(false);
            }
            if (list.get(5).isCheck() && position == 5) {
                list.get(4).setCheck(false);
                list.get(3).setCheck(false);
            }

            if (position == 1) {
                list.get(3).setCheck(false);
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
            }
            if (position == 2) {
                list.get(3).setCheck(false);
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
            }
        }
        airControlAdapter.notifyDataSetChanged();
    }


    //AirCleanActivity
    /*
    0 	0x00	离线模式（未使用）
    1 	0x01	深度待机
    2 	0x02	睡眠模式
    3 	0x03	自动模式
    4 	0x04	手动控制
    5 	0x05	狂风模式
    129	0x81	儿童场景
    130	0x82	全天智能场景
    255	0xFF	设备关机
    *
    *
    * */
    public void getMod(String power,String airPower) {
        if(airPower.equals("3")){
            setSelectedItem();
        }
        else{
            if (power.equals("2")) {
                setSelectedItem(0,5);
            } else if (power.equals("1")) {
                setSelectedItem(0,3);
            } else if (power.equals("4")) {
                setSelectedItem(0);
            } else if (power.equals("0")) {
                setSelectedItem(0,4);
            }
        }


//        if(airPower.equals("0")||airPower.equals("1")){
//            list.get(0).setCheck(true);
//        }

        airControlAdapter.notifyDataSetChanged();
    }

    public void getDeviceState(String state){
        if (state.equals("2")) {
            setSelectedItem(0,5);
        } else if (state.equals("1")) {
            setSelectedItem(0,3);
        } else if (state.equals("4")) {//seek模式
            setSelectedItem(0);
        } else if (state.equals("0")) {
            setSelectedItem(0,4);
        }
        airControlAdapter.notifyDataSetChanged();
    }

    public void getDeviceAirPower(String airPower){
        if (airPower.equals("0")) {
            setSelectedItem(0);
        }
        else if(airPower.equals("1")){
//            setSelectedItem(0,5);
        }
        else if(airPower.equals("3")){
            setSelectedItem();
        }
        airControlAdapter.notifyDataSetChanged();
    }

    private void setSelectedItem(int ...indexs){
        for (AirClean airClean : list) {
            airClean.setCheck(false);
        }
        for(int i:indexs){
            list.get(i).setCheck(true);
        }
    }

    interface OnControlFragmentClickItem {
        void posCmd(String cmdType, int cmdValue);
    }

    void setControlFragmentListener(OnControlFragmentClickItem onClickItem) {

        this.onClickItem = onClickItem;

    }


    public void setSkin() {

        if (!isAdded())
            return;

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setResSelector(WifiAirCleanDetailsActivity.skinColor);
        }
        airControlAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
