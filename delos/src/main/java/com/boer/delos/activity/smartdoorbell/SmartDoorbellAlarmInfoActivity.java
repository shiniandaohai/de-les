package com.boer.delos.activity.smartdoorbell;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.AlarmListAdapter;
import com.boer.delos.activity.smartdoorbell.imageloader.AlarmMessageInfo;
import com.boer.delos.activity.smartdoorbell.imageloader.CommonEventBusEntity;
import com.boer.delos.activity.smartdoorbell.imageloader.EventBusEntity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.request.smartdoorbell.ICVSSUserModule;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.eques.icvss.api.ICVSSUserInstance;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2018/1/16.
 */

public class SmartDoorbellAlarmInfoActivity extends CommonBaseActivity {
    public final static int MSG_GET_ALARM_LIST = 1001;
    public final static int MSG_DEL_ALARM = 1002;
    public final static int MSG_DEL_ALL_ALARM = 1003;
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView pullToRefreshListView;
    @Bind(R.id.llEdit)
    LinearLayout llEdit;
    @Bind(R.id.tvAllChoice)
    TextView tvAllChoice;
    @Bind(R.id.tvDel)
    TextView tvDel;
    private boolean isEdit;
    private AlarmListAdapter alarmListAdapter;
    private ICVSSUserInstance icvss;
    private String bidTemp;
    int dayIndex = -1;
    private ListView listview;
    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_alarm_info;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("报警消息");
        tlTitleLayout.setRightText("编辑");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        icvss = ICVSSUserModule.getInstance(null).getIcvss();
        bidTemp = getIntent().getStringExtra("bid");
        alarmListAdapter=new AlarmListAdapter(this,icvss);
        listview = pullToRefreshListView.getRefreshableView();
        listview.setAdapter(alarmListAdapter);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                dayIndex=0;
                getAlarmList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                dayIndex--;
                getAlarmList();
            }
        });
        pullToRefreshListView.setRefreshing();
        dayIndex=0;
        getAlarmList();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isEdit) {
                    alarmListAdapter.setSelected(position-1);
                    tvDel.setEnabled(alarmListAdapter.isSelect());
                    if(alarmListAdapter.isSelect()){
                        tvAllChoice.setText("取消选择");
                    }
                    else{
                        tvAllChoice.setText("全选");
                    }
                } else {
                    //position-1
                    startActivity(new Intent(mContext,SmartDoorbellShowAlarmActivity.class).putExtra("alarmInfo",alarmListAdapter.getItem(position-1)));
                }
            }
        });
    }

    @Override
    public void rightViewClick() {
        isEdit = !isEdit;
        if (isEdit) {
            tlTitleLayout.setRightText("取消");
            llEdit.setVisibility(View.VISIBLE);
            alarmListAdapter.setIsEdit(true);
        } else {
            tlTitleLayout.setRightText("编辑");
            llEdit.setVisibility(View.GONE);
            alarmListAdapter.setIsEdit(false);
        }
    }

    Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_ALARM_LIST:
                    pullToRefreshListView.onRefreshComplete();
                    ArrayList<AlarmMessageInfo> infos = (ArrayList<AlarmMessageInfo>)msg.obj;
                    if(dayIndex==0){
                        alarmListAdapter.clearData();
                    }
                    alarmListAdapter.addData(infos);
                    break;
                case MSG_DEL_ALARM:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    if(jsonObject.optInt("code")==4000){
                        alarmListAdapter.delAlarm();
                        tvDel.setEnabled(alarmListAdapter.isSelect());
                        if(alarmListAdapter.isSelect()){
                            tvAllChoice.setText("取消选择");
                        }
                        else{
                            tvAllChoice.setText("全选");
                        }
                        ToastHelper.showShortMsg("删除成功");
                    }
                    else{
                        ToastHelper.showShortMsg("删除失败");
                    }
                    break;
                case MSG_DEL_ALL_ALARM:

                    break;
            }
        }
    };

    private long start;
    private long end;
    private void getAlarmList() {
        if(dayIndex==0){
            start = getTimesmorning(System.currentTimeMillis()).getTime();
            end = getTimesnight(System.currentTimeMillis()).getTime();
        }
        long time = dayIndex * (1000 * 60 * 60 * 24);
        start += time;
        end += time;
        EventBusEntity entity = new EventBusEntity();
        entity.setAction(EventBusEntity.GET_ALARM_LIST);
        entity.setStartTime(start);
        entity.setEndTime(end);
        entity.setBid(bidTemp);
        entity.setmHandler(mHanlder);
        EventBus.getDefault().post(entity);
    }

    public String format(Date date, String format) {
        String dateString = null;
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        try {
            dateString = df.format(date);
        } catch (Exception e) {
            dateString = df.format(new Date());
        }
        return dateString;
    }

    // 获得当天0点时间
    public static Date getTimesmorning(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 59);
        return cal.getTime();
    }

    @OnClick({R.id.tvAllChoice, R.id.tvDel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvAllChoice:
                if(tvAllChoice.getText().toString().equals("全选")){
                    alarmListAdapter.setAllSelectedOrNot(true);
                }
                else if(tvAllChoice.getText().toString().equals("取消选择")){
                    alarmListAdapter.setAllSelectedOrNot(false);
                }
                tvDel.setEnabled(alarmListAdapter.isSelect());
                if(alarmListAdapter.isSelect()){
                    tvAllChoice.setText("取消选择");
                }
                else{
                    tvAllChoice.setText("全选");
                }
                break;
            case R.id.tvDel:
                delPic();
                break;
        }
    }

    private CustomFragmentDialog deleteDialog;
    private void delPic(){
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        if(deleteDialog==null){
            deleteDialog = CustomFragmentDialog.newInstanse(
                    getString(R.string.text_prompt) ,
                    "确认删除选中的告警信息？", false);
        }
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                deleteDialog.dismiss();
                delAlarm();
            }
        });
    }

    private void delAlarm(){
        List<Integer> selecteds=alarmListAdapter.getSelected();
        String[] aids=new String[selecteds.size()];
        for(int i=0;i<selecteds.size();i++){
            aids[i]=alarmListAdapter.getItem(selecteds.get(i)).getAid();
        }
        CommonEventBusEntity entity = new CommonEventBusEntity();
        entity.setEventType(MSG_DEL_ALARM);
        entity.setmHandler(mHanlder);
        entity.setEventData(aids);
        EventBus.getDefault().post(entity);
    }

    private void delAllAlarm(){
        CommonEventBusEntity entity = new CommonEventBusEntity();
        entity.setEventType(MSG_DEL_ALL_ALARM);
        entity.setmHandler(mHanlder);
        EventBus.getDefault().post(entity);
    }
}
