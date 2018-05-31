package com.boer.delos.activity.healthylife.weight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.adapter.WeightHistoryAdapter;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author PengJiYang
 * @Description:体重记录 界面
 * create at 2016/5/6 16:36
 */
public class WeightHistoryActivity extends BaseActivity {

    private TextView etStartDate;
    private TextView etEndDate;
    private TextView text_fail;
    private PullToRefreshExpandableListView lvUrinalysisHistory;

    private WeightHistoryAdapter adapter;
    private List<List<WeightBean>> mParentList;
    private ExpandableListView realExpandableListView;
    private static final String RECENT_DAYS = "7"; //默认查询的天数
    private long lastTime = 0; // 最后一条时间
    private TimePickerView timePickerView;
    /*查询区间记录用*/
    private long fromTime = 0;
    private long toTime = 0;
    private ArrayList<String> mTimes;
    private ArrayList<Integer> mStampAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urinalysis_history);
        initView();

        mParentList = new ArrayList<>();
        mTimes = new ArrayList<>();
        mStampAll = new ArrayList<>();

        initData(TimeUtil.getCurrentstamp() + "",
                HealthController.HEATHY_WEIGHT, RECENT_DAYS, Constant.USERID, true);
        initListener();
    }

    private void initView() {
        initTopBar(R.string.text_weight_history, null, true, false);

        lvUrinalysisHistory = (PullToRefreshExpandableListView) findViewById(R.id.exListUrinalysisHistory);
        etEndDate = (TextView) findViewById(R.id.etEndDate);
        etStartDate = (TextView) findViewById(R.id.etStartDate);
        text_fail = (TextView) findViewById(R.id.text_fail);
        text_fail.setVisibility(View.GONE);

        lvUrinalysisHistory.setMode(PullToRefreshBase.Mode.BOTH);

        adapter = new WeightHistoryAdapter(this, mParentList);
        realExpandableListView = lvUrinalysisHistory.getRefreshableView();
        realExpandableListView.setAdapter(adapter);

        etStartDate.setInputType(InputType.TYPE_NULL);
        etEndDate.setInputType(InputType.TYPE_NULL);
    }

    private void initListener() {

        adapter.setListener(new WeightHistoryAdapter.OnDeleteDataClickListener() {
            @Override
            public void deleteData(WeightBean data, int groupPos, int childPos) {
                //TODO 删除数据
//                ToastHelper.showShortMsg("呵呵");
                String familyId = "0"; //默认是自己
                deleteWeightHistory(familyId, data.getMeasuretime() + "", groupPos, childPos);
            }
        });
        realExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                ToastHelper.showShortMsg("嘿嘿");
                Intent intent = new Intent(WeightHistoryActivity.this, CurrentWeightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("weight", mParentList.get(groupPosition).get(childPosition));

                intent.putExtras(bundle);
                startActivity(intent);

                return true;
            }
        });
        lvUrinalysisHistory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                initData(TimeUtil.getCurrentstamp() + "", HealthController.HEATHY_WEIGHT, RECENT_DAYS, Constant.USERID, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                if (lastTime == 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    return;
                }
                initData(lastTime + "", HealthController.HEATHY_WEIGHT, RECENT_DAYS, Constant.USERID, false);

            }
        });

        //日期选择器
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerShow(etStartDate);
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerShow(etEndDate);
            }
        });


    }

    private void timePickerShow(final TextView editText) {
        //时间选择器
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);
        timePickerView.setRange(1900, 2100);
        timePickerView.setTime(new Date());

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date currentDate = new Date();
                if (date.after(currentDate)) {//如果选中时间比现在时间靠后，提示
                    ToastHelper.showShortMsg("所选时间不能超过当前时间");
                    return;
                }
                long time = TimeUtil.getTargetTimeStamp(date);
                String showTime = TimeUtil.formatStamp2Time(time, "yy-MM-dd");
                editText.setText(showTime);

                if (editText.getId() == etStartDate.getId()) {
                    fromTime = time;
                } else {
                    toTime = time + 60 * 60 * 24;
                }
                if (toTime != 0 && fromTime != 0) {
                    if (fromTime <= toTime) {
                        lastTime = 0;
                        queryTime2TimeUrineData(fromTime + "", toTime + "",
                                Constant.USERID, HealthController.HEATHY_URINE);
                    }
                }

            }
        });
        timePickerView.show();
    }

    private void deleteWeightHistory(String familayId, final String measuretime, final int groupPos, final int childPos) {
        HealthController.getInstance().deleteWeight(this, familayId, measuretime, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    mParentList.remove(mParentList.get(groupPos).get(childPos));
                    adapter.setData(mParentList);
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        toastUtils.showErrorWithStatus("删除失败");
                    } else toastUtils.showErrorWithStatus(result.getMsg());
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                if (toastUtils != null) {
                    toastUtils.showErrorWithStatus("删除失败");
                }
            }
        });
    }

    /**
     * 查询历史记录
     */
    private void initData(String fromTime,
                          String healthyType, final String recent, String userId, final boolean isClear) {
        HealthController.getInstance().queryRecentHealth(this,
                fromTime, healthyType, recent, userId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        Loger.d(json);
                        try {
                            WeightResult result = GsonUtil.getObject(json, WeightResult.class);

                            if (result.getRet() != 0) {
                                if (StringUtil.isEmpty(result.getMsg())) {
                                    mHandler.sendEmptyMessageDelayed(1, 1000);
                                    toastUtils.showErrorWithStatus("体重查询异常");
                                    return;
                                }
                                toastUtils.showErrorWithStatus(result.getMsg());
                                return;
                            }
                            dealWithData(result.getData(), isClear);

                            mHandler.sendEmptyMessage(0);

                        } catch (Exception e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            Loger.d("体重 detail空");
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d(json);
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                });

    }

    /**
     * 查询目标时间间的数据
     *
     * @param fromTime
     * @param toTime
     * @param userId
     * @param healthyType
     */
    private void queryTime2TimeUrineData(String fromTime, String toTime, String userId, String healthyType) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime, healthyType, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    WeightResult result = GsonUtil.getObject(json, WeightResult.class);
                    if (result.getRet() != 0) {
                        if (StringUtil.isEmpty(result.getMsg())) {
                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            toastUtils.showErrorWithStatus("尿检查询异常");
                            return;
                        }
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    if (mParentList == null)
                        mParentList = new ArrayList<>();
                    mParentList.clear();
                    dealWithData(result.getData(), true);

                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    Loger.d("尿检 detail空");
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    private void dealWithData(List<WeightBean> list, boolean isClear) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (isClear) {
            lastTime = 0;
            mParentList.clear();
            mTimes.clear();
        }

        List<WeightBean> childList = null;
        if (mParentList == null) mParentList = new ArrayList<>();
        if (mTimes == null) mTimes = new ArrayList<>();
        if (mStampAll == null) mStampAll = new ArrayList<>();

        for (WeightBean bean : list) {
            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy-MM-dd");
            if (lastTime != 0
                    && mStampAll.contains(bean.getMeasuretime())
                    && bean.getMeasuretime() >= lastTime) {
                continue;
            }

            mStampAll.add(bean.getMeasuretime());
            lastTime = bean.getMeasuretime();

            int index = mTimes.indexOf(time);
            if (index != -1) {
                childList = mParentList.get(index);


                childList.add(bean);
            } else {
                mTimes.add(time);
                childList = new ArrayList<>();

                childList.add(bean);
                mParentList.add(childList);
            }
        }
         /*保存最后一条时间*/
//        lastTime = list.get(list.size() - 1).getMeasuretime();

//        Loger.d(mParentList.size() + "");
    }

    /**
     * 处理上啦加载
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //成功
                    text_fail.setVisibility(View.GONE);
                    break;
                case 1: // 失败
                    if (mParentList.size() == 0) {
                        text_fail.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            if (adapter != null)
                adapter.setData(mParentList);
            expandAllitem();
            if (lvUrinalysisHistory.isRefreshing())
                lvUrinalysisHistory.onRefreshComplete();
        }
    };

    private void expandAllitem() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                realExpandableListView.expandGroup(i);
            }
        }
    }
}
