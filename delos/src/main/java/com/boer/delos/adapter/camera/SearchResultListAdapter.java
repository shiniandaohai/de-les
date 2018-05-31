package com.boer.delos.adapter.camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.camera.SearchResult;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class SearchResultListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SearchResult> list;
    public SearchResultListAdapter(LayoutInflater inflater,List<SearchResult> list) {
        this.list = list;
        this.mInflater = inflater;
    }

    public int getCount() {

        return list.size();
    }

    public Object getItem(int position) {

        return list.get(position);
    }

    public long getItemId(int position) {

        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final SearchResult result = (SearchResult) getItem(position);
        ViewHolder holder = null;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.search_device_result, null);

            holder = new ViewHolder();
            holder.uid = (TextView) convertView.findViewById(R.id.uid);
            holder.ip = (TextView) convertView.findViewById(R.id.ip);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.uid.setText(result.UID);
        holder.ip.setText(result.IP);
        // holder.port.setText(result.Port);

        return convertView;
    }// getView()

    public final class ViewHolder {
        public TextView uid;
        public TextView ip;
    }
}
