package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.MemberAdapter;
import com.boer.delos.utils.Loger;

/**
 * 主机绑定信息提示popupWindow
 *
 * @author pengjiyang
 *         create by 2016/03/28
 * @Description:delos
 */
public class GatewayBindPopUpWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    LayoutInflater inflater;
    View view;

    private TextView tvGatewayContent;
    private ClickListener listener;

    private View bg_base_pop;
    private View bg_pop;

    public GatewayBindPopUpWindow(Context context, ClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_gateway_bind, null);

        setContentView(view);
        setProperty();
        initView();
        initListener();
    }

    private void initListener() {
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
        tvGatewayContent = (TextView) view.findViewById(R.id.tvGatewayContent);
        TextView tvGatewayCancel = (TextView) view.findViewById(R.id.tvGatewayCancel);
        TextView tvGatewayApply = (TextView) view.findViewById(R.id.tvGatewayApply);

        tvGatewayCancel.setOnClickListener(this);
        tvGatewayApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGatewayCancel:
                dismiss();
                break;
            case R.id.tvGatewayApply:
                listener.okClick();
                dismiss();
                break;
        }
    }

    public void setGatewayTipContent(String managerName) {
        tvGatewayContent.setText(String.format(context.getResources().getString(R.string.gateway_popup_content), managerName));
    }

    public interface ClickListener {
        void okClick();
    }
}
