package com.boer.delos.activity.healthylife.sugar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.TimeUtil;

/**
 * @author XieQingTing
 * @Description: 当前血糖界面
 * create at 2016/5/27 17:49
 */
public class BloodSugarMajorActivity extends CommonBaseActivity
        implements View.OnClickListener {
    private TextView tvCurrentDate;
    private TextView tvCurrentValue;
    private TextView tvCurrentLevel;
    private ImageView ivHeight;
    private ImageView ivNormal;
    private ImageView ivLow;

    private int period;
    private long date;
    private float value;

    @Override
    protected int initLayout() {
        return R.layout.activity_current_blood_sugar;
    }

    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_major_info));
    }

    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    public void onClick(View v) {

    }


    private void init() {
        Bundle intent = getIntent().getExtras();
        period = intent.getInt("period", -1);
        date = intent.getLong("measureDate", 0);
        value = intent.getFloat("value", 0f);

        String time = TimeUtil.formatStamp2Time(date, "yyyy-MM-dd");
        if (time.contains("1970")) {
        } else
            tvCurrentDate.setText(time);
        StringBuffer sb = new StringBuffer();
        switch (period) {
            case 0:
            case 2:
            case 4:
            case 6:
                sb.append("餐前");
                break;
            case 1:
            case 3:
            case 5:
                sb.append("餐后");
                break;
            case 7:
                sb.append("睡前");
                break;
        }
        sb.append(value);
        sb.append(" ");
        tvCurrentValue.setText(sb.toString());

        int level = DealWithValues.judgeBloodSugarState(value);
        switch (level) {
            case 0:
                tvCurrentLevel.setText("偏高");
                tvCurrentLevel.setTextColor(getResources().getColor(R.color.blood_red));
                ivHeight.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvCurrentLevel.setText("正常");
                tvCurrentLevel.setTextColor(getResources().getColor(R.color.blood_green));
                ivNormal.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvCurrentLevel.setText("偏低");
                tvCurrentLevel.setTextColor(getResources().getColor(R.color.blood_yellow));
                ivLow.setVisibility(View.VISIBLE);
                break;
        }
    }
}
