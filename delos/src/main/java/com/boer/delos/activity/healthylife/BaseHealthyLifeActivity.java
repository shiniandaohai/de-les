package com.boer.delos.activity.healthylife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.boer.delos.R;
import com.boer.delos.adapter.MySpinnerAdapter2;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterface2;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.gateway.GatewayController;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/20 0020 13:48
 * @Modify:
 * @ModifyDate:
 */


public class BaseHealthyLifeActivity extends BaseActivity {
    protected Host mHost;
    private List<Family> familyArrayList;
    protected static List<Family> mSharefamilyList;
    protected static List<String> mShareIds;

    protected ISimpleInterface2 simpleInterface2;
    protected ListPopupWindow mListPop;
    private MySpinnerAdapter2 mySpinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (familyArrayList == null)
            familyArrayList = new ArrayList<>();
        if (mShareIds == null)
            mShareIds = new ArrayList<>();
        if (mSharefamilyList == null)
            mSharefamilyList = new ArrayList<>();

    }


    protected void queryHealthyShare() {
        GatewayController.getInstance().queryAllHost(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                HostResult result = new Gson().fromJson(json, HostResult.class);
                if (result.getRet() != 0) {
                    return;
                }
                List<Host> hostList = result.getHosts();
                String currentHostId = result.getCurrentHostId();
                for (Host host : hostList) {
                    if (!host.getHostId().equals(currentHostId)) continue;

                    mHost = host;
                }
                familyArrayList.clear();
                familyArrayList.addAll(mHost.getFamilies());
                mSharefamilyList.clear();
                for (Family family : familyArrayList) {

                    if (family.getUserId().equals(Constant.USERID)) {

                        mShareIds = family.getShareIds();
                        mSharefamilyList.add(family); //自己

                        for (String id : mShareIds) {
                            for (Family f : familyArrayList) {
                                if (f.getUserId().equals(id)) {
                                    mSharefamilyList.add(f);
                                    break;
                                }
                            }
                        }

                    }
                }
                if (simpleInterface2 != null) {
                    simpleInterface2.clickListener("finish");
                }
//                if (mySpinnerAdapter != null)
//                    mySpinnerAdapter.setDatas(mSharefamilyList);
            }

            @Override
            public void onFailed(String json) {

            }
        });

    }

    protected void showListPopup(View anchor) {
//        mySpinnerAdapter = new MySpinnerAdapter2(this, mSharefamilyList,
//                R.layout.item_spinner_dropdown);
        mListPop = new ListPopupWindow(this);
        mListPop.setAdapter(mySpinnerAdapter);
        mListPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setAnchorView(anchor);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.setModal(true);//设置是否是模式
        mListPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                if (simpleInterface2 != null) {
                    simpleInterface2.clickListener2(position);
                    mListPop.dismiss();
                }
            }
        });

    }
    public void setSimpleInterface2(ISimpleInterface2 simpleInterface2) {
        this.simpleInterface2 = simpleInterface2;
    }
}
