package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;


/**
 * @author gaolong
 */
public class ShowRequestEditPopupWindow {
    private Context mContext;
    private View topView;
    private IShowRequest iShowRequest;
    private PopupWindow pop;
    private TextView tv_textCancel, tv_textSure, tv_title;
    private EditText editText;
    String title, content;


    public ShowRequestEditPopupWindow(Context context, View topView, String title) {
        mContext = context;
        this.topView = topView;
        this.title = title;
        this.content = content;
    }

    public void showPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.popup_window_edit_request, null);
        tv_textCancel = (TextView) view.findViewById(R.id.tv_textCancel);
        tv_textSure = (TextView) view.findViewById(R.id.tv_textSure);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        editText = (EditText) view.findViewById(R.id.edt_content);
        tv_title.setText(title);

        tv_textSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iShowRequest != null) {

                    iShowRequest.rightButtonClick(!TextUtils.isEmpty(editText.getText()) ? editText.getText().toString() : "");
                    dismissPop();
                }
            }
        });
        tv_textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }

        pop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // pop.setAnimationStyle(R.style.MenuAnimationFade);
        pop.setBackgroundDrawable(new BitmapDrawable());//需要设置背景，用物理键返回的时候
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);

        pop.showAtLocation(topView, Gravity.CENTER, 0, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pop.dismiss();
            }
        });

    }

    public void setShowRequestPopupWindow(IShowRequest s) {
        iShowRequest = s;
    }

    public void dismissPop() {
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public interface IShowRequest {
        void rightButtonClick(String result);
    }

    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }
}
