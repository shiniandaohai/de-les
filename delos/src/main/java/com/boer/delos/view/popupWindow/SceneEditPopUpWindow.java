package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.SceneEditPopAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Room;
import com.boer.delos.model.SceneManage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 场景编辑popup
 * create at 2016/4/12 9:56
 *
 */
public class SceneEditPopUpWindow extends PopupWindow implements View.OnClickListener {
    private static final int TAG = 0;
    private  int checkPositon;
    private List<SceneManage> list;
    private ClickResultListener listener;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private GridView gvScenePopup;
    private TextView tvSceneEditPopupCancle;
    private TextView tvSceneEditPopupOk;
    private TextView etPopupScenceCustomName;
    private String customName;
    public int checkPosition;
    private SceneEditPopAdapter mSceneEditPopAdapter;
    private RelativeLayout rlPopupSceneName;
    private String checkType;
    private Room room;

    public SceneEditPopUpWindow(Context context, Room room, ClickResultListener clickResultListener) {
        this.context = context;
        this.listener = clickResultListener;
        this.room = room;

        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.popup_scence_edit, null);

        setContentView(view);
        setProperty();
        initView();
        initData();
    }


    private void setProperty() {
        //设置弹窗体的宽高
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    private void initView() {
        tvSceneEditPopupCancle = (TextView) view.findViewById(R.id.tvScenceEditPopupCancle);
        tvSceneEditPopupOk = (TextView) view.findViewById(R.id.tvScenceEditPopupOk);
        etPopupScenceCustomName = (TextView) view.findViewById(R.id.etPopupScenceCustomName);
        gvScenePopup = (GridView) view.findViewById(R.id.gvScencePopup);
        rlPopupSceneName = (RelativeLayout) view.findViewById(R.id.rlPopupScenceName);

        tvSceneEditPopupCancle.setOnClickListener(this);
        tvSceneEditPopupOk.setOnClickListener(this);
        rlPopupSceneName.setOnClickListener(this);

    }


    private void initData() {
        this.list = new ArrayList<>();
        this.list= Constant.sceneTypeList();
        for (int i = 0; i < list.size(); i++) {
            SceneManage manage  = list.get(i);
            if(room.getType().equals(context.getString(manage.getType()))){
                this.checkPositon = i;
                break;
            }
        }
        this.checkType = room.getType();
        etPopupScenceCustomName.setText(this.room.getName());
        mSceneEditPopAdapter = new SceneEditPopAdapter(1, checkPositon, context);
        mSceneEditPopAdapter.setDatas(list);
        mSceneEditPopAdapter.setImageView(checkPositon);
        gvScenePopup.setAdapter(mSceneEditPopAdapter);
        initListener();
    }

    private void initListener() {
        gvScenePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position;
                mSceneEditPopAdapter.setImageView(position);
                mSceneEditPopAdapter.notifyDataSetChanged();
                checkType = context.getResources().getString(list.get(position).getType());
//                L.e("选中的房间类型==="+list.get(position).getType()+"     从string里面获取的"+context.getResources().readString(list.get(position).getType()));
            }
        });
    }

    public String getCheckType() {
        return checkType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvScenceEditPopupCancle:
                dismiss();
                break;
            case R.id.tvScenceEditPopupOk:
                customName= etPopupScenceCustomName.getText().toString();
                listener.ClickResult(1);
                break;
        }
    }

    public interface ClickResultListener {
        void ClickResult(int tag);
    }

    public String getcustomName() {
        return customName;
    }
}
