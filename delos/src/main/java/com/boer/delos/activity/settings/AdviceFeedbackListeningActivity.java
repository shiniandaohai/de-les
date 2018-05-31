package com.boer.delos.activity.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.BaseApplication;

/**
 * @author PengJiYang
 * @Description: "意见反馈"界面
 * create at 2016/4/6 11:47
 */
public class AdviceFeedbackListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private EditText etAdviceFeedback;
    private TextView tvAdviceFeedbackNum;
    private TextView tvAdviceFeedbackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice_feedback);

        initView();
    }

    private void initView() {
        initTopBar(R.string.advice_feedback, null, true, false);
        tvAdviceFeedbackBtn = (TextView) findViewById(R.id.tvAdviceFeedbackBtn);
        tvAdviceFeedbackNum = (TextView) findViewById(R.id.tvAdviceFeedbackNum);
        etAdviceFeedback = (EditText) findViewById(R.id.etAdviceFeedback);

        tvAdviceFeedbackBtn.setEnabled(false);

        tvAdviceFeedbackBtn.setOnClickListener(this);
        etAdviceFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvAdviceFeedbackBtn.setEnabled(true);
                    tvAdviceFeedbackBtn.setBackgroundResource(R.drawable.shape_login_btn_bg);
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
                    BaseApplication.showToast("您输入的字数超过了限制");
                } else {
                    tvAdviceFeedbackNum.setText((200 - s.length()) + "");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAdviceFeedbackBtn:
                // TODO 提交反馈的意见并关闭当前界面

                finish();
                break;
        }
    }
}
