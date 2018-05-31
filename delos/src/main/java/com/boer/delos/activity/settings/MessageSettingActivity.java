package com.boer.delos.activity.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.MsgSettings;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.utils.L;
import com.boer.delos.view.popupWindow.ShowTimePopupWindow;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/30.
 */
public class MessageSettingActivity extends CommonBaseActivity {

    @Bind(R.id.tv_sleep_start)
    TextView tvSleepStart;
    @Bind(R.id.tv_sleep_end)
    TextView tvSleepEnd;
    @Bind(R.id.switch_sys_msg)
    ToggleButton switchSysMsg;
    @Bind(R.id.switch_alert_msg)
    ToggleButton switchAlertMsg;
    @Bind(R.id.switch_msg_sleep)
    ToggleButton switchMsgSleep;
    ShowTimePopupWindow showTimePopupWindow;
    @Bind(R.id.layout_time)
    LinearLayout layoutTime;
    @Bind(R.id.tv_divider_v)
    TextView tvDividerV;
    @Bind(R.id.tv_no_trouble_title)
    TextView tvNoTroubleTitle;

    private AlarmController alarmController;
    private String mStartTime="03:00";
    private String mEndTime="17:00";

    @Override
    protected int initLayout() {
        return R.layout.activity_message_setting;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.setting_msg);

        tlTitleLayout.setRightText(R.string.save);

        switchMsgSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvSleepStart.setText(mStartTime);
                    tvSleepEnd.setText(mEndTime);
                    tvDividerV.setText("-");
                    layoutTime.setClickable(true);
                    tvNoTroubleTitle.setEnabled(true);
                }
                else{
                    tvSleepStart.setText("");
                    tvSleepEnd.setText("");
                    tvDividerV.setText("");
                    layoutTime.setClickable(false);
                    tvNoTroubleTitle.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void initData() {
        alarmController = new AlarmController();
        showTimePopupWindow = new ShowTimePopupWindow(MessageSettingActivity.this, layoutTime);


        getMsgSettings();

    }


    @Override
    protected void initAction() {

    }

    private void showTimePicker() {

        showTimePopupWindow.setShowTimePopupWindowInterface(new ShowTimePopupWindow.ShowTimePopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String startTime, String endTime) {
                mStartTime=startTime;
                mEndTime=endTime;
                tvSleepStart.setText(startTime);
                tvSleepEnd.setText(endTime);

            }
        });
        showTimePopupWindow.showPopupWindow();

    }


    @OnClick({R.id.layout_time})
    public void onClick(View view) {
        if (view.getId() == R.id.layout_time)
            showTimePicker();
    }


    @Override
    public void rightViewClick() {
        super.rightViewClick();


        setMsgSettings();


    }

    private void getMsgSettings() {

        alarmController.getMsgSettings(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {


                try {
                    JSONObject jsonobject = new JSONObject(json);
                    String settings = jsonobject.getString("settings");
                    Gson gson = new Gson();
                    MsgSettings msgSettings = gson.fromJson(settings, MsgSettings.class);

//                    mStartTime=msgSettings.getUnDisturbStartTime();
//                    mEndTime=msgSettings.getUnDisturbEndTime();
//                    tvSleepStart.setText(msgSettings.getUnDisturbStartTime());
//                    tvSleepEnd.setText(msgSettings.getUnDisturbEndTime());

                    if (msgSettings.getUnDisturb().equals("1")) {
                        switchMsgSleep.setChecked(true);
                    } else {
                        switchMsgSleep.setChecked(false);
                    }

                    if (msgSettings.getReceiveAlarmMessage().equals("1")) {
                        switchAlertMsg.setChecked(true);
                    } else {
                        switchAlertMsg.setChecked(false);
                    }


                    if (msgSettings.getReceiveSystemMessage().equals("1")) {
                        switchSysMsg.setChecked(true);
                    } else {
                        switchSysMsg.setChecked(false);
                    }


                    if (!TextUtils.isEmpty(msgSettings.getUnDisturbStartTime()) && !TextUtils.isEmpty(msgSettings.getUnDisturbEndTime())) {
                        mStartTime=msgSettings.getUnDisturbStartTime();
                        mEndTime=msgSettings.getUnDisturbEndTime();
                        tvSleepStart.setText(msgSettings.getUnDisturbStartTime());
                        tvSleepEnd.setText(msgSettings.getUnDisturbEndTime());
                        switchMsgSleep.setChecked(true);
                        tvDividerV.setText("-");
                        layoutTime.setClickable(true);
                        tvNoTroubleTitle.setEnabled(true);
                    } else {
                        tvSleepStart.setText("");
                        tvSleepEnd.setText("");
                        tvDividerV.setText("");
                        layoutTime.setClickable(false);
                        switchMsgSleep.setChecked(false);
                        tvNoTroubleTitle.setEnabled(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {

            }
        });


    }


    private void setMsgSettings() {

        MsgSettings msgSettings = new MsgSettings();


        if (switchSysMsg.isChecked())
            msgSettings.setReceiveSystemMessage("1");
        else
            msgSettings.setReceiveSystemMessage("0");


        if (switchAlertMsg.isChecked())
            msgSettings.setReceiveAlarmMessage("1");
        else
            msgSettings.setReceiveAlarmMessage("0");


        if (switchMsgSleep.isChecked())
            msgSettings.setUnDisturb("1");
        else
            msgSettings.setUnDisturb("0");


        msgSettings.setUnDisturbStartTime(tvSleepStart.getText().toString());
        msgSettings.setUnDisturbEndTime(tvSleepEnd.getText().toString());


        alarmController.setMsgSettings(this, msgSettings, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {

                finish();


            }

            @Override
            public void onFailed(String json) {

                finish();

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
