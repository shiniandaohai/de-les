package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.boer.delos.R;
/**
 * TODO: 风速选择弹框
 */
public class WindlevelPopUpWindow extends PopupWindow implements View.OnClickListener{
    private ClickResultListener listener;
    private View view;
    private LinearLayout weaklayout,midlayout,stronglayout;

    public WindlevelPopUpWindow(Context context, ClickResultListener clickResultListener) {
        super(context);
        this.listener = clickResultListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_windlevel, null);
        setContentView(view);
        setProperty();
        initView();
    }
    private  void setProperty(){
        this.setFocusable(true);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        this.setAnimationStyle(R.style.PopupAnimation);
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        this.setBackgroundDrawable(null);
//        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
//        animation.setDuration(500);
//        view.startAnimation(animation);
    }
    private void initView(){
        weaklayout = (LinearLayout)view.findViewById(R.id.layout_weak);
        weaklayout.setOnClickListener(this);
        midlayout = (LinearLayout)view.findViewById(R.id.layout_mid);
        midlayout.setOnClickListener(this);
        stronglayout = (LinearLayout)view.findViewById(R.id.layout_strong);
        stronglayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_weak:
               listener.ClickResult(0);
                this.dismiss();
                break;
            case R.id.layout_mid:
                listener.ClickResult(1);
                this.dismiss();
                break;
            case R.id.layout_strong:
                listener.ClickResult(2);
                this.dismiss();
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }
}
