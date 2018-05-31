package com.boer.delos.view.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.os.BuildCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.HealthHomeActivity;
import com.boer.delos.activity.healthylife.pressure.BloodPressureActivity;
import com.boer.delos.activity.healthylife.pressure.BloodPressureConnActivity;
import com.boer.delos.activity.healthylife.pressure.BloodPressureInputActivity;
import com.boer.delos.activity.healthylife.skintest.SkinTestActivity;
import com.boer.delos.activity.healthylife.sugar.BloodSugarActivity;
import com.boer.delos.activity.healthylife.sugar.SugarConn2Activity;
import com.boer.delos.activity.healthylife.urine.UrineListeningActivity;
import com.boer.delos.activity.healthylife.weight.ScaleConnActivity;
import com.boer.delos.activity.healthylife.weight.WeightInputActivity;
import com.boer.delos.activity.healthylife.weight.WeigthHomeActivity;
import com.boer.delos.commen.BasePopupWindow;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.User;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/17 0017 13:10
 * @Modify:
 * @ModifyDate:
 */


public class HealthPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private Button mButtonBuy;
    private Button mButtonGo;
    private Button mButtonInput;

    private ISimpleInterfaceInt mlistener;
    private LinearLayout pop_bg;
    private String mtype = null;

    public HealthPopupWindow(Context context, @LayoutRes int resId) {
        super(context, resId);

        initView();
    }

    private void initView() {
        mButtonBuy = getView(R.id.btn_buy);
        mButtonGo = getView(R.id.btn_go);
        mButtonInput = getView(R.id.btn_input);
        pop_bg = getView(R.id.pop_bg);

        mButtonBuy.setOnClickListener(this);
        mButtonGo.setOnClickListener(this);
        mButtonInput.setOnClickListener(this);
        mButtonBuy.setOnClickListener(this);
        pop_bg.setOnClickListener(this);
    }

    public void popShow(String type) {
        if(type.equals("皮肤检测仪")){
            mButtonInput.setVisibility(View.GONE);
        }
        mButtonBuy.setText(String.format(mContext.getString(R.string.device_buy), type));
        mButtonGo.setText(String.format(mContext.getString(R.string.device_go), type));
        mButtonInput.setText(String.format(mContext.getString(R.string.device_input), type));
        mtype = type;
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void setProperty() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,);
//        animation.setDuration(300);
        setAnimationStyle(R.style.MenuAnimationFadeDown);

    }

    @Override
    public int setPopWindowResId() {
        return R.layout.popup_healthy_home2;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_bg:
                dismiss();
                break;
            case R.id.btn_buy:
                if (mlistener != null) {
                    mlistener.clickListener(0);
                }
                dismiss();

                break;
            case R.id.btn_go:
                if (mlistener != null) {
                    mlistener.clickListener(1);
                }
                startNewActivity(mtype);

                dismiss();

                break;
            case R.id.btn_input:
                if (mlistener != null) {
                    mlistener.clickListener(2);
                }
                startNewActivity2(mtype);

                dismiss();

                break;
        }
    }

    public void setMlistener(ISimpleInterfaceInt mlistener) {
        this.mlistener = mlistener;
    }


    private void startNewActivity(String type) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (type) {
            case "血压仪":
            case "血压计":
                intent.setClass(mContext, BloodPressureConnActivity.class);
                break;
            case "血糖仪":
                intent.setClass(mContext, SugarConn2Activity.class);
                break;
            case "体脂秤":

                intent.setClass(mContext, ScaleConnActivity.class);
                break;
            case "尿常规":
                ToastHelper.showShortMsg("开发中，敬请期待");
//                intent.setClass(mContext, UrineListeningActivity.class);
                break;
            case "皮肤检测仪":
                intent.setClass(mContext, SkinTestActivity.class);
                break;
            default:
                intent.setClass(mContext, HealthHomeActivity.class);
                break;
        }
        if (Constant.LOGIN_USER == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(mContext);
        }

        bundle.putSerializable("user", Constant.LOGIN_USER);
        intent.putExtras(bundle);
        mContext.startActivity(intent);

    }

    private void startNewActivity2(String type) {
        Intent intent = new Intent();

        switch (type) {
            case "血压仪":
            case "血压计":
                intent.setClass(mContext, BloodPressureInputActivity.class);
                break;
            case "血糖仪":
                intent.setClass(mContext, BloodSugarActivity.class);
                break;
            case "体脂秤":
                intent.setClass(mContext, WeightInputActivity.class);
                break;
            case "尿常规":
                ToastHelper.showShortMsg("开发中，敬请期待");
//                intent.setClass(mContext, UrineListeningActivity.class);
                break;
            case "皮肤检测仪":
//                intent.setClass(mContext, SkinIn.class);
                break;
            default:
                intent.setClass(mContext, HealthHomeActivity.class);
                break;
        }
        mContext.startActivity(intent);
    }

}
