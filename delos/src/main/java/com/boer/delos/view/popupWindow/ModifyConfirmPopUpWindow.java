package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.ToastUtils;


/**
 * @author XieQingTing
 * @Description: 绑定邮箱
 * create at 2016/5/11 16:30
 */
public class ModifyConfirmPopUpWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView mTextViewTitle;
    private TextView tvShowDetail;
    private TextView tvBindCancel;
    private TextView tvBindConfirm;
    private ClickResultListener listener;
    private ToastUtils toastUtils;
    private String newEmail;
    private String oldEmail;

    public ModifyConfirmPopUpWindow(Context context, ClickResultListener listener) {
        this.context = context;
        this.listener = listener;
        toastUtils = new ToastUtils(context);
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_bind_email, null);
        setContentView(view);

        setProperty();
        initView();
    }

    //设置标题
    public void setTextViewTitle(String name) {
        mTextViewTitle.setText(name);
    }

    private void setProperty() {
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        tvShowDetail = (TextView) view.findViewById(R.id.tv_show_detail);
        tvBindCancel = (TextView) view.findViewById(R.id.tvBindCancel);
        tvBindConfirm = (TextView) view.findViewById(R.id.tvBindConfirm);
        mTextViewTitle = (TextView) view.findViewById(R.id.id_textViewTitle);
        tvBindCancel.setOnClickListener(this);
        tvBindConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBindCancel:
                dismiss();
                break;
            case R.id.tvBindConfirm:

                break;
        }
    }
    public interface ClickResultListener {
        void ClickResult(int tag);
    }
}
