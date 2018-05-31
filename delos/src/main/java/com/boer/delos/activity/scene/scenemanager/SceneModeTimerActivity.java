package com.boer.delos.activity.scene.scenemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.adapter.WeekAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.EventCode;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.TimeTask;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.widget.WheelView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/8/18.
 */
public class SceneModeTimerActivity extends CommonBaseActivity implements View.OnClickListener {

    private static List<String> HOURS;
    private static List<String> MINUTES;
    private static String[] Timer;
    private WheelView wvMinute;
    private WheelView wmHour;
    private ListView listView;
    private static List<Map<String, Object>> WEEK_LIST;
    private TimeTask timeTask;
    private WeekAdapter adapter;
    private String modeId = "";

    @Override
    protected int initLayout() {
        return R.layout.activity_mode_timer;
    }

    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        timeTask = (TimeTask) bundle.getSerializable("task");
        modeId = bundle.getString("modeId");

        wmHour = (WheelView) findViewById(R.id.wvHour);
        wvMinute = (WheelView) findViewById(R.id.wvMinute);
        listView = (ListView) findViewById(R.id.listView);

        tlTitleLayout.setRightText(getString(R.string.text_certain));
    }

    protected void initData() {
        HOURS = getHours();
        MINUTES = getMinutes();
        Timer = getTimer();
        WEEK_LIST = getWeekList();
        //小时
        wmHour.setOffset(1);
        wmHour.setItems(HOURS);
        wmHour.setSeletion(HOURS.indexOf(Timer[0]));
        //分钟
        wvMinute.setOffset(1);
        wvMinute.setItems(MINUTES);

        wvMinute.setSeletion(MINUTES.indexOf(Timer[1]));
        adapter = new WeekAdapter(this, WEEK_LIST);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> map = (Map<String, Object>) adapterView.getItemAtPosition(i);
                boolean checked = (boolean) map.get("checked");
                if (checked) {
                    map.put("checked", false);
                } else {
                    map.put("checked", true);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        dealTimeInfo();
    }

    /**
     * 获取时间
     *
     * @return
     */
    private String[] getTimer() {
        String time = Constant.getTimeSetting(timeTask);
        if (StringUtil.isEmpty(time)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
            time = simpleDateFormat.format(new Date());
        }
        return time.split(":");
    }


    private void dealTimeInfo() {
        Intent intent = new Intent();
        String hour = wmHour.getSeletedItem();
        String minute = wvMinute.getSeletedItem();
        String time = hour + ":" + minute;
        Map<String, String> map = null;

        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < WEEK_LIST.size(); i++) {
            if ((boolean) WEEK_LIST.get(i).get("checked")) {
                map = new HashMap<>();
                map.put((String) WEEK_LIST.get(i).get("key"), time);
                mapList.add(map);
            }
        }
//        if (mapList.size() == 0) {
//            return;
//        }
        settingTimeTask(mapList, time);

    }

    /**
     * 设置定时任务
     *
     * @param mapList    多个的选项
     * @param tiggerTime 单个选项
     */
    private void settingTimeTask(List<Map<String, String>> mapList, String tiggerTime) {
        if (timeTask == null) {
            timeTask = new TimeTask();
            if (!TextUtils.isEmpty(modeId)) {
                timeTask.setModeId(Integer.valueOf(modeId));
            }
        }
        final TimeTask tempTimeTask = timeTask;

        String type = "";
        switch (mapList.size()) {
            case 0:
                type = "delay";
                //mapList.clearAll();
                timeTask.setRepeat(mapList);

                break;
            default:
                type = "repeat";
                tiggerTime = null;
                timeTask.setTriggerTime(tiggerTime);
                break;
        }
        timeTask.setType(type);

        if (tiggerTime != null) {
            tempTimeTask.setTriggerTime(tiggerTime);
        }
        if (mapList.size() != 0) {
            tempTimeTask.setRepeat(mapList);
        }

        Intent intent = new Intent();
        if (tempTimeTask != null)
            intent.putExtra("TIME_TASK", tempTimeTask);
        setResult(RESULT_OK, intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
        return;

    }

    @Override
    public void onClick(View view) {

    }


    /**
     * 获取小时
     *
     * @return
     */
    public List<String> getHours() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        return hours;
    }

    /**
     * 获取分钟
     *
     * @return
     */
    public List<String> getMinutes() {
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minutes.add(String.format("%02d", i));
        }
        return minutes;
    }

    /**
     * 获取星期字典
     *
     * @return
     */
    public List<Map<String, Object>> getWeekList() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", getString(R.string.monday));
        map.put("key", "0");
        map.put("checked", getChecked("0"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.tuesday));
        map.put("key", "1");
        map.put("checked", getChecked("1"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.wednesday));
        map.put("key", "2");
        map.put("checked", getChecked("2"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.thursday));
        map.put("key", "3");
        map.put("checked", getChecked("3"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.friday));
        map.put("key", "4");
        map.put("checked", getChecked("4"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.saturday));
        map.put("key", "5");
        map.put("checked", getChecked("5"));
        list.add(map);
        map = new HashMap<>();
        map.put("name", getString(R.string.sunday));
        map.put("key", "6");
        map.put("checked", getChecked("6"));
        list.add(map);
        return list;
    }

    /**
     * 是否选中
     *
     * @param key
     * @return
     */
    private boolean getChecked(String key) {
        if (timeTask == null)
            return false;
        if (timeTask.getRepeat() == null)
            return false;
        List<Map<String, String>> list = timeTask.getRepeat();
        for (Map<String, String> map : list) {
            String value = map.get(key);
            if (value != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}
