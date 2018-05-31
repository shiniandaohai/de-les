package com.boer.delos.activity.healthylife.weight;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.adapter.WeigthRecordAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.User;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.ShowYearMonthPopupWindow;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/20 0020 13:16
 * @Modify:
 * @ModifyDate:
 */


public class WeightRecordActivity extends CommonBaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.ctv_choice)
    CheckedTextView mCtvChoice;
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView mPullToRefreshListView;


    private ListView mLvRecord;

    private WeigthRecordAdapter mRecordAdapter;
    private List<WeightBean> mDataBeanList;
    private User mUser;
    private long lastTime = 0;
    private TimePickerView timePickerView;


    private long mFromTime = 0;
    private long mToTime = 0;
    private ShowYearMonthPopupWindow showYearMonthPopupWindow;


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

    @Override
    protected void initData() {
        mLvRecord = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mDataBeanList = new ArrayList<>();
        mRecordAdapter = new WeigthRecordAdapter(this, mDataBeanList,
                R.layout.item_weight_history);

        mLvRecord.setAdapter(mRecordAdapter);
//        queryRecentData(TimeUtil.getCurrentstamp() + "", mUser.getId());
        mFromTime = TimeUtil.getTimesMonthmorning(0);
        mToTime = TimeUtil.getCurrentstamp();
        queryWeightHistoryDadta(mFromTime + "", mToTime + "", mUser.getId());
//        initTimerPicker();
        mTvDate.setText(TimeUtil.formatStamp2Time(TimeUtil.getCurrentstamp(),"yyyy年MM月"));
        timePickerShow(mTvDate);
    }

    @Override
    protected void initAction() {
        mRecordAdapter.setListener(new ISimpleInterfaceInt() {
            @Override
            public void clickListener(int tag) {
                deleteWeightHistory(mUser.getId().equals(Constant.USERID) ? "0"
                        : mUser.getId(), mDataBeanList.get(tag).getMeasuretime() + "", tag);
            }
        });

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                queryRecentData(TimeUtil.getCurrentstamp() + "", mUser.getId());

                queryWeightHistoryDadta(mFromTime + "", mToTime + "", mUser.getId());
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (lastTime == 0) {
//                    lastTime = TimeUtil.getCurrentstamp();
//                }
//                queryRecentData(lastTime + "", mUser.getId());
            }
        });

        tlTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFromTime = TimeUtil.getTimesMonthmorning(0);
                mToTime = TimeUtil.getCurrentstamp();
                queryWeightHistoryDadta(mFromTime + "", mToTime + "", mUser.getId());
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


//    private void initTimerPicker() {
//        //时间选择器
//        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
//        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
//        timePickerView.setCyclic(true);
//        timePickerView.setCancelable(true);
//        timePickerView.setRange(1900, 2100);
//        timePickerView.setTime(new Date());
//
//    }

//    private void timePickerShow(final TextView editText) {
//
//        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date) {
//                Date currentDate = new Date();
//                if (date.after(currentDate)) {
//                    ToastHelper.showShortMsg(getString(R.string.text_over_current_time));
//                    return;
//                }
//                mCtvChoice.toggle();
//                lastTime = TimeUtil.getTargetTimeStamp(date);
//                String showTime = TimeUtil.formatStamp2Time(lastTime, "yyyy/MM/dd");
//                editText.setText(showTime);
//            }
//        });
//        timePickerView.show();
//    }

    private void timePickerShow(final TextView editText) {

        showYearMonthPopupWindow = new ShowYearMonthPopupWindow(this, tlTitleLayout);
        showYearMonthPopupWindow.setShowTimePopupWindowInterface(new ShowYearMonthPopupWindow.ShowTimePopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String startTime, String endTime) {

                int year = Integer.parseInt(startTime);
                int month = Integer.parseInt(endTime);

                mFromTime = TimeUtil.getTargetTimeStamp(year, month, 1, 0, 0, 0);
                int daysOfMonth = TimeUtil.getDaysByYearMonth(year, month);
                mToTime = TimeUtil.getTargetTimeStamp(year, month, daysOfMonth, 23, 59, 59);

                editText.setText(startTime + getString(R.string.pick_year) + endTime + getString(R.string.pick_month));
                mPullToRefreshListView.setRefreshing();
//                queryWeightHistoryDadta(mFromTime + "", mToTime + "", );
                mCtvChoice.toggle();
            }
        });

    }
    private void queryRecentData(String fromTime, String userId) {
        HealthController.getInstance().queryRecentHealth(this, fromTime, HealthController.HEATHY_WEIGHT, "7", userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                WeightResult result = GsonUtil.getObject(json, WeightResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg())
                            ? getString(R.string.toast_query_failure)
                            : result.getMsg());
                    return;
                }
                mHandler.sendEmptyMessage(0);
                List<WeightBean> lists = result.getData();
                mDataBeanList.clear();
                if (lists == null) {
                    return;
                }
                mDataBeanList.addAll(lists);
                lastTime = mDataBeanList.get(mDataBeanList.size() - 1).getMeasuretime();
            }

            @Override
            public void onFailed(String json) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    /**
     * 查询历史数据
     *
     * @param fromtime
     * @param totime
     * @param userId
     */
    private void queryWeightHistoryDadta(String fromtime, String totime, String userId) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromtime, totime, HealthController.HEATHY_WEIGHT, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                WeightResult result = GsonUtil.getObject(json, WeightResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg())
                            ? getString(R.string.toast_query_failure)
                            : result.getMsg());
                    return;
                }
                mHandler.sendEmptyMessage(0);
                List<WeightBean> lists = result.getData();
                mDataBeanList.clear();
                if (lists == null) {
                    return;
                }
                mDataBeanList.addAll(lists);
                Collections.reverse(mDataBeanList);
                mRecordAdapter.setDatas(mDataBeanList);
//                lastTime = mDataBeanList.get(mDataBeanList.size() - 1).getMeasuretime();
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
                    if (mPullToRefreshListView != null)
                        mPullToRefreshListView.onRefreshComplete();
                    break;
                case 1:
                    if (mPullToRefreshListView != null) {
                        mPullToRefreshListView.onRefreshComplete();
                    }
                    break;
            }
        }
    };

    private void deleteWeightHistory(String familyId, final String measuretime, final int position) {
        HealthController.getInstance().deleteWeight(this, familyId, measuretime,
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
