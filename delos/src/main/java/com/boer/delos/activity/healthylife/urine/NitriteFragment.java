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
 * @Description: 亚硝酸盐界面
 * create at 2016/5/5 20:37
 *
 */
public class NitriteFragment extends Fragment{

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_nitrite, null);
        return rootView;
    }
}
