/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boer.delos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.model.Device;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ExpressionAdapter extends ArrayAdapter<Device> {

    private List<Device> datas = new ArrayList<>();
    private LayoutInflater inflater = null;

    public ExpressionAdapter(Context context, int textViewResourceId, List<Device> objects) {
        super(context, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        this.datas = objects;
        L.e("ExpressionAdapter's datas===" + new Gson().toJson(objects));
    }

    public void setDatas(List<Device> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Device getItem(int position) {
        return datas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_comfort_live, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.ivItemImage);
            viewHolder.itemText = (TextView) convertView.findViewById(R.id.tvItemText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device comfortLive = datas.get(position);
        L.e("ExpressionAdapter's datas===" + new Gson().toJson(comfortLive));
//        viewHolder.itemImage.setImageResource(comfortLive.getResId());
        viewHolder.itemText.setText(comfortLive.getRoomname());

        return convertView;
    }

    class ViewHolder {
        public ImageView itemImage;
        public TextView itemText;
    }

}
