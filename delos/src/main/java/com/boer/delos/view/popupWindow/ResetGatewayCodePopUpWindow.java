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
 * Created by Administrator on 2016/7/17 0017.
 */
public class ResetGatewayCodePopUpWindow extends PopupWindow implements View.OnClickListener{

    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private View view;
    private EditText etCode;
    private EditText etNewCode;
    private TextView tvAlarmPhoneCancle,tvAlarmPhoneConfirm;
    private int TAG=0;
    private String code,newCode;

    public ResetGatewayCodePopUpWindow(Context context, ClickResultListener listener) {
        super(context);
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.listener=listener;
        view=inflater.inflate(R.layout.popup_reset_gateway_code,null);
        setContentView(view);

        setProperty();
        initView();
    }
public ResetGatewayCodePopUpWindow(Context context) {
        super(context);
        this.context=context;
        inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.popup_reset_gateway_code,null);
        setContentView(view);

        setProperty();
        initView();
    }

    public void clearEditTextView() {
        etCode.setText("");
        etNewCode.setText("");
    }

    private void initView() {
        this.etCode= (EditText) view.findViewById(R.id.etCode);
        this.etNewCode= (EditText) view.findViewById(R.id.etNewCode);
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
            imm.hideSoftInputFromWindow(etNewCode.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etCode.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        switch (v.getId()){
            case R.id.tvAlarmPhoneCancle:
                dismiss();
                break;
            case R.id.tvAlarmPhoneConfirm:
                code=etCode.getText().toString();
                newCode=etNewCode.getText().toString();
                    if (listener ==null) return;
                listener.ClickResult(code, newCode);
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(String c1, String c2);
    }
    public void setClickResultListener(ClickResultListener listener) {
        this.listener = listener;
    }
}

