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
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

/**
 * Created by zhukang on 16/7/18.
 */
public class SingleEditTextPopUpWindow extends PopupWindow implements View.OnClickListener {

    private View view;
    private LayoutInflater inflater;
    private Context mContext;
    private ClickListener mListener;
    private int mTitle;
    private int mEditTextHint;
    private EditText mEditText;

    public SingleEditTextPopUpWindow(Context context, int title, int editTextHint, ClickListener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        this.mTitle = title;
        this.mEditTextHint = editTextHint;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_single_edittext, null);
        setContentView(view);

        setProperty();
        initView();
    }

    private void initView() {
        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        title.setText(mTitle);
        mEditText = (EditText) view.findViewById(R.id.etEditText);
        mEditText.setHint(mEditTextHint);
        view.findViewById(R.id.tvCancel).setOnClickListener(this);
        view.findViewById(R.id.tvOk).setOnClickListener(this);
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
    public void onClick(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (view.getId()) {
            case R.id.tvCancel:
                dismiss();
                break;
            case R.id.tvOk:

                if (!judgeInputIsLegitimate(mEditText.getText().toString())) {
                    return;
                }

                mListener.okClick(mEditText.getText().toString());
                dismiss();
                break;
        }
    }

    /**
     * 判断区域名是否合法
     *
     * @param input
     * @return
     */
    private boolean judgeInputIsLegitimate(String input) {
        if (StringUtil.isEmpty(input)) {
            ToastHelper.showShortMsg("区域名能不能为空");
            return false;
        }
        if (input.contains(" ")) {
            ToastHelper.showShortMsg("区域名不能包含空格");
            return false;
        }
        return true;
    }

    public interface ClickListener {
        void okClick(String text);
    }

}
