package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
public class BindEmailPopUpWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView mTextViewTitle;
    private EditText etBindEmail;
    private TextView tvBindCancel;
    private TextView tvBindConfirm;
    private ClickResultListener listener;
    private ToastUtils toastUtils;
    private String newEmail;
    private String oldEmail;
    private String title;

    public BindEmailPopUpWindow(Context context, String title,ClickResultListener listener) {
        this.context = context;
        this.listener = listener;
        this.title =title;
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

    //设置EditText的Hint值
    //设置EditText的Hint值
    public void setEditTextHint(String name) {
        etBindEmail.setHint(name);
    }

    public void setEditText(String name) {
        etBindEmail.setText(name);
        etBindEmail.setSelection(etBindEmail.getText().length());
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
        etBindEmail = (EditText) view.findViewById(R.id.etBindEmail);
        showSoftKey(etBindEmail);
        tvBindCancel = (TextView) view.findViewById(R.id.tvBindCancel);
        tvBindConfirm = (TextView) view.findViewById(R.id.tvBindConfirm);
        mTextViewTitle = (TextView) view.findViewById(R.id.id_textViewTitle);
        tvBindCancel.setOnClickListener(this);
        tvBindConfirm.setOnClickListener(this);
        mTextViewTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etBindEmail.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.tvBindCancel:
                dismiss();
                break;
            case R.id.tvBindConfirm:
                newEmail = etBindEmail.getText().toString();
                listener.ClickResult(newEmail);
                break;
        }
    }

    public String getNewEmail() {
        return newEmail;
    }

    public interface ClickResultListener {
        void ClickResult(String tag);
    }

    private void showSoftKey(final View view){
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    view.requestFocus();
                    imm.showSoftInput(view, 0);
                }
            }
        },60);
    }
}
