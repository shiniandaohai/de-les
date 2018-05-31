package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.PanelSettingAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.ModeAct;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunzhibin on 2017/8/9.
 */

public class PanelSettingActivity extends CommonBaseActivity {
    @Bind(R.id.iv_model_1)
    ImageView ivModel1;
    @Bind(R.id.tv_model_1)
    TextView tvModel1;
    @Bind(R.id.ll_model_1)
    LinearLayout llModel1;
    @Bind(R.id.iv_model_2)
    ImageView ivModel2;
    @Bind(R.id.tv_model_2)
    TextView tvModel2;
    @Bind(R.id.ll_model_2)
    LinearLayout llModel2;
    @Bind(R.id.iv_model_3)
    ImageView ivModel3;
    @Bind(R.id.tv_model_3)
    TextView tvModel3;
    @Bind(R.id.ll_model_3)
    LinearLayout llModel3;
    @Bind(R.id.iv_model_4)
    ImageView ivModel4;
    @Bind(R.id.tv_model_4)
    TextView tvModel4;
    @Bind(R.id.ll_model_4)
    LinearLayout llModel4;
    @Bind(R.id.exListView)
    ExpandableListView exListView;
    @Bind(R.id.tv_loading)
    TextView tvLoading;
    @Bind(R.id.tv_loading_fail)
    TextView tvLoadingFail;
    private Map<String, List<ModeAct>> mModelMaps;
    private Device mDevcie;
    // key modeId
    private Map<String, ModeAct> mModeConfigure;
    private PanelSettingAdapter mAdapter;
    private String selectKey = "";//选中的模式
    private String selectBtn = "0";//选中的btn默认不选中
    private String selectPos = "0";//选中的pos group+child
    private Device.ModecfgBean mModecfgBean;

    @Override
    protected int initLayout() {
        return R.layout.activity_panel_setting;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.pannel_name));
        tlTitleLayout.setRightText(getString(R.string.reset_pass_confirm));
        Bundle bundle = getIntent().getExtras();
        DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDevcie = deviceRelate.getDeviceProp();
        mModecfgBean = mDevcie.getModecfg();
        if (mModecfgBean == null) {
            mModecfgBean = new Device.ModecfgBean();
            mDevcie.setModecfg(mModecfgBean);
        }
        if (TextUtils.isEmpty(mDevcie.getProtocol())) mDevcie.setProtocol("0");
        initUI();
