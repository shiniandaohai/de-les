package com.boer.delos.activity.healthylife.urine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boer.delos.R;

/**
 * @author PengJiYang
 * @Description: 白细胞界面
 * create at 2016/5/5 20:33
 *
 */
public class WhiteCellFragment  extends Fragment{

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_white_cell, null);
        return rootView;
    }
}
