package com.boer.delos.activity.greenlive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseActivity;
import com.github.mikephil.charting.charts.LineChart;

import butterknife.Bind;

import static com.boer.delos.R.id.spElectricYear;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 能耗详情
 * @CreateDate: 2017/2/22 0022 11:11
 * @Modify:
 * @ModifyDate:
 */
public class SocketEnergyDetailsActivity extends BaseActivity {
    @Bind(R.id.ivBack)
    ImageView mIvBack;
    @Bind(R.id.tvVoltage)
    TextView mTvVoltage;
    @Bind(R.id.tvPower)
    TextView mTvPower;
    @Bind(R.id.tvElectricCurrent)
    TextView mTvElectricCurrent;
    @Bind(R.id.tvElectric)
    TextView mTvElectric;
    @Bind(spElectricYear)
    Spinner mSpElectricYear;
    @Bind(R.id.tvElectricYear)
    TextView mTvElectricYear;
    @Bind(R.id.tvElectricMonth)
    TextView mTvElectricMonth;
    @Bind(R.id.tvElectricDay)
    TextView mTvElectricDay;
    @Bind(R.id.rvElectricityDay)
    RecyclerView mRvElectricityDay;
    @Bind(R.id.total_kWh)
    TextView mTotalKWh;
    @Bind(R.id.lcElectricityHistory)
    LineChart mLcElectricityHistory;
    @Bind(R.id.tvElectricDate)
    TextView mTvElectricDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_energy_detail);
    }


}
