package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.boer.delos.R;
import com.boer.delos.adapter.RoomModeAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ModeAct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/7/28.
 */
public class ModeRoomPopUpWindow extends PopupWindow {

    private List<ModeAct> modeActList;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private ModeSelectListener listener;
    private GridView gridView;
    private RoomModeAdapter adapter;
    private List<Map<String, Object>> list = new ArrayList<>();
    private LinearLayout modelManagerLayout;

    public ModeRoomPopUpWindow(Context context, List<ModeAct> modeActList, ModeSelectListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.modeActList = modeActList;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_mode_selection_room, null);
        setContentView(view);
        setProperty();
        initView();
        initData();
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
        view.findViewById(R.id.vCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        modelManagerLayout = (LinearLayout) view.findViewById(R.id.llModelSelectManage);
        gridView = (GridView) view.findViewById(R.id.gvMode);
        adapter = new RoomModeAdapter(context, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener == null) {
                    return;
                }
                listener.result(i);
                dismiss();
            }
        });
        modelManagerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener == null)
                    return;
                listener.modelmanager();
                dismiss();
            }
        });
    }

    private void initData() {
        list.clear();
        list.addAll(Constant.modeActionListToMapList(modeActList));
        adapter.notifyDataSetChanged();
    }

    public void refreshData(List<ModeAct> modeActList) {
        this.modeActList = modeActList;
        list.clear();
        list.addAll(Constant.modeActionListToMapList(this.modeActList));
        if (adapter != null)
            adapter.notifyDataSetChanged();

    }
    public interface ModeSelectListener {
        void result(int position);
        void modelmanager();
    }
}
