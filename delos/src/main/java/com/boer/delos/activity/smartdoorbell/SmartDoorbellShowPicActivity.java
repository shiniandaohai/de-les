package com.boer.delos.activity.smartdoorbell;

import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.MyAdapter;
import com.boer.delos.commen.CommonBaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.boer.delos.activity.smartdoorbell.imageloader.FileHelper.hasSDCard;

/**
 * Created by Administrator on 2018/1/22.
 */

public class SmartDoorbellShowPicActivity extends CommonBaseActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_show_pic;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerView.setAdapter(new MyAdapter((ArrayList<String>) getFilePath()));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.smoothScrollToPosition(getIntent().getIntExtra("pos",0));
    }

    public String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/";
        }
    }

    public String getCamPath() {
        String rootPath = getRootFilePath();
        String camPicPath = rootPath + "delos" + File.separator;
        return camPicPath;
    }

    private List<String> getFilePath() {
        List<String> paths = new ArrayList<>();
        File file = new File(getCamPath());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
                paths.add(tempFile.getAbsolutePath());
            }
        }
        return paths;
    }
}
