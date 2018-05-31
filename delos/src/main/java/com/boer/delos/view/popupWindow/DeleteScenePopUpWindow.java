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

/**
 * @author XieQingTing
 * @Description: 场景删除popup
 * create at 2016/4/12 9:55
 *
 */
public class DeleteScenePopUpWindow extends PopupWindow implements View.OnClickListener {

    private static final int TAG = 1;
    private  ClickResultListener listener;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView tvDisagree, tvAgree, tvTitle, tvMessage;
    private String title;// 弹出框标题
    private String message;// 弹出框内容

    public DeleteScenePopUpWindow(Context context, String title, String message, ClickResultListener clickResultListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_scence_delete, null);
        this.listener=clickResultListener;
        this.title = title;
        this.message = message;

        setContentView(view);
        initView();
        setProperty();


    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        tvDisagree = (TextView) view.findViewById(R.id.tvDisagree);
        tvAgree = (TextView) view.findViewById(R.id.tvAgree);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        tvAgree.setOnClickListener(this);
        tvDisagree.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDisagree:
                dismiss();
                break;
            case R.id.tvAgree:
                listener.ClickResult(TAG);
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }



}
