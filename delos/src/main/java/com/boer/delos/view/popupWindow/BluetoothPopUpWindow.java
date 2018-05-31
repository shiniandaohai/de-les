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
 * @author PengJiYang
 * @Description: 链接蓝牙的popupWindow
 * create at 2016/5/27 17:15
 *
 */
public class BluetoothPopUpWindow extends PopupWindow implements View.OnClickListener{

    Context context;
    LayoutInflater inflater;
    View view;

    public BluetoothPopUpWindow(Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_bluetooth,null);

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
        TextView tvHomeApplyDisagree = (TextView) view.findViewById(R.id.tvHomeApplyDisagree);
        TextView tvHomeApplyAgree = (TextView) view.findViewById(R.id.tvHomeApplyAgree);

        tvHomeApplyDisagree.setOnClickListener(this);
        tvHomeApplyAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvHomeApplyDisagree:
                dismiss();
                break;
            case R.id.tvHomeApplyAgree:
                dismiss();
                break;
        }
    }

    interface ClickResultListener {
        void result();
    }

}
