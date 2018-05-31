package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.boer.delos.R;

public class WindDirecPopUpWindow extends PopupWindow implements View.OnClickListener {

    private ClickResultListener listener;
    private View view;
    private LinearLayout shakelayout,horilayout,vertlayout;

    public WindDirecPopUpWindow(Context context, ClickResultListener clickResultListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_wind_direc, null);
        this.listener = clickResultListener;
        setContentView(view);
        setProperty();
        initView();
    }

    private void setProperty(){
        this.setFocusable(true);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        this.setAnimationStyle(R.style.PopupAnimation);
//        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
//        animation.setDuration(500);
//        view.startAnimation(animation);
    }

    private void initView(){
        shakelayout = (LinearLayout)view.findViewById(R.id.layout_shake);
        shakelayout.setOnClickListener(this);
        horilayout = (LinearLayout)view.findViewById(R.id.layout_hori);
        horilayout.setOnClickListener(this);
        vertlayout = (LinearLayout)view.findViewById(R.id.layout_vert);
        vertlayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_shake:
                listener.ClickResult(0);
                this.dismiss();
                break;
            case R.id.layout_hori:
                listener.ClickResult(1);
                this.dismiss();
                break;
            case R.id.layout_vert:
                listener.ClickResult(2);
                this.dismiss();
                break;
        }
    }

    public interface ClickResultListener{
        void ClickResult(int tag);
    }
}
