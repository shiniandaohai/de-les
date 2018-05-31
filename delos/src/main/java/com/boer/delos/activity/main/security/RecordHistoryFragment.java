package com.boer.delos.activity.main.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.adapter.RecordHistoryAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.model.HistoryRecord;
import com.boer.delos.view.popupWindow.MonitorRecordPopUpWindow;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "录像记录"fragment
 * create at 2016/3/31 10:59
 *
 */
public class RecordHistoryFragment extends Fragment implements View.OnClickListener{

    private android.widget.ListView lvRecordHistory;
    private ImageView ivRecordDownload;
    private ImageView ivRecordDelete;
    private PercentRelativeLayout rlRecordDelete;
    private PercentRelativeLayout rlRecordSelectAll;
    private PercentLinearLayout llRecordBottom;

    private RecordHistoryAdapter adapter;
    private List<HistoryRecord> records = new ArrayList<>();

    private View rootView;
    private boolean isSelect = true;// 判断头部右侧的按钮是不是"选择"按钮

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_record_history, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        this.rlRecordSelectAll = (PercentRelativeLayout) rootView.findViewById(R.id.rlRecordSelectAll);
        this.rlRecordDelete = (PercentRelativeLayout) rootView.findViewById(R.id.rlRecordDelete);
        this.ivRecordDelete = (ImageView) rootView.findViewById(R.id.ivRecordDelete);
        this.ivRecordDownload = (ImageView) rootView.findViewById(R.id.ivRecordDownload);
        this.lvRecordHistory = (ListView) rootView.findViewById(R.id.lvRecordHistory);
        this.llRecordBottom = (PercentLinearLayout) rootView.findViewById(R.id.llRecordBottom);

        this.rlRecordSelectAll.setOnClickListener(this);
        this.ivRecordDownload.setOnClickListener(this);
        this.ivRecordDelete.setOnClickListener(this);

        adapter = new RecordHistoryAdapter(getActivity());
        this.lvRecordHistory.setAdapter(adapter);
        this.lvRecordHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isSelect) {
                    // TODO 跳转到播放界面
                } else {
                    ImageView ivVideoRecordChecked = (ImageView) view.findViewById(R.id.ivVideoRecordChecked);
                    if (!records.get(position).isDelete()) {
                        ivVideoRecordChecked.setImageResource(R.drawable.ic_video_record_checked);
                        records.get(position).setIsDelete(true);
                    } else {
                        ivVideoRecordChecked.setImageResource(R.drawable.ic_video_record_unchecked);
                        records.get(position).setIsDelete(false);
                    }
                    if(!hasItemSelected()) { // 每次点击时,判断是否要显示界面底部的全选
                        rlRecordSelectAll.setVisibility(View.VISIBLE);
                        rlRecordDelete.setVisibility(View.GONE);
                    } else {
                        rlRecordSelectAll.setVisibility(View.GONE);
                        rlRecordDelete.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        getRecordsFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRecordSelectAll:
                setSelectAll();
                break;
            case R.id.ivRecordDownload:
                BaseApplication.showToast("从云端下载");
                break;
            case R.id.ivRecordDelete:
                MonitorRecordPopUpWindow popUpWindow = new MonitorRecordPopUpWindow(getActivity(), new MonitorRecordPopUpWindow.DeleteConfirmListener() {
                    @Override
                    public void result(boolean isConfirm) {
                        if(isConfirm) {
                            List<HistoryRecord> list = new ArrayList<>();
                            for (int i = 0; i < records.size(); i ++) {
                                if (!records.get(i).isDelete()) {
                                    list.add(records.get(i));
                                }
                            }
                            records.clear();
                            for (int i = 0; i < list.size(); i ++) {
                                records.add(list.get(i));
                            }
                            adapter.setDatas(records);
                            adapter.notifyDataSetChanged();
                            rlRecordSelectAll.setVisibility(View.VISIBLE);
                            rlRecordDelete.setVisibility(View.GONE);
                        }
                    }
                });
                popUpWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    /**
     * 从服务器获取录像记录列表数据
     */
    private void getRecordsFromServer() {
        this.llRecordBottom.setVisibility(View.GONE);
        records.clear();
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像1", "0:03", "2016-03-08 18:00", false));
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像2", "0:03", "2016-03-08 18:00", false));
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像3", "0:03", "2016-03-08 18:00", false));
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像4", "0:03", "2016-03-08 18:00", false));
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像5", "0:03", "2016-03-08 18:00", false));
        records.add(new HistoryRecord(R.drawable.ic_gateway_bind_wifilink, "录像6", "0:03", "2016-03-08 18:00", false));
        adapter.setDatas(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置显示播放按钮
     */
    public void setPlay() {
        llRecordBottom.setVisibility(View.GONE);
        if(records.size() == 0) {
            return;
        }
        isSelect = true;
        adapter.setIsSelect(isSelect);
        adapter.setDatas(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置显示删除按钮
     */
    public void setDelete() {
        llRecordBottom.setVisibility(View.VISIBLE);
        if(records.size() == 0) {
            return;
        }
        isSelect = false;
        adapter.setIsSelect(isSelect);

        if(!hasItemSelected()) { // 初始化时,判断是否要显示界面底部的全选
            rlRecordSelectAll.setVisibility(View.VISIBLE);
            rlRecordDelete.setVisibility(View.GONE);
        } else {
            rlRecordSelectAll.setVisibility(View.GONE);
            rlRecordDelete.setVisibility(View.VISIBLE);
        }
        adapter.setDatas(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * 全部选择
     */
    private void setSelectAll() {
        if(records.size() == 0) {
            return;
        }
        this.rlRecordSelectAll.setVisibility(View.GONE);
        this.rlRecordDelete.setVisibility(View.VISIBLE);
        for (int i = 0; i < records.size(); i ++) {
            records.get(i).setIsDelete(true);
        }
        adapter.setDatas(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void cancelSelectAll() {
        if(records.size() == 0) {
            return;
        }
        this.rlRecordSelectAll.setVisibility(View.VISIBLE);
        this.rlRecordDelete.setVisibility(View.GONE);
        for (int i = 0; i < records.size(); i ++) {
            records.get(i).setIsDelete(false);
        }
        adapter.setDatas(records);
        adapter.notifyDataSetChanged();
    }

    /**
     * 判断是否有条目被选中
     * @return
     */
    private boolean hasItemSelected() {
        if(records.size() == 0) {
            return false;
        }
        for (int i = 0; i < records.size(); i ++) {
            if(records.get(i).isDelete()) {
                return true;
            }
        }
        return false;
    }
}
