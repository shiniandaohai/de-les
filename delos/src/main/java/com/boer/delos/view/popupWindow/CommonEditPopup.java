package com.boer.delos.view.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.interf.ISimpleInterfaceString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunzhibin on 2017/8/4.
 */

public class CommonEditPopup extends PopupWindow implements View.OnClickListener {
    private final Context context;
    private View mRootView;
    private ISimpleInterfaceString mStringListener;

    private TextView tv_title;
    private TextView tvDisagree;
    private TextView tvAgree;
    private EditText etModelName;

    public CommonEditPopup(Context context, String... titles) {
        this.context = context;
        mRootView = View.inflate(context, R.layout.popup_edit_common, null);
        setContentView(mRootView);
        setProperty();
        initView(titles);
    }


    private void setProperty() {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        mRootView.startAnimation(animation);
    }

    private void initView(String... titles) {
        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
        tvDisagree = (TextView) mRootView.findViewById(R.id.tvDisagree);
        tvAgree = (TextView) mRootView.findViewById(R.id.tvAgree);
        etModelName = (EditText) mRootView.findViewById(R.id.etModelName);
        showSoftKey(etModelName);


        if (TextUtils.isEmpty(titles[0])) {
            return;
        }
        tv_title.setText(titles[0]);
        etModelName.setText(titles[1]);
        etModelName.setSelection(etModelName.getText().length());
        tvDisagree.setOnClickListener(this);
        tvAgree.setOnClickListener(this);


    }

    public void setStringListener(ISimpleInterfaceString stringListener) {
        mStringListener = stringListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDisagree:
                dismiss();
                break;
            case R.id.tvAgree:
                if (mStringListener != null) {
                    mStringListener.clickListener(etModelName.getText().toString());
                }
                break;

        }
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

    private String mType;
    public void setLayoutByType(String type,Map<String,String> map){
        mType=type;
        if(type.equals("Light2")){
            mRootView.findViewById(R.id.llLight3).setVisibility(View.GONE);
            mRootView.findViewById(R.id.llLight4).setVisibility(View.GONE);
            if(map!=null){
                ((EditText)mRootView.findViewById(R.id.etLight1)).setText(map.get("name1"));
                ((EditText)mRootView.findViewById(R.id.etLight2)).setText(map.get("name2"));
            }
        }
        else if(type.equals("Light3")){
            mRootView.findViewById(R.id.llLight4).setVisibility(View.GONE);
            if(map!=null){
                ((EditText)mRootView.findViewById(R.id.etLight1)).setText(map.get("name1"));
                ((EditText)mRootView.findViewById(R.id.etLight2)).setText(map.get("name2"));
                ((EditText)mRootView.findViewById(R.id.etLight3)).setText(map.get("name3"));
            }
        }
        else if(type.equals("Light4")){
            if(map!=null){
                ((EditText)mRootView.findViewById(R.id.etLight1)).setText(map.get("name1"));
                ((EditText)mRootView.findViewById(R.id.etLight2)).setText(map.get("name2"));
                ((EditText)mRootView.findViewById(R.id.etLight3)).setText(map.get("name3"));
                ((EditText)mRootView.findViewById(R.id.etLight4)).setText(map.get("name4"));
            }
        }
        else{
            mRootView.findViewById(R.id.llLight1).setVisibility(View.GONE);
            mRootView.findViewById(R.id.llLight2).setVisibility(View.GONE);
            mRootView.findViewById(R.id.llLight3).setVisibility(View.GONE);
            mRootView.findViewById(R.id.llLight4).setVisibility(View.GONE);
        }
    }

    public Map<String,String> getNames(String type){
        Map<String,String> map=new HashMap<String, String>();
        String name1="";
        String name2="";
        String name3="";
        String name4="";
        if(mType.equals("Light2")){
            name1=((EditText)mRootView.findViewById(R.id.etLight1)).getText().toString();
            name2=((EditText)mRootView.findViewById(R.id.etLight2)).getText().toString();
            map.put("name1",name1);
            map.put("name2",name2);
            return map;
        }
        else if(mType.equals("Light3")){
            name1=((EditText)mRootView.findViewById(R.id.etLight1)).getText().toString();
            name2=((EditText)mRootView.findViewById(R.id.etLight2)).getText().toString();
            name3=((EditText)mRootView.findViewById(R.id.etLight3)).getText().toString();
            map.put("name1",name1);
            map.put("name2",name2);
            map.put("name3",name3);
            return map;
        }
        else if(mType.equals("Light4")){
            name1=((EditText)mRootView.findViewById(R.id.etLight1)).getText().toString();
            name2=((EditText)mRootView.findViewById(R.id.etLight2)).getText().toString();
            name3=((EditText)mRootView.findViewById(R.id.etLight3)).getText().toString();
            name4=((EditText)mRootView.findViewById(R.id.etLight4)).getText().toString();
            map.put("name1",name1);
            map.put("name2",name2);
            map.put("name3",name3);
            map.put("name4",name4);
            return map;
        }
        return null;
    }
}
