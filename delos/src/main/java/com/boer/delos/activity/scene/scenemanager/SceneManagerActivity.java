package com.boer.delos.activity.scene.scenemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.activity.link.AlarmLinkPlanSettingActivity;
import com.boer.delos.adapter.SceneManagerAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkResult;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.RoomModeActionResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:场景管理界面
 * @CreateDate: 2017/4/8 0008 15:16
 * @Modify:
 * @ModifyDate:
 */
public class SceneManagerActivity extends CommonBaseActivity {

    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    private SceneManagerAdapter mManagerAdapter;
    private List<Link> mLinks;


    private String mFlag = "0";  // mFlag :  1：房间模式   0：场景模式
    private String mRoomId; // 房间模式下的roomId 否则null
    private List<ModeAct> mModeActList; // 房间的模式

    @Override
    protected int initLayout() {
        return R.layout.activity_scene_manager;
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (mFlag) {
            case "0":
                getGlobalMode();
                break;
            case "1":
                queryRoomMode(mRoomId);
                break;
            default:
                getGlobalMode();
                break;
        }

//        mPullToRefreshListView.onRefreshComplete();
//        mPullToRefreshListView.setRefreshing(true);
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.scene_manager));
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView = mPullToRefreshListView.getRefreshableView();

        // fromActivity  // 房间跳转到此 传递flag 1
        mFlag = getIntent().getStringExtra("flag");
        if (TextUtils.isEmpty(mFlag)) {
            mFlag = "0";
        }
        mRoomId = getIntent().getStringExtra("roomId");
    }

    @Override
    protected void initData() {
        mModeActList = new ArrayList<>();
        mLinks = new ArrayList<>();
        mManagerAdapter = new SceneManagerAdapter(this, mLinks, R.layout.item_scene_manager);
        mListView.setAdapter(mManagerAdapter);
//        getGlobalMode();

    }

    @Override
    protected void initAction() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                switch (mFlag) {
                    case "0":
                        getGlobalMode();
                        break;
                    case "1":
                        queryRoomMode(mRoomId);
                        break;
                    default:
                        getGlobalMode();
                        break;
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mFlag) {
                    case "0":
                        Link link = mLinks.get(position - 1);
                        if (!TextUtils.isEmpty(link.getFlag()) && link.getFlag().equals("alarmLinkPlan")) {
                            startActivity(new Intent(SceneManagerActivity.this,
                                    AlarmLinkPlanSettingActivity.class));
                            return;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("link", link);
                        Intent intent = new Intent(getBaseContext(), SceneTimerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case "1":
                        ModeAct modeAct = mModeActList.get(position - 1);
                        Bundle b = new Bundle();
                        b.putSerializable("room", modeAct);
                        b.putInt("type",1);
                        Intent intent2 = new Intent(getBaseContext(), SceneTimerActivity.class);
                        intent2.putExtras(b);
                        startActivity(intent2);

                        break;
                }

//                finish();
            }
        });
    }

    /**
     * 获取全局模式
     */
    public void getGlobalMode() {
        LinkManageController.getInstance().requestGlobalModes(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Loger.d(Json);
                    LinkResult result = GsonUtil.getObject(Json, LinkResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    if (result.getResponse() == null) {
                        return;
                    }
                    mLinks.clear();
                    mLinks.addAll(result.getResponse());
                    Link link = new Link();
                    link.setFlag("alarmLinkPlan");
                    mLinks.add(link);
                    mManagerAdapter.setDatas(mLinks);

                    mHadler.sendEmptyMessage(0);
                } catch (Exception e) {
                    L.e("getGlobalMode:" + e);
                    mHadler.sendEmptyMessageDelayed(0, 1000);
                }
            }

            @Override
            public void onFailed(String Json) {
                L.e("getGlobalMode:" + Json);
                if (mHadler != null)
                    mHadler.sendEmptyMessageDelayed(0, 1000);
            }
        });
    }

    Handler mHadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mPullToRefreshListView != null) {
                        mPullToRefreshListView.onRefreshComplete();
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    };

    /*****************房间模式*******************/

    private void queryRoomMode(String roomId) {
        RoomController.getInstance().showMode(this, null, roomId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                RoomModeActionResult result = new Gson().fromJson(json, RoomModeActionResult.class);

                mModeActList.clear();
                mModeActList.addAll(result.getResponse());

                addMode2FourMode(mModeActList);
                roomMode2GlobalMode(mModeActList);
                mHadler.sendEmptyMessage(0);
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                if (mHadler!=null)
                mHadler.sendEmptyMessageDelayed(0, 1000);
            }
        });

    }


    private void addMode2FourMode(List<ModeAct> mModeActList) {

//        if (mModeActList == null) {
        List<ModeAct> modeActList = new ArrayList<>();
//        }
        List<String> setialNo = new ArrayList<>();
        for (ModeAct modeAct : mModeActList) {
            setialNo.add(modeAct.getSerialNo());
        }
        for (int i = 0; i < 4; i++) {
            if (setialNo.contains(i + "")) {
                modeActList.add(mModeActList.get(setialNo.indexOf(i + "")));

                continue;
            }
            ModeAct modeAct = new ModeAct();
            modeAct.setSerialNo(i + "");
            modeAct.setRoomId(mRoomId);
            switch (i) {
                case 0:
                    modeAct.setName(getString(R.string.model_select_morning) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_morning));
                    break;
                case 1:
                    modeAct.setName(getString(R.string.model_select_sleep) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_sleep));
                    break;
                case 2:
                    modeAct.setName(getString(R.string.model_select_read) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_read));
                    break;
                case 3:
                    modeAct.setName(getString(R.string.model_select_recreation) + "_" + mRoomId);
                    modeAct.setTag(getString(R.string.model_select_recreation));
                    break;
            }
            modeActList.add(modeAct);

        }
        mModeActList.clear();
        mModeActList.addAll(modeActList);
    }

    private void roomMode2GlobalMode(List<ModeAct> mModeActList) {
        mLinks.clear();
        for (ModeAct modeAct : mModeActList) {

            Link link = new Link();
            link.setName(modeAct.getName().split("_")[0] + getString(R.string.model_select_model));
            link.setTag(modeAct.getTag());
            link.setModeId(TextUtils.isEmpty(modeAct.getModeId()) ? -1 : Integer.valueOf(modeAct.getModeId()));
            link.setFlag("room");

            if (modeAct.getTimeTask() != null && !TextUtils.isEmpty(modeAct.getTimeTask().getOn())) {
                if (modeAct.getTimeTask().getOn().equals("on")) {
                    link.setHasActiveTask(true);
                    //return;
                } else link.setHasActiveTask(false);
            } else
                link.setHasActiveTask(false);
            mLinks.add(link);
        }

        mManagerAdapter.setDatas(mLinks);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHadler.removeCallbacksAndMessages(null);
    }
}
