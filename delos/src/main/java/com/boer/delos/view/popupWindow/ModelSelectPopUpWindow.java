package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.boer.delos.R;
import com.boer.delos.model.Link;

import java.util.List;


public class ModelSelectPopUpWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    LayoutInflater inflater;
    View view;
    private List<Link> modes;
    private ModelSelectListener listener;

    private TextView tv_mode_gohome;
    private TextView tv_mode_leave_home;
    private TextView tv_mode_meet_visitor;
    private TextView tv_mode_dinner;
    private TextView tv_mode_undefence;
    private TextView tv_mode_defence;

    public ModelSelectPopUpWindow(Context context, List<Link> modes, ModelSelectListener listener) {
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
        LinearLayout llModelSelectUndefence = (LinearLayout) view.findViewById(R.id.llModelSelectUndefence);
        LinearLayout llModelSelectDefence = (LinearLayout) view.findViewById(R.id.llModelSelectDefence);
        LinearLayout llModelSelectManage = (LinearLayout) view.findViewById(R.id.llModelSelectManage);

        tv_mode_gohome = (TextView) view.findViewById(R.id.tv_mode_gohome);
        tv_mode_leave_home = (TextView) view.findViewById(R.id.tv_mode_leave_home);
        tv_mode_meet_visitor = (TextView) view.findViewById(R.id.tv_mode_meet_visitor);
        tv_mode_dinner = (TextView) view.findViewById(R.id.tv_mode_dinner);
        tv_mode_undefence = (TextView) view.findViewById(R.id.tv_mode_undefence);
        tv_mode_defence = (TextView) view.findViewById(R.id.tv_mode_defence);

        llModelSelectGoHome.setOnClickListener(this);
        llModelSelectMeetVisitor.setOnClickListener(this);
        llModelSelectLeaveHome.setOnClickListener(this);
        llModelSelectDinner.setOnClickListener(this);
        llModelSelectDefence.setOnClickListener(this);
        llModelSelectUndefence.setOnClickListener(this);
        llModelSelectManage.setOnClickListener(this);


        updataUI(modes);

    }

    public void updataUI(List<Link> mod) {
        modes=mod;
        if(modes.size()>0){
            tv_mode_gohome.setText(modes.get(0).getTag());
            tv_mode_leave_home.setText(modes.get(1).getTag());
            tv_mode_meet_visitor.setText(modes.get(2).getTag());
            tv_mode_dinner.setText(modes.get(3).getTag());
            tv_mode_undefence.setText(modes.get(4).getTag());
            tv_mode_defence.setText(modes.get(5).getTag());
        }
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
            case R.id.llModelSelectUndefence:
                listener.result(5);
                dismiss();
                break;
            case R.id.llModelSelectDefence:
                listener.result(6);
                dismiss();
                break;
            case R.id.llModelSelectManage:
                listener.result(7);
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
