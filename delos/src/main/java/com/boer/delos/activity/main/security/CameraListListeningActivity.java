package com.boer.delos.activity.main.security;

import android.os.Bundle;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.CameraListAdapter;
import com.boer.delos.model.CameraList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "摄像头"列表界面
 * create at 2016/3/30 11:57
 *
 */
public class CameraListListeningActivity extends BaseListeningActivity {

    private android.widget.ListView lvCameraList;
    private CameraListAdapter adapter;
    private List<CameraList> cameras = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);

        initView();
    }

    private void initView() {
        initTopBar(R.string.camera_list_title, null, true, false);
        this.lvCameraList = (ListView) findViewById(R.id.lvCameraList);

        adapter = new CameraListAdapter(this);
        this.lvCameraList.setAdapter(adapter);
        getCameraList();
    }

    /**
     * 从服务器获取摄像头列表数据
     */
    private void getCameraList() {
        cameras.clear();
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        cameras.add(new CameraList(R.drawable.ic_gateway_bind_wifilink, "摄像头", "历史记录"));
        adapter.setDatas(cameras);
        adapter.notifyDataSetChanged();
    }
}
