package com.boer.delos.activity.settings;

import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.adapter.HomePageDevicesManageAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.recyclerHelper.OnStartDragListener;
import com.boer.delos.utils.recyclerHelper.SimpleItemTouchHelperCallback;
import com.boer.delos.view.TitleLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

/**
 * Created by gaolong on 2017/3/31.
 */
public class HomePageSettingActivity extends CommonBaseActivity implements OnStartDragListener ,TitleLayout.titleLayoutClick{
    @Bind(R.id.recycler_devices)
    RecyclerView recyclerDevices;

    private HomePageDevicesManageAdapter homePageDevicesManageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private SharedPreferences sp;
    private List<String> list = new ArrayList<String>();

    @Override
    protected int initLayout() {
        return R.layout.activity_home_page_setting;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.setting_home_page);
        tlTitleLayout.setRightText(R.string.save);
    }

    @Override
    protected void initData() {

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerDevices.setLayoutManager(linearLayoutManager);

        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String fragStr = sp.getString("fragmentlist","");
        list = GsonUtil.getList(fragStr,String.class);
        if(list!=null&&list.size()>0){
            //list.addAll(Constant.getFragmentList);
        }else {
            list = new ArrayList<String>();
            list.add(getString(R.string.setting_classify_devices));
            list.add(getString(R.string.setting_classify_functoin));
            list.add(getString(R.string.setting_classify_rooms));
        }
        homePageDevicesManageAdapter = new HomePageDevicesManageAdapter(this, list);
        recyclerDevices.setAdapter(homePageDevicesManageAdapter);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(homePageDevicesManageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerDevices);
        homePageDevicesManageAdapter.setItemTouchHelper(mItemTouchHelper);
    }

    @Override
    protected void initAction() {

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();
        List<String> strlist = new ArrayList<String>();
        for(int i=0;i<homePageDevicesManageAdapter.getList().size();i++){
            strlist.add(homePageDevicesManageAdapter.getList().get(i));
        }
        String str = new Gson().toJson(strlist);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fragmentlist",str);
        editor.commit();
        this.finish();
    }
}
