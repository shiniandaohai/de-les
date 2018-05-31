package com.boer.delos.activity.healthylife.wifiAirClean;

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
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class WifiAirDataFragment extends Fragment {


    @Bind(R.id.gv_air_control)
    GridView gvAirControl;
    AirControlAdapter airControlAdapter;
    WifiAirDataFragment.OnDataFragmentClickItem onClickItem;
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
                if(StringUtil.isEmpty(mAirPower)||mAirPower.equals("3")){
                    ToastHelper.showShortMsg(WifiAirDataFragment.this.getActivity().getString(R.string.tip_take_on));
                }
                else{
                    boolean isCheck = list.get(position).isCheck();
                    isCheck = !isCheck;
                    list.get(position).setCheck(isCheck);
                    airControlAdapter.notifyDataSetChanged();
                    airControlValue(position, isCheck);
                }
            }
        });

        AirClean airClean = new AirClean();
        airClean.setResSelector(WifiAirCleanDetailsActivity.skinColor);
        airClean.setRes(R.mipmap.air_lighting_selector);
        airClean.setAirRes(R.mipmap.air_lighting);
        airClean.setName(getString(R.string.air_txt_black_screen));
        list.add(airClean);
    }

    private void airControlValue(int pos, boolean isCheck) {
        if(isCheck){
            onClickItem.posCmd("AirPower",0);
        }
        else{
            onClickItem.posCmd("AirPower",1);
        }
    }


    interface OnDataFragmentClickItem {
        void posCmd(String cmdType, int cmdValue);
    }

    void setDataFragmentListener(WifiAirDataFragment.OnDataFragmentClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setSkin() {

        if (!isAdded())
            return;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setResSelector(WifiAirCleanDetailsActivity.skinColor);
        }
        airControlAdapter.notifyDataSetChanged();
    }



    private String mAirPower;
    public void getMod(String power,String airPower) {
        mAirPower=airPower;
        if(airPower.equals("0")){
            setSelectedItem(0);
        }
        else{
            setSelectedItem();
        }
        airControlAdapter.notifyDataSetChanged();
    }

    public void getDeviceAirPower(String airPower){
        mAirPower=airPower;
        if (airPower.equals("0")) {
            setSelectedItem(0);
        }
        else if(airPower.equals("1")){
            setSelectedItem();
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
}
