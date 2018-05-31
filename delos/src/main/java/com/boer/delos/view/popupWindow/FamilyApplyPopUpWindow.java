package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.login.DistributeLimitListeningActivity;

/**
 * 首页家人申请popupWindow
 * @author pengjiyang
 * create by 2016/03/29
 */
public class FamilyApplyPopUpWindow extends PopupWindow implements View.OnClickListener{

    Context context;
    LayoutInflater inflater;
    View view;

    private TextView tvHomeApplyContent;

    public FamilyApplyPopUpWindow(Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_family_apply,null);

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
        tvHomeApplyContent = (TextView) view.findViewById(R.id.tvHomeApplyContent);
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
                // TODO
                Intent intent = new Intent(context, DistributeLimitListeningActivity.class);
                context.startActivity(intent);
                dismiss();
                break;
        }
    }

    /**
     * 设置首页弹出对话框的内容
     * @param applicant 申请人的用户名
     */
    public void setHomeApplyContent(String applicant) {
        tvHomeApplyContent.setText(String.format(context.getResources().getString(R.string.home_apply_content), applicant));
    }

}
