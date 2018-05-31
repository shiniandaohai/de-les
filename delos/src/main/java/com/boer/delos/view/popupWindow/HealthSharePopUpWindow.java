package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.HealthShareAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Family;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家人管理界面的健康分享popupWindow
 * create at 2016/5/26 11:58
 */
public class HealthSharePopUpWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private ClickResultListener listener;

    private TextView tvCancel, tvConfirm;
    private ImageView ivSelectAll;
    private ListView lvSharePop;
    private LinearLayout llSelectAll;

    private HealthShareAdapter adapter;
    private List<Family> families;
    private List<Boolean> checkList;// 标记家人的选中状态
    private Boolean isSelectAll = false;

    public HealthSharePopUpWindow(Context context, List<Family> familyList, ClickResultListener listener) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_health_share, null);
//        this.families = groupAllDeviceByType(familyList);
        this.families = familyList;
        this.listener = listener;
        setContentView(view);
        setProperty();
        initView();
        initCheckList();
    }

    private void initCheckList() {
        checkList = new ArrayList<>();
        List<Family> tempList = new ArrayList<>();

        Family tempFamily = null; //当前用户family
        List<String> ids = null;
        for (int i = 0; i < families.size(); i++) {
            if (!families.get(i).getUserId().equals(Constant.USERID)) {
                tempList.add(families.get(i));
                continue;
            }
            tempFamily = families.get(i);
            if (tempFamily.getShare() == 0) continue;
            ids = tempFamily.getShareIds();
        }

        for (int i = 0; i < tempList.size(); i++) {
            if (ids == null) ids = new ArrayList<>();
            if (ids.contains(tempList.get(i).getUserId())) {
                checkList.add(true);
                continue;
            }
            checkList.add(false);// true:选中, false:未选中
        }

        adapter = new HealthShareAdapter(context, tempList, checkList);
        lvSharePop.setAdapter(adapter);

    }

    private void initView() {
        ivSelectAll = (ImageView) view.findViewById(R.id.ivSelectAll);
        llSelectAll = (LinearLayout) view.findViewById(R.id.llSelectAll);
        lvSharePop = (ListView) view.findViewById(R.id.lvSharePop);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvConfirm = (TextView) view.findViewById(R.id.tvConfirm);

        lvSharePop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView ivItemChecked = (ImageView) view.findViewById(R.id.ivItemChecked);
                if (checkList.get(position)) {
                    ivItemChecked.setVisibility(View.GONE);
                    checkList.set(position, false);
                } else {
                    ivItemChecked.setVisibility(View.VISIBLE);
                    checkList.set(position, true);
                }
            }
        });

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        llSelectAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSelectAll:
                selectAll();
                break;
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvConfirm:
                confirm();
                break;
        }
    }

    private void selectAll() {
        if (isSelectAll) {
            for (int i = 0; i < checkList.size(); i++) {
                checkList.set(i, false);
            }
            adapter.setCheckList(checkList);
            adapter.notifyDataSetChanged();
            isSelectAll = false;
            ivSelectAll.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < checkList.size(); i++) {
                checkList.set(i, true);
            }
            adapter.setCheckList(checkList);
            adapter.notifyDataSetChanged();
            isSelectAll = true;
            ivSelectAll.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 确定分享
     */
    private void confirm() {
        List<Family> resultList = new ArrayList<>();


        List<Family> tempList = new ArrayList<>();
        for (int i = 0; i < families.size(); i++) {
            if (!families.get(i).getUserId().equals(Constant.USERID)) {
                tempList.add(families.get(i));
                continue;
            }
        }



        for (int i = 0; i < checkList.size(); i++) {
            if (checkList.get(i)) {
                resultList.add(tempList.get(i));
            }
        }
        listener.result(resultList);
        dismiss();
    }

    public interface ClickResultListener {
        void result(List<Family> families);
    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }
}
