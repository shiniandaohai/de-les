package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;

/**
 * 系统设置中的一键还原popupWindow
 *
 * @author pengjiyang
 *         create by 2016/04/1
 */
public class AlarmPopUpWindow extends PopupWindow implements View.OnClickListener {

    private final LinearLayout pop_alarm_bg;
    Context context;
    LayoutInflater inflater;
    View view;
    RestoreConfirmListener listener;
    TextView textViewContent;

    public AlarmPopUpWindow(Context context, RestoreConfirmListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_alarm, null);
        textViewContent = (TextView) view.findViewById(R.id.id_textViewContent);
        pop_alarm_bg = (LinearLayout) view.findViewById(R.id.pop_alarm_bg);
        setContentView(view);
        setProperty();
        initView();
    }

    public void setTextViewContent(String content) {
        textViewContent.setText(content);
    }

    public void setPopAlarmBg() {
        pop_alarm_bg.setBackgroundColor(Color.parseColor("#00000000"));

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
        TextView tvRestoreConfirm = (TextView) view.findViewById(R.id.tvRestoreConfirm);

        tvRestoreConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRestoreConfirm:
                if (listener != null) {
                    listener.result(true);
                }
                dismiss();
                break;
        }
    }

    public interface RestoreConfirmListener {
        void result(boolean isConfirm);
    }
}
