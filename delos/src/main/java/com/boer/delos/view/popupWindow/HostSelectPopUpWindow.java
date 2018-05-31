package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.GatewayAdapter;
import com.boer.delos.model.GatewayInfo;

import java.util.List;

/**
 * Created by zhukang on 16/7/22.
 */
public class HostSelectPopUpWindow extends PopupWindow implements View.OnClickListener {

    private ListView lvSelectHost;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private List<GatewayInfo> gatewayList;
    private GatewayAdapter adapter;
    private ClickResultListener listener;
    private GatewayInfo selectGateway;

    public HostSelectPopUpWindow(Context context, List<GatewayInfo> gatewayList, ClickResultListener listener) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_select_host, null);
        this.gatewayList = gatewayList;
        this.listener = listener;

        setContentView(view);
        setProperty();
        initView();
    }

    private void setProperty() {
        // 设置弹窗体宽度，高度
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        TextView tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        lvSelectHost = (ListView) view.findViewById(R.id.lvSelectHost);
        adapter = new GatewayAdapter(context, gatewayList);
        lvSelectHost.setAdapter(adapter);
        lvSelectHost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGateway = gatewayList.get(position);
                listener.result(selectGateway);
                dismiss();
            }
        });

        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                if (listener != null) {
                    listener.result(null);
                }
                dismiss();
                break;
        }
    }

    public interface ClickResultListener {
        void result(GatewayInfo gatewayInfo);
    }

}
