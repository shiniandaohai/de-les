package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.github.mikephil.charting.charts.LineChart;

/**
 * @author XieQingTing
 * @Description: 取暖器界面
 * create at 2016/5/19 14:55
 */
public class WarmDeviceListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private android.widget.TextView tvWarmElectric;
    private android.widget.TextView tvWarmPower;
    private android.widget.TextView tvWarmVoltage;
    private android.widget.TextView tvWarmElectricEnergy;
    private android.widget.Spinner spWarmElectricYear;
    private android.widget.TextView tvWarmElectricYear;
    private android.widget.TextView tvWarmElectricMonth;
    private android.widget.TextView tvWarmElectricDay;
    private android.support.v7.widget.RecyclerView rvWarmYear;
    private android.support.v7.widget.RecyclerView rvWarmMonth;
    private android.support.v7.widget.RecyclerView rvWarmDay;
    private com.github.mikephil.charting.charts.LineChart chartWarmElectric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO 设置数据
        setContentView(R.layout.activity_warm_device);

        initView();

    }

    private void initView() {
        initTopBar(R.string.power_warmer,null,true,false);
        this.chartWarmElectric = (LineChart) findViewById(R.id.chartWarmElectric);
        this.rvWarmDay = (RecyclerView) findViewById(R.id.rvWarmDay);
        this.rvWarmMonth = (RecyclerView) findViewById(R.id.rvWarmMonth);
        this.rvWarmYear = (RecyclerView) findViewById(R.id.rvWarmYear);
        this.tvWarmElectricDay = (TextView) findViewById(R.id.tvWarmElectricDay);
        this.tvWarmElectricMonth = (TextView) findViewById(R.id.tvWarmElectricMonth);
        this.tvWarmElectricYear = (TextView) findViewById(R.id.tvWarmElectricYear);
        this.spWarmElectricYear = (Spinner) findViewById(R.id.spWarmElectricYear);
        this.tvWarmElectricEnergy = (TextView) findViewById(R.id.tvWarmElectricEnergy);
        this.tvWarmVoltage = (TextView) findViewById(R.id.tvWarmVoltage);
        this.tvWarmPower = (TextView) findViewById(R.id.tvWarmPower);
        this.tvWarmElectric = (TextView) findViewById(R.id.tvWarmElectric);
    }

    @Override
    public void onClick(View v) {

    }
}
