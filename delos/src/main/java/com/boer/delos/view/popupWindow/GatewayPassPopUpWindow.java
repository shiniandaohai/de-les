package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

/**
 * 主机安全码popupWindow
 *
 * @author pengjiyang
 *         create by 2016/03/28
 */
public class GatewayPassPopUpWindow extends PopupWindow implements View.OnClickListener {

    private int TAG = 1;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private ClickResultListener listener;

    public EditText etGatewaySecureCode;
    private String secureCode;

    public GatewayPassPopUpWindow(Context context, ClickResultListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_gateway_pass, null);

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
        etGatewaySecureCode = (EditText) view.findViewById(R.id.etGatewaySecureCode);
        TextView tvGatewayCancel = (TextView) view.findViewById(R.id.tvGatewayCancel);
        TextView tvGatewayApply = (TextView) view.findViewById(R.id.tvGatewayApply);

        LinearLayout pop_bg = (LinearLayout) view.findViewById(R.id.pop_base_bg);

        tvGatewayCancel.setOnClickListener(this);
        tvGatewayApply.setOnClickListener(this);
        pop_bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.pop_content_bg) {
                    dismiss();
                    return false;
                }

                return false;
            }
        });
    }

    public String getSecureCode() {
        return secureCode;
    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etGatewaySecureCode.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.tvGatewayCancel:
                dismiss();
                break;

            case R.id.tvGatewayApply:
                secureCode = etGatewaySecureCode.getText().toString();
                if (StringUtil.isEmpty(secureCode)) {
                    ToastHelper.showShortMsg(context.getString(R.string.toast_input_PIN));

//                   new ToastUtils(context).showErrorWithStatus("");
                    return;
                }
                listener.ClickResult(secureCode);
                dismiss();
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(String secureCode);
    }

}
