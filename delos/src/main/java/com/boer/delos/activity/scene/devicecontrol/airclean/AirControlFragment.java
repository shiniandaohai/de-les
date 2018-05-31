package com.boer.delos.activity.scene.devicecontrol.airclean;

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
import com.boer.delos.model.AirCleanDevice;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirControlFragment extends Fragment {


    @Bind(R.id.gv_air_control)
    GridView gvAirControl;
    AirControlAdapter airControlAdapter;
    List<AirClean> list;
    OnControlFragmentClickItem onClickItem;
    int speed = 1;


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

                if(position==0){
                    airControlValue(position);
                    if(list.get(0).isCheck()){
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    }
                    else{
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    }
                }
                else{
                    if(list.get(0).isCheck()){
                        airControlValue(position);
                        TextView tv_wind_speed = (TextView) getActivity().findViewById(R.id.tv_wind_speed);
                        tv_wind_speed.setText(speed + "");
                    }
                    else{
                        ToastHelper.showShortMsg(getString(R.string.tip_take_on));
                    }
                }
            }
        });

    }

    private void airControlValue(int pos) {

//        1 	0x01 	深度待机模式
//        2 	0x02 	睡眠模式
//        3 	0x03 	自动模式
//        4 	0x04 	手动模式
//        5 	0x05 	狂风模式
//        254 	0xFE 	开机
//        255 	0xFF 	关机
        String data = "4";//手动

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        2-模式和风速
//        3-重置HEAP
//        4-重置空气净化量
//        5-开关屏幕
        String cmd = "2";


//        cmd=2并且data=4时发2
//        其他情况都发1
        String dataLen = "1";


        switch (pos) {

            case 0:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "254";

                } else {
                    cmd = "2";
                    data = "255";

                }


                break;


            case 1:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "4";
                    if (speed > 10)
                        speed -= 10;
                    else
                        speed = 1;


                }


                break;
            case 2:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "4";
                    if (speed < 90)
                        speed += 10;
                    else
                        speed = 100;


                }


                break;
            case 3:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "3";


                }


                break;

            case 4:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "5";


                }


                break;
            case 5:

                if (list.get(0).isCheck()) {

                    cmd = "2";
                    data = "2";


                }


                break;


        }

        if (data.equals("4") && cmd.equals("2")) {

            dataLen = "2";

        }

        AirCleanDevice.ValueBean value = new AirCleanDevice.ValueBean();


        value.setCmd(cmd);
        value.setSpeed(speed + "");
        value.setData(data);
        value.setDataLen(dataLen);


        onClickItem.posCmd(value);

    }


    private void initMod(int position) {

        boolean toggle = list.get(0).isCheck();
        boolean isCheck = list.get(position).isCheck();

        isCheck = !isCheck;

        if (position == 0) {
            list.get(position).setCheck(isCheck);
        }else {
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
    public void getMod(int mod) {
        for(AirClean airClean:list){
            airClean.setCheck(false);
        }
        if (mod == 255 || mod == 1 || mod == 0) {
            list.get(0).setCheck(false);
        } else if (mod == 2) {
            list.get(0).setCheck(true);
            list.get(5).setCheck(true);
        } else if (mod == 3) {
            list.get(0).setCheck(true);
            list.get(3).setCheck(true);
        } else if (mod == 4) {
            list.get(0).setCheck(true);
        } else if (mod == 5) {
            list.get(0).setCheck(true);
            list.get(4).setCheck(true);
        }
        airControlAdapter.notifyDataSetChanged();
    }


    interface OnControlFragmentClickItem {
        void posCmd(AirCleanDevice.ValueBean pos);
    }

    void setControlFragmentListener(OnControlFragmentClickItem onClickItem) {


        this.onClickItem = onClickItem;

    }


    public void setSkin() {


        if (!isAdded())
            return;
        list.clear();
        for (int i = 0; i < 6; i++) {
            AirClean airClean = new AirClean();
            switch (i) {

                case 0:
                    airClean.setResSelector(AirCleanActivity.skinColor);
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
                    airClean.setResSelector(AirCleanActivity.skinColor);
                    airClean.setRes(R.mipmap.air_auto_mod_selector);
                    airClean.setAirRes(R.mipmap.air_auto_mod);
                    airClean.setName(getString(R.string.air_txt_auto_mode));
                    break;
                case 4:
                    airClean.setResSelector(AirCleanActivity.skinColor);
                    airClean.setAirRes(R.mipmap.air_wind_mod);
                    airClean.setRes(R.mipmap.air_wind_mod_selector);
                    airClean.setName(getString(R.string.air_txt_hurricane_mode));
                    break;
                case 5:
                    airClean.setResSelector(AirCleanActivity.skinColor);
                    airClean.setAirRes(R.mipmap.air_sleep_mod);
                    airClean.setRes(R.mipmap.air_sleep_mod_selector);
                    airClean.setName(getString(R.string.air_txt_sleep_mode));
                    break;

            }


            list.add(airClean);
        }
        airControlAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
