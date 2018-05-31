package com.boer.delos.activity.scene.devicecontrol.airclean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boer.delos.R;
import com.boer.delos.adapter.AirControlAdapter;
import com.boer.delos.model.AirClean;
import com.boer.delos.model.AirCleanData;
import com.boer.delos.model.AirCleanDevice;
import com.boer.delos.model.Device;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirDataFragment extends Fragment {


    @Bind(R.id.gv_air_control)
    GridView gvAirControl;
    AirControlAdapter airControlAdapter;
    AirCleanDevice airCleanDevice;
    AirDataFragment.OnDataFragmentClickItem onClickItem;
    public AirCleanData valueBean;
    public Device deviceValueBean;
    List<AirClean> list;


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
                if(position!=3){
                    list.get(position).setCheck(false);
                }
                airControlAdapter.notifyDataSetChanged();



                if (position == 0) {
                    Intent intent = new Intent(getActivity(), AirCleanHistoryActivity.class);
                    intent.putExtra("deviceValueBean", getDeviceValueBean());
                    startActivity(intent);
                }
                if (position == 1) {
                    Intent intent = new Intent(getActivity(), AirCleanDataActivity.class);
                    intent.putExtra("valueBean", getValueBean());
                    startActivity(intent);
                }
                if (position == 2) {
                    int mod=getValueBean().getValue().getMode();
                    if (mod == 255 || mod == 1 || mod == 0) {
                        ToastHelper.showShortMsg(AirDataFragment.this.getActivity().getString(R.string.tip_take_on));
                    }
                    else{
                        Intent intent = new Intent(getActivity(), AirFilterActivity.class);
                        intent.putExtra("valueBean", getValueBean());
                        intent.putExtra("deviceValueBean", getDeviceValueBean());
                        startActivity(intent);
                    }
                }

                if (position == 3) {
                    int mod=getValueBean().getValue().getMode();
                    if (mod == 255 || mod == 1 || mod == 0) {
                        ToastHelper.showShortMsg(AirDataFragment.this.getActivity().getString(R.string.tip_take_on));
                    }
                    else{
                        boolean isCheck = list.get(position).isCheck();
                        isCheck = !isCheck;
                        list.get(position).setCheck(isCheck);
                        airControlAdapter.notifyDataSetChanged();
                        airControlValue(position, isCheck);
                    }
                }

            }
        });

        setSkin();
    }

    private void airControlValue(int pos, boolean isCheck) {

//        cmd=5时，0-息屏，1-亮屏
        String data;
        if (isCheck)
            data = "1";
        else
            data = "0";


/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        2-模式和风速
//        3-重置HEAP
//        4-重置空气净化量
//        5-开关屏幕
        String cmd = "5";


//        cmd=2并且data=4时发2
//        其他情况都发1
        String dataLen = "1";

        AirCleanDevice.ValueBean value = new AirCleanDevice.ValueBean();


        value.setCmd(cmd);
        value.setData(data);
        value.setDataLen(dataLen);


        onClickItem.posCmd(value);

    }


    interface OnDataFragmentClickItem {
        void posCmd(AirCleanDevice.ValueBean pos);
    }

    void setDataFragmentListener(AirDataFragment.OnDataFragmentClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public AirCleanData getValueBean() {
        return valueBean;
    }

    public void setValueBean(AirCleanData valueBean) {
        this.valueBean = valueBean;

        int mod=getValueBean().getValue().getMode();
        if (mod == 255 || mod == 1 || mod == 0) {
            list.get(3).setCheck(false);
        }
        else{
            list.get(3).setCheck(valueBean.getValue().getScreen()==1?true:false);
        }
        airControlAdapter.notifyDataSetInvalidated();
    }

    public Device getDeviceValueBean() {
        return deviceValueBean;
    }

    public void setDeviceValueBean(Device deviceValueBean) {
        this.deviceValueBean = deviceValueBean;

    }




    private void setSkin() {

        if(!isAdded())
            return;
        list.clear();
        for (int i = 0; i < 4; i++) {
            AirClean airClean = new AirClean();

            switch (i) {
                case 0:
                    airClean.setResSelector(getResources().getColor(R.color.transparent));
                    airClean.setRes(R.mipmap.air_data_history);
                    airClean.setAirRes(R.mipmap.air_data_history);
                    airClean.setName(getString(R.string.air_txt_data_history));
                    break;
                case 1:
                    airClean.setResSelector(getResources().getColor(R.color.transparent));
                    airClean.setRes(R.mipmap.air_data_clean);
                    airClean.setAirRes(R.mipmap.air_data_clean);
                    airClean.setName(getString(R.string.air_txt_data_clean));
                    break;
                case 2:
                    airClean.setResSelector(getResources().getColor(R.color.transparent));
                    airClean.setRes(R.mipmap.air_data_hepa);
                    airClean.setAirRes(R.mipmap.air_data_hepa);
                    airClean.setName(getString(R.string.air_txt_data_hepa));
                    break;
                case 3:
                    airClean.setResSelector(AirCleanActivity.skinColor);
                    airClean.setRes(R.mipmap.air_lighting_selector);
                    airClean.setAirRes(R.mipmap.air_lighting);
                    airClean.setName(getString(R.string.air_txt_black_screen));
                    break;
            }
            list.add(airClean);
        }

        airControlAdapter.notifyDataSetChanged();
    }





}
