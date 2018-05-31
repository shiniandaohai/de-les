package com.boer.delos.activity.healthylife.pressure;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.adapter.BloodPressureRecordAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.PressureResult;
import com.boer.delos.model.Time;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.ShowYearMonthPopupWindow;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/17 0017 21:05
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressuRecordActivity extends CommonBaseActivity {
    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.ctv_choice)
    CheckedTextView mCtvChoice;
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView pullToRefreshListView;

    private ListView mLvRecord;

    private BloodPressureRecordAdapter mRecordAdapter;
    private List<PressureResult.PressureBean> mDataBeanList;
    private User mUser;
    private long lastTime = 0;
    private TimePickerView timePickerView;
    private ShowYearMonthPopupWindow showYearMonthPopupWindow;

    private long mFromTime = 0;
    private long mToTime = 0;

    @Override
    protected int initLayout() {
        return R.layout.activity_health_record;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
        if (mUser == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(getApplicationContext());
            mUser = Constant.LOGIN_USER;
        }
        tlTitleLayout.setVisibility(View.GONE);
    }

    protected void initData() {
        mLvRecord = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mDataBeanList = new ArrayList<>();
        mRecordAdapter = new BloodPressureRecordAdapter(this, mDataBeanList,
                R.layout.item_blood_pressure_record);

        mLvRecord.setAdapter(mRecordAdapter);

        mFromTime = TimeUtil.getTimesMonthmorning(0);
        mToTime = TimeUtil.getCurrentstamp();

        queryBloodPressureData(mFromTime + "", mToTime + "", mUser.getId(), true);

        Loger.d("  fromtime " + mFromTime + " totime " + mToTime);
        mTvDate.setText(TimeUtil.getCurrentTime("yyyy年MM月"));
        timePickerShow(mTvDate);
    }

    @Override
    protected void initAction() {
        mRecordAdapter.setListener(new ISimpleInterfaceInt() {
            @Override
            public void clickListener(int tag) {
                deleteBPHistory(mUser.getId().equals(Constant.USERID) ? "0"
                        : mUser.getId(), mDataBeanList.get(tag).getMeasuretime() + "", tag);
            }
        });

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                queryBloodPressureData(mFromTime + "", mToTime + "", mUser.getId(), true);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (lastTime == 0) {
//                    lastTime = TimeUtil.getCurrentstamp();
//                }
//                queryRecentData(TimeUtil.getTimesMonthmorning(0) + "", TimeUtil.getCurrentstamp() + "", mUser.getId(), true);

            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_date, R.id.ctv_choice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_date:
            case R.id.ctv_choice:
                mCtvChoice.toggle();
                showYearMonthPopupWindow.showPopupWindow();
                break;
        }
    }

    private void timePickerShow(final TextView editText) {

        showYearMonthPopupWindow = new ShowYearMonthPopupWindow(this, tlTitleLayout);
        showYearMonthPopupWindow.setShowTimePopupWindowInterface(new ShowYearMonthPopupWindow.ShowTimePopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {
                mCtvChoice.toggle();
            }

            @Override
            public void rightButtonClick(String startTime, String endTime) {

                int year = Integer.parseInt(startTime);
                int month = Integer.parseInt(endTime);

                mFromTime = TimeUtil.getTargetTimeStamp(year, month, 1, 0, 0, 0);
                int daysOfMonth = TimeUtil.getDaysByYearMonth(year, month);
                mToTime = TimeUtil.getTargetTimeStamp(year, month, daysOfMonth, 23, 59, 59);
                mCtvChoice.toggle();
                editText.setText(startTime + getString(R.string.pick_year) + endTime + getString(R.string.pick_month));
                pullToRefreshListView.setRefreshing();
            }
        });

    }

    private void queryBloodPressureData(String fromTime, String toTime, String userId, final boolean isClearData) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime,
                HealthController.HEATHY_PERSURE, userId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        PressureResult result = GsonUtil.getObject(json, PressureResult.class);
                        if (result.getRet() != 0) {
                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg()) ? getString(R.string.text_query_fail) : result.getMsg());
                            return;
                        }
                        mHandler.sendEmptyMessage(0);
                        List<PressureResult.PressureBean> lists = result.getData();
                        if (isClearData) {
                            mDataBeanList.clear();
                        }
                        if (lists == null) {
                            return;
                        }

                        mDataBeanList.addAll(lists);
                        Collections.reverse(mDataBeanList);
                        mRecordAdapter.setDatas(mDataBeanList);
//                        mHandler.sendEmptyMessageDelayed(0, 0);
                    }

                    @Override
                    public void onFailed(String json) {
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                });
    }

    private void queryRecentData(String fromTime, String userId, final boolean isClearData) {
        HealthController.getInstance().queryRecentHealth(this, fromTime, HealthController.HEATHY_PERSURE, "7", userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                PressureResult result = GsonUtil.getObject(json, PressureResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg())
                            ? getString(R.string.toast_query_failure)
                            : result.getMsg());
                    return;
                }
                mHandler.sendEmptyMessage(0);
                List<PressureResult.PressureBean> lists = result.getData();

                if (lists == null) {
                    return;
                }
                if (isClearData) {
                    mDataBeanList.clear();
                }
                mDataBeanList.addAll(lists);
                mRecordAdapter.setDatas(mDataBeanList);
                lastTime = mDataBeanList.get(mDataBeanList.size() - 1).getMeasuretime();
                mHandler.sendEmptyMessageDelayed(0, 0);
            }

            @Override
            public void onFailed(String json) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (pullToRefreshListView != null)
                        pullToRefreshListView.onRefreshComplete();
                    break;
                case 1:
                    if (pullToRefreshListView != null) {
                        pullToRefreshListView.onRefreshComplete();
                    }
                    break;
            }
        }
    };

    private void deleteBPHistory(String familyId, final String measuretime, final int position) {
        HealthController.getInstance().deletePressure(this, familyId, measuretime,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        Loger.d(json);
                        BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                        if (result.getRet() == 0) {
                            mHandler.sendEmptyMessage(0);
                            mDataBeanList.remove(position);
                            mRecordAdapter.setDatas(mDataBeanList);
                        } else {
                            toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg())
                                    ? getString(R.string.toast_query_failure)
                                    : result.getMsg());
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d(json);
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus(getString(R.string.toast_delete_failure));
                        }
                    }
                });

    }

}
