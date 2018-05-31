package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.boer.delos.R;
import com.boer.delos.model.Link;

import java.util.List;


public class ModeFamilySelectPopUpWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    LayoutInflater inflater;
    View view;
    private List<Link> modes;
    private ModelSelectListener listener;

    public ModeFamilySelectPopUpWindow(Context context, List<Link> modes, ModelSelectListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.modes = modes;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_model_selection, null);
        setContentView(view);
        setProperty();
        initView();
    }

    private void setProperty() {
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        View viewCancle = view.findViewById(R.id.vCancel);
        viewCancle.setOnClickListener(this);
        LinearLayout llModelSelectGoHome = (LinearLayout) view.findViewById(R.id.llModelSelectGoHome);
        LinearLayout llModelSelectMeetVisitor = (LinearLayout) view.findViewById(R.id.llModelSelectMeetVisitor);
        LinearLayout llModelSelectLeaveHome = (LinearLayout) view.findViewById(R.id.llModelSelectLeaveHome);
        LinearLayout llModelSelectDinner = (LinearLayout) view.findViewById(R.id.llModelSelectDinner);
        LinearLayout llModelSelectManage = (LinearLayout) view.findViewById(R.id.llModelSelectManage);



        llModelSelectGoHome.setOnClickListener(this);
        llModelSelectMeetVisitor.setOnClickListener(this);
        llModelSelectLeaveHome.setOnClickListener(this);
        llModelSelectDinner.setOnClickListener(this);
        llModelSelectManage.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llModelSelectGoHome:
                listener.result(1);
                dismiss();
                break;
            case R.id.llModelSelectMeetVisitor:
                listener.result(2);
                dismiss();
                break;
            case R.id.llModelSelectLeaveHome:
                listener.result(3);
                dismiss();
                break;
            case R.id.llModelSelectDinner:
                listener.result(4);
                dismiss();
                break;
            case R.id.llModelSelectManage:
                listener.result(5);
                dismiss();
                break;
            case R.id.vCancel:
                dismiss();
        }
    }

    public interface ModelSelectListener {
        void result(int position);
    }
}
