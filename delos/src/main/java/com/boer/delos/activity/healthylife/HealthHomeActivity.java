package com.boer.delos.activity.healthylife;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.pressure.BloodPressureActivity;
import com.boer.delos.activity.healthylife.skintest.SkinChartActivity;
import com.boer.delos.activity.healthylife.smartmirror.SmartMirrorActivity;
import com.boer.delos.activity.healthylife.sugar.BloodSugarActivity;
import com.boer.delos.activity.healthylife.weight.WeigthHomeActivity;
import com.boer.delos.activity.healthylife.wifiAirClean.WifiAirCleanManagerActivity;
import com.boer.delos.adapter.MySpinnerAdapter3;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.HealthResult;
import com.boer.delos.model.Host;
import com.boer.delos.model.PressureResult;
import com.boer.delos.model.ShareUser;
import com.boer.delos.model.SkinArea;
import com.boer.delos.model.SugarResult;
import com.boer.delos.model.UrineResult;
import com.boer.delos.model.User;
import com.boer.delos.model.WeightBean;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.HealthPopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:健康首页
 * @CreateDate: 2017/4/17 0017 14:05
 * @Modify:
 * @ModifyDate:
 */


public class HealthHomeActivity extends CommonBaseActivity {
    @Bind(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @Bind(R.id.tv_user_name)
    TextView mTvUserName;
    @Bind(R.id.ctv_choice)
    CheckedTextView mCtvChoice;
    @Bind(R.id.ll_user_change)
    LinearLayout mLlUserChange;

    @Bind(R.id.tv_blood_pressure)
    TextView mTvBloodPressure;
    @Bind(R.id.tv_date_bp)
    TextView mTvDateBp;
    @Bind(R.id.ll_blood_pressure)
    LinearLayout mLlBloodPressure;

    @Bind(R.id.tv_blood_sugar)
    TextView mTvBloodSugar;
    @Bind(R.id.tv_date_sugar)
    TextView mTvDateSugar;
    @Bind(R.id.ll_blood_sugar)
    LinearLayout mLlBloodSugar;

    @Bind(R.id.tv_blood_weight)
    TextView mTvBloodWeight;
    @Bind(R.id.tv_date_weight)
    TextView mTvDateWeight;
    @Bind(R.id.ll_weight)
    LinearLayout mLlWeight;

    @Bind(R.id.tv_health_urine)
    TextView mTvHealthUrine;
    @Bind(R.id.tv_date_urine)
    TextView mTvDateUrine;
    @Bind(R.id.ll_urine)
    LinearLayout mLlUrine;

    @Bind(R.id.tv_health_skin)
    TextView mTvHealthSkin;
    @Bind(R.id.tv_date_skin)
    TextView mTvDateSkin;
    @Bind(R.id.ll_skin)
    LinearLayout mLlSkin;
    private Host mHost;
    private List<ShareUser.UserBean> mShareUserList;
    private List<String> mShareIds;
    private android.support.v7.widget.ListPopupWindow mListPop;
    private MySpinnerAdapter3 mySpinnerAdapter;

    private User mUser = null;
    private HealthPopupWindow healthPopupWindow;
    @Override
    protected int initLayout() {
        return R.layout.activity_health_home;
    }

    @Override
    protected void initView() {
        SharedPreferencesUtils.readLoginUserNameAndPassword(getApplicationContext());
        mUser = Constant.LOGIN_USER;
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }
        tlTitleLayout.setTitle(getString(R.string.text_healthy_manager));
        mShareUserList = new ArrayList<>();
        mTvUserName.setText(TextUtils.isEmpty(mUser.getName()) ? mUser.getMobile() : mUser.getName());
        if(!TextUtils.isEmpty(mUser.getAvatarUrl())){
            ImageLoader.getInstance().displayImage(URLConfig.HTTP + mUser.getAvatarUrl(),
                    mIvAvatar, BaseApplication.getInstance().displayImageOptions);
        }
        clearUI();
    }

    @Override
    protected void initData() {
        showListPopup(mLlUserChange);
        queryHealthyShare(Constant.CURRENTHOSTID, Constant.USERID);
        for (int i = 0; i < 1; i++) {
            if (mListHealthDatas == null) mListHealthDatas = new ArrayList<>();
            mListHealthDatas.add(false);
        }
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryHealthData();
    }

