package com.boer.delos.activity.healthylife.weight;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.BaseHealthyLifeActivity;
import com.boer.delos.activity.healthylife.MPandroidChartHelper;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.interf.ISimpleInterface2;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.User;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.view.CircleImageView;
import com.github.mikephil.charting.charts.BarChart;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.boer.delos.R.id.ivBloodHelp;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:体重 界面
 * @CreateDate: 2017/3/21 0021 10:11
 * @Modify:
 * @ModifyDate:
 */

public class ScaleDetailActivity extends BaseHealthyLifeActivity
        implements View.OnClickListener, ISimpleInterface2 {
    @Bind(R.id.ivBack)
    ImageView mIvBack;
    @Bind(R.id.ivRight)
    ImageView mIvRight;
    @Bind(R.id.tvTime1)
    TextView mTvTime1;
    @Bind(R.id.tvNum1)
    TextView mTvNum1;
    @Bind(R.id.textView6)
    TextView mTextView6;
    @Bind(ivBloodHelp)
    ImageView mIvBloodHelp;
    @Bind(R.id.chart1)
    BarChart mChart1;
    @Bind(R.id.llBloodSugar)
    LinearLayout mLlBloodSugar;
    @Bind(R.id.tvTime2)
    TextView mTvTime2;
    @Bind(R.id.tvNum2)
    TextView mTvNum2;
    @Bind(R.id.chart2)
    BarChart mChart2;

    @Bind(R.id.llBloodSugar2)
    LinearLayout mLlBloodSugar2;
    @Bind(R.id.tv_muscle)
    TextView mTvMuscle;
    @Bind(R.id.tv_water)
    TextView mTvWater;
    @Bind(R.id.tv_bone)
    TextView mBone;
    @Bind(R.id.tv_bmr)
    TextView mTvBmr;
    @Bind(R.id.tv_bmi)
    TextView mTvBmi;

    @Bind(R.id.ic_avatar)
    CircleImageView mIcAvatar;
    @Bind(R.id.user_drop)
    CheckedTextView mUserDrop;
    @Bind(R.id.tv_userName)
    TextView mTvUserName;

    private List<String> valsX1;
    private List<String> valsX2;
    private List<Float> valsY1;
    private List<Float> valsY2;
    private List<WeightBean> mWeightList;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_detail);
        ButterKnife.bind(this);

        iniView();
        initChart();
        initData(mWeightList);

        user = Constant.LOGIN_USER;

        settingHeader();
    }

    private void iniView() {
        initTopBar(R.string.homepage_weight, null, true, true);

        ivRight.setImageResource(R.drawable.ic_health_live_more);
        mWeightList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        List<WeightBean> tempList = (List<WeightBean>) bundle.getSerializable("weight");

        mWeightList.addAll(tempList);
    }

    //TODO 设置已分享健康给自己的用户 展示
    private void settingHeader() {
        showListPopup(mTvUserName);
        ImageLoader.getInstance().displayImage((URLConfig.HTTP + user.getAvatarUrl()).trim(),
                mIcAvatar, BaseApplication.getInstance().displayImageOptions);

        setSimpleInterface2(this);

        mTvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharefamilyList.size() == 0) {
                    return;
                }
                mUserDrop.toggle();
                mListPop.show();
            }
        });
    }

    private void initData(List<WeightBean> weightLists) {
        valsX1.clear();
        valsX2.clear();
        valsY1.clear();
        valsY2.clear();
        if (weightLists == null || weightLists.size() == 0) {
            mTvTime1.setText("");
            mTvTime2.setText("");

            mTvNum1.setText("0");
            mTvNum2.setText("0");

            MPandroidChartHelper.setBarChartData(mChart1, valsX1, valsY1);
            MPandroidChartHelper.setBarChartData(mChart2, valsX2, valsY2);
            calculateData(null);
            return;
        }
        for (WeightBean bean : weightLists) {
            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "MM.dd");
            valsX1.add(time);
            valsX2.add(time);

            valsY1.add(bean.getWeight());
            valsY2.add(bean.getFatrate());

            if (weightLists.indexOf(bean) == weightLists.size() - 1) {
                String time1 = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy-MM-dd");
                mTvTime1.setText(time1);
                mTvTime2.setText(time1);

                mTvNum1.setText(String.valueOf(bean.getWeight()));
                mTvNum2.setText(String.valueOf(bean.getFatrate()));
            }
        }

        MPandroidChartHelper.setBarChartData(mChart1, valsX1, valsY1);
        MPandroidChartHelper.setBarChartData(mChart2, valsX2, valsY2);

        calculateData(weightLists.get(weightLists.size() - 1).getDetail());

    }

    private void initChart() {
        MPandroidChartHelper.initBarChart(mChart1);
        MPandroidChartHelper.initBarChart(mChart2);
        mChart1.setPinchZoom(false); //手势缩放
        mChart2.setPinchZoom(false); //手势缩放

        valsX1 = new ArrayList<>();
        valsX2 = new ArrayList<>();
        valsY1 = new ArrayList<>();
        valsY2 = new ArrayList<>();
    }

    private void calculateData(String s) {
        if (StringUtil.isEmpty(s)) {
            mTvMuscle.setText("0%");
            mTvWater.setText("0%");
            mBone.setText("0%");
            mTvBmr.setText("0 Kcal");
            mTvBmi.setText("0%");
            return;
        }
        WeightBean.WeightDetailBean bean = GsonUtil.getObject(s, WeightBean.WeightDetailBean.class);
        mTvMuscle.setText(bean.getMuscle() + "%");
        mTvWater.setText(bean.getWater() + "%");
        mBone.setText(bean.getBone() + "%");
        mTvBmr.setText(bean.getKcal() + "Kcal");
        mTvBmi.setText(bean.getBMI() + "%");

    }

    @OnClick({R.id.ivRight, R.id.ivBloodHelp})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ivRight:
                intent = new Intent(this, WeightHistoryActivity.class);

                break;
            case R.id.ivBloodHelp:

                intent = new Intent(this, CurrentWeightActivity.class);

                Bundle bundle = new Bundle();
                if (mWeightList.size() != 0) {
                    bundle.putSerializable("weight", mWeightList.get(mWeightList.size() - 1));
                }
                intent.putExtras(bundle);
                break;
        }
        startActivity(intent);
    }

    /**
     * 请求健康数据
     */
    private void queryRecentHealth(String userId, final String healthyType, String recent) {
        Calendar c = Calendar.getInstance();
        long millTime = c.getTimeInMillis() / 1000;
        HealthController.getInstance().queryRecentHealth(this, millTime + "", healthyType, recent, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i(Json);
                try {
                    BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showInfoWithStatus("健康数据，请求错误");
                        return;
                    }

                    WeightResult Weight = new Gson().fromJson(Json, WeightResult.class);
                    mWeightList.clear();
                    mWeightList.addAll(Weight.getData());

                    initData(mWeightList);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String Json) {
                L.i(Json);
            }
        });
    }

    @Override
    public void clickListener(String tag) {

    }

    @Override
    public void clickListener2(int pos) {
        mUserDrop.toggle();

        if (user == mSharefamilyList.get(pos).getUser()) {
            return;
        }
        user = mSharefamilyList.get(pos).getUser();
        if (user.getId().equals(Constant.USERID)) {
            mTvUserName.setText("我");
        } else {
            String name = mSharefamilyList.get(pos).getUserAlias();
            if (StringUtil.isEmpty(name)) {
                name = user.getName();
                if (StringUtil.isEmpty(name)) {
                    name = user.getMobile();
                }
            }
            mTvUserName.setText(name);
        }
        ImageLoader.getInstance().displayImage((URLConfig.HTTP + user.getAvatarUrl()).trim(),
                mIcAvatar, BaseApplication.getInstance().displayImageOptions);

        queryRecentHealth(user.getId(), HealthController.HEATHY_WEIGHT, "7");
    }
}
