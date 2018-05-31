package com.boer.delos.activity.healthylife;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.sugar.SugarConn2Activity;
import com.boer.delos.activity.healthylife.urine.BaseUrineActivity;
import com.boer.delos.activity.healthylife.urine.UrineListeningActivity;
import com.boer.delos.activity.healthylife.pressure.PressuerListeningActivity;
import com.boer.delos.activity.healthylife.weight.ScaleConnActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.activity.healthylife.tool.SavePressureDataPreferences;
import com.boer.delos.activity.healthylife.tool.SaveScaleDataPreferences;
import com.boer.delos.activity.healthylife.tool.SaveSugarDataPreferences;
import com.boer.delos.activity.healthylife.tool.SaveUrineDataPreferences;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author PengJiYang
 * @Description: 智能设备 界面
 * create at 2016/5/27 15:25
 */
public class IntelligentDeviceListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private TextView tvBloodSugarStatus;
    private TextView tvBloodSugarValue;
    private TextView tvBloodSugarDate;

    private TextView tvBloodPressStatus;
    private TextView tvBloodPressValue;
    private TextView tvBloodPressDate;

    private TextView tvBodyFatStatus;
    private TextView tvBodyFatValue;
    private TextView tvBodyFatDate;

    private TextView tvUroStatus;
    private TextView tvUroValue;
    private TextView tvUroDate;

    private PercentLinearLayout llBloodSugar;
    private PercentLinearLayout llBloodPressure;
    private PercentLinearLayout llBodyFat;
    private PercentLinearLayout llUro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligent_device);

        initView();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        initTopBar(R.string.health_device, null, true, false);
        llUro = (PercentLinearLayout) findViewById(R.id.llUro);
        tvUroDate = (TextView) findViewById(R.id.tvUroDate);
        tvUroValue = (TextView) findViewById(R.id.tvUroValue);
        tvUroStatus = (TextView) findViewById(R.id.tvUroStatus);

        llBodyFat = (PercentLinearLayout) findViewById(R.id.llBodyFat);
        tvBodyFatDate = (TextView) findViewById(R.id.tvBodyFatDate);
        tvBodyFatValue = (TextView) findViewById(R.id.tvBodyFatValue);
        tvBodyFatStatus = (TextView) findViewById(R.id.tvBodyFatStatus);

        llBloodPressure = (PercentLinearLayout) findViewById(R.id.llBloodPressure);
        tvBloodPressDate = (TextView) findViewById(R.id.tvBloodPressDate);
        tvBloodPressValue = (TextView) findViewById(R.id.tvBloodPressValue);
        tvBloodPressStatus = (TextView) findViewById(R.id.tvBloodPressStatus);

        llBloodSugar = (PercentLinearLayout) findViewById(R.id.llBloodSugar);
        tvBloodSugarDate = (TextView) findViewById(R.id.tvBloodSugarDate);
        tvBloodSugarValue = (TextView) findViewById(R.id.tvBloodSugarValue);
        tvBloodSugarStatus = (TextView) findViewById(R.id.tvBloodSugarStatus);


    }

    private void initData() {
        SavePressureDataPreferences savePressureDataPreferences = SavePressureDataPreferences.getInstance();
        SaveScaleDataPreferences saveScaleDataPreferences = SaveScaleDataPreferences.getInstance();
        SaveSugarDataPreferences saveSugarDataPreferences = SaveSugarDataPreferences.getInstance();
        SaveUrineDataPreferences saveUrineDataPreferences = SaveUrineDataPreferences.getInstance();
        //读取本地文件中保存的数据
        savePressureDataPreferences.readPressureDataXml(this);
        saveScaleDataPreferences.readScaleDataXml(this);
        saveSugarDataPreferences.readSugarDataXml(this);
        saveUrineDataPreferences.readUrineDataXml(this);

        tvBloodPressValue.setText(savePressureDataPreferences.getValueH() + "/" + savePressureDataPreferences.getValueL() + " mmHg");
        tvBloodPressDate.setText(savePressureDataPreferences.getDate());

        tvBloodSugarValue.setText(saveSugarDataPreferences.getValue() + " mmol/L");
        tvBloodSugarDate.setText(saveSugarDataPreferences.getDate());

        tvBodyFatValue.setText(saveScaleDataPreferences.getKg() + " kg");
        tvBodyFatDate.setText(saveScaleDataPreferences.getTime());

        tvUroDate.setText(saveUrineDataPreferences.getDate());
        tvUroValue.setText(saveUrineDataPreferences.getUrineScore());

        judgeBloodPressureStateWithHighValueAndLowValue(Integer.valueOf(savePressureDataPreferences.getValueH()), Integer.valueOf(savePressureDataPreferences.getValueL()), tvBloodPressStatus);

        judgeBloodSugarState(Float.parseFloat(saveSugarDataPreferences.getValue()), tvBloodSugarStatus);

        judgeBodyWeightState(Float.parseFloat(saveScaleDataPreferences.getBMI()), tvBodyFatStatus);

        judgeUrineState(Float.parseFloat(saveUrineDataPreferences.getUrineScore()), tvUroStatus);


    }


    private void initListener() {
        llBloodSugar.setOnClickListener(this);
        llBloodPressure.setOnClickListener(this);
        llBodyFat.setOnClickListener(this);
        llUro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBloodSugar:
                startActivity(new Intent(IntelligentDeviceListeningActivity.this, SugarConn2Activity.class));
                break;
            case R.id.llBloodPressure:
                startActivity(new Intent(IntelligentDeviceListeningActivity.this, PressuerListeningActivity.class));
                break;
            case R.id.llBodyFat:
                startActivity(new Intent(IntelligentDeviceListeningActivity.this, ScaleConnActivity.class));
                break;
            case R.id.llUro:
                startActivity(new Intent(IntelligentDeviceListeningActivity.this, UrineListeningActivity.class));
                break;
        }
    }


    //根据高压和低压来设置控件颜色和控件值
    private void judgeBloodPressureStateWithHighValueAndLowValue(int highValue, int lowValue, TextView stateLabel) {

        if (highValue == 0 && lowValue == 0) {
            return;
        }
        int i = DealWithValues.judgeBloodPressureState(highValue, lowValue);
        try {
            DealWithValues dealWithValues = DealWithValues.getInstance();
            ArrayList<Map> arrayList = dealWithValues.dealWithPressure();
            int textColor = Integer.parseInt(arrayList.get(i).get("color").toString());

            stateLabel.setTextColor(textColor);
            stateLabel.setText(arrayList.get(i).get("title").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据血糖值设置控件颜色和控件值
    private void judgeBloodSugarState(float value, TextView stateLabel) {
        if (value == 0) {
            return;
        }
        int i = DealWithValues.judgeBloodSugarState(value);

        DealWithValues dealWithValues = DealWithValues.getInstance();
        ArrayList<Map> arrayList = dealWithValues.dealWithSugar();
        int textColor = Integer.parseInt(arrayList.get(i).get("color").toString());
        stateLabel.setTextColor(textColor);
        stateLabel.setText(arrayList.get(i).get("title").toString());
    }

    //根据体重值设置控件颜色和控件值
    private void judgeBodyWeightState(float BMINum, TextView stateLabel) {
        float j = BMINum;
        if (j == 0) {
            return;
        }
        int i = DealWithValues.judgeWeightState(j);

        DealWithValues dealWithValues = DealWithValues.getInstance();
        ArrayList<Map> arrayList = dealWithValues.dealWithWeight();
        int textColor = Integer.parseInt(arrayList.get(i).get("color").toString());
        stateLabel.setTextColor(textColor);
        stateLabel.setText(arrayList.get(i).get("title").toString());
    }

    private void judgeUrineState(float score, TextView urineStatus) {
        if (score >= BaseUrineActivity.SCORE_STANDARD) {
            urineStatus.setText("正常");
            urineStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            urineStatus.setText("异常");
            urineStatus.setTextColor(getResources().getColor(R.color.red));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null &&
                (bluetoothAdapter.isEnabled() || bluetoothAdapter.isDiscovering())) {
            bluetoothAdapter.disable();
        }
    }
}