    @OnClick({R.id.ctv_choice, R.id.ll_user_change, R.id.ll_blood_pressure,
            R.id.ll_blood_sugar, R.id.ll_weight, R.id.ll_urine,
            R.id.ll_skin, R.id.ll_magic_mirror,R.id.ll_air_clean})
    public void onClick(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (Constant.LOGIN_USER == null || mUser == null) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_login_again));
            return;
        }
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }
        bundle.putSerializable("user", mUser);
        intent.putExtras(bundle);
        switch (view.getId()) {
            case R.id.ctv_choice:
            case R.id.ll_user_change:
                if (mListPop == null) {
                    showListPopup(mLlUserChange);
                    mListPop.show();
                    mCtvChoice.setChecked(false);
                } else {
                    if (mListPop.isShowing()) {
                        mListPop.dismiss();
                    }
                    mySpinnerAdapter.setDatas(mShareUserList);
                    mListPop.show();
                    mCtvChoice.setChecked(false);
                }

                break;
            case R.id.ll_blood_pressure:
                intent.setClass(this, BloodPressureActivity.class);
                break;
            case R.id.ll_blood_sugar:
                intent.setClass(this, BloodSugarActivity.class);

                break;
            case R.id.ll_weight:
                intent.setClass(this, WeigthHomeActivity.class);
                break;
            case R.id.ll_urine:
                //TODO
                ToastHelper.showShortMsg("开发中，敬请期待");

                break;
            case R.id.ll_skin:
//                intent.setClass(this, SkinChartActivity.class);

                intent=null;
                healthPopupwindow(0);
                break;
            case R.id.ll_magic_mirror:
                intent.setClass(this, SmartMirrorActivity.class);
                break;
            case R.id.ll_air_clean:
                intent.setClass(this, WifiAirCleanManagerActivity.class);
                break;

        }
        if (intent!=null&&intent.getComponent() != null && intent.getComponent().getClassName() != null) {
            startActivity(intent);
        }
    }

    protected void queryHealthyShare(String hostId, final String userId) {
        HealthController.getInstance().queryShareHealth2Me(this, hostId, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                ShareUser shareUser = GsonUtil.getObject(json, ShareUser.class);
                if (shareUser.getRet() != 0) {
                    return;
                }
                if (shareUser.getUser() == null) {
                    return;
                }

                mShareUserList.addAll(shareUser.getUser());


            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });

    }

    protected void showListPopup(View anchor) {
        mySpinnerAdapter = new MySpinnerAdapter3(this, mShareUserList,
                R.layout.item_spinner_dropdown);
        mListPop = new android.support.v7.widget.ListPopupWindow(this);
        mListPop.setAdapter(mySpinnerAdapter);
        mListPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setAnchorView(anchor);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.setModal(true);//设置是否是模式
        mListPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                mListPop.dismiss();
                ShareUser.UserBean userBean = mShareUserList.get(position);
                mUser = changeUserBean2User(userBean);
                if (mUser.getId().equals(Constant.USERID)) {
                    mShareUserList.remove(userBean);
                } else {
                    ShareUser.UserBean userBean1 = new ShareUser.UserBean();
                    userBean1.setUserId(Constant.USERID);
                    if (!mShareUserList.contains(userBean1)) {
                        mShareUserList.add(changeUser2UserBean(Constant.LOGIN_USER));
                    }
                    mySpinnerAdapter.notifyDataSetChanged();
                }
                mCtvChoice.setChecked(false);
                mTvUserName.setText(mUser.getName());
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + mUser.getAvatarUrl(),
                        mIvAvatar, BaseApplication.getInstance().displayImageOptions);

                queryHealthData();
            }
        });

        mListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mCtvChoice.setChecked(true);
            }
        });

    }


    /**
     * 查询所有数据
     */
    private void queryHealthData() {
        long fromTime = TimeUtil.getCurrentstamp();
        HealthController.getInstance().queryRecentHealth(this, fromTime + "", "0", "1", mUser.getId(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                clearUI();
                /**
                 * TODO 皮肤
                 */
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("ret") != 0) {
                        return;
                    }
                    object = object.getJSONObject("response");
                    JSONObject jsonObject = null;
                    JSONArray jsonArray = null;

                    //血压
                    jsonObject = object.getJSONObject("blood_pressure");
                    if (jsonObject != null) {
                        jsonArray = jsonObject.getJSONArray("data");
                    }
                    if (jsonArray != null && !jsonArray.isNull(0)) {
                        PressureResult.PressureBean pressureBean = GsonUtil.getObject(jsonArray.getString(0), PressureResult.PressureBean.class);
                        mTvBloodPressure.setText(pressureBean.getValueH() + "/" + pressureBean.getValueL());
                        String pressureTime = TimeUtil.formatStamp2Time(pressureBean.getMeasuretime(), "yyyy-MM-dd HH:mm:ss");
                        mTvDateBp.setText(pressureTime);
                    }


                    // 血糖
                    jsonObject = object.getJSONObject("blood_glucose");
                    if (jsonObject != null) {
                        jsonArray = jsonObject.getJSONArray("data");
                    }
                    SugarResult.SugarBean sugarBean = GsonUtil.getObject(jsonArray.getString(0), SugarResult.SugarBean.class);
                    if (jsonArray != null && !jsonArray.isNull(0)) {
                        mTvBloodSugar.setText(sugarBean.getValue() + "");
                        String sugarTime = TimeUtil.formatStamp2Time(sugarBean.getMesuredate(), "yyyy-MM-dd HH:mm:ss");
                        mTvDateSugar.setText(sugarTime);
                    }


                    // 尿检
                    jsonObject = object.getJSONObject("urine");
                    if (jsonObject != null) {
                        jsonArray = jsonObject.getJSONArray("data");
                    }
                    if (jsonArray != null && !jsonArray.isNull(0)) {
                        UrineResult.UrineBean urineBean = GsonUtil.getObject(jsonArray.getString(0), UrineResult.UrineBean.class);
                        //TODO
                        String urineTime = TimeUtil.formatStamp2Time(urineBean.getMeasuretime(), "yyyy-MM-dd HH:mm:ss");
                        mTvDateUrine.setText(urineTime);
                    }


                    // 体重
                    jsonObject = object.getJSONObject("body_weight");
                    if (jsonObject != null) {
                        jsonArray = jsonObject.getJSONArray("data");
                    }
                    if (jsonArray != null && !jsonArray.isNull(0)) {
                        WeightBean weightBean = GsonUtil.getObject(jsonArray.getString(0), WeightBean.class);

                        mTvBloodWeight.setText(weightBean.getFatrate() + "");
                        String weightTime = TimeUtil.formatStamp2Time(weightBean.getMeasuretime(), "yyyy-MM-dd HH:mm:ss");
                        mTvDateWeight.setText(weightTime);
                    }


                    jsonObject = object.getJSONObject("skin");
                    if (jsonObject != null) {
                        jsonArray = jsonObject.getJSONArray("data");
                    }
                    if (jsonArray != null && !jsonArray.isNull(0)) {
                        mListHealthDatas.set(0,true);
                        SkinArea skinArea = GsonUtil.getObject(jsonArray.getString(0), SkinArea.class);
                        String skinTime = TimeUtil.formatStamp2Time(Integer.valueOf(skinArea.getMeasuretime()), "yyyy-MM-dd HH:mm:ss");
                        mTvDateSkin.setText(skinTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                clearUI();
            }
        });

    }

    private void updateUI(HealthResult.ResponseBean response) {
        SugarResult.SugarBean sugarBean = response.getBlood_glucose();
        PressureResult.PressureBean pressureBean = response.getBlood_pressure();
        UrineResult.UrineBean urineBean = response.getUrine();
        WeightBean weightBean = response.getBody_weight();
        try {
            mTvBloodPressure.setText(pressureBean.getValueH() + "/" + pressureBean.getValueL());
            mTvBloodSugar.setText(sugarBean.getValue() + "");
            mTvBloodWeight.setText(weightBean.getWeight() + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO 皮肤


//        DealWithValue2.judgeBPColor(this, mTvBloodPressure, pressureBean.getValueH(), pressureBean.getValueL(), true);
//        DealWithValue2.judgeBSColor(this, mTvBloodSugar, sugarBean.getValue(), true);
//        DealWithValue2.judgeWeightColor(this, mTvBloodWeight, weightBean.getFatrate(), true);


    }


    private User changeUserBean2User(ShareUser.UserBean userBean) {
        User user = new User();
        user.setAvatarUrl(userBean.getAvatarUrl());
        user.setMobile(userBean.getMobile());
        user.setId(userBean.getUserId());
        user.setName(userBean.getUserName());

        return user;
    }

    private ShareUser.UserBean changeUser2UserBean(User user) {
        ShareUser.UserBean userBean = new ShareUser.UserBean();
        userBean.setAvatarUrl(user.getAvatarUrl());
        userBean.setMobile(user.getMobile());
        userBean.setUserId(user.getId());
        userBean.setUserName(user.getName());

        return userBean;
    }


    private void clearUI() {
        if (mTvBloodSugar == null) {
            return;
        }
        mTvDateSkin.setText("");
        mTvDateWeight.setText("");
        mTvDateBp.setText("");
        mTvDateSugar.setText("");
        mTvDateUrine.setText("");

        mTvBloodPressure.setText("");
        mTvBloodSugar.setText("");
        mTvHealthSkin.setText("");
        mTvBloodWeight.setText("");
        mTvHealthUrine.setText("");
    }

    private List<Boolean> mListHealthDatas;
    private void healthPopupwindow(int position) {
        if (mListHealthDatas.get(position)) {
            switch (position) {
                case 0:
                    Bundle bundle=new Bundle();
                    Intent intent=new Intent();
                    bundle.putSerializable("user", mUser);
                    intent.putExtras(bundle);
                    intent.setClass(this,SkinChartActivity.class);
                    startActivity(intent);
                    break;
            }
            return;
        }

        if (healthPopupWindow != null && healthPopupWindow.isShowing()) {
            healthPopupWindow.dismiss();
            healthPopupWindow = null;
        }

        healthPopupWindow = new HealthPopupWindow(this, R.layout.popup_healthy_home2);

        switch (position) {
            case 0:
                healthPopupWindow.popShow("皮肤检测仪");
                break;
        }

    }
}
