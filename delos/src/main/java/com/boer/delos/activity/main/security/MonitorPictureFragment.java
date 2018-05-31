package com.boer.delos.activity.main.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.adapter.MonitorPictureAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.model.MonitorPicture;
import com.boer.delos.view.popupWindow.MonitorRecordPopUpWindow;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "监控快照"fragment
 * create at 2016/3/31 11:02
 *
 */
public class MonitorPictureFragment extends Fragment implements View.OnClickListener{

    private android.widget.GridView gvMonitorPicture;
    private ImageView ivMonitorPicDownload;
    private ImageView ivMonitorPicDelete;
    private PercentRelativeLayout rlMonitorDelete;
    private PercentRelativeLayout rlMonitorSelectAll;
    private PercentLinearLayout llMonitorBottom;

    private MonitorPictureAdapter adapter;
    private List<MonitorPicture> pictures = new ArrayList<>();

    private View rootView;
    private boolean isSelect = true;// 判断头部右侧的按钮是不是"选择"按钮

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_monitor_picture, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        this.rlMonitorSelectAll = (PercentRelativeLayout) rootView.findViewById(R.id.rlMonitorSelectAll);
        this.rlMonitorDelete = (PercentRelativeLayout) rootView.findViewById(R.id.rlMonitorDelete);
        this.ivMonitorPicDelete = (ImageView) rootView.findViewById(R.id.ivMonitorPicDelete);
        this.ivMonitorPicDownload = (ImageView) rootView.findViewById(R.id.ivMonitorPicDownload);
        this.gvMonitorPicture = (GridView) rootView.findViewById(R.id.gvMonitorPicture);
        this.llMonitorBottom = (PercentLinearLayout) rootView.findViewById(R.id.llMonitorBottom);

        this.rlMonitorSelectAll.setOnClickListener(this);
        this.ivMonitorPicDownload.setOnClickListener(this);
        this.ivMonitorPicDelete.setOnClickListener(this);

        adapter = new MonitorPictureAdapter(getActivity());
        this.gvMonitorPicture.setAdapter(adapter);
        this.gvMonitorPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSelect) {
                    ImageView ivMonitorPicChecked = (ImageView) view.findViewById(R.id.ivMonitorPicChecked);
                    if (!pictures.get(position).isDelete()) {
                        ivMonitorPicChecked.setVisibility(View.VISIBLE);
                        pictures.get(position).setIsDelete(true);
                    } else {
                        ivMonitorPicChecked.setVisibility(View.GONE);
                        pictures.get(position).setIsDelete(false);
                    }
                    if(!hasItemSelected()) { // 每次点击时,判断是否要显示界面底部的全选
                        rlMonitorSelectAll.setVisibility(View.VISIBLE);
                        rlMonitorDelete.setVisibility(View.GONE);
                    } else {
                        rlMonitorSelectAll.setVisibility(View.GONE);
                        rlMonitorDelete.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        getPictureFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMonitorSelectAll:
                setSelectAll();
                break;
            case R.id.ivMonitorPicDownload:
                BaseApplication.showToast("从云端下载");
                break;
            case R.id.ivMonitorPicDelete:
                MonitorRecordPopUpWindow popUpWindow = new MonitorRecordPopUpWindow(getActivity(), new MonitorRecordPopUpWindow.DeleteConfirmListener() {
                    @Override
                    public void result(boolean isConfirm) {
                        if(isConfirm) {
                            List<MonitorPicture> list = new ArrayList<>();
                            for (int i = 0; i < pictures.size(); i ++) {
                                if (!pictures.get(i).isDelete()) {
                                    list.add(pictures.get(i));
                                }
                            }
                            pictures.clear();
                            for (int i = 0; i < list.size(); i ++) {
                                pictures.add(list.get(i));
                            }
                            adapter.setDatas(pictures);
                            adapter.notifyDataSetChanged();
                            rlMonitorSelectAll.setVisibility(View.VISIBLE);
                            rlMonitorDelete.setVisibility(View.GONE);
                        }
                    }
                });
                popUpWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    /**
     * 从服务器获取图片
     */
    private void getPictureFromServer() {
        llMonitorBottom.setVisibility(View.GONE);
        pictures.clear();
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, false, true));
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, false, true));
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, true, true));
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, true, true));
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, true, true));
        pictures.add(new MonitorPicture(R.drawable.ic_gateway_bind_wifilink, false, true));
        adapter.setDatas(pictures);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置显示播放按钮
     */
    public void setPlay() {
        llMonitorBottom.setVisibility(View.GONE);
        if(pictures.size() == 0) {
            return;
        }
        isSelect = true;
        adapter.setIsSelect(isSelect);
        adapter.setDatas(pictures);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置显示删除按钮
     */
    public void setDelete() {
        llMonitorBottom.setVisibility(View.VISIBLE);
        if(pictures.size() == 0) {
            return;
        }
        isSelect = false;
        adapter.setIsSelect(isSelect);

        if(!hasItemSelected()) { // 初始化时,判断是否要显示界面底部的全选
            rlMonitorSelectAll.setVisibility(View.VISIBLE);
            rlMonitorDelete.setVisibility(View.GONE);
        } else {
            rlMonitorSelectAll.setVisibility(View.GONE);
            rlMonitorDelete.setVisibility(View.VISIBLE);
        }
        adapter.setDatas(pictures);
        adapter.notifyDataSetChanged();
    }

    /**
     * 全部选择
     */
    private void setSelectAll() {
        if(pictures.size() == 0) {
            return;
        }
        this.rlMonitorSelectAll.setVisibility(View.GONE);
        this.rlMonitorDelete.setVisibility(View.VISIBLE);
        for (int i = 0; i < pictures.size(); i ++) {
            pictures.get(i).setIsDelete(true);
        }
        adapter.setDatas(pictures);
        adapter.notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void cancelSelectAll() {
        if(pictures.size() == 0) {
            return;
        }
        this.rlMonitorSelectAll.setVisibility(View.VISIBLE);
        this.rlMonitorDelete.setVisibility(View.GONE);
        for (int i = 0; i < pictures.size(); i ++) {
            pictures.get(i).setIsDelete(false);
        }
        adapter.setDatas(pictures);
        adapter.notifyDataSetChanged();
    }

    /**
     * 判断是否有条目被选中
     * @return
     */
    private boolean hasItemSelected() {
        if(pictures.size() == 0) {
            return false;
        }
        for (int i = 0; i < pictures.size(); i ++) {
            if(pictures.get(i).isDelete()) {
                return true;
            }
        }
        return false;
    }
}
