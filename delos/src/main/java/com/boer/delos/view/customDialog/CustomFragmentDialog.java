package com.boer.delos.view.customDialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.zhy.android.percent.support.PercentLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/2/15 0015 09:35
 * @Modify:
 * @ModifyDate:
 */


public class CustomFragmentDialog extends DialogFragment implements TextView.OnEditorActionListener {
    @Bind(R.id.id_textViewContent)
    TextView idTextViewContent;
    @Bind(R.id.id_editContent)
    EditText idEditContent;
    @Bind(R.id.tvRestoreCancel)
    TextView tvRestoreCancel;
    @Bind(R.id.tvRestoreConfirm)
    TextView tvRestoreConfirm;
    @Bind(R.id.pop_alarm_bg)
    PercentLinearLayout popAlarmBg;

    private View rootView;
    private String title;
    private String content;
    private boolean cancle;

    public static CustomFragmentDialog newInstanse(String title, String content, boolean leftCancel) {
        CustomFragmentDialog fragmentDialog = new CustomFragmentDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putBoolean("leftCancel", leftCancel);
        fragmentDialog.setArguments(args);
        return fragmentDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        title = getArguments().getString("title");
        content = getArguments().getString("content");
        cancle = getArguments().getBoolean("leftCancel");
        rootView = inflater.inflate(R.layout.fragment_dialog_show, container);
        ButterKnife.bind(this, rootView);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        return rootView;
    }

    private void initView() {
//        getDialog().setTitle(title);
//        getDialog().setContentView(rootView);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        idTextViewContent.setText(content);

        if (idEditContent.getVisibility() == View.VISIBLE) {
            idEditContent.requestFocus();                       // EditText获得焦点
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 显示软键盘
            idEditContent.setOnEditorActionListener(this);      // 设置Action监听器
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            activity.onFinishEditDialog(idEditContent.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }

    @OnClick({R.id.tvRestoreCancel, R.id.tvRestoreConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRestoreCancel:
                dismiss();
                break;
            case R.id.tvRestoreConfirm:
                if (listener != null) {
                    listener.onComfire(idEditContent.getText().toString());
                }
                break;
        }
    }


    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public interface EditComfireDialogListener {
        void onComfire(String inputText);
    }

    private EditComfireDialogListener listener;

    public void setListener(EditComfireDialogListener listener) {
        this.listener = listener;
    }

}
