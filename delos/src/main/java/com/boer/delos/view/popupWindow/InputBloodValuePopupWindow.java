package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

/**
 * @author XieQingTing
 * @Description: 输入血糖值的popup
 * create at 2016/5/27 14:41
 */
public class InputBloodValuePopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private LayoutInflater inflater;
    private ClickResultListener listener;
    private View view;
    private android.widget.EditText etBloodValue;
    private android.widget.TextView tvInputDisagree;
    private android.widget.TextView tvInputAgree;

    public InputBloodValuePopupWindow(Context context, ClickResultListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.popup_input_blood_sugar, null);

        setContentView(view);

        setProperty();
        initView();
        initListener();

    }

    private void setProperty() {
        //设置弹窗体的宽高
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        this.tvInputAgree = (TextView) view.findViewById(R.id.tvInputAgree);
        this.tvInputDisagree = (TextView) view.findViewById(R.id.tvInputDisagree);
        this.etBloodValue = (EditText) view.findViewById(R.id.etBloodValue);
    }

    private void initListener() {
        this.tvInputAgree.setOnClickListener(this);
        this.tvInputDisagree.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etBloodValue.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.tvInputAgree:
                String value = etBloodValue.getText().toString();
                if (StringUtil.isEmpty(value)) {
                    ToastHelper.showShortMsg(context.getString(R.string.text_not_null));
                    return;
//                    value = "0";
                }

                try {
                    Float.valueOf(value);
                } catch (NumberFormatException e) {
                    ToastHelper.showShortMsg(context.getString(R.string.text_not_null));
                    return;
                }


                listener.ClickResult(value);
                break;
            case R.id.tvInputDisagree:
                dismiss();
                break;
        }

    }

    public EditText getEtBloodValue() {
        return etBloodValue;
    }

    public interface ClickResultListener {
        void ClickResult(String tag);
    }
}
