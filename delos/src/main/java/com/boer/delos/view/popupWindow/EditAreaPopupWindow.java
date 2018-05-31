package com.boer.delos.view.popupWindow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.boer.delos.R;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/1 0001 09:50
 * @Modify:
 * @ModifyDate:
 */


public class EditAreaPopupWindow extends PopupWindow implements View.OnClickListener {


    private final Activity mContext;
    private final LayoutInflater inflater;
    private final View rootView;
    private LinearLayout mLlEdit;
    private LinearLayout mLlDelete;
    private LinearLayout mLlAddNewDevice;
    private LinearLayout mAddHaveDevice;

    private AreaEditClickListener listener;


    public EditAreaPopupWindow(Activity context, AreaEditClickListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;

        inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.popup_area_edit, null);
        setContentView(rootView);
        setProperty();
        initView();

    }

    private void initView() {
        mLlEdit = (LinearLayout) rootView.findViewById(R.id.ll_edit);
        mLlDelete = (LinearLayout) rootView.findViewById(R.id.ll_delete);
        mLlAddNewDevice = (LinearLayout) rootView.findViewById(R.id.ll_add_new_device);
        mAddHaveDevice = (LinearLayout) rootView.findViewById(R.id.add_have_device);

        mLlEdit.setOnClickListener(this);
        mLlDelete.setOnClickListener(this);
        mLlAddNewDevice.setOnClickListener(this);
        mAddHaveDevice.setOnClickListener(this);
    }


    private void setProperty() {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);

        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        rootView.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(mContext, 1f);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        backgroundAlpha(mContext, 0.8f);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit:
                if (listener != null) {
                    listener.editAreaName();
                }
                break;
            case R.id.ll_delete:
                if (listener != null) {
                    listener.deleteAreaName();

                }
                break;
            case R.id.ll_add_new_device:
                if (listener != null) {
                    listener.areaAddNewDevice();
                }
                break;
            case R.id.add_have_device:
                if (listener != null) {
                    listener.areaAddHaveDevice();
                }
                break;
        }
        dismiss();
    }

    public interface AreaEditClickListener {
        void editAreaName();

        void deleteAreaName();

        void areaAddNewDevice();

        void areaAddHaveDevice();
    }


}
