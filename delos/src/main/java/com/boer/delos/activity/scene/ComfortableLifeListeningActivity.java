package com.boer.delos.activity.scene;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.SceneManageAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhukang on 16/8/3.
 */
public class ComfortableLifeListeningActivity extends BaseListeningActivity {

    private GridView gvScene;
    private SceneManageAdapter adapter;
    private List<Room> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_scence_manage, null);
        setContentView(view);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        adapter = new SceneManageAdapter(this, datas, null);
        gvScene.setAdapter(adapter);
        if(Constant.GATEWAY == null){
            toastUtils.showInfoWithStatus("数据还未获取,请稍等");
            return;
        }
        datas.clear();
        datas.addAll(Constant.GATEWAY.getRoom());

//        L.i("AAAA" + datas.size());

        adapter.notifyDataSetChanged();
    }

    private void initView() {
        initTopBar("舒适生活",null,true,false);
        gvScene = (GridView)view.findViewById(R.id.gvSceneManage);
        gvScene.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Room room = (Room) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ComfortableLifeListeningActivity.this, SceneDisplayListeningActivity.class);
                intent.putExtra("RoomObject", room);
                startActivity(intent);
            }
        });
    }



}
