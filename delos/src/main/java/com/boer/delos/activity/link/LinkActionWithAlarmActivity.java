package com.boer.delos.activity.link;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.ModeTimerExListViewAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkPlan;
import com.boer.delos.model.LinkPlanAlarmAct;
import com.boer.delos.model.LinkPlanResult;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeActionResult;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.Loger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

//报警联动预案配置
public class LinkActionWithAlarmActivity extends CommonBaseActivity
        implements ExpandableListView.OnGroupExpandListener,
        ExpandableListView.OnGroupCollapseListener,
        SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.id_textViewMessage1)
    TextView mIdTextViewMessage1;
    @Bind(R.id.id_imageAdd1)
    ImageView mIdImageAdd1;
    //    @Bind(R.id.lvDeviceList1)
//    MyListView mLvDeviceList1;
    @Bind(R.id.expandableListView1)
    ExpandableListView expandableListView1;

    @Bind(R.id.id_textViewMessage2)
    TextView mIdTextViewMessage2;
    @Bind(R.id.id_imageAdd2)
    ImageView mIdImageAdd2;
    //    @Bind(R.id.lvDeviceList2)
//    MyListView mLvDeviceList2;
    @Bind(R.id.expandableListView2)
    ExpandableListView expandableListView2;

    @Bind(R.id.id_linear1)
    LinearLayout mIdLinear1;
    @Bind(R.id.id_linear2)
    LinearLayout mIdLinear2;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    //两个类型的列表分别对应的adapter和数据list
//    private LinkModelAdapter alarmAdapter;
//    private LinkModelAdapter recoverAdapter;
    private List<Map<String, Object>> alarmDeviceList = new ArrayList<>();
    private List<Map<String, Object>> recoverDeviceList = new ArrayList<>();
    private List<ModeDevice> alarmModeDeviceList = new ArrayList<>();
    private List<ModeDevice> recoverModeDeviceList = new ArrayList<>();

