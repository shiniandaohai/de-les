package com.boer.delos.activity.remotectler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.RemoteDeviceTypeAdapter;
import com.boer.delos.dao.DAO;
import com.boer.delos.dao.entity.DeviceTypeEntity;

import java.util.List;

/**
 * 设备类型- 空调，电视，。。。
 * Created by dell on 2016/7/14.
 */
public class DeviceTypeListeningActivity extends BaseListeningActivity {
    ListView mListView;
    RemoteDeviceTypeAdapter mAdapter;
    List<DeviceTypeEntity> mDatas;

    DAO mDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_device_type);

        initView();
    }


    private void initView() {
        initTopBar(R.string.remote_device_type_title, null, true, false);

        mListView = (ListView) findViewById(R.id.device_type_lv);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toastUtils.showInfoWithStatus(mDatas.get(position).getDevice_name());
                int deviceId = mDatas.get(position).getId();

                Bundle bundle = new Bundle();
                bundle.putInt(DeviceModeListeningActivity.BUNDLE_ACITON_DEVICE_ID, deviceId);
                Intent intent = new Intent(DeviceTypeListeningActivity.this, DeviceModeListeningActivity.class);
                intent.putExtras(bundle);
                DeviceTypeListeningActivity.this.startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //这里是第一次初始化的。 以后放到 APP 中吧。
        mDao = DAO.getSingleton(this);
        mDao.connectDb();
        mDatas = mDao.getDeviceType();

        if(mAdapter == null) {
            mAdapter =  new RemoteDeviceTypeAdapter(this, mDatas);
        }
        mListView.setAdapter(mAdapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //do not close here !!!
        //mDao.closeDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