//        updateUI(true);
        showTextHint("0");
    }

    private void initUI() {
//        llModel1.setSelected(true);
        String protocol = mDevcie.getProtocol();
        switch (protocol) {
            case "1":
                llModel2.setVisibility(View.GONE);
                llModel3.setVisibility(View.GONE);
                llModel4.setVisibility(View.GONE);
                break;
            case "2":
                llModel3.setVisibility(View.GONE);
                llModel4.setVisibility(View.GONE);
                break;
            case "3":
                llModel4.setVisibility(View.GONE);
                break;
            case "4":
                break;

        }
//        if (modecfgBean.getValue1()) {
//
//        } else if (modecfgBean.getValue2()) {
//
//        } else if (modecfgBean.getValue3()) {
//
//        } else if (modecfgBean.getValue4()) {
//
//        }
    }

    @Override
    protected void initData() {

        queryAllModesInfo();
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        mAdapter = new PanelSettingAdapter(this, mModelMaps);
        exListView.setAdapter(mAdapter);
        exListView.setGroupIndicator(null);

    }

    @Override
    protected void initAction() {
        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ModeAct modeAct = mAdapter.getChild(groupPosition, childPosition);
                if (!TextUtils.isEmpty(selectKey)
                        && (groupPosition + "" + childPosition).equals(selectPos)) {
                    selectKey = "";
                } else
                    selectKey = modeAct.getModeId();
                selectPos = groupPosition + "" + childPosition;
                String modeId = selectKey;
                //sss
                mDevcie.setModecfg(selectMode2Btn(selectBtn, modeId));
                mAdapter.updateShow(selectKey);

//                upDataDeviceInfo(mDevcie);
                return true;
            }
        });
    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();
        if((TextUtils.isEmpty(selectKey))&&(TextUtils.isEmpty(selectBtn) || selectBtn.equals("0"))){
            toastUtils.showInfoWithStatus(getString(R.string.toast_please_choice_btn));
            return;
        }
        if (TextUtils.isEmpty(selectKey)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_please_choice_mode));
            return;
        }
        if (TextUtils.isEmpty(selectBtn) || selectBtn.equals("0")) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_please_choice_btn));
            return;
        }
        upDataDeviceInfo(mDevcie);
    }

    private void showTextHint(String isShow) {
        if (tvLoading == null && tvLoadingFail == null) {
            return;
        }
        if (TextUtils.isEmpty(isShow)) {
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.GONE);
        } else if (isShow.equals("0")) { //loading
            tvLoading.setVisibility(View.VISIBLE);
            tvLoadingFail.setVisibility(View.GONE);
        } else if (isShow.equals("1")) { // loading error
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.VISIBLE);
        } else if (isShow.equals("2")) {//loading success
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.GONE);
        }

    }

    private void queryAllModesInfo() {
//        toastUtils.showProgress(getString(R.string.toast_loading));
        LinkManageController.getInstance().queryAllMode2CunrrentGateWay(this, null, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
//                toastUtils.dismiss();

                try {
                    int ret = JsonUtil.parseInt(json, "ret");
                    if (ret != 0) {
                        showTextHint("1");
                        return;
                    }
                    showTextHint("2");

                    String temp = JsonUtil.parseString(json, "response");
                    if (!StringUtil.isEmpty(temp)) parseJson(temp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                showTextHint("");
            }
        });
    }

    private void parseJson(String Json) {
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        String key = null;
        try {
            JSONObject jsonObject = new JSONObject(Json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                key = iterator.next();
                List<ModeAct> modeActList = JsonUtil.parseDataList(Json, ModeAct.class, key);
                mModelMaps.put(key, modeActList);
            }
            updateUI(mModelMaps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ll_model_1, R.id.ll_model_2, R.id.ll_model_3,
            R.id.ll_model_4, R.id.tv_loading, R.id.tv_loading_fail})
    public void onViewClicked(View view) {
        Device.ModecfgBean modecfgBean = mDevcie.getModecfg();
        switch (view.getId()) {
            case R.id.ll_model_1:
                selectBtn = "1";
//                selectKey = modecfgBean.getValue1();
//                selectKey = "";
                ivModel1.setSelected(true);
                ivModel2.setSelected(false);
                ivModel3.setSelected(false);
                ivModel4.setSelected(false);
                mDevcie.setModecfg(selectMode2Btn(selectBtn, selectKey));
//                mAdapter.updateShow(selectKey);
                break;
            case R.id.ll_model_2:
                selectBtn = "2";
//                selectKey = modecfgBean.getValue2();
//                selectKey = "";
                ivModel1.setSelected(false);
                ivModel2.setSelected(true);
                ivModel3.setSelected(false);
                ivModel4.setSelected(false);
                mDevcie.setModecfg(selectMode2Btn(selectBtn, selectKey));
//                mAdapter.updateShow(selectKey);
                break;
            case R.id.ll_model_3:
                selectBtn = "3";
//                selectKey = modecfgBean.getValue3();
//                selectKey = "";
                ivModel1.setSelected(false);
                ivModel2.setSelected(false);
                ivModel3.setSelected(true);
                ivModel4.setSelected(false);
                mDevcie.setModecfg(selectMode2Btn(selectBtn, selectKey));
//                mAdapter.updateShow(selectKey);
                break;
            case R.id.ll_model_4:
                selectBtn = "4";
//                selectKey = modecfgBean.getValue4();
//                selectKey = "";
                ivModel1.setSelected(false);
                ivModel2.setSelected(false);
                ivModel3.setSelected(false);
                ivModel4.setSelected(true);
                mDevcie.setModecfg(selectMode2Btn(selectBtn, selectKey));
//                mAdapter.updateShow(selectKey);
                break;
            case R.id.tv_loading:
            case R.id.tv_loading_fail:
                queryAllModesInfo();
                break;
        }
    }

    private void upDataDeviceInfo(Device device) {
        DeviceController.getInstance().updateProp(this, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("moshi", json);
                int ret = JsonUtil.parseInt(json, "ret");
                if (ret == 0) {
                    toastUtils.showSuccessWithStatus(getString(R.string.edit_success));

                    selectBtn = "0";
                    selectKey = "";
                    updateUI(mModelMaps);

                    ivModel1.setSelected(false);
                    ivModel2.setSelected(false);
                    ivModel3.setSelected(false);
                    ivModel4.setSelected(false);

//                    mAdapter.updateShow("");
                } else {
                    toastUtils.showSuccessWithStatus(getString(R.string.edit_fail));
                }
            }

            @Override
            public void onFailed(String Json) {
                L.i(Json);

            }
        });
    }

    /**
     * 设置上面的Button 按钮
     *
     * @param clear true 清空 设置默认 false 更新
     */
    private void updateUI(boolean clear) {
        String modeId = null;
        if (mModeConfigure == null) {
            mModeConfigure = new HashMap<>();
        } else if (clear)
            mModeConfigure.clear();

        Device.ModecfgBean modecfgBean = mDevcie.getModecfg();
        if (!TextUtils.isEmpty(modecfgBean.getValue1())) {
            modeId = modecfgBean.getValue1();
            if (mModeConfigure.get(modeId) == null) {
                mModeConfigure.put(modeId, null);
            } else {
                ModeAct modeAct = mModeConfigure.get(modeId);
                tvModel1.setText(TextUtils.isEmpty(modeAct.getTag())
                        ? modeAct.getName() : modeAct.getTag());
            }

        } else {
            tvModel1.setText(getString(R.string.mode_default));

        }
        if (!TextUtils.isEmpty(modecfgBean.getValue2())) {
            modeId = modecfgBean.getValue2();
            if (mModeConfigure.get(modeId) == null) {
                mModeConfigure.put(modeId, null);
            } else {
                ModeAct modeAct = mModeConfigure.get(modeId);
                tvModel2.setText(TextUtils.isEmpty(modeAct.getTag())
                        ? modeAct.getName() : modeAct.getTag());
            }
        } else {
            tvModel2.setText(getString(R.string.mode_default));

        }
        if (!TextUtils.isEmpty(modecfgBean.getValue3())) {
            modeId = modecfgBean.getValue3();
            if (mModeConfigure.get(modeId) == null) {
                mModeConfigure.put(modeId, null);
            } else {
                ModeAct modeAct = mModeConfigure.get(modeId);
                tvModel3.setText(TextUtils.isEmpty(modeAct.getTag())
                        ? modeAct.getName() : modeAct.getTag());
            }
        } else {
            tvModel3.setText(getString(R.string.mode_default));

        }
        if (!TextUtils.isEmpty(modecfgBean.getValue4())) {
            modeId = modecfgBean.getValue4();
            if (mModeConfigure.get(modeId) == null) {
                mModeConfigure.put(modeId, null);
            } else {
                ModeAct modeAct = mModeConfigure.get(modeId);
                tvModel4.setText(TextUtils.isEmpty(modeAct.getTag())
                        ? modeAct.getName() : modeAct.getTag());
            }
        } else {
            tvModel4.setText(getString(R.string.mode_default));

        }
    }

    private void updateUI(Map<String, List<ModeAct>> mModelMaps) {
        if (mModelMaps == null) {
            return;
        }
        updateUI(true);
        for (List<ModeAct> modeActList : mModelMaps.values()) {

            for (String tempId : mModeConfigure.keySet()) {
                for (ModeAct modeAct : modeActList) {
                    if (tempId.equals(modeAct.getModeId())) {
                        mModeConfigure.put(tempId, modeAct);
                    }
                }

            }
        }
        updateUI(false);
        swichMode();
        mAdapter.setmListData(mModelMaps, selectKey);

    }

    private void swichMode() {
        Device.ModecfgBean modecfgBean = mDevcie.getModecfg();

        switch (selectBtn) {
            case "1":
                selectKey = modecfgBean.getValue1();
                break;
            case "2":
                selectKey = modecfgBean.getValue2();
                break;
            case "3":
                selectKey = modecfgBean.getValue3();
                break;
            case "4":
                selectKey = modecfgBean.getValue4();
                break;

        }
    }

    private Device.ModecfgBean selectMode2Btn(String selectBtn, String modeId) {
        Device.ModecfgBean modecfgBean = mDevcie.getModecfg();
        switch (selectBtn) {
            case "1":
                modecfgBean.setValue1(modeId);
                if (!TextUtils.isEmpty(modecfgBean.getValue2())
                        && modecfgBean.getValue2().equals(modeId)) {
                    modecfgBean.setValue2("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue3())
                        && modecfgBean.getValue3().equals(modeId)) {
                    modecfgBean.setValue3("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue4())
                        && modecfgBean.getValue4().equals(modeId)) {
                    modecfgBean.setValue4("");
                }
                break;
            case "2":
                modecfgBean.setValue2(modeId);
                if (!TextUtils.isEmpty(modecfgBean.getValue1())
                        && modecfgBean.getValue1().equals(modeId)) {
                    modecfgBean.setValue1("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue3())
                        && modecfgBean.getValue3().equals(modeId)) {
                    modecfgBean.setValue3("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue4())
                        && modecfgBean.getValue4().equals(modeId)) {
                    modecfgBean.setValue4("");
                }
                break;
            case "3":
                modecfgBean.setValue3(modeId);
                if (!TextUtils.isEmpty(modecfgBean.getValue2())
                        && modecfgBean.getValue2().equals(modeId)) {
                    modecfgBean.setValue2("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue1())
                        && modecfgBean.getValue1().equals(modeId)) {
                    modecfgBean.setValue1("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue4())
                        && modecfgBean.getValue4().equals(modeId)) {
                    modecfgBean.setValue4("");
                }
                break;
            case "4":
                modecfgBean.setValue4(modeId);
                if (!TextUtils.isEmpty(modecfgBean.getValue2())
                        && modecfgBean.getValue2().equals(modeId)) {
                    modecfgBean.setValue2("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue3())
                        && modecfgBean.getValue3().equals(modeId)) {
                    modecfgBean.setValue3("");
                }
                if (!TextUtils.isEmpty(modecfgBean.getValue1())
                        && modecfgBean.getValue1().equals(modeId)) {
                    modecfgBean.setValue1("");
                }
                break;
        }


        return modecfgBean;
    }
}
