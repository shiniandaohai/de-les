package com.boer.delos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;

/**
 * Created by Administrator on 2016/7/5 0005.
 * 声音设置列表项
 */
public class SoundView extends LinearLayout implements Checkable {
    private TextView mTitle;
    private CheckBox mCheckBox;

    public SoundView(Context context) {
        this(context, null);
    }

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.sounds_view, this, true);
        mTitle = (TextView) v.findViewById(R.id.sound_view_title);
        mCheckBox = (CheckBox) v.findViewById(R.id.sound_view_checkbox);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    @Override
    public void toggle() {
        mCheckBox.toggle();
    }
}
