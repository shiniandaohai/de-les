package com.boer.delos.activity.healthylife.pressure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.R.id.iv_show1;
import static com.boer.delos.activity.healthylife.tool.DealWithValues.judgeHeartRateState;

/**
 * @author liyang
 * @Description:当前血压 界面
 * create at 2016/5/5 21:04
 */
public class CurrentBloodPressListeningActivity extends BaseListeningActivity {
    @Bind(R.id.tv_date)
    TextView mTvDate;

    @Bind(R.id.tv_bmi)
    TextView mTvBloodPressure;
    @Bind(R.id.weight_state)
    TextView mPressureState;

    @Bind(iv_show1)
    ImageView mIvShow1;
    @Bind(R.id.iv_show2)
    ImageView mIvShow2;
    @Bind(R.id.iv_show3)
    ImageView mIvShow3;
    @Bind(R.id.iv_show4)
    ImageView mIvShow4;
    @Bind(R.id.iv_show5)
    ImageView mIvShow5;
    @Bind(R.id.iv_show6)
    ImageView mIvShow6;
    @Bind(R.id.iv_show7)
    ImageView mIvShow7;

    @Bind(R.id.tv_date2)
    TextView mTvDate2;

    @Bind(R.id.tv_heart_rate)
    TextView mTvHeartRate;
    @Bind(R.id.tv_belong2)
    TextView mTvBelong2;

    @Bind(R.id.iv_show21)
    ImageView mIvShow21;
    @Bind(R.id.iv_show22)
    ImageView mIvShow22;
    @Bind(R.id.iv_show23)
    ImageView mIvShow23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lately_blood_pressure);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        initTopBar(getString(R.string.lately_blood_press), null, true, false);

        mIvShow1.setVisibility(View.INVISIBLE);
        mIvShow2.setVisibility(View.INVISIBLE);
        mIvShow3.setVisibility(View.INVISIBLE);
        mIvShow4.setVisibility(View.INVISIBLE);
        mIvShow5.setVisibility(View.INVISIBLE);
        mIvShow6.setVisibility(View.INVISIBLE);
        mIvShow7.setVisibility(View.INVISIBLE);
        mIvShow21.setVisibility(View.INVISIBLE);
        mIvShow22.setVisibility(View.INVISIBLE);
        mIvShow23.setVisibility(View.INVISIBLE);

    }

    private void initData() {

        Intent intent = getIntent();
        int pressureH = intent.getIntExtra("PressureH", 0);
        int pressureL = intent.getIntExtra("PressureL", 0);
        int pressureP = intent.getIntExtra("PressureP", 0);
        long pressureT = intent.getIntExtra("PressureT", 0);
        //没有数据传入
        if (pressureH == 0 && pressureL == 0 && pressureP == 0) {
            return;
        }
        int i = DealWithValues.judgeBloodPressureState(pressureH, pressureL);
        ArrayList<Map> mapArrayList = DealWithValues.getInstance().dealWithPressureChart2();

        int j = judgeHeartRateState(pressureP);
        ArrayList<Map> maps = DealWithValues.getInstance().dealWithHeartRate2();


        mTvDate.setText(TimeUtil.formatStamp2Time(pressureT, "yyyy-MM-dd"));
        mTvDate2.setText(TimeUtil.formatStamp2Time(pressureT, "yyyy-MM-dd"));

        mTvBloodPressure.setText(" " + pressureH + "/" + pressureL);
        mPressureState.setText("  " + mapArrayList.get(i).get("title").toString());
        mPressureState.setTextColor((int) mapArrayList.get(i).get("color"));

        mTvHeartRate.setText(" " + pressureP + "");
        mTvBelong2.setText("  " + maps.get(j).get("title").toString());
        mTvBelong2.setTextColor((int) maps.get(j).get("color"));

        switch (i) {
            case 0:
                mIvShow1.setVisibility(View.VISIBLE);
                break;
            case 1:
                mIvShow2.setVisibility(View.VISIBLE);
                break;
            case 2:
                mIvShow3.setVisibility(View.VISIBLE);
                break;
            case 3:
                mIvShow4.setVisibility(View.VISIBLE);
                break;
            case 4:
                mIvShow5.setVisibility(View.VISIBLE);
                break;
            case 5:
                mIvShow6.setVisibility(View.VISIBLE);
                break;
            case 6:
                mIvShow7.setVisibility(View.VISIBLE);
                break;
        }
        switch (j) {
            case 0:
                mIvShow21.setVisibility(View.VISIBLE);
                break;
            case 1:
                mIvShow22.setVisibility(View.VISIBLE);
                break;
            case 2:
                mIvShow23.setVisibility(View.VISIBLE);
                break;

        }
    }

}