//    private List<ModeAct> actList = new ArrayList<>();

    private int fromActivity;
    private String roomModelId;
    private Intent intent;
    private Link mode;
    private Device device;
    private ModeAct modeAct;

    private LinkPlan plan;
    private LinkPlan recoverPlan;

    private String mDeviceType = "1"; //标识：报警时、恢复后
    private static final String[] gas = new String[]{"Ch4CO", "O2CO2", "Smoke", "Env"};

    // LinkActionExAdapter
    private ModeTimerExListViewAdapter mAlarmLinkAdapter;
    private ModeTimerExListViewAdapter mRecoverAdapter;


    @Override
    protected int initLayout() {
        return R.layout.activity_link_action_with_alarm;
    }

    @Override
    protected void initView() {
        fromActivity = getIntent().getIntExtra("ActivityName", -1);
        //设备模式配置
        DeviceRelate deviceRelate = (DeviceRelate) getIntent().getSerializableExtra("device");
        device = deviceRelate.getDeviceProp();
        tlTitleLayout.setTitle(device.getName() + getString(R.string.text_link_setting));
        tlTitleLayout.setRightText(getString(R.string.text_save));

        mDeviceType = getIntent().getStringExtra("type");
        expandableListView2.setGroupIndicator(null);
        expandableListView1.setGroupIndicator(null);
        if (!TextUtils.isEmpty(mDeviceType) && mDeviceType.equals("1")) {
            mIdLinear2.setVisibility(View.GONE);
//            mLvDeviceList2.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    protected void initData() {
        String deviceTypeName = Constant.getDeviceTypeNameWithType(device.getType());
        String alarmMessage1 = deviceTypeName + getString(R.string.text_alarm_ing);

        if (Arrays.asList(gas).contains(device.getType())) {
            alarmMessage1 = deviceTypeName + getString(R.string.text_gas_over_standard);

        }
        mIdTextViewMessage1.setText(alarmMessage1);

        String alarmMessage2 = deviceTypeName + getString(R.string.text_alarm_revert);

        if (Arrays.asList(gas).contains(device.getType())) {
            alarmMessage2 = deviceTypeName + getString(R.string.text_gas_reach_standard);

        }
        mIdTextViewMessage2.setText(alarmMessage2);
        mAlarmLinkAdapter = new ModeTimerExListViewAdapter(this, alarmDeviceList, null);
        mRecoverAdapter = new ModeTimerExListViewAdapter(this, recoverDeviceList, null);

        mAlarmLinkAdapter.setActivityType("LinkActionAlarmActivity");
        mRecoverAdapter.setActivityType("LinkActionAlarmActivity");

        expandableListView2.setAdapter(mRecoverAdapter);
        expandableListView1.setAdapter(mAlarmLinkAdapter);

        onRefresh();
    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();
        if (device.getType().equals(ConstantDeviceType.LOCK)) {
            //TODO
            modeAct.setDevicelist(alarmModeDeviceList);
            updateLockMode(modeAct);
            return;
        }
//        //更新恢复后的模式
        if (recoverPlan != null && TextUtils.isEmpty(recoverPlan.getAddr())) {
            updateRecoverMode();
        } else
            //更新全局模式
            updatePlanMode();

    }

    @Override
    protected void initAction() {
        expandableListView1.setOnGroupExpandListener(this);
        expandableListView2.setOnGroupExpandListener(this);
        expandableListView1.setOnGroupCollapseListener(this);
        expandableListView2.setOnGroupCollapseListener(this);


        //linkType添加一个额外数据，用来处理在ChooseDeviceActivity类返回的数据，根据linkType设置adapter1或者adapter2的值
        mIdImageAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinkActionWithAlarmActivity.this, ChooseDeviceActivity.class);
                intent.putExtra("ActivityName", fromActivity);
                intent.putExtra("modeDeviceList", (Serializable) alarmModeDeviceList);
                intent.putExtra("linkType", "1");
                startActivityForResult(intent, 100);
            }
        });

        mIdImageAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinkActionWithAlarmActivity.this, ChooseDeviceActivity.class);
                intent.putExtra("ActivityName", fromActivity);
                intent.putExtra("modeDeviceList", (Serializable) recoverModeDeviceList);
                intent.putExtra("linkType", "2");
                startActivityForResult(intent, 200);
            }
        });


        mIdLinear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                expandableListView1.setVisibility(View.VISIBLE);
                if (View.VISIBLE == expandableListView1.getVisibility()) {//如果列表不显示，点击显示
                    expandableListView1.setVisibility(View.GONE);
                    for (int i = 0; i < mAlarmLinkAdapter.getGroupCount(); i++) {
                        expandableListView1.collapseGroup(i);
                    }
                } else {
                    expandableListView1.setVisibility(View.VISIBLE);
                }
            }
        });

        mIdLinear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                expandableListView2.setVisibility(View.VISIBLE);
                if (View.VISIBLE == expandableListView2.getVisibility()) {//如果列表不显示，点击显示
                    expandableListView2.setVisibility(View.GONE);
                    for (int i = 0; i < mRecoverAdapter.getGroupCount(); i++) {
                        expandableListView1.collapseGroup(i);
                    }
                } else {
                    expandableListView2.setVisibility(View.VISIBLE);
                }
            }
        });
        mAlarmLinkAdapter.setmCloseListener(new ModeTimerExListViewAdapter.ICloseOrOpenListener() {
            @Override
            public void closeOrOpen(int position, boolean open) {
                if (expandableListView1 != null) {
                    try {
                        if (!open)
                            expandableListView1.collapseGroup(position);
                        else
                            expandableListView1.expandGroup(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mRecoverAdapter.setmCloseListener(new ModeTimerExListViewAdapter.ICloseOrOpenListener() {
            @Override
            public void closeOrOpen(int position, boolean open) {
                if (expandableListView2 != null) {
                    try {
                        if (!open)
                            expandableListView2.collapseGroup(position);
                        else
                            expandableListView2.expandGroup(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 请求联动模式设置
     */
    private void requestModeWithDevice() {
        toastUtils.showProgress(getString(R.string.toast_loading));
        LinkManageController.getInstance().planShow(this, device.getAddr(), device.getType(),
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            LinkPlanResult result = new Gson().fromJson(Json, LinkPlanResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                plan = result.getResponse();
                                recoverPlan = plan.getAlarmRecover();

                                alarmModeDeviceList = plan.getAlarmAct().getDevicelist();
                                recoverModeDeviceList = plan.getAlarmRecover().getDevicelist();
                                if (alarmModeDeviceList != null && alarmModeDeviceList.size() > 0) {
                                    alarmDeviceList.clear();
                                    alarmDeviceList.addAll(Constant.convertModeDeviceToList(alarmModeDeviceList));
//                                    alarmAdapter.notifyDataSetChanged();

                                    mAlarmLinkAdapter.notifyDataSetChanged();
//                                    setExpandableListViewHeight(expandableListView1);

                                    int groupCount = expandableListView1.getCount();
                                    for (int i=0; i<groupCount; i++)
                                    {
                                        expandableListView1.expandGroup(i);
                                    };

//                                    setListViewHeightBasedOnChildren(mLvDeviceList1);
                                }
                                if (recoverModeDeviceList != null && recoverModeDeviceList.size() > 0) {
                                    recoverDeviceList.clear();
                                    recoverDeviceList.addAll(Constant.convertModeDeviceToList(recoverModeDeviceList));
//                                    recoverAdapter.notifyDataSetChanged();
                                    mRecoverAdapter.notifyDataSetChanged();
//                                    setExpandableListViewHeight(expandableListView2);
                                    int groupCount = expandableListView2.getCount();
                                    for (int i=0; i<groupCount; i++)
                                    {
                                        expandableListView2.expandGroup(i);
                                    };
//                                    setListViewHeightBasedOnChildren(mLvDeviceList2);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    //TODO LOCK 专用
    private void requestModeLock() {
        LinkManageController.getInstance().showRoomModel(this, "开门模式", null, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                swipeRefreshLayout.setRefreshing(false);
                ModeActionResult result = new Gson().fromJson(json, ModeActionResult.class);
                if (result.getRet() != 0) {
                    return;
                }
                modeAct = result.getResponse();
                alarmModeDeviceList = modeAct.getDevicelist();
                alarmDeviceList.clear();
                alarmDeviceList.addAll(Constant.convertModeDeviceToList(modeAct.getDevicelist()));
                expandableListView1.requestFocusFromTouch();
                mAlarmLinkAdapter.setListData(alarmDeviceList);
                int groupCount = expandableListView1.getCount();
                for (int i=0; i<groupCount; i++)
                {
                    expandableListView1.expandGroup(i);
                };
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 100:
                List<Device> selectDeviceList1 = (List<Device>) data.getSerializableExtra("selectDeviceList");
                alarmModeDeviceList = Constant.modeDeviceListFromDeviceList(selectDeviceList1, alarmModeDeviceList);
                alarmDeviceList.clear();
                alarmDeviceList.addAll(Constant.convertModeDeviceToList(alarmModeDeviceList));
//                alarmAdapter.notifyDataSetChanged();
                mAlarmLinkAdapter.setListData(alarmDeviceList);
                setExpandableListViewHeight(expandableListView1);
//                setListViewHeightBasedOnChildren(mLvDeviceList1);

                break;
            case 200:
                List<Device> selectDeviceList2 = (List<Device>) data.getSerializableExtra("selectDeviceList");
                recoverModeDeviceList = Constant.modeDeviceListFromDeviceList(selectDeviceList2, recoverModeDeviceList);
                recoverDeviceList.clear();
                recoverDeviceList.addAll(Constant.convertModeDeviceToList(recoverModeDeviceList));
//                recoverAdapter.notifyDataSetChanged();

                mAlarmLinkAdapter.setListData(alarmDeviceList);
                setExpandableListViewHeight(expandableListView2);

//                setListViewHeightBasedOnChildren(mLvDeviceList2);
                break;
        }
    }

    /**
     * 更新联动配置
     */
    private void updatePlanMode() {
//        List<ModeDevice> deviceList = Constant.deviceStatusHandlerWithModeDeviceList(alarmModeDeviceList, alarmDeviceList);
        if (plan.getAlarmAct() == null) {
            LinkPlanAlarmAct act = new LinkPlanAlarmAct();
            plan.setAlarmAct(act);
        }
        if (TextUtils.isEmpty(recoverPlan.getAddr()))
            recoverPlan.setAddr(device.getAddr() + "_recover");
        recoverPlan.setDevicelist(recoverModeDeviceList);
        plan.setAlarmRecover(recoverPlan);
        plan.getAlarmAct().setDevicelist(alarmModeDeviceList);

//        String json = new Gson().Object2Json(plan);
//        L.e(json);
        LinkManageController.getInstance().planUpdate(this, plan,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                                return;
                            }
                            toastUtils.showSuccessWithStatus(getString(R.string.edit_success));
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    /**
     * 更新报警恢复后的模式
     */
    private void updateRecoverMode() {
        if (TextUtils.isEmpty(recoverPlan.getAddr()))
            recoverPlan.setAddr(device.getAddr() + "_recover");
        recoverPlan.setDevicelist(recoverModeDeviceList);
//        recoverPlan.setDeviceList(null);

        LinkManageController.getInstance().planUpdate(this, recoverPlan,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Log.d("恢复后", Json);
                        try {
                            JSONObject jsonObject = new JSONObject(Json);
                            int ret = jsonObject.getInt("ret");
                            if (ret == 0) {
                                jsonObject = jsonObject.getJSONObject("response");
                                long timestamp = jsonObject.getLong("timestamp");
                                int modeId = jsonObject.getInt("modeId");
                                recoverPlan.setModeId(modeId);
                                recoverPlan.setTimestamp(timestamp);

                                updatePlanMode();

                            } else {
                                toastUtils.showErrorWithStatus(getString(R.string.save_fail));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    /**
     * 更新指纹锁模式配置
     *
     * @param modeAct
     */
    private void updateLockMode(ModeAct modeAct) {
        LinkManageController.getInstance().updateRoomMode(this, modeAct, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                ModeActionResult result = new Gson().fromJson(json, ModeActionResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(getString(R.string.save_fail));
                    return;
                }
                toastUtils.showSuccessWithStatus(getString(R.string.edit_success));
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        finish();
                    }
                };
                mHandler.sendEmptyMessageDelayed(0, 1000 * 2);
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(getString(R.string.save_fail));
            }
        });
    }

    /**
     * expandableListView
     *
     * @param expandableListView
     */
    private void setExpandableListViewHeight(ExpandableListView expandableListView) {
        if (expandableListView == null) return;
        ExpandableListAdapter listAdapter = expandableListView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            Log.d("计算高度", listAdapter.getChildrenCount(i) + " ");
            totalHeight += getResources().getDimension(R.dimen.item_ex_group);
            totalHeight += expandableListView.isGroupExpanded(i)
                    ? getResources().getDimension(R.dimen.item_ex_child) : 0;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = (int) (totalHeight +
                (getResources().getDimension(R.dimen.item_ex_split) * (listAdapter.getGroupCount() - 1)));

        Log.d("高度计算 ", "params.height " + params.height);
        expandableListView.setLayoutParams(params);

    }

    @Override
    public void onGroupExpand(int i) {
        setExpandableListViewHeight(expandableListView1);
        setExpandableListViewHeight(expandableListView2);
    }

    @Override
    public void onGroupCollapse(int i) {
        setExpandableListViewHeight(expandableListView1);
        setExpandableListViewHeight(expandableListView2);
    }

    @Override
    public void onRefresh() {
        if (device.getType().equals(ConstantDeviceType.LOCK)) {
            requestModeLock();
        } else
            requestModeWithDevice();
    }

}
