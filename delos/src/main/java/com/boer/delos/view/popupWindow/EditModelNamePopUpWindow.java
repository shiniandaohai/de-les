package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.BaseResult;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sign.HexUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.boer.delos.utils.StringUtil.isOnlyEightByte;
import static com.boer.delos.utils.sign.HexUtils.HexStringTobytes;

/**
 * @author XieQingTing
 * @Description: 编辑模式名称popup
 * create at 2016/4/12 9:35
 */
public class EditModelNamePopUpWindow extends PopupWindow implements View.OnClickListener {

    private ClickResultListener listener;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    public EditText etModelName;
    private TextView tvDisagree;
    private TextView tvAgree;
    private int TAG = 2;
    private int TAG1 = 3;
    private String modelName;
    private ISimpleInterfaceString mStringListener;

    public EditModelNamePopUpWindow(Context context) {
        this(context, null);
    }

    public EditModelNamePopUpWindow(Context context, ClickResultListener clickResultListener) {
        this.context = context;
        this.listener = clickResultListener;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_edit_mudel_name, null);
        setContentView(view);

        setProperty();
        initView();

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

    private void initView() {
        etModelName = (EditText) view.findViewById(R.id.etModelName);
        tvDisagree = (TextView) view.findViewById(R.id.tvDisagree);
        tvAgree = (TextView) view.findViewById(R.id.tvAgree);

        tvDisagree.setOnClickListener(this);
        tvAgree.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etModelName.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.tvDisagree:

                dismiss();
                break;
            case R.id.tvAgree:
                modelName = etModelName.getText().toString();
                if (StringUtil.isEmpty(modelName)) {
                    BaseApplication.showToast(context.getString(R.string.text_not_null));
                    return;
                }
                if (modelName.contains(" ")) {
                    ToastHelper.showShortMsg(context.getString(R.string.text_nor_space));
                }
                if (!StringUtil.isOnlyEightByte(modelName)) {
                    ToastHelper.showShortMsg(context.getString(R.string.text_over_four));
                    return;
                }

//                Pattern pa = Pattern.compile("[\\u4e00-\\u9fa5]");
//                Matcher matcher = pa.matcher(modelName);
//                if (!matcher.find()) {
//
//                    return;
//                } else {
//                    if (modelName.length() > 4) {
//                        BaseApplication.showToast(context.getString(R.string.text_over_four));
//                        return;
//                    }
//                }

                if (mStringListener != null) {
                    mStringListener.clickListener(modelName);
                } else if (listener != null) {
                    listener.ClickResult(TAG);
                }
                dismiss();
                break;
        }
    }

    public String getModelName() {
        return modelName;
    }

    public interface ClickResultListener {
        void ClickResult(int tag);

    }

    public void setStringListener(ISimpleInterfaceString stringListener) {
        mStringListener = stringListener;
    }
}
