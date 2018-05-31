package com.boer.delos.activity.healthylife.weight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.WeightBean;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/21 0021 09:56
 * @Modify:
 * @ModifyDate:
 */


public class CurrentWeightActivity extends CommonBaseActivity {
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_bmi)
    TextView mTvBmi;
    @Bind(R.id.weight_state)
    TextView mWeightState;
    @Bind(R.id.iv_show1)
    ImageView mIvShow1;
    @Bind(R.id.iv_show2)
    ImageView mIvShow2;
    @Bind(R.id.iv_show3)
    ImageView mIvShow3;
    @Bind(R.id.iv_show4)
    ImageView mIvShow4;
    @Bind(R.id.iv_show5)
    ImageView mIvShow5;
    @Bind(R.id.iv_show7)
    ImageView mIvShow7;

    @Override
    protected int initLayout() {
        return R.layout.activity_current_weight;
    }

    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_major_info));
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        WeightBean bean = (WeightBean) bundle.getSerializable("weight");
        if (bean == null) {
            return;
        }
//        calculate(bean);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    private void calculate(WeightBean bean) {

        String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy-MM-dd");
        mTvDate.setText(time);

        WeightBean.WeightDetailBean detailBean = GsonUtil.getObject(bean.getDetail(), WeightBean.WeightDetailBean.class);
        if (detailBean == null || StringUtil.isEmpty(detailBean.getBMI())) {
            return;
        }
        float bmi = 0f;
        try {
            bmi = Float.valueOf(detailBean.getBMI());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        mTvBmi.setText(String.format("%s%.1f ", " ", bmi));
        int index = DealWithValues.judgeWeightState(bmi);
        switch (index) {
            case 0:
                mIvShow1.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_fat_super));
                mWeightState.setTextColor(getResources().getColor(R.color.red_bp_1));
                break;
            case 1:
                mIvShow2.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_fat_severe));
                mWeightState.setTextColor(getResources().getColor(R.color.red_bp_2));
                break;
            case 2:
                mIvShow3.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_fat));
                mWeightState.setTextColor(getResources().getColor(R.color.red_bp_3));
                break;
            case 3:
                mIvShow4.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_weight_over));
                mWeightState.setTextColor(getResources().getColor(R.color.red_bp_4));
                break;
            case 4:
                mIvShow5.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_weight_normal));
                mWeightState.setTextColor(getResources().getColor(R.color.green_bp));
                break;
            case 5:
                mIvShow7.setVisibility(View.VISIBLE);
                mWeightState.setText(getString(R.string.text_weight_pink));
                mWeightState.setTextColor(getResources().getColor(R.color.yellow_bp));
                break;

        }

    }
}
