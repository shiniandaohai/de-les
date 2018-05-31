package com.boer.delos.activity.link;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.LinkListAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Link;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeActionResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.L;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 联动管理界面
 * create at 2016/4/12 9:40
 */
public class LinkManageListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private View view;
    private com.zhy.android.percent.support.PercentRelativeLayout llLinkPlan;
    private android.widget.ListView lvLinkList;
    private LinkListAdapter adapter;
    private List<Link> list = new ArrayList<>();
    private String title;
    private BroadcastReceiver mModeUpdateReceiver;
    List<Map<String, Object>> timerShow; //显示定时的图标
    private ModeAct modeAct;
    private Map<Integer, Boolean> flagMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_link_manage, null);
        setContentView(view);

        initView();
        initData();
        initListener();

        mModeUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateMode();
            }
        };
        registerReceiver(mModeUpdateReceiver, new IntentFilter(Constant.ACTION_GLOBAL_MODE_UPDATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMode();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mModeUpdateReceiver);
        super.onDestroy();
    }

    private void initView() {
        initTopBar(R.string.link_manage_limit, null, true, false);
        this.lvLinkList = (ListView) findViewById(R.id.lvLinkList);
        this.llLinkPlan = (PercentRelativeLayout) findViewById(R.id.llLinkPlan);
        this.llLinkPlan.setOnClickListener(this);
    }

    private void initData() {
        adapter = new LinkListAdapter(LinkManageListeningActivity.this);
        adapter.setDatas(list);
        timerShow = new ArrayList<>();
        lvLinkList.setAdapter(adapter);
        updateMode();
    }

    /**
     * 更新模式信息
     */
    private void updateMode() {
        List<Link> modes = Constant.GLOBAL_MODE;
        if (modes.size() > 0) {
            list.clear();
            list.addAll(modes);
            adapter.setDatas(list);

            settingTimerShow(list);
        }
    }

    private void initListener() {
        lvLinkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Link link = list.get(position);
                Intent intent = new Intent(LinkManageListeningActivity.this, LinkModelListeningActivity.class);
                intent.putExtra("ActivityName", Constant.MODE_SETTING_LINKMODE_GLOBAL);
                intent.putExtra("mode", link);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, LinkPlanListeningActivity.class));
//        finish();
    }

    private void settingTimerShow(List<Link> datas) {
        if (flagMap == null) flagMap = new HashMap<>();
        flagMap.clear();
        for (int i = 0; i < datas.size(); i++) {
            String name = datas.get(i).getName();
            requestWithRoomModel(name, null, i);
        }

    }

    /**
     * 查询模式
     *
     * @param name
     * @param modeId
     */
    private void requestWithRoomModel(String name, String modeId, final int position) {

        LinkManageController.getInstance().showRoomModel(this, name, modeId,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            ModeActionResult result = new Gson().fromJson(Json, ModeActionResult.class);
                            if (result.getRet() != 0) {
                                return;
                            }
                            modeAct = result.getResponse();
                            //判断是否有定时任务
                            if (modeAct.getTimeTask() != null && modeAct.getTimeTask().getOn() != null) {
                                if ("on".equals(modeAct.getTimeTask().getOn())) {
                                    flagMap.put(position, true);
                                    return;
                                } else {
                                    flagMap.put(position, false);
                                }
                            } else
                                flagMap.put(position, false);

                            if (flagMap.size() == list.size()) {
                                adapter.setFlagMap(flagMap);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        L.d(Json);
                    }
                });
    }


}
