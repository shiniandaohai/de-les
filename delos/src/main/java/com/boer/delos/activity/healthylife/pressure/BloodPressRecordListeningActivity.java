package com.boer.delos.activity.healthylife.pressure;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.adapter.BloodPressRecordexAdapter;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.PressureResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author PengJiYang
 * @Description:"血压记录" 界面
 * create at 2016/5/27 13:36
 */
public class BloodPressRecordListeningActivity extends BaseListeningActivity {

    private ImageView ivUserHead;
    private TextView tvName;
    private PercentLinearLayout llTop;
    private TextView etStartDate;
    private TextView etEndDate;
    private TextView text_fail;
    private PullToRefreshExpandableListView exBloodPressRecord;
    private ExpandableListView realExListView;

    private BloodPressRecordexAdapter mAdapter;
    private List<List<PressureResult.PressureBean>> mparentLists; //adapter源数据
    private List<PressureResult.PressureBean> mLists; //查询到的数据源
    private int lastTime = 0;
    private TimePickerView timePickerView;
    private long fromTime;
    private long toTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_press_record);

        initView();
        initData(TimeUtil.getCurrentstamp() + "", Constant.USERID, true);
        initListener();
    }

    private void initView() {
        initTopBar(R.string.blood_pressure_record, null, true, false);
        exBloodPressRecord = (PullToRefreshExpandableListView) findViewById(R.id.exBloodPressRecord);
        etEndDate = (TextView) findViewById(R.id.etEndDate);
        etStartDate = (TextView) findViewById(R.id.etStartDate);
        llTop = (PercentLinearLayout) findViewById(R.id.llTop);
        tvName = (TextView) findViewById(R.id.tvName);
        ivUserHead = (ImageView) findViewById(R.id.ivUserHead);
        text_fail = (TextView) findViewById(R.id.text_fail);

        mparentLists = new ArrayList<>();
        mLists = new ArrayList<>();
        mAdapter = new BloodPressRecordexAdapter(this, mparentLists);

        exBloodPressRecord.setMode(PullToRefreshBase.Mode.BOTH);
        realExListView = exBloodPressRecord.getRefreshableView();
        realExListView.setAdapter(mAdapter);
    }

    private void initListener() {
        exBloodPressRecord.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                initData(TimeUtil.getCurrentstamp() + "", Constant.USERID, true);
                lastTime = 0;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                initData(lastTime + "", Constant.USERID, false);
            }
        });


        mAdapter.setListener(new BloodPressRecordexAdapter.OnDeleteDataClickListener() {
            @Override
            public void deleteData(PressureResult.PressureBean data, int groupPos, int childPos) {
                //TODO 删除数据
//                ToastHelper.showShortMsg("呵呵");
                String familyId = "0"; //默认是自己

                deleteBPHistory(familyId, data.getMeasuretime() + "", groupPos, childPos);
            }
        });
//        realExListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                PressureResult.PressureBean bean = mparentLists.get(groupPosition).get(childPosition);
//                Intent intent = new Intent(getApplicationContext(), CurrentBloodPressListeningActivity.class);
//                intent.putExtra("PressureH", bean.getValueH());
//                intent.putExtra("PressureL", bean.getValueL());
//                intent.putExtra("PressureP", bean.getBpm());
//                intent.putExtra("PressureT", bean.getMeasuretime());
//                startActivity(intent);
//                return true;
//            }
//        });
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

    private void deleteBPHistory(String familyId, final String measuretime, final int groupPos, final int childPos) {
        HealthController.getInstance().deleteUrine(this, familyId, measuretime, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    mparentLists.remove(mparentLists.get(groupPos).get(childPos));
                    mAdapter.notifyDataSetChanged();
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
                        exBloodPressRecord.setRefreshing();
                        queryBloodPressureData(fromTime + "", toTime + "", Constant.USERID);
                    }
                }

            }
        });
        timePickerView.show();
    }

    private void queryBloodPressureData(String fromTime, String toTime, String userId) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime, HealthController.HEATHY_PERSURE, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                PressureResult result = GsonUtil.getObject(json, PressureResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg()) ? "未查询到历史记录" : result.getMsg());
                    return;
                }
                mLists = result.getData();
                dealWithData(mLists, true);
            }

            @Override
            public void onFailed(String json) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    private void initData(String fromTime, String userId, final boolean isClear) {
        HealthController.getInstance().queryRecentHealth(this, fromTime, HealthController.HEATHY_PERSURE, "7", userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                PressureResult result = GsonUtil.getObject(json, PressureResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg()) ? "未查询到历史记录" : result.getMsg());
                    return;
                }
                mLists = result.getData();
                dealWithData(mLists, isClear);

            }

            @Override
            public void onFailed(String json) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    private void dealWithData(List<PressureResult.PressureBean> list, boolean isClear) {

        if (list == null || list.size() == 0) {
            return;
        }
        if (isClear) {
            mparentLists.clear();
        }
        List<PressureResult.PressureBean> childList = null;
        if (mparentLists == null) mparentLists = new ArrayList<>();
        List<String> stamps = new ArrayList<>();
        for (PressureResult.PressureBean bean : list) {
            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy-MM-dd");
            if (lastTime != 0 && bean.getMeasuretime() >= lastTime) {
                continue;
            }
            int index = stamps.indexOf(time);
            if (index != -1) {
                childList = mparentLists.get(index);
                childList.add(bean);
            } else {
                stamps.add(time);
                childList = new ArrayList<>();
                childList.add(bean);
                mparentLists.add(childList);
            }
        }
            /*保存最后一条时间*/
        lastTime = list.get(list.size() - 1).getMeasuretime();
        mHandler.sendEmptyMessage(0);
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
                    if (mparentLists.size() == 0) {
                        text_fail.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            if (mAdapter != null)
                mAdapter.setData(mparentLists);
            expandAllitem();
            if (exBloodPressRecord.isRefreshing())
                exBloodPressRecord.onRefreshComplete();
        }
    };

    private void expandAllitem() {
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                realExListView.expandGroup(i);
            }
        }
    }

}
