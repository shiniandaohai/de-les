package com.boer.delos.activity.settings;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;


public class HelpAndFeedbackActivity extends CommonBaseActivity {


    @Bind(R.id.etAdviceFeedback)
    EditText etAdviceFeedback;
    @Bind(R.id.tvAdviceFeedbackNum)
    TextView tvAdviceFeedbackNum;
    @Bind(R.id.tvAdviceFeedbackBtn)
    TextView tvAdviceFeedbackBtn;

    @Override
    protected int initLayout() {
        return R.layout.activity_help_and_feedback;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.advice_feedback);

    }

    @Override
    protected void initData() {

        tvAdviceFeedbackBtn.setEnabled(false);


    }

    @Override
    protected void initAction() {

        etAdviceFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvAdviceFeedbackBtn.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 200) {
                    etAdviceFeedback.setText(s.subSequence(0, 200));
                    CharSequence content = etAdviceFeedback.getText();
                    if (content instanceof Spannable) {
                        Spannable spannable = (Spannable) content;
                        Selection.setSelection(spannable, content.length());
                    }
                    ToastHelper.showShortMsg(getString(R.string.setting_feedback_fliter));
                } else {
                    tvAdviceFeedbackNum.setText((200 - s.length()) + "");
                }
            }
        });


    }


    @OnClick(R.id.tvAdviceFeedbackBtn)
    public void onClick() {
        ToastUtil.show(this,"提交成功！\n感谢您的反馈！");
        finish();
    }
}
