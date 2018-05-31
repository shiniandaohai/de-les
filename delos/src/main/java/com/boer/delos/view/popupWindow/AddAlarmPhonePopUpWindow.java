package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;

/**
 * @author XieQingTing
 * @Description: 添加报警电话
 * create at 2016/5/19 15:19
 *
 */
public class AddAlarmPhonePopUpWindow extends PopupWindow implements View.OnClickListener{

    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private View view;
    private EditText etAlarmPhone;
    private TextView tvAlarmPhoneCancle,tvAlarmPhoneConfirm;
    private int TAG=0;
    private String alarmPhone;

    public AddAlarmPhonePopUpWindow(Context context,ClickResultListener listener) {
        super(context);
        this.context=context;
        this.listener=listener;
        inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.popup_add_alarm_phone,null);
        setContentView(view);

        setProperty();
        initView();
    }

    private void initView() {
        this.etAlarmPhone= (EditText) view.findViewById(R.id.etAlarmPhone);
        this.tvAlarmPhoneCancle= (TextView) view.findViewById(R.id.tvAlarmPhoneCancle);
        this.tvAlarmPhoneConfirm= (TextView) view.findViewById(R.id.tvAlarmPhoneConfirm);

        tvAlarmPhoneCancle.setOnClickListener(this);
        tvAlarmPhoneConfirm.setOnClickListener(this);
    }

    private void setProperty() {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        try{
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etAlarmPhone.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        switch (v.getId()){
            case R.id.tvAlarmPhoneCancle:
                dismiss();
                break;
            case R.id.tvAlarmPhoneConfirm:
                alarmPhone=etAlarmPhone.getText().toString();
                listener.ClickResult(TAG);
                break;
        }
    }

    public String getAlarmPhone() {
        return alarmPhone;
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }

}
