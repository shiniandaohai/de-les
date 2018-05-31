package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;

/**
 * @author XieQingTing
 * @Description: 下线通知popup
 * create at 2016/4/12 9:55
 *
 */
public class OffLinePopUpWindow extends PopupWindow implements View.OnClickListener {

    private static final int TAG = 1;
    private  ClickResultListener listener;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    public TextView tvTime;
    public TextView tvKnow;

    public OffLinePopUpWindow(Context context, ClickResultListener clickResultListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_off_line_notification, null);
        this.listener=clickResultListener;

        setContentView(view);
        initView();
        setProperty();


    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        tvKnow = (TextView) view.findViewById(R.id.tvKnow);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvKnow.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvKnow:
                listener.ClickResult(TAG);
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }



}
