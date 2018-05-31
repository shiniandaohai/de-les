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
import com.boer.delos.constant.Constant;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;

/**
 * @author XieQingTing
 * @Description: 重置密码的popup
 * create at 2016/5/10 11:07
 */
public class ResetPasswordPopUpWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private View view;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private TextView tvResetConfirm;
    private TextView tvResetCancel;
    private ClickResultListener listener;
    private ToastUtils toastUtils;
    public static ResetPasswordPopUpWindow instance = null;
    private String oldPassword;
    private String newPassword;

    public ResetPasswordPopUpWindow(Context context, ClickResultListener listener) {
        this.context = context;
        this.listener = listener;

        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_reset_password, null);
        toastUtils = new ToastUtils(this.context);

        setContentView(view);
        setProperty();
        initView();


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
        etOldPassword = (EditText) view.findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) view.findViewById(R.id.etNewPassword);
        tvResetConfirm = (TextView) view.findViewById(R.id.tvResetConfirm);
        tvResetCancel = (TextView) view.findViewById(R.id.tvResetCancel);

        tvResetCancel.setOnClickListener(this);
        tvResetConfirm.setOnClickListener(this);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etOldPassword.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etNewPassword.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.tvResetCancel:
                dismiss();
                break;
            case R.id.tvResetConfirm:

                oldPassword = etOldPassword.getText().toString();
                SharedPreferencesUtils.readLoginUserNameAndPassword(context);
                if (StringUtil.isEmpty(oldPassword)) {
//                    ((BaseActivity) context).toastUtils.showInfoWithStatus("请输入旧密码");
                    ToastHelper.showShortMsg("请输入旧密码");
                    return;
                } else if (!StringUtil.checkPassword(oldPassword)) {
//                    ((BaseActivity) context).toastUtils.showErrorWithStatus(context.getResources().readString(R.string.error_password_form));
                    ToastHelper.showShortMsg(context.getResources().getString(R.string.error_password_form));
                    return;
                } else if (!oldPassword.equals(Constant.LOGIN_PASSWORD)) {

//                    ((BaseActivity) context).toastUtils.showSuccessWithStatus("旧密码与登录密码不一致");
                    ToastHelper.showShortMsg("旧密码与登录密码不一致");
                    return;
                }

                newPassword = etNewPassword.getText().toString();
                if (StringUtil.isEmpty(newPassword)) {
//                    ((BaseActivity) context).toastUtils.showInfoWithStatus("请输入新密码");
                    ToastHelper.showShortMsg("请输入新密码");
                    return;
                } else if (!StringUtil.checkPassword(newPassword)) {
//                    ((BaseActivity) context).toastUtils.showErrorWithStatus(context.getResources().readString(R.string.error_password_form));
                    ToastHelper.showShortMsg(context.getResources().getString(R.string.error_password_form));
                    return;
                }
                if (newPassword.equals(oldPassword)) {
//                    ((BaseActivity) context).toastUtils.showInfoWithStatus("旧密码与新密码一致");
                    ToastHelper.showShortMsg("旧密码与新密码一致");
                    return;
                }
                listener.ClickResult(0);
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }

}
