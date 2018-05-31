package com.boer.delos.activity.smartdoorbell;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.DoorbellPicAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.view.customDialog.CustomFragmentDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.boer.delos.activity.smartdoorbell.imageloader.FileHelper.hasSDCard;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SmartDoorbellPicActivity extends CommonBaseActivity {
    @Bind(R.id.gridview)
    GridView mGridView;
    @Bind(R.id.llEdit)
    LinearLayout llEdit;
    @Bind(R.id.tvAllChoice)
    TextView tvAllChoice;
    @Bind(R.id.tvDel)
    TextView tvDel;
    private DoorbellPicAdapter mDoorbellPicAdapter;
    private boolean isEdit;

    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_pic;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("图像信息");
        tlTitleLayout.setRightText("编辑");
    }

    @Override
    public void rightViewClick() {
        isEdit = !isEdit;
        if (isEdit) {
            tlTitleLayout.setRightText("取消");
            llEdit.setVisibility(View.VISIBLE);
        } else {
            tlTitleLayout.setRightText("编辑");
            llEdit.setVisibility(View.GONE);
            mDoorbellPicAdapter.setAllSelectedOrNot(false);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isEdit) {
                    mDoorbellPicAdapter.setSelected(position);
                    tvDel.setEnabled(mDoorbellPicAdapter.isSelect());
                    if(mDoorbellPicAdapter.isSelect()){
                        tvAllChoice.setText("取消选择");
                    }
                    else{
                        tvAllChoice.setText("全选");
                    }
                } else {
                    startActivity(new Intent(mContext,SmartDoorbellShowPicActivity.class).putExtra("pos",position));
                }
            }
        });
        mDoorbellPicAdapter=new DoorbellPicAdapter(this, (ArrayList<String>) getFilePath());
        mGridView.setAdapter(mDoorbellPicAdapter);
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

    @OnClick({R.id.tvAllChoice, R.id.tvDel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvAllChoice:
                if(tvAllChoice.getText().toString().equals("全选")){
                    mDoorbellPicAdapter.setAllSelectedOrNot(true);
                }
                else if(tvAllChoice.getText().toString().equals("取消选择")){
                    mDoorbellPicAdapter.setAllSelectedOrNot(false);
                }
                tvDel.setEnabled(mDoorbellPicAdapter.isSelect());
                if(mDoorbellPicAdapter.isSelect()){
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
                mDoorbellPicAdapter.delPic();
                tvDel.setEnabled(mDoorbellPicAdapter.isSelect());
                if(mDoorbellPicAdapter.isSelect()){
                    tvAllChoice.setText("取消选择");
                }
                else{
                    tvAllChoice.setText("全选");
                }
            }
        });
    }
}
