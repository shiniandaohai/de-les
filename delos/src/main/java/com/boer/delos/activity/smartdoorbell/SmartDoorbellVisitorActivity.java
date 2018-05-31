package com.boer.delos.activity.smartdoorbell;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.VisitorReceiveEventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity;
import com.boer.delos.adapter.smartdoorbell.DoorbellVisitorListAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_DEL_ALL_VISITOR;
import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_DEL_ONE_VISITOR;
import static com.boer.delos.activity.smartdoorbell.imageloader.VisitorSendEventBusEntity.EVENT_TYPE_GET_VISITOR_LIST;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SmartDoorbellVisitorActivity extends CommonBaseActivity{
    ListView listview;
    private DoorbellVisitorListAdapter mDoorbellVisitorListAdapter;
    private List<JSONObject> datas=new ArrayList<>();
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView pullToRefreshListView;
    private int dayIndex;
    private int delIndex=-1;
    private long curTime;
    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_visitor;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("访客记录");
        tlTitleLayout.setRightText("清除记录");
    }

    @Override
    protected void initData() {

    }

    private CustomFragmentDialog deleteDialog;
    @Override
    public void rightViewClick() {
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        if(deleteDialog==null){
            deleteDialog = CustomFragmentDialog.newInstanse(
                    getString(R.string.text_prompt) ,
                    "确定要删除全部访客记录吗？", false);
        }
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                deleteDialog.dismiss();
                delAllVisitorHistory();
                delIndex=-2;
            }
        });
    }

    @Override
    protected void initAction() {
        EventBus.getDefault().register(this);
        mDoorbellVisitorListAdapter=new DoorbellVisitorListAdapter(this,datas,R.layout.activity_door_bell_visitor_item);
        listview = pullToRefreshListView.getRefreshableView();
        listview.setAdapter(mDoorbellVisitorListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fid=datas.get(position-1).optString("fid");
                String bid=datas.get(position-1).optString("bid");
                startActivity(new Intent(mContext,SmartDoorbellShowVisitorActivity.class).
                        putExtra("fid",fid).
                        putExtra("bid",bid));
            }
        });
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                dayIndex=0;
                VisitorSendEventBusEntity entity=new VisitorSendEventBusEntity();
                entity.setEventType(EVENT_TYPE_GET_VISITOR_LIST);
                entity.setEventData(getStartEndTime(dayIndex));
                EventBus.getDefault().post(entity);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                dayIndex--;
                VisitorSendEventBusEntity entity=new VisitorSendEventBusEntity();
                entity.setEventType(EVENT_TYPE_GET_VISITOR_LIST);
                entity.setEventData(getStartEndTime(dayIndex));
                EventBus.getDefault().post(entity);
            }
        });
        pullToRefreshListView.setRefreshing();
        dayIndex=0;
        VisitorSendEventBusEntity entity=new VisitorSendEventBusEntity();
        entity.setEventType(EVENT_TYPE_GET_VISITOR_LIST);
        entity.setEventData(getStartEndTime(dayIndex));
        EventBus.getDefault().post(entity);

        mDoorbellVisitorListAdapter.setOnDelListener(new DoorbellVisitorListAdapter.OnDelListener() {
            @Override
            public void onDel(int position) {
                delIndex=position;
                delOneVisitorHistory(datas.get(position));
            }
        });
    }

    public void onEventMainThread(VisitorReceiveEventBusEntity entity){
        JSONObject json=(JSONObject)entity.getEventData();
        if(entity.getEventType()==EVENT_TYPE_GET_VISITOR_LIST){
            pullToRefreshListView.onRefreshComplete();
            JSONArray rings=json.optJSONArray("rings");
            if(dayIndex==0){
                datas.clear();
            }
            for(int i=0;i<rings.length();i++){
                try {
                    datas.add(rings.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mDoorbellVisitorListAdapter.notifyDataSetChanged();
        }
        else if(entity.getEventType()==(EVENT_TYPE_DEL_ONE_VISITOR|EVENT_TYPE_DEL_ALL_VISITOR)){
            int code=json.optInt("code");
            if(code==4000){
                ToastHelper.showShortMsg("删除成功");
                if(delIndex>=0){
                    datas.remove(delIndex);
                    mDoorbellVisitorListAdapter.notifyDataSetChanged();
                }
                else if(delIndex==-2){
                    datas.clear();
                    mDoorbellVisitorListAdapter.notifyDataSetChanged();
                }
            }
            else{
                ToastHelper.showShortMsg("删除失败");
            }
        }
    }

    private static long TIME_INTERVAL=1000 * 60 * 60 * 24;
    private long[] getStartEndTime(int dayIndex){
        if(dayIndex==0){
            curTime=System.currentTimeMillis();
        }
        long endTime = curTime+dayIndex*TIME_INTERVAL;
        long startTime=endTime-TIME_INTERVAL+dayIndex*TIME_INTERVAL;
        return new long[]{startTime,endTime};
    }

    private void delOneVisitorHistory(JSONObject jsonObject){
        VisitorSendEventBusEntity entity=new VisitorSendEventBusEntity();
        entity.setEventType(EVENT_TYPE_DEL_ONE_VISITOR);
        entity.setEventData(new String[]{jsonObject.optString("fid")});
        EventBus.getDefault().post(entity);
    }

    private void delAllVisitorHistory(){
        VisitorSendEventBusEntity entity=new VisitorSendEventBusEntity();
        entity.setEventType(EVENT_TYPE_DEL_ALL_VISITOR);
        EventBus.getDefault().post(entity);
    }
}
