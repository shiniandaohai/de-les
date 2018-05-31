
package com.boer.delos.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.InformationCenterListeningActivity;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.adapter.HistoricalAlarmsAdapter;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.model.AlarmInfo;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.ToastUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricalAlarmsFragment extends LazyFragment {

    private PullToRefreshListView mPullRefreshListView;
    private HistoricalAlarmsAdapter adapter;

    private User user;
    private List<AlarmInfo> alarms = new ArrayList<>();

    //刷新数据的页数
    private int dataPage = 0;
    //是否显示加载对话框，如果是上拉下拉列表加载数据，则不显示加载对话框
    private boolean isShowLoadDialog = true;
    //每页显示数据的条数
    private int pageSize = 10;
    //临时保存刷新数据前后数据的条数，如果前后数据条数一致，则说明没有新数据，关闭上拉刷新功能
    private int tmpDataSize = 0;
    private int tmpPosition = -1;//临时变量，记录点击的筛选条件的位置
    private static boolean isloadingData = false;//是否正在加载数据，防止在调用数据的同时重复调用方法
    private ListView actualListView;

    //接口需要的参数
    private String dateString = "";//时间
    private String hostID = "";//主机
    private String type = "";//报警类型
    private ToastUtils toastUtils;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private static final int FAILED = 400;

    public HistoricalAlarmsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toastUtils = ((BaseActivity) getActivity()).toastUtils;
        View view = inflater.inflate(R.layout.fragment_historical_alarms, container, false);

        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lvAlarm);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText("没有查询到相应数据");
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        textView.setVisibility(View.GONE);
        ((ViewGroup) mPullRefreshListView.getParent()).addView(textView);
        mPullRefreshListView.setEmptyView(textView);

        final ILoadingLayout startLabels = mPullRefreshListView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        //设置默认值
        Date date = new Date();
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        dateString = myFmt.format(date);

        tmpDataSize = alarms.size();
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                isShowLoadDialog = false;
                // Do work to refresh the list here.
                // new GetDataTask().execute();
                dataPage = 0;
                isShowLoadDialog = false;
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

                getAlarmInformation(dateString, hostID, type);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                isShowLoadDialog = false;
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getAlarmInformation(dateString, hostID, type);
            }
        });
        actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //减去头文件位置，有头文件，position从1开始
                position = position - actualListView.getHeaderViewsCount();

            }
        });
        adapter = new HistoricalAlarmsAdapter(getActivity(), alarms, false);
        this.mPullRefreshListView.setAdapter(adapter);
        isPrepared = true;
        lazyLoad();

        isShowLoadDialog = false;
        dataPage = 0;
        isShowLoadDialog = false;
        getAlarmInformation(dateString, hostID, type);
        return view;
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        if(alarms.size()>0){
            ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(true);
        }
        else{
            ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(false);
        }

        //有网进入刷新
        if (NetUtil.checkNet(getActivity()) && alarms.size() == 0) {
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            mPullRefreshListView.setRefreshing(true);
        }
    }

    loadUiHandler myHandler = new loadUiHandler();

    private class loadUiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }


                    if(adapter.getCount()>0){
                        ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(true);
                    }
                    else{
                        ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(false);
                    }

                    // Call onRefreshComplete when the list has been refreshed.
                    if (mPullRefreshListView!= null)
                    mPullRefreshListView.onRefreshComplete();
                    break;
                case FAILED:
                    if (mPullRefreshListView != null)
                        mPullRefreshListView.onRefreshComplete();
                    break;
            }

        }
    }

    private void getAlarmInformation(String date, String host, String type) {
        if (!NetUtil.checkNet(getContext())) { //没网不进入
            myHandler.sendEmptyMessageDelayed(FAILED, 1000);
        }
        AlarmController.getInstance().getAlarm(getActivity(), host, date, pageSize + "", dataPage + "", type, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                L.e("getAlarmInformation_Json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {

                    if (dataPage == 0) {//第一页清空数据
                        alarms.clear();
                        tmpDataSize = 0;
                    }
                    alarms.addAll(JsonUtil.parseDataList(Json, AlarmInfo.class, "alarms"));
                    //新加数据之前的ckheckBox是否选中数值不变，将新加数据设置为默认值false
                    for (int i = adapter.getIsSelected().size(); i < alarms.size(); i++) {
                        adapter.getIsSelected().put(i, false);
                    }
                    for (int i = adapter.getAlarmIDSelected().size(); i < alarms.size(); i++) {
                        adapter.getAlarmIDSelected().put(i, "");
                    }



                    //如果获得的数据的条数是pageSize的倍数，说明没有到达最后一页,继续刷新
                    if (alarms.size() % pageSize == 0) {
                        //防止最后一页正好是pageSize的倍数，则根据刷新后有没有新数据来判断是否关闭上拉刷新功能
                        //保存刷新前后的数据
                        if (tmpDataSize != alarms.size()) {
                            tmpDataSize = alarms.size();
                            dataPage++;
                        } else {//如果刷新前后的数据条数一致，说明刷新不出新数据，则关闭上拉刷新功能
                            mPullRefreshListView.onRefreshComplete();
                            mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    } else {//数据不是pageSize的倍数，说明已到最后一页
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
                Message message = new Message();
                message.what = 0;
                myHandler.sendMessage(message);
            }

            @Override
            public void onFailed(String json) {
                if (myHandler != null)
                    myHandler.sendEmptyMessageDelayed(FAILED, 1000 * 2);
            }
        });
    }

    public void refreshData(String mDate, String mHostid, String mType) {
        dateString = mDate;
        hostID = mHostid;
        type = mType;
        dataPage = 0;
        toastUtils.showProgress("加载数据...");
        getAlarmInformation(dateString, hostID, type);
    }

    //得到数据
    public List getDatas() {
        return alarms;
    }

}

