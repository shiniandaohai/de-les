package com.boer.delos.activity.healthylife.weight;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/20 0020 11:37
 * @Modify:
 * @ModifyDate:
 */


public class WeightInputActivity extends CommonBaseActivity {

    @Bind(R.id.tv_weight)
    TextView mTvWeight;
    @Bind(R.id.et_blood_pressure_H)
    EditText mEtBloodPressureH;
    @Bind(R.id.weight_fat_rate)
    TextView mWeightFatRate;
    @Bind(R.id.et_blood_pressure_L)
    EditText mEtBloodPressureL;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.iv_1)
    ImageView mIv1;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    private User mUser;
    private TimePickerView timePickerView;
    private long mMeasuretime;

    @Override
    protected int initLayout() {
        return R.layout.activity_weight_input;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_hand_input));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(this);
            mUser = Constant.LOGIN_USER;
        }

        mWeightFatRate.setText(getString(R.string.text_fat_rate) + "(%)");
        mTvWeight.setText(getString(R.string.text_weight) + "(kg)");
    }

    @Override
    protected void initData() {
        initTimerPicker();
        mMeasuretime = TimeUtil.getCurrentstamp();
        String showTime = TimeUtil.formatStamp2Time(mMeasuretime, "yyyy/MM/dd");
        mTvDate.setText(showTime);
        initTimerPicker();
    }

    @Override
    protected void initAction() {

        mEtBloodPressureH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mEtBloodPressureH.setCursorVisible(hasFocus);
            }
        });
        mEtBloodPressureL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mEtBloodPressureL.setCursorVisible(hasFocus);
            }
        });
    }


    @OnClick({R.id.tv_date, R.id.iv_1, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
            case R.id.iv_1:
                hideInput();
                timePickerShow(mTvDate);

                break;
            case R.id.btn_commit:
                reportBloodPressureData();
                break;
        }
    }

    private void initTimerPicker() {
        //时间选择器
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);
        timePickerView.setRange(1900, 2100);
        timePickerView.setTime(new Date());

    }

    private void timePickerShow(final TextView editText) {

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date currentDate = new Date();
                if (date.after(currentDate)) {
                    ToastHelper.showShortMsg(getString(R.string.text_over_current_time));
                    return;
                }
                mMeasuretime = TimeUtil.getTargetTimeStamp(date);
                String showTime = TimeUtil.formatStamp2Time(mMeasuretime, "yyyy/MM/dd");
                editText.setText(showTime);
            }
        });
        timePickerView.show();
    }

    private void reportBloodPressureData() {
        reportBodyweight(mMeasuretime + "",
                StringUtil.getTextViewString(mEtBloodPressureH),
                StringUtil.getTextViewString(mEtBloodPressureL));

    }

    //上传体重
    private void reportBodyweight(String measuretime,
                                  String weightKg, String fatRate) {
        if (mUser == null) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_login_again));
            return;
        }

        if (StringUtil.isEmpty(weightKg) && StringUtil.isEmpty(fatRate)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_please)
                    + getString(R.string.text_weight) +
                    "," + getString(R.string.text_fat_rate));
            return;
        }
        if (StringUtil.isEmpty(weightKg)){
            weightKg = "0";
        }
        if (StringUtil.isEmpty(fatRate)){
            fatRate = "0";
        }
        toastUtils.showProgress(getString(R.string.toast_update_ing));//"上传中..."

        HealthController.getInstance().reportBodyweight(this, mUser.getId().equals(Constant.USERID) ? "0" : mUser.getId(),
                measuretime + "", weightKg, fatRate, "", new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {

                            String ret = JsonUtil.parseString(json, "ret");
                            if ("0".equals(ret)) {
                                toastUtils.dismiss();
                                toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            } else {
                                String msg = JsonUtil.parseString(json, "msg");
                                toastUtils.showErrorWithStatus(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toastUtils.dismiss();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null)
                            toastUtils.showErrorWithStatus(getString(R.string.toast_update_failure));
                    }
                });

    }


    private boolean judgeInput(EditText editText) {
        if (editText == null) {
            return false;
        }
        if (editText.getText() == null) {
            return false;
        }
        if (TextUtils.isEmpty(editText.getText().toString())) {
            return false;
        }
        return true;
    }
}
