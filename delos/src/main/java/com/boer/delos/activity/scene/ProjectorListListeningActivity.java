package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.ProjectorListAdapter;

/**
 * @author PengJiYang
 * @Description: "投影仪列表"界面
 * create at 2016/6/1 15:29
 */
public class ProjectorListListeningActivity extends BaseListeningActivity {

    private android.widget.ListView lvProjectorList;

    private ProjectorListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projector_list);

        initView();
    }

    private void initView() {
        initTopBar(getString(R.string.projector_list_title), null, true, false);
        this.lvProjectorList = (ListView) findViewById(R.id.lvProjectorList);
        adapter = new ProjectorListAdapter(this);
        this.lvProjectorList.setAdapter(adapter);
    }
}
