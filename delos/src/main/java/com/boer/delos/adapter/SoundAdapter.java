package com.boer.delos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.boer.delos.model.Sound;
import com.boer.delos.view.SoundView;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5 0005.
 * 声音列表
 */
public class SoundAdapter extends ArrayAdapter<Sound> {

    public SoundAdapter(Context context, int textViewResourceId,
                          List<Sound> objects) {
        super(context, textViewResourceId, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null ) {
            convertView = new SoundView(getContext());
        }

        Sound sound = getItem(position);

        SoundView countryView = (SoundView) convertView;
        countryView.setTitle(sound.getName());

        return convertView;
    }

}
