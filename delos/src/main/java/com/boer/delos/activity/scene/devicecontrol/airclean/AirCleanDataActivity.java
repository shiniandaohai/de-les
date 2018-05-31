package com.boer.delos.activity.scene.devicecontrol.airclean;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.AirCleanData;
import com.boer.delos.widget.AirCleanView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirCleanDataActivity extends CommonBaseActivity {

    public AirCleanData valueBean;
    @Bind(R.id.tv_week_data)
    TextView tvWeekData;
    @Bind(R.id.tv_year_data)
    TextView tvYearData;
    @Bind(R.id.tv_month_data)
    TextView tvMonthData;
    @Bind(R.id.acview_all_data)
    AirCleanView acviewAllData;
    @Bind(R.id.llayout_data)
    LinearLayout llayoutData;


    @Override
    protected int initLayout() {
        return R.layout.activity_air_clean_data;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.air_clean_amount, R.color.white);
        llayoutStatus.setBackgroundColor(AirCleanActivity.skinColor);
        tlTitleLayout.setTitleBackgroundColor(AirCleanActivity.skinColor);
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back_white);
        llayoutData.setBackgroundColor(AirCleanActivity.skinColor);
        acviewAllData.setBackgroundColor(AirCleanActivity.skinColor);

    }

    @Override
    protected void initData() {


        Intent intent = getIntent();
        valueBean = (AirCleanData) intent.getSerializableExtra("valueBean");


        if (valueBean != null) {
            AirCleanData.ValueBean value = valueBean.getValue();
            if (value != null) {
                acviewAllData.setText(value.getTotalAccPur() + "ug");
                tvMonthData.setText(value.getMonthAccPur() + "ug");
                tvYearData.setText(value.getYearAccPur() + "ug");
                tvWeekData.setText(value.getWeekAccPur() + "ug");
            }
        }
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
