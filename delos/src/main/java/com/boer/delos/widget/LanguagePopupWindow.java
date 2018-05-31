package com.boer.delos.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.boer.delos.R;

import java.util.ArrayList;

/**
 * Created by gaolong on 2017/3/14.
 */
public class LanguagePopupWindow {
    private Context context;
    private View view;
    private ArrayList<String> bean;
    private OnItemClick selectListener;
    private PopupWindow popupWindow;
    String language_simple_chinese;
    String language_english;

    public LanguagePopupWindow(Context context, View view, ArrayList<String> bean) {

        this.context = context;
        this.view = view;
        this.bean = bean;


    }

    public void showView() {

        language_simple_chinese = context.getString(R.string.language_simple_chinese);
        language_english = context.getString(R.string.language_english);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.popupwindow_line, null);
        ListView lvYear = (ListView) layout.findViewById(R.id.lv_line);
        View view_parent = layout.findViewById(R.id.view_parent);
        lvYear.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, bean));
        lvYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectListener != null) {

                    String language = "";
                    if (bean.get(position).equals(language_simple_chinese)) {
                        language = "zh";
                    } else if (bean.get(position).equals(language_english)) {
                        language = "en";
                    }

                    selectListener.select(language, position);
                    popupWindow.dismiss();
                }
            }
        });

        view_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            }
        });

        if (popupWindow != null) {
            if (popupWindow.isShowing())
                popupWindow.dismiss();
        }
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());//需要设置背景，用物理键返回的时候
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
//        popupWindow.showAsDropDown(view, 0, 0);
    }

    public interface OnItemClick {
        void select(String txt, int pos);
    }

    public void setOnItemClick(OnItemClick listener) {
        this.selectListener = listener;
    }

}
