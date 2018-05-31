package com.boer.delos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.ISimpleInterfaceString;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/31 0031 17:21
 * @Modify:
 * @ModifyDate:
 */

public class InterfaceFragment extends LazyFragment implements ISimpleInterfaceString {

    @Override
    public void clickListener(String tag) {

    }

    @Override
    protected void lazyLoad() {

    }
}
