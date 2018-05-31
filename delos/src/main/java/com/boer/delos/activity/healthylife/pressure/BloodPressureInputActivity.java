package com.boer.delos.activity.healthylife.pressure;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Time;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/17 0017 20:39
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressureInputActivity extends CommonBaseActivity {
    @Bind(R.id.et_blood_pressure_H)
    EditText mEtBloodPressureH;
    @Bind(R.id.et_blood_pressure_L)
    EditText mEtBloodPressureL;
    @Bind(R.id.et_heart_rate)
    EditText mEtHeartRate;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    private User mUser;
    private long mMeasuretime;
    private TimePickerView timePickerView;

    @Override
    protected int initLayout() {
        return R.layout.activity_blood_pressure_input;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        tlTitleLayout.setTitle(getString(R.string.text_hand_input));
    }

    @Override
    protected void initData() {
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
        mEtHeartRate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mEtHeartRate.setCursorVisible(hasFocus);
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
        if (!judgeInput(mEtBloodPressureH)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_please) +
                    getString(R.string.text_diastolic_pressure));
            return;

        }
        if (!judgeInput(mEtBloodPressureL)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_please) +
                    getString(R.string.text_systolic_pressure));
            return;
        }
        if (!judgeInput(mEtHeartRate)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_please) +
                    getString(R.string.text_heart_rate));
            return;
        }

        if (mTvDate.getText() == null || TextUtils.isEmpty(mTvDate.getText().toString())) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_please) +
                    getString(R.string.text_date));
            return;

        }

        reportBloodpressure(mMeasuretime + "",
                StringUtil.getTextViewString(mEtBloodPressureH),
                StringUtil.getTextViewString(mEtBloodPressureL),
                StringUtil.getTextViewString(mEtHeartRate)
        );

    }

    //上传血压值
    private void reportBloodpressure(String measuretime,
                                     String valueH, String valueL, String bpm) {

        toastUtils.showProgress(getString(R.string.toast_update_ing));//"上传中..."
        HealthController.getInstance().reportBloodpressure(this, mUser == null ? "0" : mUser.getId(),
                measuretime + "",
                valueH, valueL, bpm, "", Constant.CURRENTHOSTID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {

                            String ret = JsonUtil.parseString(Json, "ret");
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
                                String msg = JsonUtil.parseString(Json, "msg");
                                toastUtils.showErrorWithStatus(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            toastUtils.dismiss();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
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
