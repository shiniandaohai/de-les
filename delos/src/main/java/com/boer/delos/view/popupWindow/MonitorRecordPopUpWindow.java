package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;

/**
 * 监控快照和录像记录popupWindow
 * @author pengjiyang
 * create by 2016/04/1
 */
public class MonitorRecordPopUpWindow extends PopupWindow implements View.OnClickListener{

    Context context;
    LayoutInflater inflater;
    View view;
    DeleteConfirmListener listener;

    private String fragmentName = "";

    public MonitorRecordPopUpWindow(Context context, DeleteConfirmListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_monitor_record,null);

        setContentView(view);
        setProperty();
        initView();
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

    private void initView() {
        TextView tvMonitorRecordCancel = (TextView) view.findViewById(R.id.tvMonitorRecordCancel);
        TextView tvMonitorRecordConfirm = (TextView) view.findViewById(R.id.tvMonitorRecordConfirm);

        tvMonitorRecordCancel.setOnClickListener(this);
        tvMonitorRecordConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMonitorRecordCancel:
                listener.result(false);
                dismiss();
                break;
            case R.id.tvMonitorRecordConfirm:
//                // TODO 根据fragmentName,判断是提交删除录像记录的请求还是提交删除监控快照的请求
//                if("MonitorPictureFragment".equals(fragmentName)) {
//                    SquirrelCallImpl.showToast("监控快照");
//                }
//                if("RecordHistoryFragment".equals(fragmentName)) {
//                    SquirrelCallImpl.showToast("录像记录");
//                }
                listener.result(true);
                dismiss();
                break;
        }
    }

    public interface DeleteConfirmListener {
        void result(boolean isConfirm);
    }
}
