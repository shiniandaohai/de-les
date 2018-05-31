package com.boer.delos.activity.scene;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.adapter.recycleview.DeviceManagerRvAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.recyclerHelper.OnStartDragListener;
import com.boer.delos.view.customDialog.CustomFragmentDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 设备管理
 * @CreateDate: 2017/4/7 0007 09:27
 * @Modify:
 * @ModifyDate:
 */


public class DeviceManagerActivity extends CommonBaseActivity
        implements OnStartDragListener, IObjectInterface<List<DeviceRelate>> {
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.cb_select_all)
    CheckBox mBtnSelectAll;
    @Bind(R.id.btn_delete)
    Button mBtnDelete;
    @Bind(R.id.ll_bottom)
    LinearLayout mLlBottom;


    private DeviceManagerRvAdapter mRvAdapter;
    private List<DeviceRelate> mRelateList;

    private ItemTouchHelper mItemTouchHelper;
    private CustomFragmentDialog deleteDialog;//删除

    private boolean IsScroll = false;
    private boolean wireOffline;//wire背影音乐离线状态、发送UDP广播来确定

    @Override
    protected int initLayout() {
        return R.layout.activity_device_manager;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_device_manager));
//        tlTitleLayout.setRightText(getString(R.string.text_done));
        wireOffline = getIntent().getBooleanExtra("Wire", false);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        if (mRelateList == null)
            mRelateList = new ArrayList<>();
        mRvAdapter = new DeviceManagerRvAdapter(this, mRelateList);
        mRvAdapter.setWireOffline(wireOffline);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mRvAdapter);
        mRvAdapter.setListener(this);

    }

    @Override
    protected void initData() {

        if (Constant.DEVICE_RELATE != null) {
            mRelateList.addAll(Constant.DEVICE_RELATE);
        }
        mRvAdapter.setList(mRelateList);
        if (mRelateList.size() != 0) {
            mBtnSelectAll.setClickable(true);
        } else {
            mBtnSelectAll.setClickable(false);
        }
    }

    @Override
    protected void initAction() {
        hideInput();
    }

    private float originY = 0;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        Loger.d("手势滑动 ");
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                originY = ev.getRawY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float y = ev.getRawY();
//                if (IsScroll) {
//                    if (y - originY < -DensityUtil.dip2px(10)) {
//                        IsScroll = false;
//                        mLlBottom.startAnimation(startAnimation(1, 0));
//                    } else if (y - originY >= DensityUtil.dip2px(10)) {
//                        IsScroll = false;
//                        mLlBottom.startAnimation(startAnimation(0, 1));
//                    }
//
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                originY = 0;
//                IsScroll = true;
//                break;
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 动画效果
     */
    //定义从右侧进入的动画效果
    protected Animation startAnimation(float fromY, float toY) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, fromY,
                Animation.RELATIVE_TO_PARENT, toY);
        inFromRight.setDuration(300);
        inFromRight.setInterpolator(new LinearOutSlowInInterpolator());
        inFromRight.setFillAfter(true);
        return inFromRight;
    }

    @Override
    public void leftViewClick() {
        super.leftViewClick();
    }

    @OnClick({R.id.cb_select_all, R.id.btn_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_select_all:
                Loger.d("mBtnSelectAll.isChecked() " + mBtnSelectAll.isChecked());
                mRvAdapter.clearAll(mBtnSelectAll.isChecked() ? -100 : -1);
                if (mBtnSelectAll.isChecked()) {
                    mBtnSelectAll.setText(getString(R.string.text_select_nor));
                    mBtnDelete.setTextColor(getResources().getColor(R.color.gray_text_delete));
                    mBtnDelete.setEnabled(true);

                } else {
                    mBtnSelectAll.setText(getString(R.string.text_select_all));
                    mBtnDelete.setTextColor(getResources().getColor(R.color.gray_text_delete_nor));
                    mBtnDelete.setEnabled(false);
                }

                break;
            case R.id.btn_delete:
                List<DeviceRelate> list = mRvAdapter.getSelectData();
                if (list == null || list.size() == 0) {
                    return;
                }
                popupwindow(list);
                break;
        }
    }

    private void popupwindow(final List<DeviceRelate> list) {
        String size = "";
        try {
            size = String.valueOf(list.size());
        } catch (NumberFormatException e) {
            size = "";
        }
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        deleteDialog = CustomFragmentDialog.newInstanse(
                getString(R.string.text_prompt),
                String.format(getString(R.string.text_prompt_delete), size), false);
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                deleteDevice(list);
                deleteDialog.dismiss();
            }
        });
    }

    /**
     * 删除设备
     *
     * @param list
     */
    private void deleteDevice(final List<DeviceRelate> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<Device> deviceLists = new ArrayList<>();
        for (DeviceRelate deviceRelate : list) {
            Device device = new Device();
            Device device1 = deviceRelate.getDeviceProp();

            device.setName(device1.getName());
            device.setAddr(device1.getAddr());
            device.setType(device1.getType());

            deviceLists.add(device);
        }
        DeviceController.getInstance().removeDevice(this, deviceLists, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    toastUtils.showSuccessWithStatus(getString(R.string.text_delete_success));
                    mRelateList.removeAll(list);
                    mRvAdapter.notifyDataSetChanged();
                    Constant.DEVICE_RELATE.clear();
                    Constant.DEVICE_RELATE.addAll(mRelateList);

                    mBtnSelectAll.performClick();

                } else {
                    toastUtils.showErrorWithStatus(getString(R.string.text_delete_fail));
                }
                mRvAdapter.clearAll(-1);
                mRvAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(getString(R.string.text_delete_fail));
                Loger.d(json);
            }
        });
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClickListenerOK(List<DeviceRelate> list, int pos, String tag) {
        if (list.size() != 0) {
            mBtnSelectAll.setText(getString(R.string.text_select_nor));
            mBtnDelete.setTextColor(getResources().getColor(R.color.gray_text_delete));
            mBtnDelete.setEnabled(true);
            mBtnSelectAll.setChecked(true);

        } else {
            mBtnSelectAll.setText(getString(R.string.text_select_all));
            mBtnDelete.setTextColor(getResources().getColor(R.color.gray_text_delete_nor));
            mBtnDelete.setEnabled(false);
            mBtnSelectAll.setChecked(false);
        }
    }

    protected Animation inFromDownAnimation() {
        Animation intoup = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        intoup.setDuration(500);
        intoup.setFillAfter(true);
        intoup.setInterpolator(new AccelerateInterpolator());
        return intoup;
    }

    @Override
    public void hideInput() {
        super.hideInput();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }


}
